/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.technofovea.hl2parse.vdf.VdfAttribute;
import com.technofovea.hl2parse.vdf.VdfNode;
import com.technofovea.hl2parse.vdf.VdfRoot;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.jxpath.JXPathContext;

/**
 *
 * @author poison
 */
public class SharedConfig {

	private static final Function<VdfAttribute, String> ATTR_VALUE=new Function<VdfAttribute, String>() {
		@Override
		public String apply(VdfAttribute input) {
			return input.getValue();
		}
	};
	private static final Predicate<VdfNode> PREDICATE_TAGS_NODE=new Predicate<VdfNode>() {
		@Override
		public boolean apply(VdfNode input) {
			return "tags".equals(input.getName());
		}
	};

	private final Path path;

	private VdfRoot rootNode;
	private VdfNode appsNode;
	private Map<Long, VdfNode> gameNodeMap;

	public static void main(String[] args) throws IOException {
		Set<String> tags=new HashSet<>();
		for (Path p : new PathResolver().findSharedConfig()) {
			SharedConfig sharedConfig=new SharedConfig(p);
			for (Long gameId : sharedConfig.getGameIds()) {
				tags.addAll(sharedConfig.getTags(gameId));
			}
		}
		System.err.println(Joiner.on("\n").join(tags));
	}

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
				rootNode=VdfParser.parse(inputStream);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}
		return rootNode;
	}

	public synchronized VdfNode getAppsNode() {
		if (null!=appsNode) {
			return appsNode;
		}
		try {
			JXPathContext pathContext=JXPathContext.newContext(getRootNode());
			appsNode=(VdfNode) pathContext.getValue("//children[name='apps']");
			return appsNode;
		} catch (IOException|RecognitionException ex) {
			System.err.println(ex);
		}
		throw new IllegalStateException();
	}

	public synchronized Map<Long, VdfNode> getGameNodeMap() {
		if (null==gameNodeMap) {
			final VdfNode aN=getAppsNode();
			gameNodeMap=new HashMap<>();
			for (VdfNode gameNode : aN.getChildren()) {
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

	public Collection<VdfNode> getGameNodes() {
		return getGameNodeMap().values();
	}

	public Set<Long> getGameIds() {
		return getGameNodeMap().keySet();
	}

	public boolean isGamePresent(Long gameId) {
		return getGameNodeMap().containsKey(gameId);
	}

	public VdfNode getGameNode(Long gameId) {
		final VdfNode gameNode=getGameNodeMap().get(gameId);
		if (null==gameNode) {
			throw new NoSuchElementException();
		}
		return gameNode;
	}

	public void addGameNode(VdfNode gameNode) {
		final long gameId=Long.parseLong(gameNode.getName());
		if (isGamePresent(gameId)) {
			throw new IllegalArgumentException("game with id: "+gameId+" is already present");
		}
		getAppsNode().addChild(gameNode);
		getGameNodeMap().put(gameId, gameNode);
	}

	public static VdfNode getTagNode(VdfNode gameNode) {
		return Iterables.find(gameNode.getChildren(), PREDICATE_TAGS_NODE);
	}

	public VdfNode getTagNode(Long gameId) {
		return getTagNode(getGameNode(gameId));
	}

	private static Set<String> getTags(VdfNode tagNode) {
		return Sets.newLinkedHashSet(Iterables.transform(tagNode.getAttributes(), ATTR_VALUE));
	}

	public Set<String> getTags(Long gameId) {
		try {
			return getTags(getTagNode(gameId));
		} catch (NoSuchElementException nsee) {
			return new HashSet<String>();
		}
	}

	public void setTags(Long gameId, Set<String> tags) {
		VdfNode gameNode;
		try {
			gameNode=getGameNode(gameId);
		} catch (NoSuchElementException nsee) {
			gameNode=new VdfNode();
			gameNode.setName(gameId.toString());
			getAppsNode().addChild(gameNode);
		}
		setTags(gameNode, tags);
	}

	public static void setTags(VdfNode gameNode, Set<String> tags) {
		VdfNode tagNode;
		try {
			tagNode=getTagNode(gameNode);
		} catch (NoSuchElementException nsee) {
			tagNode=new VdfNode();
			tagNode.setName("tags");
			gameNode.addChild(tagNode);
		}
		setTagsTagNode(tagNode, tags);
	}

	private static void setTagsTagNode(VdfNode tagNode, Set<String> tags) {
		List<VdfAttribute> attributes=tagNode.getAttributes();
		attributes.clear();
		for (String tag : tags) {
			tagNode.addAttribute(Integer.toString(attributes.size()), tag);
		}
	}

}
