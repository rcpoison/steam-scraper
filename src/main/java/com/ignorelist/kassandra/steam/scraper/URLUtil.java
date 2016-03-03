/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author poison
 */
public final class URLUtil {

	private URLUtil() {
	}

	public static InputStream openInputStream(URL url) throws IOException {
		URLConnection uRLConnection=url.openConnection();
		uRLConnection.setRequestProperty("User-Agent", "Valve/Steam HTTP Client 1.0 (tenfoot)");
		uRLConnection.setRequestProperty("Cookie", "birthtime=-3599; lastagecheckage=1-January-1970");
		uRLConnection.setConnectTimeout(8000);
		uRLConnection.setReadTimeout(8000);
		return uRLConnection.getInputStream();
	}

}
