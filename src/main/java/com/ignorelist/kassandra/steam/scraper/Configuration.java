/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author poison
 */
public class Configuration {

	private static final Logger LOG=Logger.getLogger(Configuration.class.getName());

	private static final Function<Path, String> PATH_TO_STRING=new Function<Path, String>() {
		@Override
		public String apply(Path input) {
			return input.toAbsolutePath().toString();
		}
	};
	private static final Function<String, Path> STRING_TO_PATH=new Function<String, Path>() {
		@Override
		public Path apply(String input) {
			if (Strings.isNullOrEmpty(input)) {
				return null;
			}
			Path path=Paths.get(input.trim());
			if (!Files.isRegularFile(path)) {
				LOG.log(Level.WARNING, "file does not exist: {0}", path.toString());
				return null;
			}
			return path;
		}
	};
	private static final Function<String, TagType> STRING_TO_TAG_TYPE=new Function<String, TagType>() {
		@Override
		public TagType apply(String input) {
			if (!Strings.isNullOrEmpty(input)) {
				try {
					return TagType.valueOf(input.trim());
				} catch (IllegalArgumentException iae) {
					LOG.log(Level.WARNING, "failed to parse TagType: "+input, iae);
				}
			}
			return null;
		}
	};
	private static final Pattern PROPERTIES_VALUE_SEPERATOR=Pattern.compile("[,:\\s]+");

	private Set<Path> sharedConfigPaths;
	private Set<TagType> tagTypes;
	private Path whiteList;
	private Path replacements;
	private Integer downloadThreads;

	public Set<Path> getSharedConfigPaths() {
		return sharedConfigPaths;
	}

	public void setSharedConfigPaths(Set<Path> sharedConfigPaths) {
		this.sharedConfigPaths=sharedConfigPaths;
	}

	public Set<TagType> getTagTypes() {
		return tagTypes;
	}

	public void setTagTypes(Set<TagType> tagTypes) {
		this.tagTypes=tagTypes;
	}

	public Path getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(Path whiteList) {
		this.whiteList=whiteList;
	}

	public Path getReplacements() {
		return replacements;
	}

	public void setReplacements(Path replacements) {
		this.replacements=replacements;
	}

	public int getDownloadThreads() {
		return downloadThreads;
	}

	public void setDownloadThreads(Integer downloadThreads) {
		this.downloadThreads=downloadThreads;
	}

	public Properties toProperties() {
		Properties properties=new Properties();
		if (null!=sharedConfigPaths) {
			properties.setProperty("sharedConfigPaths", Joiner.on(':').join(Iterables.transform(sharedConfigPaths, PATH_TO_STRING)));
		}
		if (null!=tagTypes) {
			properties.setProperty("tagTypes", Joiner.on(',').join(tagTypes));
		}
		if (null!=whiteList) {
			properties.setProperty("whiteList", PATH_TO_STRING.apply(whiteList));
		}
		if (null!=replacements) {
			properties.setProperty("replacements", PATH_TO_STRING.apply(replacements));
		}
		if (null!=downloadThreads) {
			properties.setProperty("downloadThreads", downloadThreads.toString());
		}
		return properties;
	}

	public static Configuration fromProperties(Properties properties) {
		Configuration configuration=new Configuration();
		String sharedConfigPaths=properties.getProperty("sharedConfigPaths");
		if (null!=sharedConfigPaths) {
			Set<Path> paths=Sets.newHashSet(
					Iterables.filter(
							Iterables.transform(Splitter.on(':').split(sharedConfigPaths), STRING_TO_PATH),
							Predicates.notNull()));
			configuration.setSharedConfigPaths(paths);
		}
		String tagTypes=properties.getProperty("tagTypes");
		if (null!=tagTypes) {
			Set<TagType> tags=Sets.newHashSet(
					Iterables.filter(
							Iterables.transform(Splitter.on(PROPERTIES_VALUE_SEPERATOR).split(tagTypes), STRING_TO_TAG_TYPE), 
							Predicates.notNull()));
			configuration.setTagTypes(tags);
		}
		configuration.setWhiteList(STRING_TO_PATH.apply(properties.getProperty("sharedConfigPaths")));
		configuration.setReplacements(STRING_TO_PATH.apply(properties.getProperty("replacements")));
		String downloadThreads=properties.getProperty("downloadThreads");
		if (null!=downloadThreads) {
			try {
				configuration.setDownloadThreads(Integer.valueOf(downloadThreads));
			} catch (NumberFormatException nfe) {
			}
		}
		return configuration;
	}

}
