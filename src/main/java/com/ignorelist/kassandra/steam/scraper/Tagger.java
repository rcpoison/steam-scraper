/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.technofovea.hl2parse.vdf.VdfNode;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author poison
 */
public class Tagger {

	public static class TaggerOptions {

		private EnumSet<TagType> tagTypes;
		private Set<String> removeTags;
		private Set<String> whiteList;
		private boolean removeNotWhiteListed;
		private Map<String, String> replacementMap;

		public EnumSet<TagType> getTagTypes() {
			return tagTypes;
		}

		public void setTagTypes(EnumSet<TagType> tagTypes) {
			this.tagTypes=tagTypes;
		}

		public Set<String> getRemoveTags() {
			return removeTags;
		}

		public void setRemoveTags(Set<String> removeTags) {
			this.removeTags=removeTags;
		}

		public Set<String> getWhiteList() {
			return whiteList;
		}

		public void setWhiteList(Set<String> whiteList) {
			this.whiteList=whiteList;
		}

		public boolean isRemoveNotWhiteListed() {
			return removeNotWhiteListed;
		}

		public void setRemoveNotWhiteListed(boolean removeNotWhiteListed) {
			this.removeNotWhiteListed=removeNotWhiteListed;
		}

		public Map<String, String> getReplacementMap() {
			return replacementMap;
		}

		public void setReplacementMap(Map<String, String> replacementMap) {
			this.replacementMap=replacementMap;
		}

		@Override
		public String toString() {
			return "TaggerOptions{"+"tagTypes="+tagTypes+", removeTags="+removeTags+", whiteList="+whiteList+", removeNotWhiteListed="+removeNotWhiteListed+", replacementMap="+replacementMap+'}';
		}

	}
	private static final Logger LOG=Logger.getLogger(Tagger.class.getName());

	private final BatchTagLoader tagLoader;

	public Tagger(BatchTagLoader tagLoader) {
		this.tagLoader=tagLoader;
	}

