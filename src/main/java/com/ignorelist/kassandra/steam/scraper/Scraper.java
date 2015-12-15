/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.RateLimiter;
import com.ignorelist.kassandra.steam.scraper.model.Category;
import com.ignorelist.kassandra.steam.scraper.model.Data;
import com.ignorelist.kassandra.steam.scraper.model.Genre;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author poison
 */
public class Scraper {

	private final RateLimiter rateLimiter=RateLimiter.create(200d/5d/60d);

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws MalformedURLException, IOException, RecognitionException {
//		ObjectMapper mapper=new ObjectMapper();
//		JsonNode rootNode=mapper.readTree(new URL("https://raw.githubusercontent.com/SteamDatabase/SteamLinux/master/GAMES.json"));
//
//		Scraper s=new Scraper();
//		int i=0;
//		for (Map.Entry<String, JsonNode> node : Lists.newArrayList(rootNode.fields())) {
//			try {
//				final long gameId=Long.parseLong(node.getKey());
//				Data data=s.load(gameId);
//				System.err.println(gameId+": "+data.getName());
//				++i;
//			} catch (Exception e) {
//				System.err.println(e);
//			}
//		}
//		System.err.println(i);
//		System.exit(0);

		// find ~/.steam-scraper -type f -size -80k -iname '*.html' -delete
		final Set<Long> gameIds=LibraryScanner.findGames(new PathResolver().findAllLibraryDirectories());
		System.err.println(Joiner.on(',').join(gameIds));
		Scraper s=new Scraper();
		s.load(gameIds);
	}

	public Scraper() {
	}

	public Set<String> loadExternalTags(Long gameId, boolean addCategories, boolean addGenres, boolean addUserTags) {
		Set<String> externalTags=new LinkedHashSet<>();
		if (addCategories||addGenres) {
			try {
				final Data gameData=load(gameId);
				if (addCategories) {
					Iterables.addAll(externalTags, Iterables.transform(gameData.getCategories(), new Function<Category, String>() {
						@Override
						public String apply(Category input) {
							return input.getDescription();
						}
					}));
				}
				if (addGenres) {
					Iterables.addAll(externalTags, Iterables.transform(gameData.getGenres(), new Function<Genre, String>() {
						@Override
						public String apply(Genre input) {
							return input.getDescription();
						}
					}));
				}
			} catch (Exception e) {
			}
		}

		if (addUserTags) {
			try {
				externalTags.addAll(loadUserTags(gameId));
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		return externalTags;
	}

	public Collection<Data> load(final Set<Long> gameIds) throws IOException {
		Collection<Data> games=new ArrayList<>();
		for (Long gameId : gameIds) {
			try {
				Data data=load(gameId);
				games.add(data);
			} catch (Exception e) {
				//System.err.println("---"+gameId+"---> failed");
			}
		}
		return games;
	}

	public Set<String> loadUserTags(Long gameId) throws IOException {
		Path cacheFilePath=cacheFilePathHtml(gameId);
		if (!Files.exists(cacheFilePath)) {
			System.err.println("--- html: "+gameId+"---> downloading");
			URL url=new URL("http://store.steampowered.com/app/"+gameId);
			URLConnection uRLConnection=url.openConnection();
			uRLConnection.setRequestProperty("User-Agent", "Valve/Steam HTTP Client 1.0 (tenfoot)");
			uRLConnection.setRequestProperty("Cookie", "birthtime=-3599; lastagecheckage=1-January-1970");
			InputStream inputStream=uRLConnection.getInputStream();
			try {
				Files.copy(inputStream, cacheFilePath);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		} else {
			System.err.println("--- html: "+gameId+"---> cached");
		}
		Document document=Jsoup.parse(cacheFilePath.toFile(), "UTF-8");
		Elements tagElements=document.select("a.app_tag");
		Set<String> tags=new LinkedHashSet<>();
		for (Element e : tagElements) {
			final String tag=e.text();
			if (null!=tag) {
				tags.add(tag.trim());
			}
		}
		return tags;
	}

	public Data load(Long gameId) throws IOException {
		Path cacheFilePath=cacheFilePathJson(gameId);
		byte[] jsonData;
		Data data;
		if (Files.exists(cacheFilePath)) {
			System.err.println("--- json: "+gameId+"---> cached");
			jsonData=Files.readAllBytes(cacheFilePath);
			data=loadAppData(jsonData);
		} else {
			try {
				double acquiredIn=rateLimiter.acquire();
				System.err.println("--- json: "+gameId+"---> rate-limiter waited: "+acquiredIn+"s");
				jsonData=openUrlData(gameId);
				data=loadAppData(jsonData);
				Files.write(cacheFilePath, jsonData, StandardOpenOption.CREATE);
			} catch (Exception e) {
				System.err.println(gameId+": failed to load data from storefront API");
				throw e;
			}
		}
		System.err.println(gameId+": "+data.getName());
		return data;
	}

	private static byte[] openUrlData(long gameId) throws MalformedURLException, IOException {
		final String urlString="http://store.steampowered.com/api/appdetails/?appids="+gameId;
		URL url=new URL(urlString);
		URLConnection uRLConnection=url.openConnection();
		uRLConnection.setRequestProperty("User-Agent", "Valve/Steam HTTP Client 1.0 (tenfoot)");

		InputStream inputStream=null;
		try {
			inputStream=uRLConnection.getInputStream();
			return ByteStreams.toByteArray(inputStream);

		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private static Path cacheFilePathJson(long appId) throws IOException {
		Path dir=cacheFilePathBase();
		return dir.resolve(Long.toString(appId)+".json");
	}

	private static Path cacheFilePathHtml(long appId) throws IOException {
		Path dir=cacheFilePathBase();
		return dir.resolve(Long.toString(appId)+".html");
	}

	private static Path cacheFilePathBase() throws IOException {
		Path dir=Paths.get(System.getProperty("user.home"), ".steam-scraper");
		if (!Files.exists(dir)) {
			Files.createDirectories(dir);
		}
		return dir;
	}

	private static Data loadAppData(byte[] data) throws MalformedURLException, IOException {
		ObjectMapper mapper=new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		try {
			return mapper.treeToValue(mapper.readTree(data).findValue("data"), Data.class);
		} catch (Exception e) {
			//System.err.println(new String(data));
			throw e;
		}
	}

}
