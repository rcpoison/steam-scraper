/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.technofovea.hl2parse.vdf.VdfNode;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;

/**
 *
 * @author poison
 */
public class SharedConfig {

	private final Path path;

	private VdfRoot rootNode;
	private Map<Long, VdfNode> gameNodeMap;

	public SharedConfig(Path path) {
		this.path=path;
	}

	public Path getPath() {
		return path;
	}

	public synchronized VdfRoot getRootNode() throws IOException, RecognitionException {
		if (null==rootNode) {
			InputStream inputStream=Files.newInputStream(path, StandardOpenOption.READ);
			try {
				VdfRoot vdfRoot=VdfParser.parse(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
		return rootNode;
	}

	public VdfNode getAppsNode() throws IOException, RecognitionException {
		JXPathContext pathContext=JXPathContext.newContext(getRootNode());
		VdfNode appsNode=(VdfNode) pathContext.getValue("//children[name='apps']");
		return appsNode;
	}

	public synchronized Map<Long, VdfNode> getGameNodeMap() throws IOException, RecognitionException {
		if (null==gameNodeMap) {
			VdfNode appsNode=getAppsNode();
			gameNodeMap=new HashMap<>();
			for (VdfNode gameNode : appsNode.getChildren()) {
				try {
					final long gameId=Long.parseLong(gameNode.getName());
					gameNodeMap.put(gameId, gameNode);
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}
		return gameNodeMap;
	}

	public Collection<VdfNode> getGameNodes() throws IOException, RecognitionException {
		return getGameNodeMap().values();
	}

	public Set<Long> getGameIds() throws IOException, RecognitionException {
		return getGameNodeMap().keySet();
	}

	public boolean isGamePresent(Long gameId) throws IOException, RecognitionException {
		return getGameNodeMap().containsKey(gameId);
	}

	public void addGameNode(VdfNode gameNode) throws IOException, RecognitionException {
		final long gameId=Long.parseLong(gameNode.getName());
		if (isGamePresent(gameId)) {
			throw new IllegalArgumentException("game with id: "+gameId+" is already present");
		}
		getAppsNode().addChild(gameNode);
		getGameNodeMap().put(gameId, gameNode);
	}

}
