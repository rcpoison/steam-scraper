/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import java.nio.file.Path;
import java.util.Set;

/**
 *
 * @author poison
 */
public class Configuration {

	private Set<Path> sharedConfigPaths;
	private Path whiteList;
	private Path replacements;
	private int downloadThreads;

	public Set<Path> getSharedConfigPaths() {
		return sharedConfigPaths;
	}

	public void setSharedConfigPaths(Set<Path> sharedConfigPaths) {
		this.sharedConfigPaths=sharedConfigPaths;
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

	public void setDownloadThreads(int downloadThreads) {
		this.downloadThreads=downloadThreads;
	}

}
