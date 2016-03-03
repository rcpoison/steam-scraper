/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	public Set<String> load(Long gameId, EnumSet<TagType> types) {
		return tagLoader.load(gameId, types);
	}

	public SetMultimap<Long, String> load(Iterable<Long> gameIds, final EnumSet<TagType> types) {
		ExecutorService executorService=Executors.newFixedThreadPool(nThreads);
		Map<Long, Future<Set<String>>> futureResults=new HashMap<>();
		for (final Long gameId : gameIds) {
			futureResults.put(gameId, executorService.submit(new Callable<Set<String>>() {
				@Override
				public Set<String> call() throws Exception {
					return load(gameId, types);
				}
			}));
		}
		SetMultimap<Long, String> results=HashMultimap.create();
		for (Map.Entry<Long, Future<Set<String>>> entry : futureResults.entrySet()) {
			final Long key=entry.getKey();
			final Future<Set<String>> value=entry.getValue();
			try {
				results.putAll(key, value.get());
			} catch (InterruptedException ex) {
				Logger.getLogger(BatchTagLoader.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ExecutionException ex) {
				Logger.getLogger(BatchTagLoader.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.MINUTES);
		return results;
	}
}
