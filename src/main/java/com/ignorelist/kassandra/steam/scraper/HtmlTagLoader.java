/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlTagLoader implements TagLoader {

	private final FileCache cache;

	public HtmlTagLoader(Path cachePath, int cacheExpiryDays) {
		cache=new FileCache(cachePath, new CacheLoader<String, InputStream>() {
			@Override
			public InputStream load(String k) throws Exception {
				URL url=new URL(buildPageUrl(k));
				return URLUtil.openInputStream(url);
			}
		}, TimeUnit.DAYS, cacheExpiryDays);
	}

	public HtmlTagLoader(Path cachePath) {
		this(cachePath, 7);
	}

	private static String buildPageUrl(String k) {
		return "http://store.steampowered.com/app/"+k;
	}

	private static String buildPageUrl(Long k) {
		return buildPageUrl(k.toString());
	}

	@Override
	public GameInfo load(Long gameId, EnumSet<TagType> types) {
		GameInfo gameInfo=new GameInfo();
		gameInfo.setId(gameId);
		try {
			if (!types.isEmpty()) {
				InputStream inputStream=cache.get(gameId.toString());
				try {
					Document document=Jsoup.parse(inputStream, Charsets.UTF_8.name(), buildPageUrl(gameId));

					Elements appName=document.select("div.apphub_AppName");
					Element nameElement=Iterables.getFirst(appName, null);
					if (null!=nameElement&&null!=nameElement.text()) {
						gameInfo.setName(nameElement.text().trim());
					}
					Elements appIcon=document.select("div.apphub_AppIcon img");
					Element iconElement=Iterables.getFirst(appIcon, null);
					if (null!=iconElement&&null!=iconElement.attr("src")) {
						try {
							gameInfo.setIcon(new URI(iconElement.attr("src")));
						} catch (URISyntaxException ex) {
							Logger.getLogger(HtmlTagLoader.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					Elements headerImage=document.select("img.game_header_image_full");
					Element headerImageElement=Iterables.getFirst(headerImage, null);
					if (null!=headerImageElement&&null!=headerImageElement.attr("src")) {
						try {
							gameInfo.setHeaderImage(new URI(headerImageElement.attr("src")));
						} catch (URISyntaxException ex) {
							Logger.getLogger(HtmlTagLoader.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					if (types.contains(TagType.CATEGORY)) {
						Elements categories=document.select("div#category_block a.name");
						copyText(categories, gameInfo.getTags().get(TagType.CATEGORY));
					}
					if (types.contains(TagType.GENRE)) {
						Elements genres=document.select("div.details_block a[href*=/genre/]");
						copyText(genres, gameInfo.getTags().get(TagType.GENRE));
					}
					if (types.contains(TagType.USER)) {
						Elements userTags=document.select("a.app_tag");
						copyText(userTags, gameInfo.getTags().get(TagType.USER));
					}
					if (types.contains(TagType.VR)) {
						Elements vrSupport=document.select("div.game_area_details_specs a.name[href*=#vrsupport=");
						copyText(vrSupport, gameInfo.getTags().get(TagType.VR));
					}
				} finally {
					IOUtils.closeQuietly(inputStream);
				}
			}
		} catch (ExecutionException ex) {
			Logger.getLogger(HtmlTagLoader.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(HtmlTagLoader.class.getName()).log(Level.SEVERE, null, ex);
		}

		return gameInfo;
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
