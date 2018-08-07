/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import org.apache.commons.io.IOUtils;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author poison
 */
public class HtmlTagLoaderNGTest {
	
	public HtmlTagLoaderNGTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
	}
	

	/**
	 * Test of load method, of class HtmlTagLoader.
	 */
	@Test
	public void testLoad() throws MalformedURLException, IOException {
		final long gameId=552440l;
		String pageUrlString=HtmlTagLoader.buildPageUrl(gameId);
		URL pageUrl=new URL(pageUrlString);
		try (InputStream is=URLUtil.openInputStream(pageUrl)) {
			GameInfo gameInfo=HtmlTagLoader.parseHtml(is, gameId, EnumSet.allOf(TagType.class));
			assertEquals(gameInfo.getName(), "The Talos Principle VR");
			assertNotNull(gameInfo.getHeaderImage());
			assertNotNull(gameInfo.getIcon());
			assertTrue(gameInfo.getAllTags(EnumSet.of(TagType.CATEGORY)).contains("Single-player"));
			assertTrue(gameInfo.getAllTags(EnumSet.of(TagType.GENRE)).contains("Adventure"));
			assertTrue(gameInfo.getAllTags(EnumSet.of(TagType.USER)).contains("Puzzle"));
			assertTrue(gameInfo.getAllTags(EnumSet.of(TagType.USER_HIDDEN)).containsAll(gameInfo.getAllTags(EnumSet.of(TagType.USER))));
			assertTrue(gameInfo.getAllTags(EnumSet.of(TagType.VR)).containsAll(ImmutableSet.of("Oculus Rift", "HTC Vive")));
//			System.err.println(gameInfo.toString());
//			System.err.println(gameInfo.getHeaderImage());
//			System.err.println(gameInfo.getIcon());
//			System.err.println(gameInfo.getAllTags(EnumSet.of(TagType.CATEGORY)));
//			System.err.println(gameInfo.getAllTags(EnumSet.of(TagType.GENRE)));
//			System.err.println(gameInfo.getAllTags(EnumSet.of(TagType.USER)));
//			System.err.println(gameInfo.getAllTags(EnumSet.of(TagType.USER_HIDDEN)));
//			System.err.println(gameInfo.getAllTags(EnumSet.of(TagType.VR)));
		}
	}
	
}
