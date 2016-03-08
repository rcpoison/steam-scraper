/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.technofovea.hl2parse.vdf.VdfNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author poison
 */
public class TaggerCli {

	private static class CliEventLoggerLoaded {

		@Subscribe
		public void logLoadedEvent(BatchTagLoader.GameInfoLoadedEvent event) {
			System.err.println(event.getGameInfo().getId()+" ("+event.getGameInfo().getName()+") loaded in "+event.getDurationMillis()+"ms");
		}
	}

	/**
	 * @param args the command line arguments
	 * @throws java.io.IOException
	 * @throws org.antlr.runtime.RecognitionException
	 * @throws org.apache.commons.cli.ParseException
	 */
	public static void main(String[] args) throws IOException, RecognitionException, ParseException {
		Options options=buildOptions();
		CommandLineParser parser=new DefaultParser();
		CommandLine commandLine=parser.parse(options, args);
		if (commandLine.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}
		final PathResolver pathResolver=new PathResolver();

		Configuration configuration;
		Path configurationFile=pathResolver.findConfiguration();
		if (Files.isRegularFile(configurationFile)) {
			configuration=Configuration.fromPropertiesFile(configurationFile);
		} else {
			configuration=new Configuration();
		}
		configuration=toConfiguration(configuration, commandLine);
		//configuration.toProperties().store(System.err, null);

		if (!Files.isRegularFile(configurationFile)) {
			configuration.writeProperties(configurationFile);
			System.err.println("no configuration file present, write based on CLI options: "+configurationFile.toString());
			configuration.toProperties().store(System.err, null);
		}

		Set<Path> sharedConfigPaths=configuration.getSharedConfigPaths();
		if (sharedConfigPaths.size()>1&&!commandLine.hasOption("w")) {
			System.err.println("multiple sharedconfig.vdf available:\n"+Joiner.on("\n").join(sharedConfigPaths)+"\n, can not write to stdout. Need to specify -w or -f with a single sharedconfig.vdf");
			System.exit(1);
		}

		Tagger.Options taggerOptions=Tagger.Options.fromConfiguration(configuration);

		final String[] removeTagsValues=commandLine.getOptionValues("remove");
		if (null!=removeTagsValues) {
			taggerOptions.setRemoveTags(Sets.newHashSet(removeTagsValues));
		}

		Set<TagType> tagTypes=configuration.getTagTypes();
		if (null==tagTypes) {
			System.err.println("no tag types!");
			System.exit(1);
		}

		final boolean printTags=commandLine.hasOption("p");

		final BatchTagLoader tagLoader=new BatchTagLoader(new HtmlTagLoader(pathResolver.findCachePath("html")), configuration.getDownloadThreads());
		if (commandLine.hasOption("v")) {
			tagLoader.registerEventListener(new CliEventLoggerLoaded());
		}
		Tagger tagger=new Tagger(tagLoader);

		if (printTags) {
			Set<String> availableTags=tagger.getAvailableTags(sharedConfigPaths, taggerOptions);
			Joiner.on("\n").appendTo(System.out, availableTags);
		} else {
			for (Path path : sharedConfigPaths) {
				VdfNode tagged=tagger.tag(path, taggerOptions);
				if (commandLine.hasOption("w")) {
					Path backup=path.getParent().resolve(path.getFileName().toString()+".bak"+new Date().getTime());
					Files.copy(path, backup, StandardCopyOption.REPLACE_EXISTING);
					System.err.println("backup up "+path+" to "+backup);
					Files.copy(new ByteArrayInputStream(tagged.toPrettyString().getBytes(StandardCharsets.UTF_8)), path, StandardCopyOption.REPLACE_EXISTING);
					System.err.println("wrote "+path);
				} else {
					System.out.println(tagged.toPrettyString());
					System.err.println("pipe to file and copy to: "+path.toString());
				}
			}
		}
	}

