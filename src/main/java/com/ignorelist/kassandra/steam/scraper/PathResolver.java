/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.collect.Iterables;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

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
		Path oldApps=base.resolve("SteamApps");
		if (Files.exists(oldApps)) {
			return oldApps;
		}
		Path newApps=base.resolve("steamapps");
		if (Files.exists(newApps)) {
			return newApps;
		}
		throw new IllegalStateException("can't resolve apps directory");
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
