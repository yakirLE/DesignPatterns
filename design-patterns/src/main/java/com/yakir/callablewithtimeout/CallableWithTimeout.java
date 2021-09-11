package com.yakir.callablewithtimeout;

import static com.yakir.callablewithtimeout.MainClass.logMessage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.yakir.utils.Utils;

public abstract class CallableWithTimeout<T> implements Runnable {
	
	// DEBUG
	private static AtomicInteger id = new AtomicInteger(0);
	protected int myId = id.incrementAndGet();
	// DEBUG
	
	private final ExecutorService timeBombsCachedThreadPool;
	private T result = null;
	private long startTimeMillis;
	private long timeoutMillis;
	private AtomicBoolean finishedSuccessfully = new AtomicBoolean(false);
	private AtomicBoolean killed = new AtomicBoolean(false);
	private Exception exception = null;
	private Future<?> innerFuture = null;
	private boolean readyForWait = false;
	
	public CallableWithTimeout(long timeout, TimeUnit unit, final ExecutorService timeBombsCachedThreadPool) {
		this.timeoutMillis = unit.toMillis(timeout);
		this.timeBombsCachedThreadPool = timeBombsCachedThreadPool;
	}
	
	protected long getStartTimeMillis() {
		return startTimeMillis;
	}
	
	protected long getTimeoutMillis() {
		return timeoutMillis;
	}

	protected void setResult(T result) {
		this.result = result;
	}

	public boolean isFinishedSuccessfully() {
		return finishedSuccessfully.get();
	}
	
	public boolean isKilled() {
		return killed.get();
	}

	public Exception getException() {
		return exception;
	}
	
	public void setInnerFuture(Future<?> innerFuture) {
		this.innerFuture = innerFuture;
	}

	private synchronized void waitFor() throws ExecutionException {
		int counter = 0;
		int maxSecondsToWait = 5;
		while(!readyForWait && counter < maxSecondsToWait * 10) {
			counter++;
			Utils.sleep(100);
		}
		
		if(isStillRunning()) {
			long timeLeft = calcTimeLeft();
			if(timeLeft > 0) {
				try {
					innerFuture.get(timeLeft, TimeUnit.MILLISECONDS);
				} catch (TimeoutException | InterruptedException e) {
					logMessage(myId + " killing using waitFor with exception");
					killMe();
				}
			} else {
				logMessage(myId + " killing using waitFor with no time left");
				killMe();
			}
		}
	}
	
	private long calcTimeLeft() {
		return startTimeMillis + timeoutMillis - System.currentTimeMillis();
	}
	
	private boolean isStillRunning() {
		return !isFinishedSuccessfully() && exception == null;
	}
	
	private void killMe() {
		if(killed.compareAndSet(false, true)) {
			finishedSuccessfully.set(false);
			innerFuture.cancel(true);
			logMessage(myId + " I'm dead");
		}
	}
	
	@Override
	public void run() {
		startTimeMillis = System.currentTimeMillis();
		readyForWait = true;
		try {
			timeBombsCachedThreadPool.submit(new TimeBomb());
			result = callInternal();
			if(!isKilled()) {
				finishedSuccessfully.set(true);
			}
		} catch(Exception e) {
			exception = e;
		}
		
		logMessage(myId + " " + (isFinishedSuccessfully() ? "succeeded" : "timedout"));
	}
	
	public T getOrWait() {
		try {
			waitFor();
		} catch(Exception e) {
			// failed for some reason
		}
		
		if(isKilled()) {
			executeIfTimedout();
		}
		
		return result;
	}
	
	protected abstract T callInternal();
	protected abstract void executeIfTimedout();
	
	private class TimeBomb implements Runnable {
		
		private int sleepIntervalsMS = 1 * 1000;

		@Override
		public void run() {
			while(!isFinishedSuccessfully() && !isKilled()) {
				Utils.sleep(sleepIntervalsMS);
				if(isStillRunning()) {
					long timeLeft = calcTimeLeft();
					if(timeLeft <= 0) {
						logMessage(myId + " killing using time bomb");
						killMe();
					}
				}
			}
		}
	}
}