	private static Configuration toConfiguration(Configuration configuration, CommandLine commandLine) throws IOException {
		if (commandLine.hasOption("f")) {
			Set<Path> sharedConfigPaths=new LinkedHashSet<>();
			String[] passedPaths=commandLine.getOptionValues("f");
			if (0==passedPaths.length) {
				throw new IllegalArgumentException("no paths passed in -f");
			}
			for (String p : passedPaths) {
				sharedConfigPaths.add(Paths.get(p));
			}
			configuration.setSharedConfigPaths(sharedConfigPaths);

		} else if (null==configuration.getSharedConfigPaths()||configuration.getSharedConfigPaths().isEmpty()) {
			configuration.setSharedConfigPaths(new PathResolver().findSharedConfig());
		}

		if (commandLine.hasOption("R")) {
			Path replacementFile=Paths.get(commandLine.getOptionValue("R"));
			configuration.setReplacements(replacementFile);
		}

		if (commandLine.hasOption("i")) {
			Path whiteListFile=Paths.get(commandLine.getOptionValue("i"));
			configuration.setWhiteList(whiteListFile);
		}

		final boolean removeNotWhiteListed=commandLine.hasOption("I");
		configuration.setRemoveNotWhiteListed(removeNotWhiteListed);

		if (commandLine.hasOption("c")||commandLine.hasOption("g")||commandLine.hasOption("u")) {
			Set<TagType> tagTypes=new HashSet<>();
			final boolean addCategories=!commandLine.hasOption("c");
			if (addCategories) {
				tagTypes.add(TagType.CATEGORY);
			}
			final boolean addGenres=!commandLine.hasOption("g");
			if (addGenres) {
				tagTypes.add(TagType.GENRE);
			}
			final boolean addUserTags=commandLine.hasOption("u");
			if (addUserTags) {
				tagTypes.add(TagType.USER);
			}
			configuration.setTagTypes(tagTypes);

		} else if (null==configuration.getTagTypes()||configuration.getTagTypes().isEmpty()) {
			configuration.setTagTypes(Sets.newHashSet(TagType.CATEGORY, TagType.GENRE));
		}
		
		if (commandLine.hasOption("t")) {
			configuration.setDownloadThreads(Integer.valueOf(commandLine.getOptionValue("t")));
		} else if (null==configuration.getDownloadThreads()) {
			configuration.setDownloadThreads(Runtime.getRuntime().availableProcessors()+1);
		}

		return configuration;
	}

	private static Options buildOptions() {
		Options options=new Options();
		options.addOption("w", false, "directly overwrite sharedconfig.vdf (quit steam before running!)");
		options.addOption(Option.builder("f").hasArgs().argName("file").desc("absolute path to sharedconfig.vdf to use").build());
		options.addOption("h", "help", false, "show this help and print paths");
		options.addOption("c", false, "don't add categories");
		options.addOption("g", false, "don't add genres");
		options.addOption("u", false, "add user tags");
		options.addOption("t", true, "number of threads");
		options.addOption("p", false, "print all available tags (respects -c, -g and -u)");
		options.addOption(Option.builder("r").longOpt("remove").hasArgs().argName("category").desc("remove categories").build());
		options.addOption(Option.builder("i").hasArg().argName("file").desc("whitelist for tags to include (one tag per line)").build());
		options.addOption("I", false, "remove all existing tags not in specified whitelist");
		options.addOption(Option.builder("R").hasArg().argName("file").desc("file containing replacements (one replacement per line, in the format original=replacement)").build());
		options.addOption("v", false, "verbose output");
		return options;
	}

	private static void printHelp(Options options) throws RecognitionException, IOException {
		HelpFormatter formatter=new HelpFormatter();
		formatter.printHelp("java -jar steam-scraper-*.one-jar.jar", options);
		PathResolver pathResolver=new PathResolver();
		System.out.println("\nlibrary directories:\n"+Joiner.on("\n").join(pathResolver.findAllLibraryDirectories()));
		System.out.println("\nsharedconfig files:\n"+Joiner.on("\n").join(pathResolver.findSharedConfig()));
	}

}
