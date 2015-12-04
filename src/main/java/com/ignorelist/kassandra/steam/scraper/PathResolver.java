/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.collect.Iterables;
import com.technofovea.hl2parse.vdf.VdfAttribute;
import com.technofovea.hl2parse.vdf.VdfNode;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Andreas Schnaiter <rc.poison@gmail.com>
 */
public class PathResolver {

	public Path findSteamBase() {
		return Paths.get(System.getProperty("user.home"), ".steam");
	}

	public Path findSteamApps() {
		Path base=findSteamBase().resolve("steam");
		return resolveAppsDirectory(base);
	}

	private Path resolveAppsDirectory(Path base) throws IllegalStateException {
		Path oldApps=base.resolve("SteamApps");
		if (Files.isDirectory(oldApps)) {
			return oldApps;
		}
		Path newApps=base.resolve("steamapps");
		if (Files.isDirectory(newApps)) {
			return newApps;
		}
		throw new IllegalStateException("can't resolve apps directory");
	}

	public Set<Path> findAllLibraryDirectories() throws IOException, RecognitionException {
		Path steamApps=findSteamApps();
		Set<Path> libraryDirectories=new LinkedHashSet<>();
		libraryDirectories.add(steamApps);
		Path directoryDescriptor=steamApps.resolve("libraryfolders.vdf");
		if (Files.exists(directoryDescriptor)) {
			InputStream vdfStream=Files.newInputStream(directoryDescriptor, StandardOpenOption.READ);
			VdfRoot vdfRoot=VdfParser.parse(vdfStream);
			IOUtils.closeQuietly(vdfStream);
			final VdfNode nodeLibrary=Iterables.getFirst(vdfRoot.getChildren(), null);
			if (null!=nodeLibrary) {
				for (VdfAttribute va : nodeLibrary.getAttributes()) {
					//System.err.println(va);
					try {
						Integer.parseInt(va.getName());
						Path libraryDirectory=Paths.get(va.getValue());
						libraryDirectories.add(resolveAppsDirectory(libraryDirectory));
					} catch (Exception e) {
					}
				}
			}
		}

		return libraryDirectories;
	}

	public Set<Path> findSharedConfig() throws IOException {
		Path base=findSteamBase().resolve("steam").resolve("userdata");
		final Set<Path> paths=new HashSet<>();
		Files.walkFileTree(base, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if ("sharedconfig.vdf".equals(file.getFileName().toString())) {
					paths.add(file);
				}
				return super.visitFile(file, attrs);
			}

		});
		if (paths.isEmpty()) {
			throw new IllegalStateException("can't find sharedconfig.vdf");
		}
		return paths;

	}
}
