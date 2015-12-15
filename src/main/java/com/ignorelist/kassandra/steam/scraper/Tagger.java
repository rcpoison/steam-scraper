/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.technofovea.hl2parse.vdf.VdfNode;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
public class Tagger {

	private final Scraper scraper;

	public Tagger(Scraper scraper) {
		this.scraper=scraper;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws IOException, RecognitionException, ParseException {
		Options options=new Options();
		options.addOption("w", false, "directly overwrite sharedconfig.vdf (potentially dangerous)");
		options.addOption(Option.builder("f").hasArgs().argName("file").desc("absolute path to sharedconfig.vdf to use").build());
		options.addOption("h", "help", false, "show this help and print paths");
		options.addOption("c", false, "don't add categories");
		options.addOption("g", false, "don't add genres");
		options.addOption("u", false, "add user tags");
		options.addOption("p", false, "print all available tags");
		options.addOption(Option.builder("r").longOpt("remove").hasArgs().argName("category").desc("remove categories").build());

		CommandLineParser parser=new DefaultParser();
		CommandLine commandLine=parser.parse(options, args);

		if (commandLine.hasOption("h")) {
			HelpFormatter formatter=new HelpFormatter();
			formatter.printHelp("java -jar steam-scraper-*.one-jar.jar", options);
			PathResolver pathResolver=new PathResolver();
			System.out.println("\nlibrary directories:\n"+Joiner.on("\n").join(pathResolver.findAllLibraryDirectories()));
			System.out.println("\nsharedconfig files:\n"+Joiner.on("\n").join(pathResolver.findSharedConfig()));
			System.exit(0);
		}
		final Scraper scraper=new Scraper();

		Tagger tagger=new Tagger(scraper);
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
			PathResolver pathResolver=new PathResolver();
			sharedConfigPaths=pathResolver.findSharedConfig();
		}

		if (sharedConfigPaths.size()>1&&!commandLine.hasOption("w")) {
			System.err.println("multiple sharedconfig.vdf available:\n"+Joiner.on("\n").join(sharedConfigPaths)+"\n, can not write to stdout. Need to specify -w or -f with a single sharedconfig.vdf");
			System.exit(1);
		}

		final String[] removeTagsValues=commandLine.getOptionValues("remove");
		final Set<String> removeTags;
		if (null!=removeTagsValues) {
			removeTags=Sets.newHashSet(removeTagsValues);
		} else {
			removeTags=Collections.<String>emptySet();
		}

		final boolean addCategories=!commandLine.hasOption("c");
		final boolean addGenres=!commandLine.hasOption("g");
		final boolean addUserTags=commandLine.hasOption("u");
		final boolean printTags=commandLine.hasOption("p");

		if (printTags) {
			Set<String> availableTags=tagger.getAvailableTags(sharedConfigPaths);
			Joiner.on("\n").appendTo(System.out, availableTags);
		} else {
			for (Path path : sharedConfigPaths) {
				VdfNode tagged=tagger.tag(path, addCategories, addGenres, addUserTags, removeTags);

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

	public Set<String> getAvailableTags(Set<Path> sharedConfigPaths) throws IOException, RecognitionException {
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
		for (Long gameId : availableGameIds) {
			availableTags.addAll(scraper.loadExternalTags(gameId, true, true, true));
		}
		return availableTags;
	}

	public VdfRoot tag(Path path, boolean addCategories, boolean addGenres, boolean addUserTags, Set<String> removeTags) throws IOException, RecognitionException {
		SharedConfig sharedConfig=new SharedConfig(path);
		Set<Long> existingGameIds=new HashSet<>();
		for (Map.Entry<Long, VdfNode> entry : sharedConfig.getGameNodeMap().entrySet()) {
			//System.err.println(gameNode.getName());
			try {
				final long gameId=entry.getKey();
				existingGameIds.add(gameId);
				addTags(sharedConfig, gameId, addCategories, addGenres, addUserTags, removeTags);
			} catch (Exception e) {
			}

		}
		// vdf doesn't contain all games, add the rest (at least the installed games)
		PathResolver pathResolver=new PathResolver();
		Set<Long> availableGameIds=LibraryScanner.findGames(pathResolver.findAllLibraryDirectories());
		availableGameIds.removeAll(existingGameIds);
		for (Long gameId : availableGameIds) {
			try {
				addTags(sharedConfig, gameId, addCategories, addGenres, addUserTags, removeTags);
			} catch (Exception e) {
			}
		}
		return sharedConfig.getRootNode();
	}

	private void addTags(SharedConfig sharedConfig, Long gameId, boolean addCategories, boolean addGenres, boolean addUserTags, Set<String> removeTags) throws IOException {
		Set<String> existingTags=sharedConfig.getTags(gameId);

		Set<String> externalTags=scraper.loadExternalTags(gameId, addCategories, addGenres, addUserTags);
		existingTags.addAll(externalTags);
		existingTags.removeAll(removeTags);

		sharedConfig.setTags(gameId, existingTags);

	}

}
