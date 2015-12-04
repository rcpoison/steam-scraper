/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import com.google.common.util.concurrent.RateLimiter;
import com.ignorelist.kassandra.steam.scraper.model.Data;
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
import java.util.Set;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author poison
 */
public class Scraper {

	private final RateLimiter rateLimiter=RateLimiter.create(200d/5d/60d);
	

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws MalformedURLException, IOException {
		final Path path=Paths.get("/home/poison/.steam/steam/steamapps");
		final Set<Long> gameIds=LibraryScanner.findGames(path);
		System.err.println(Joiner.on(',').join(gameIds));
		Scraper s=new Scraper();
		s.load(gameIds);

	}

	public Scraper() {
	}
	
	

	public Collection<Data> load(final Set<Long> gameIds) throws IOException {
		Collection<Data> games=new ArrayList<>();
		for (Long gameId : gameIds) {
			Data data=load(gameId);
			games.add(data);
		}
		return games;
	}

	public Data load(Long gameId) throws IOException {
		Path cacheFilePath=cacheFilePath(gameId);
		byte[] jsonData;
		Data data;
		if (Files.exists(cacheFilePath)) {
			System.err.println("---"+gameId+"---> cached");
			jsonData=Files.readAllBytes(cacheFilePath);
			data=loadAppData(jsonData);
		} else {
			double acquiredIn=rateLimiter.acquire();
			System.err.println("---"+gameId+"---> slept: "+acquiredIn+"s");
			jsonData=openUrlData(gameId);
			data=loadAppData(jsonData);
			Files.write(cacheFilePath, jsonData, StandardOpenOption.CREATE);
		}
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

	private static Path cacheFilePath(long appId) throws IOException {
		Path dir=Paths.get(System.getProperty("user.home"), ".steam-scraper");
		if (!Files.exists(dir)) {
			Files.createDirectories(dir);
		}
		return dir.resolve(Long.toString(appId)+".json");
	}

	private static Data loadAppData(byte[] data) throws MalformedURLException, IOException {
		ObjectMapper mapper=new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		try {
			return mapper.treeToValue(mapper.readTree(data).findValue("data"), Data.class);
		} catch (Exception e) {
			System.err.println(new String(data));
			throw e;
		}
	}

}
