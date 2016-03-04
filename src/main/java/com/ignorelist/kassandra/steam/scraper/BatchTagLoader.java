/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ignorelist.kassandra.steam.scraper;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author poison
 */
public class BatchTagLoader implements TagLoader {

	public static class GameInfoLoadedEvent {

		private final UUID loadId;
		private final int current;
		private final int total;
		private final GameInfo gameInfo;
		private final long durationMillis;

		public GameInfoLoadedEvent(UUID loadId, int current, int total, GameInfo gameInfo, long durationMillis) {
			this.loadId=loadId;
			this.current=current;
			this.total=total;
			this.gameInfo=gameInfo;
			this.durationMillis=durationMillis;
		}

		public UUID getLoadId() {
			return loadId;
		}

		public int getCurrent() {
			return current;
		}

		public int getTotal() {
			return total;
		}

		public GameInfo getGameInfo() {
			return gameInfo;
		}

		public long getDurationMillis() {
			return durationMillis;
		}

		@Override
		public int hashCode() {
			int hash=5;
			hash=67*hash+Objects.hashCode(this.loadId);
			hash=67*hash+this.current;
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
			final GameInfoLoadedEvent other=(GameInfoLoadedEvent) obj;
			if (this.current!=other.current) {
				return false;
			}
			if (!Objects.equals(this.loadId, other.loadId)) {
				return false;
			}
			return true;
		}

	}

	private final TagLoader tagLoader;
	private final int nThreads;
	private final EventBus eventBus;

	public BatchTagLoader(TagLoader tagLoader, int nThreads) {
		this.tagLoader=tagLoader;
		this.nThreads=nThreads;
		eventBus=new EventBus(BatchTagLoader.class.getSimpleName());
	}

	public BatchTagLoader(TagLoader tagLoader) {
		this(tagLoader, Runtime.getRuntime().availableProcessors()+1);
	}

	public void registerEventListener(Object object) {
		eventBus.register(object);
	}

	public void unregisterEventListener(Object object) {
		eventBus.unregister(object);
	}

	@Override
	public GameInfo load(Long gameId, EnumSet<TagType> types) {
		return tagLoader.load(gameId, types);
	}

	public Map<Long, GameInfo> load(Collection<Long> gameIds, final EnumSet<TagType> types) {
		final UUID loadId=UUID.randomUUID();
		final int total=gameIds.size();

		long wallTimeStart=System.currentTimeMillis();
		ExecutorService executorService=Executors.newFixedThreadPool(nThreads);
		final ConcurrentMap<Long, GameInfo> results=new ConcurrentHashMap<>();
		final AtomicLong time=new AtomicLong();
		final AtomicInteger current=new AtomicInteger();
		for (final Long gameId : gameIds) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					long start=System.currentTimeMillis();
					GameInfo loaded=load(gameId, types);
					results.put(gameId, loaded);
					long end=System.currentTimeMillis();
					final long duration=end-start;
					//System.err.println("loaded "+gameId+" in "+(end-start)+"ms");
					time.addAndGet(duration);
					final int currentCount=current.incrementAndGet();
					eventBus.post(new GameInfoLoadedEvent(loadId, currentCount, total, loaded, duration));
				}
			});
		}

		MoreExecutors.shutdownAndAwaitTermination(executorService, 1, TimeUnit.DAYS);
		long wallTimeEnd=System.currentTimeMillis();
		System.err.println("finished loading "+total+" games");
		System.err.println("time: "+time.longValue()+"ms");
		System.err.println("wallTime: "+(wallTimeEnd-wallTimeStart)+"ms");
		return results;
	}
}
