/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author poison
 */
public class LibraryScanner {

	private static final Pattern PATTERN=Pattern.compile("appmanifest_(\\d+)\\.acf");

	public static Set<Long> findGames(Iterable<Path> paths) throws IOException {
		Set<Long> gameIds=new HashSet<>();
		for (Path p : paths) {
			gameIds.addAll(findGames(p));
		}
		return gameIds;
	}

	public static Set<Long> findGames(Path path) throws IOException {
		Set<Long> gameIds=new HashSet<>();
		DirectoryStream<Path> directoryStream=null;
		try {
			directoryStream=Files.newDirectoryStream(path, new DirectoryStream.Filter<Path>() {

				@Override
				public boolean accept(Path entry) throws IOException {
					return Files.isRegularFile(entry);
				}
			});
			for (Path f : directoryStream) {
				final String fileName=f.getFileName().toString();
				Matcher matcher=PATTERN.matcher(fileName);
				if (matcher.matches()) {
					gameIds.add(Long.parseLong(matcher.group(1)));
				}
			}
			return gameIds;
		} finally {
			IOUtils.closeQuietly(directoryStream);
		}
	}
}
