/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.technofovea.hl2parse.vdf.VdfAttribute;
import com.technofovea.hl2parse.vdf.VdfNode;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;

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

		Tagger tagger=new Tagger(new Scraper());
		Set<Path> paths=new LinkedHashSet<>();
		if (commandLine.hasOption("f")) {
			String[] passedPaths=commandLine.getOptionValues("f");
			if (0==passedPaths.length) {
				throw new IllegalArgumentException("no paths passed in -f");
			}
			for (String p : passedPaths) {
				paths.add(Paths.get(p));
			}

		} else {
			PathResolver pathResolver=new PathResolver();
			paths=pathResolver.findSharedConfig();
		}

		if (paths.size()>1&&!commandLine.hasOption("w")) {
			System.err.println("multiple sharedconfig.vdf available:\n"+Joiner.on("\n").join(paths)+"\n, can not write to stdout. Need to specify -w or -f with a single sharedconfig.vdf");
			System.exit(1);
		}

		final String[] removeTagsValues=commandLine.getOptionValues("remove");
		final Set<String> removeTags;
		if (null!=removeTagsValues) {
			removeTags=Sets.newHashSet(removeTagsValues);
		} else {
			removeTags=Collections.<String>emptySet();
		}

		for (Path path : paths) {
			VdfNode tagged=tagger.tag(path, !commandLine.hasOption("c"), !commandLine.hasOption("g"), commandLine.hasOption("u"), removeTags);

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

	public VdfNode tag(Path path, boolean addCategories, boolean addGenres, boolean addUserTags, Set<String> removeTags) throws IOException, RecognitionException {
		InputStream inputStream=Files.newInputStream(path, StandardOpenOption.READ);
		VdfRoot vdfRoot=VdfParser.parse(inputStream);
		IOUtils.closeQuietly(inputStream);

		JXPathContext pathContext=JXPathContext.newContext(vdfRoot);
		VdfNode appsNode=(VdfNode) pathContext.getValue("//children[name='apps']");
		System.err.println(appsNode.getChildren().size());
		Set<Long> existingGameIds=new HashSet<>();
		for (VdfNode gameNode : appsNode.getChildren()) {
			//System.err.println(gameNode.getName());
			try {
				final long gameId=Long.parseLong(gameNode.getName());
				existingGameIds.add(gameId);
				addTags(gameId, gameNode, addCategories, addGenres, addUserTags, removeTags);
			} catch (Exception e) {
			}

		}
		// vdf doesn't contain all games, add the rest (at least the installed games)
		PathResolver pathResolver=new PathResolver();
		Set<Long> availableGameIds=LibraryScanner.findGames(pathResolver.findAllLibraryDirectories());
		availableGameIds.removeAll(existingGameIds);
		for (Long gameId : availableGameIds) {
			try {
				VdfNode gameNode=new VdfNode();
				gameNode.setName(gameId.toString());
				addTags(gameId, gameNode, addCategories, addGenres, addUserTags, removeTags);
				appsNode.addChild(gameNode);
			} catch (Exception e) {
			}
		}
		return vdfRoot;
	}

	private void addTags(Long gameId, VdfNode gameNode, boolean addCategories, boolean addGenres, boolean addUserTags, Set<String> removeTags) throws IOException {
		VdfNode tagNode=Iterables.find(gameNode.getChildren(), new Predicate<VdfNode>() {
			@Override
			public boolean apply(VdfNode input) {
				return "tags".equals(input.getName());
			}
		}, null);
		if (null==tagNode) {
			tagNode=new VdfNode();
			tagNode.setName("tags");
			gameNode.addChild(tagNode);
		}
		Set<String> existingTags=Sets.newLinkedHashSet(Iterables.transform(tagNode.getAttributes(), new Function<VdfAttribute, String>() {
			@Override
			public String apply(VdfAttribute input) {
				return input.getValue();
			}
		}));

		Set<String> externalTags=scraper.loadExternalTags(gameId, addCategories, addGenres, addUserTags);
		existingTags.addAll(externalTags);
		existingTags.removeAll(removeTags);

		List<VdfAttribute> attributes=tagNode.getAttributes();
		attributes.clear();
		for (String tag : existingTags) {
			tagNode.addAttribute(Integer.toString(attributes.size()), tag);
		}

	}

}
