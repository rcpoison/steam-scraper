/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.technofovea.hl2parse.vdf.VdfNode;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author poison
 */
public class Tagger {

	public static class Options {

		private EnumSet<TagType> tagTypes;
		private Set<String> removeTags;
		private Set<String> whiteList;
		private boolean removeNotWhiteListed;
		private Map<String, String> replacementMap;

		public static Options fromConfiguration(Configuration configuration) throws IOException {
			Options taggerOptions=new Options();

			final Path whiteListFile=configuration.getWhiteList();
			if (null!=whiteListFile) {
				taggerOptions.setWhiteList(readWhiteList(whiteListFile));
			}

			final Path replacementFile=configuration.getReplacements();
			if (null!=replacementFile) {
				taggerOptions.setReplacementMap(readReplacements(replacementFile));
			}

			taggerOptions.setTagTypes(EnumSet.copyOf(configuration.getTagTypes()));

			taggerOptions.setRemoveNotWhiteListed(null==configuration.getRemoveNotWhiteListed() ? false : configuration.getRemoveNotWhiteListed());

			return taggerOptions;
		}

		private static Set<String> readWhiteList(Path whiteListFile) throws IOException {
			return com.google.common.io.Files.asCharSource(whiteListFile.toFile(), Charsets.UTF_8)
					.lines()
					.filter(Predicates.notNull())
					.map(String::trim)
					.filter(s -> !s.isEmpty())
					.filter(s -> !s.startsWith("#"))
					.collect(Collectors.toCollection(HashSet::new));
		}

		private static Map<String, String> readReplacements(Path replacementFile) throws IOException {
			final Reader replacementFileReader=Files.newBufferedReader(replacementFile, Charsets.UTF_8);
			try {
				Properties replacementProperties=new Properties();
				replacementProperties.load(replacementFileReader);
				Map<String, String> replacementMap=Maps.fromProperties(replacementProperties);
				return replacementMap;
			} finally {
				IOUtils.closeQuietly(replacementFileReader);
			}
		}

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

	public Set<String> getAvailableTags(Set<Path> sharedConfigPaths, Options taggerOptions) throws IOException, RecognitionException {
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
		Map<Long, GameInfo> load=tagLoader.load(availableGameIds, taggerOptions.getTagTypes());
		Iterables.addAll(availableTags, GameInfo.getAllTags(load.values()));
		return availableTags;
	}

	public VdfRoot tag(Path path, Options taggerOptions) throws IOException, RecognitionException {
		SharedConfig sharedConfig=new SharedConfig(path);
		Set<Long> availableGameIds=new HashSet<>();

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
		
		Map<Long, GameInfo> availableTags=tagLoader.load(availableGameIds, taggerOptions.getTagTypes());

		for (Long gameId : availableTags.keySet()) {
			addTags(sharedConfig, gameId, taggerOptions, availableTags.get(gameId).getAllTags(taggerOptions.getTagTypes()));
		}
		return sharedConfig.getRootNode();
	}

	private void addTags(SharedConfig sharedConfig, Long gameId, Options taggerOptions, Set<String> externalTags) {
		Set<String> existingTags=sharedConfig.getTags(gameId);
		
		if (null!=taggerOptions.getWhiteList()&&!taggerOptions.getWhiteList().isEmpty()) {
			externalTags.retainAll(taggerOptions.getWhiteList());
		}

		existingTags.addAll(externalTags);

		if (null!=taggerOptions.getReplacementMap()) {
			for (Map.Entry<String, String> e : taggerOptions.getReplacementMap().entrySet()) {
				if (existingTags.remove(e.getKey())) {
					existingTags.add(e.getValue());
				}
			}
		}

		if (null!=taggerOptions.getRemoveTags()) {
			existingTags.removeAll(taggerOptions.getRemoveTags());
		}
		if (null!=taggerOptions.getWhiteList()&&!taggerOptions.getWhiteList().isEmpty()&&taggerOptions.isRemoveNotWhiteListed()) {
			existingTags.retainAll(taggerOptions.getWhiteList());
		}

		sharedConfig.setTags(gameId, existingTags);
	}

}
