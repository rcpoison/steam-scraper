/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.util.concurrent.MoreExecutors;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author poison
 */
public class BatchTagLoader implements TagLoader {

	private final TagLoader tagLoader;
	private final int nThreads;

	public BatchTagLoader(TagLoader tagLoader, int nThreads) {
		this.tagLoader=tagLoader;
		this.nThreads=nThreads;
	}

	public BatchTagLoader(TagLoader tagLoader) {
		this(tagLoader, Runtime.getRuntime().availableProcessors()+1);
	}

	@Override
	public GameInfo load(Long gameId, EnumSet<TagType> types) {
		return tagLoader.load(gameId, types);
	}

	public Map<Long, GameInfo> load(Iterable<Long> gameIds, final EnumSet<TagType> types) {
		ExecutorService executorService=Executors.newFixedThreadPool(nThreads);
		final ConcurrentMap<Long, GameInfo> results=new ConcurrentHashMap<>();
		for (final Long gameId : gameIds) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					long start=System.currentTimeMillis();
					GameInfo loaded=load(gameId, types);
					long end=System.currentTimeMillis();
					//System.err.println("loaded "+gameId+" in "+(end-start)+"ms");
					results.put(gameId, loaded);
				}
			});
		}

		MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.DAYS);
		return results;
	}
}
