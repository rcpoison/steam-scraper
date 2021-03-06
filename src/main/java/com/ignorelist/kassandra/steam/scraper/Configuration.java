/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

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
			final Path path=Paths.get(input.trim());
			if (Files.isRegularFile(path)) {
				return path.toAbsolutePath();
			}
			if (!path.isAbsolute()) {
				try {
					final Path siblingPath=new PathResolver().findConfiguration().resolveSibling(path);
					if (!Files.isRegularFile(siblingPath)) {
						return siblingPath.toAbsolutePath();
					}
				} catch (IOException ex) {
					LOG.log(Level.SEVERE, null, ex);
				}
			}
			LOG.log(Level.WARNING, "file does not exist: {0}", path.toString());
			return null;
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

	private static final String CONFIG_DOWNLOAD_THREADS="downloadThreads";
	private static final String CONFIG_REPLACEMENTS="replacements";
	private static final String CONFIG_WHITE_LIST="whiteList";
	private static final String CONFIG_REMOVE_NOT_WHITE_LISTED="removeNotWhiteListed";
	private static final String CONFIG_TAG_TYPES="tagTypes";
	private static final String CONFIG_SHARED_CONFIG_PATHS="sharedConfigPaths";
	private static final String CONFIG_CACHE_EXPIRY_DAYS="cacheExpiryDays";

	private Set<Path> sharedConfigPaths;
	private Set<TagType> tagTypes;
	private Path whiteList;
	private Boolean removeNotWhiteListed;
	private Path replacements;
	private Integer downloadThreads;
	private Integer cacheExpiryDays;

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

	public Boolean getRemoveNotWhiteListed() {
		return removeNotWhiteListed;
	}

	public void setRemoveNotWhiteListed(Boolean removeNotWhiteListed) {
		this.removeNotWhiteListed=removeNotWhiteListed;
	}

	public Path getReplacements() {
		return replacements;
	}

	public void setReplacements(Path replacements) {
		this.replacements=replacements;
	}

	public Integer getDownloadThreads() {
		return downloadThreads;
	}

	public void setDownloadThreads(Integer downloadThreads) {
		this.downloadThreads=downloadThreads;
	}

	public Integer getCacheExpiryDays() {
		return cacheExpiryDays;
	}

	public void setCacheExpiryDays(Integer cacheExpiryDays) {
		this.cacheExpiryDays=cacheExpiryDays;
	}

	public void writeProperties(Path file) throws IOException {
		Writer propertiesWriter=Files.newBufferedWriter(file, Charsets.UTF_8);
		try {
			toProperties().store(propertiesWriter, null);
		} finally {
			IOUtils.closeQuietly(propertiesWriter);
		}
	}

	public Properties toProperties() {
		Properties properties=new Properties();
		if (null!=sharedConfigPaths) {
			properties.setProperty(CONFIG_SHARED_CONFIG_PATHS, Joiner.on(':').join(Iterables.transform(sharedConfigPaths, PATH_TO_STRING)));
		}
		if (null!=tagTypes) {
			properties.setProperty(CONFIG_TAG_TYPES, Joiner.on(',').join(tagTypes));
		}
		if (null!=whiteList) {
			properties.setProperty(CONFIG_WHITE_LIST, PATH_TO_STRING.apply(whiteList));
		}
		if (null!=removeNotWhiteListed) {
			properties.setProperty(CONFIG_REMOVE_NOT_WHITE_LISTED, removeNotWhiteListed.toString());
		}
		if (null!=replacements) {
			properties.setProperty(CONFIG_REPLACEMENTS, PATH_TO_STRING.apply(replacements));
		}
		if (null!=downloadThreads) {
			properties.setProperty(CONFIG_DOWNLOAD_THREADS, downloadThreads.toString());
		}
		if (null!=cacheExpiryDays) {
			properties.setProperty(CONFIG_CACHE_EXPIRY_DAYS, cacheExpiryDays.toString());
		}
		return properties;
	}

	public static Configuration fromPropertiesFile(Path propertiesFile) throws IOException {
		Reader propertiesReader=Files.newBufferedReader(propertiesFile, Charsets.UTF_8);
		try {
			Properties properties=new Properties();
			properties.load(propertiesReader);
			return fromProperties(properties);
		} finally {
			IOUtils.closeQuietly(propertiesReader);
		}
	}

	public static Configuration fromProperties(Properties properties) {
		Configuration configuration=new Configuration();
		String sharedConfigPaths=properties.getProperty(CONFIG_SHARED_CONFIG_PATHS);
		if (null!=sharedConfigPaths) {
			Set<Path> paths=Sets.newHashSet(
					Iterables.filter(
							Iterables.transform(Splitter.on(':').split(sharedConfigPaths), STRING_TO_PATH),
							Predicates.notNull()));
			configuration.setSharedConfigPaths(paths);
		}
		String tagTypes=properties.getProperty(CONFIG_TAG_TYPES);
		if (null!=tagTypes) {
			Set<TagType> tags=Sets.newHashSet(
					Iterables.filter(
							Iterables.transform(Splitter.on(PROPERTIES_VALUE_SEPERATOR).split(tagTypes), STRING_TO_TAG_TYPE),
							Predicates.notNull()));
			configuration.setTagTypes(tags);
		}
		configuration.setWhiteList(STRING_TO_PATH.apply(properties.getProperty(CONFIG_WHITE_LIST)));
		String removeNotWhiteListed=properties.getProperty(CONFIG_REMOVE_NOT_WHITE_LISTED);
		if (null!=removeNotWhiteListed) {
			configuration.setRemoveNotWhiteListed(Boolean.valueOf(removeNotWhiteListed));
		}
		configuration.setReplacements(STRING_TO_PATH.apply(properties.getProperty(CONFIG_REPLACEMENTS)));
		String downloadThreads=properties.getProperty(CONFIG_DOWNLOAD_THREADS);
		if (null!=downloadThreads) {
			try {
				configuration.setDownloadThreads(Integer.valueOf(downloadThreads));
			} catch (NumberFormatException nfe) {
				LOG.log(Level.WARNING, "failed to parse downloadThreads: {0}", downloadThreads);
			}
		}
		String cacheExpiryDays=properties.getProperty(CONFIG_CACHE_EXPIRY_DAYS);
		if (null!=cacheExpiryDays) {
			try {
				final Integer d=Integer.valueOf(cacheExpiryDays);
				if (d>=1) {
					configuration.setCacheExpiryDays(d);
				} else {
					LOG.log(Level.WARNING, "cacheExpiryDays must be >= 1");
				}
			} catch (NumberFormatException nfe) {
				LOG.log(Level.WARNING, "failed to parse cacheExpiryDays: {0}", downloadThreads);
			}
		}
		return configuration;
	}

}
