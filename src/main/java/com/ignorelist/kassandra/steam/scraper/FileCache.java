/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.cache.AbstractLoadingCache;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.Striped;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author poison
 */
public class FileCache extends AbstractLoadingCache<String, InputStream> {

	private static final Logger LOG=Logger.getLogger(FileCache.class.getName());

	private final Base32 codec;
	private final Striped<ReadWriteLock> stripedLock;
	private final Path cacheDirectory;
	private final CacheLoader<String, ? extends InputStream> valueLoader;
	private final long expireAfterMillis;

	public FileCache(Path cacheDirectory, CacheLoader<String, ? extends InputStream> valueLoader, long expireAfterMillis) {
		codec=new Base32();
		stripedLock=Striped.lazyWeakReadWriteLock(4*Runtime.getRuntime().availableProcessors());
		this.cacheDirectory=cacheDirectory;
		this.valueLoader=valueLoader;
		this.expireAfterMillis=expireAfterMillis;
	}

	public FileCache(Path cacheDirectory, CacheLoader<String, ? extends InputStream> valueLoader, TimeUnit expireUnit, long expireDuration) {
		this(cacheDirectory, valueLoader, TimeUnit.MILLISECONDS.convert(expireDuration, expireUnit));
	}

	private String encodeFileName(String key) {
		if (Strings.isNullOrEmpty(key)) {
			throw new IllegalArgumentException("key may not be null or empty");
		}
		return codec.encodeAsString(key.getBytes(Charsets.UTF_8));
	}

	private String decodeFileName(String fileName) {
		return new String(codec.decode(fileName), Charsets.UTF_8);
	}

	private Path buildCacheFile(String key) {
		return cacheDirectory.resolve(encodeFileName(key));
	}

	private boolean exists(String key) {
		return Files.isRegularFile(buildCacheFile(key), LinkOption.NOFOLLOW_LINKS);
	}

	private long getModified(String key) throws IOException {
		FileTime lastModifiedTime=Files.getLastModifiedTime(buildCacheFile(key), LinkOption.NOFOLLOW_LINKS);
		return lastModifiedTime.toMillis();
	}

	private boolean isExpired(String key) {
		try {
			return (System.currentTimeMillis()-getModified(key))>=expireAfterMillis;
		} catch (IOException ex) {
			return true;
		}
	}

	@Override
	public InputStream get(String key, Callable<? extends InputStream> valueLoader) throws ExecutionException {
		InputStream inputStream=getIfPresent(key);
		if (null!=inputStream) {
			return inputStream;
		}
		final Lock writeLock=stripedLock.get(key).writeLock();
		writeLock.lock();
		try {
			inputStream=getIfPresentNonBlocking(key);
			if (null!=inputStream) {
				return inputStream;
			}
			try {
				inputStream=valueLoader.call();
				try {
					putNonBlocking(key, inputStream);
					return getIfPresentNonBlocking(key);
				} catch (IOException ex) {
					Logger.getLogger(FileCache.class.getName()).log(Level.SEVERE, "failed to write cache file", ex);
					throw new ExecutionException("failed to load "+key, ex);
				}
			} catch (Exception e) {
				Logger.getLogger(FileCache.class.getName()).log(Level.SEVERE, null, e);
				throw new ExecutionException(e);
			}
		} finally {
			writeLock.unlock();
		}

	}

	@Override
	public void put(String key, InputStream value) {
		final Lock writeLock=stripedLock.get(key).writeLock();
		writeLock.lock();
		try {
			putNonBlocking(key, value);
		} catch (IOException ex) {
			Logger.getLogger(FileCache.class.getName()).log(Level.SEVERE, null, ex);
		} finally {
			writeLock.unlock();
		}

	}

	private void putNonBlocking(String key, InputStream value) throws IOException {
		final Path cacheFile=buildCacheFile(key);
		OutputStream outputStream=new GZIPOutputStream(Files.newOutputStream(cacheFile));
		try {
			IOUtils.copy(value, outputStream);
		} finally {
			IOUtils.closeQuietly(outputStream);
		}
	}

	@Override
	public InputStream get(final String key) throws ExecutionException {
		return get(key, new Callable<InputStream>() {
			@Override
			public InputStream call() throws Exception {
				return valueLoader.load(key);
			}
		});
	}

	@Override
	public InputStream getIfPresent(Object key) {
		final Lock readLock=stripedLock.get(key).readLock();
		readLock.lock();
		try {
			return getIfPresentNonBlocking(key);
		} finally {
			readLock.unlock();
		}
	}

	private InputStream getIfPresentNonBlocking(Object key) {
		if (isExpired(key.toString())) {
			return null;
		}
		try {
			return new GZIPInputStream(Files.newInputStream(buildCacheFile(key.toString()), StandardOpenOption.READ));
		} catch (IOException ex) {
			LOG.log(Level.WARNING, "failed to open InputStream", ex);
			return null;
		}
	}

}
