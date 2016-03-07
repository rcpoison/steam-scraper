/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.net.URI;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author poison
 */
public class GameInfo implements Iterable<String>, Serializable {

	private static final long serialVersionUID=1L;

	private Long id;
	private String name;
	private URI icon;
	private URI headerImage;
	private SetMultimap<TagType, String> tags;

	public GameInfo() {
	}

	public static Iterable<String> getAllTags(Iterable<GameInfo> gameInfos) {
		return Iterables.concat(gameInfos);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public URI getIcon() {
		return icon;
	}

	public void setIcon(URI icon) {
		this.icon=icon;
	}

	public URI getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(URI headerImage) {
		this.headerImage=headerImage;
	}

	public SetMultimap<TagType, String> getTags() {
		if (null==tags) {
			tags=HashMultimap.create();
		}
		return tags;
	}

	public void setTags(SetMultimap<TagType, String> tags) {
		this.tags=tags;
	}

	public void putTags(TagType tagType, Iterable<String> tagValues) {
		getTags().putAll(tagType, tagValues);
	}

	public Set<String> getAllTags() {
		return Sets.newHashSet(this);
	}

	@Override
	public Iterator<String> iterator() {
		return getTags().values().iterator();
	}

	@Override
	public int hashCode() {
		int hash=7;
		hash=89*hash+Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null) {
			return false;
		}
		if (getClass()!=obj.getClass()) {
			return false;
		}
		final GameInfo other=(GameInfo) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}

}
