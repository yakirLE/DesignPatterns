package com.yakir.callablewithtimeout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		for(SelfTimeoutCallable l : lst) {
			logMessage(l.getMyId() + " waiting for");
			l.getOrWait();
		}
		
		pool.shutdown();
		timeBombsPool.shutdown();
		System.out.println("main done");
	}
	
	private static void initSleeper(int sleepTime, ExecutorService pool, ExecutorService timeBombsPool, List<SelfTimeoutCallable> lst) {
		SelfTimeoutCallable init = new SelfTimeoutCallable(sleepTime, 7, timeBombsPool);
		lst.add(init);
		init.setInnerFuture(pool.submit(init));
		Utils.sleep(10);
	}
}
