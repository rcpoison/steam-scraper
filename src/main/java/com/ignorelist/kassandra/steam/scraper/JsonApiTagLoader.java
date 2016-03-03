/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.RateLimiter;
import com.ignorelist.kassandra.steam.scraper.model.Category;
import com.ignorelist.kassandra.steam.scraper.model.Data;
import com.ignorelist.kassandra.steam.scraper.model.Genre;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author poison
 */
public class JsonApiTagLoader implements TagLoader {

	private final FileCache cache;
	private final RateLimiter rateLimiter=RateLimiter.create(200d/5d/60d);

	public JsonApiTagLoader(Path cachePath) {
		this.cache=new FileCache(cachePath, new CacheLoader<String, InputStream>() {
			@Override
			public InputStream load(String k) throws Exception {
				rateLimiter.acquire();
				URL url=new URL(buildApiUrl(k));

				InputStream inputStream=URLUtil.openInputStream(url);
				try {
					byte[] data=ByteStreams.toByteArray(inputStream);
					loadAppData(data); // fugly
					return new ByteArrayInputStream(data);
				} finally {
					IOUtils.closeQuietly(inputStream);
				}
			}
		}, TimeUnit.DAYS, 7);
	}

	private static String buildApiUrl(Long gameId) {
		return buildApiUrl(gameId.toString());
	}

	private static String buildApiUrl(String gameId) {
		return "http://store.steampowered.com/api/appdetails/?appids="+gameId;
	}

	private static Data loadAppData(byte[] data) throws MalformedURLException, IOException {
		ObjectMapper mapper=buildMapper();
		final JsonNode tree=mapper.readTree(data);
		return mapper.treeToValue(tree.findValue("data"), Data.class);
	}

	private static Data loadAppData(InputStream data) throws MalformedURLException, IOException {
		ObjectMapper mapper=buildMapper();
		final JsonNode tree=mapper.readTree(data);
		return mapper.treeToValue(tree.findValue("data"), Data.class);
	}

	private static ObjectMapper buildMapper() {
		ObjectMapper mapper=new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		return mapper;
	}

	@Override
	public Set<String> load(Long gameId, EnumSet<TagType> types) {
		try {
			InputStream inputStream=cache.get(gameId.toString());
			try {
				final Data gameData=loadAppData(inputStream);
				Set<String> externalTags=new HashSet<>();
				if (types.contains(TagType.CATEGORY)) {
					Iterables.addAll(externalTags, Iterables.transform(gameData.getCategories(), new Function<Category, String>() {
						@Override
						public String apply(Category input) {
							return input.getDescription();
						}
					}));
				}
				if (types.contains(TagType.GENRE)) {
					Iterables.addAll(externalTags, Iterables.transform(gameData.getGenres(), new Function<Genre, String>() {
						@Override
						public String apply(Genre input) {
							return input.getDescription();
						}
					}));
				}
				return externalTags;
			} catch (IOException ex) {
				Logger.getLogger(JsonApiTagLoader.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		} catch (ExecutionException ex) {
			Logger.getLogger(JsonApiTagLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new HashSet<>();
	}

}
