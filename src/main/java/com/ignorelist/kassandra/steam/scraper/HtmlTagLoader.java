/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlTagLoader implements TagLoader {

	private static final Pattern DISPLAY_NONE=Pattern.compile("display\\s*:\\s*none", Pattern.CASE_INSENSITIVE);
	private static final Predicate<Element> DISPLAY_NONE_PREDICATE=new Predicate<Element>() {

		@Override
		public boolean apply(Element input) {
			String style=input.attr("style");
			if (Strings.isNullOrEmpty(style)) {
				return false;
			}
			return DISPLAY_NONE.matcher(style).lookingAt();
		}
	};
	private static final ImmutableMap<String, String> URL_REPLACE=ImmutableMap.<String, String>builder()
			.put("%CDN_HOST_MEDIA%", "steamcdn-a.akamaihd.net")
			.build();

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

					Elements appIconElements=document.select("div.apphub_AppIcon img");
					gameInfo.setIcon(getSrcUri(appIconElements));

					Elements headerImageElements=document.select("img.game_header_image_full");
					gameInfo.setHeaderImage(getSrcUri(headerImageElements));
					final SetMultimap<TagType, String> tags=gameInfo.getTags();

					if (types.contains(TagType.CATEGORY)) {
						Elements categories=document.select("div#category_block a.name");
						copyText(categories, tags.get(TagType.CATEGORY));
					}
					if (types.contains(TagType.GENRE)) {
						Elements genres=document.select("div.details_block a[href*=/genre/]");
						copyText(genres, tags.get(TagType.GENRE));
					}
					if (types.contains(TagType.USER)) {
						Elements userTags=document.select("a.app_tag");
						copyText(Iterables.filter(userTags, Predicates.not(DISPLAY_NONE_PREDICATE)), tags.get(TagType.USER));
						copyText(Iterables.filter(userTags, DISPLAY_NONE_PREDICATE), tags.get(TagType.USER_HIDDEN));
					}
					if (types.contains(TagType.VR)) {
						Elements vrSupport=document.select("div.game_area_details_specs a.name[href*=#vrsupport=");
						copyText(vrSupport, tags.get(TagType.VR));
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

	private static URI getSrcUri(Elements elements) {
		final Element first=Iterables.getFirst(elements, null);
		if (null==first) {
			return null;
		}
		String srcString=first.attr("src");
		if (null==srcString) {
			return null;
		}
		try {
			for (Map.Entry<String, String> replacement : URL_REPLACE.entrySet()) {
				srcString=srcString.replace(replacement.getKey(), replacement.getValue());
			}
			return new URI(srcString);
		} catch (URISyntaxException ex) {
			Logger.getLogger(HtmlTagLoader.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	private static void copyText(Iterable<Element> elements, Set<String> target) {
		for (Element element : elements) {
			final String text=element.text();
			if (!Strings.isNullOrEmpty(text)) {
				target.add(text.trim());
			}
		}
	}

}
