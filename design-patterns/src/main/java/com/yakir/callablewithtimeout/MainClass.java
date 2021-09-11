package com.yakir.callablewithtimeout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.yakir.utils.Utils;

public class MainClass {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	static void logMessage(String msg) {
		System.out.println(String.format("%s - %d -\t%s", sdf.format(new Date()), Thread.currentThread().getId(), msg));
	}
	
	public static void main(String[] args) throws Exception {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		ExecutorService timeBombsPool = Executors.newCachedThreadPool();
		List<SelfTimeoutCallable> lst = new ArrayList<>();
		long start = System.currentTimeMillis();

		initSleeper(5, pool, timeBombsPool, lst);
		initSleeper(9, pool, timeBombsPool, lst);
		initSleeper(7, pool, timeBombsPool, lst);
		initSleeper(9, pool, timeBombsPool, lst);
		initSleeper(40, pool, timeBombsPool, lst);
		initSleeper(6, pool, timeBombsPool, lst);
		
		for(int i = 0; i < 34; i++) {
			initSleeper(new Random().nextInt(30-4) + 4, pool, timeBombsPool, lst);
		}
		
		Utils.sleep(start + 10000 - System.currentTimeMillis());
		logMessage("starting wait for");
		Map<String, Integer> resultsCounter = new HashMap<>();
		for(SelfTimeoutCallable l : lst) {
			logMessage(l.getMyId() + " waiting for");
			l.getOrWait();
			if(l.isFinishedSuccessfully()) {
				increase("succeeded", resultsCounter);
			} else if(l.isTimedout()) {
				increase("timedout", resultsCounter);
			} else if(l.isExecutionError()) {
				increase("execution error", resultsCounter);
			}
		}
		
		pool.shutdown();
		timeBombsPool.shutdown();
		System.out.println("main done");
		
		pool.awaitTermination(99999, TimeUnit.MINUTES);
		timeBombsPool.awaitTermination(99999, TimeUnit.MINUTES);
		System.out.println(resultsCounter);
	}
	
	private static void increase(String key, Map<String, Integer> map) {
		map.putIfAbsent(key, 0);
		map.put(key, map.get(key) + 1);
	}
	
	private static void initSleeper(int sleepTime, ExecutorService pool, ExecutorService timeBombsPool, List<SelfTimeoutCallable> lst) {
		SelfTimeoutCallable init = new SelfTimeoutCallable(sleepTime, 7, timeBombsPool);
		lst.add(init);
		init.setInnerFuture(pool.submit(init));
		Utils.sleep(10);
	}
}