	/**
	 * @param args the command line arguments
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
		final BatchTagLoader tagLoader=new BatchTagLoader(new HtmlTagLoader(pathResolver.findCachePath("html")));

		Tagger tagger=new Tagger(tagLoader);
		Set<Path> sharedConfigPaths=new LinkedHashSet<>();
		if (commandLine.hasOption("f")) {
			String[] passedPaths=commandLine.getOptionValues("f");
			if (0==passedPaths.length) {
				throw new IllegalArgumentException("no paths passed in -f");
			}
			for (String p : passedPaths) {
				sharedConfigPaths.add(Paths.get(p));
			}

		} else {
			sharedConfigPaths=pathResolver.findSharedConfig();
		}

		if (sharedConfigPaths.size()>1&&!commandLine.hasOption("w")) {
			System.err.println("multiple sharedconfig.vdf available:\n"+Joiner.on("\n").join(sharedConfigPaths)+"\n, can not write to stdout. Need to specify -w or -f with a single sharedconfig.vdf");
			System.exit(1);
		}

		TaggerOptions taggerOptions=new TaggerOptions();
		final String[] removeTagsValues=commandLine.getOptionValues("remove");
		if (null!=removeTagsValues) {
			taggerOptions.setRemoveTags(Sets.newHashSet(removeTagsValues));
		}

		if (commandLine.hasOption("i")) {
			final Path whitelistPath=Paths.get(commandLine.getOptionValue("i"));
			Set<String> whiteList=Sets.newHashSet(
					Iterables.filter(Files.readAllLines(whitelistPath, Charsets.UTF_8), new Predicate<String>() {
						@Override
						public boolean apply(String input) {
							return null!=input&&input.length()>0&&!input.startsWith("#");
						}
					}));
			taggerOptions.setWhiteList(whiteList);
		}
		if (commandLine.hasOption("R")) {
			Path replacementFile=Paths.get(commandLine.getOptionValue("R"));
			final Reader replacementFileReader=Files.newBufferedReader(replacementFile, Charsets.UTF_8);
			try {
				Properties replacementProperties=new Properties();
				replacementProperties.load(replacementFileReader);
				Map<String, String> replacementMap=Maps.fromProperties(replacementProperties);
				taggerOptions.setReplacementMap(replacementMap);
			} finally {
				IOUtils.closeQuietly(replacementFileReader);
			}

		}

		final boolean removeNotWhiteListed=commandLine.hasOption("I");
		taggerOptions.setRemoveNotWhiteListed(removeNotWhiteListed);

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
		taggerOptions.setTagTypes(EnumSet.copyOf(tagTypes));

		final boolean printTags=commandLine.hasOption("p");

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

	private static void printHelp(Options options) throws RecognitionException, IOException {
		HelpFormatter formatter=new HelpFormatter();
		formatter.printHelp("java -jar steam-scraper-*.one-jar.jar", options);
		PathResolver pathResolver=new PathResolver();
		System.out.println("\nlibrary directories:\n"+Joiner.on("\n").join(pathResolver.findAllLibraryDirectories()));
		System.out.println("\nsharedconfig files:\n"+Joiner.on("\n").join(pathResolver.findSharedConfig()));
	}

	private static Options buildOptions() {
		Options options=new Options();
		options.addOption("w", false, "directly overwrite sharedconfig.vdf (quit steam before running!)");
		options.addOption(Option.builder("f").hasArgs().argName("file").desc("absolute path to sharedconfig.vdf to use").build());
		options.addOption("h", "help", false, "show this help and print paths");
		options.addOption("c", false, "don't add categories");
		options.addOption("g", false, "don't add genres");
		options.addOption("u", false, "add user tags");
		options.addOption("p", false, "print all available tags (respects -c, -g and -u)");
		options.addOption(Option.builder("r").longOpt("remove").hasArgs().argName("category").desc("remove categories").build());
		options.addOption(Option.builder("i").hasArg().argName("file").desc("whitelist for tags to include (one tag per line)").build());
		options.addOption("I", false, "remove all existing tags not in specified whitelist");
		options.addOption(Option.builder("R").hasArg().argName("file").desc("file containing replacements (one replacement per line, in the format original=replacement)").build());
		return options;
	}

	public Set<String> getAvailableTags(Set<Path> sharedConfigPaths, TaggerOptions taggerOptions) throws IOException, RecognitionException {
		Set<String> availableTags=new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		Set<Long> availableGameIds=new HashSet<>();
		for (Path path : sharedConfigPaths) {
			SharedConfig sharedConfig=new SharedConfig(path);
			availableGameIds.addAll(sharedConfig.getGameIds());
			for (Long gameId : sharedConfig.getGameIds()) {
				availableTags.addAll(sharedConfig.getTags(gameId));
			}
		}
		availableGameIds.addAll(LibraryScanner.findGames(new PathResolver().findAllLibraryDirectories()));
		SetMultimap<Long, String> load=tagLoader.load(availableGameIds, taggerOptions.getTagTypes());
		availableTags.addAll(load.values());
		return availableTags;
	}

	public VdfRoot tag(Path path, TaggerOptions taggerOptions) throws IOException, RecognitionException {
		SharedConfig sharedConfig=new SharedConfig(path);
		Set<Long> availableGameIds=new HashSet<>();
		SetMultimap<Long, String> gameTags=HashMultimap.create();

		for (Map.Entry<Long, VdfNode> entry : sharedConfig.getGameNodeMap().entrySet()) {
			try {
				final long gameId=entry.getKey();
				availableGameIds.add(gameId);
			} catch (Exception e) {
				LOG.log(Level.WARNING, "failed to load gameId", e);
			}

		}

		// vdf doesn't contain all games, add the rest (at least the installed games)
		PathResolver pathResolver=new PathResolver();
		availableGameIds.addAll(LibraryScanner.findGames(pathResolver.findAllLibraryDirectories()));
		SetMultimap<Long, String> availableTags=tagLoader.load(availableGameIds, taggerOptions.getTagTypes());
		
		for (Long gameId : availableGameIds) {
			addTags(sharedConfig, gameId, taggerOptions, availableTags.get(gameId));
		}
		return sharedConfig.getRootNode();
	}

	private void addTags(SharedConfig sharedConfig, Long gameId, TaggerOptions taggerOptions, Set<String> externalTags) {
		Set<String> existingTags=sharedConfig.getTags(gameId);

		if (null!=taggerOptions.getWhiteList()&&!taggerOptions.getWhiteList().isEmpty()) {
			externalTags.retainAll(taggerOptions.getWhiteList());
		}
		existingTags.addAll(externalTags);
		if (null!=taggerOptions.getRemoveTags()) {
			existingTags.removeAll(taggerOptions.getRemoveTags());
		}
		if (null!=taggerOptions.getWhiteList()&&!taggerOptions.getWhiteList().isEmpty()&&taggerOptions.isRemoveNotWhiteListed()) {
			existingTags.retainAll(taggerOptions.getWhiteList());
		}
		if (null!=taggerOptions.getReplacementMap()) {
			for (Map.Entry<String, String> e : taggerOptions.getReplacementMap().entrySet()) {
				if (existingTags.remove(e.getKey())) {
					existingTags.add(e.getValue());
				}
			}
		}
		sharedConfig.setTags(gameId, existingTags);
	}

}
