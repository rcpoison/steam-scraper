/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.cache.CacheLoader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlTagLoader implements TagLoader {

	private final FileCache cache;

	public HtmlTagLoader(Path cachePath) {
		cache=new FileCache(cachePath, new CacheLoader<String, InputStream>() {
			@Override
			public InputStream load(String k) throws Exception {
				URL url=new URL(buildPageUrl(k));
				return URLUtil.openInputStream(url);
			}
		}, TimeUnit.DAYS, 7);
	}

	private static String buildPageUrl(String k) {
		return "http://store.steampowered.com/app/"+k;
	}

	private static String buildPageUrl(Long k) {
		return buildPageUrl(k.toString());
	}


	@Override
	public Set<String> load(Long gameId, EnumSet<TagType> types) {
		try {
			Set<String> tags=new HashSet<>();
			if (!types.isEmpty()) {
				InputStream inputStream=cache.get(gameId.toString());
				Document document=Jsoup.parse(inputStream, Charsets.UTF_8.name(), buildPageUrl(gameId));
				if (types.contains(TagType.CATEGORY)) {
					Elements categories=document.select("div#category_block a.name");
					copyText(categories, tags);
				}
				if (types.contains(TagType.GENRE)) {
					Elements genres=document.select("div.details_block a[href*=/genre/]");
					copyText(genres, tags);
				}
				if (types.contains(TagType.USER)) {
					Elements userTags=document.select("a.app_tag");
					copyText(userTags, tags);
				}
			}
			return tags;
		} catch (ExecutionException ex) {
			Logger.getLogger(HtmlTagLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(HtmlTagLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new HashSet<>();
	}

	private static void copyText(Elements elements, Set<String> target) {
		for (Element element : elements) {
			final String text=element.text();
			if (!Strings.isNullOrEmpty(text)) {
				target.add(text.trim());
			}
		}
	}

}
