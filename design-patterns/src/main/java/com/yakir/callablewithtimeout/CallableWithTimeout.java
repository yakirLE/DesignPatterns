package com.yakir.callablewithtimeout;

import static com.yakir.callablewithtimeout.MainClass.logMessage;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.yakir.utils.Utils;

/**
 * Callable that handles it's own timeout.<br>
 * Prerequisites:<br>
 *    - The Future<T> of the callable must be passed to the callable itself using the setInnerFuture method<br>
 *    - It's recommended to create exit points in the callable implementation using the isKilled method to help the thread to stop gracefully<br>
 *    - a CachedThreadPool pool must be provided to the constructor in order to create TimeBombs instances for each callable<br>
 *      so the timeout will be enforced before we call the getOrWait method if needed (CachedThreadPool meant to help with threads performance)<br>
 * Notes:<br>
 *    - The method getOrWait is executed from the main thread, not the callable itself<br>
 *    - execution results can be:<br>
 *      * success - callable executed properly within it's time limit, e.g isFinishedSuccessfully method<br>
 *      * timedout - callable received a timeout because it exceeded it's time limit, e.g isTimedout method<br>
 *      * execution error - callable received an exception during it logic execution, e.g isExecutionError method<br>
 *      * killed - callable was killed due to timeout or execution error, eg isKilled method<br>
 * 
 * @author yakir
 *
 * @param <T> the value type we would like to get back from each callable 
 */
public abstract class CallableWithTimeout<T> implements Runnable {
	
	// DEBUG - delete those and followed errors
	private static AtomicInteger id = new AtomicInteger(0);
	protected int myId = id.incrementAndGet();
	// DEBUG
	
	private final ExecutorService timeBombsCachedThreadPool;
	private T result = null;
	private long startTimeMillis;
	private long timeoutMillis;
	private AtomicBoolean finishedSuccessfully = new AtomicBoolean(false);
	private AtomicBoolean killed = new AtomicBoolean(false);
	private AtomicBoolean timedout = new AtomicBoolean(false);
	private AtomicReference<Exception> exception = new AtomicReference<>();
	private AtomicReference<Future<?>> innerFuture = new AtomicReference<>();
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
	
	public boolean isTimedout() {
		return timedout.get();
	}
	
	public boolean isExecutionError() {
		return Optional.ofNullable(exception.get()).isPresent();
	}

	public Exception getException() {
		return exception.get();
	}
	
	public void setInnerFuture(Future<?> innerFuture) {
		this.innerFuture.compareAndSet(null, innerFuture);
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
					innerFuture.get().get(timeLeft, TimeUnit.MILLISECONDS);
				} catch (TimeoutException | InterruptedException e) {
					logMessage(myId + " killing using waitFor with exception");
					killMe(true);
				}
			} else {
				logMessage(myId + " killing using waitFor with no time left");
				killMe(true);
			}
		}
	}
	
	private long calcTimeLeft() {
		return startTimeMillis + timeoutMillis - System.currentTimeMillis();
	}
	
	private boolean isStillRunning() {
		return !isFinishedSuccessfully() && !isExecutionError();
	}
	
	private void killMe(boolean timeout) {
		if(killed.compareAndSet(false, true)) {
			finishedSuccessfully.set(false);
			timedout.set(timeout);
			Optional.ofNullable(innerFuture.get()).ifPresent(f -> f.cancel(true));
			logMessage(myId + " I'm dead");
		}
	}
	
	@Override
	public void run() {
		startTimeMillis = System.currentTimeMillis();
		readyForWait = true;
		timeBombsCachedThreadPool.submit(new TimeBomb());
		try {
			result = callInternal();
			if(!isKilled()) {
				finishedSuccessfully.set(true);
			}
		} catch(Exception e) {
			exception.set(e);
			killMe(false);
		}
		
		logMessage(myId + " " + (isFinishedSuccessfully() ? "succeeded" : (isTimedout() ? "timedout" : "execution error")));
	}
	
	public T getOrWait() {
		try {
			waitFor();
		} catch(Exception e) {
			logMessage(myId + " finished with exception in wait for");
			// failed for some reason
		}
		
		if(isTimedout()) {
			executeIfTimedout();
		}
		
		return result;
	}
	
	protected abstract T callInternal();
	protected abstract void executeIfTimedout();
	
	private class TimeBomb implements Runnable {
		
		private long sleepIntervalsMS;

		// DEBUG - delete it and followed errors
		public TimeBomb() {
			this(1, TimeUnit.SECONDS);
		}
		// DEBUG
		
		public TimeBomb(long sleepInterval, TimeUnit unit) {
			this.sleepIntervalsMS = unit.toMillis(sleepInterval);
		}
		
		@Override
		public void run() {
			while(!isFinishedSuccessfully() && !isKilled()) {
				Utils.sleep(sleepIntervalsMS);
				if(isStillRunning()) {
					long timeLeft = calcTimeLeft();
					if(timeLeft <= 0) {
						logMessage(myId + " killing using time bomb");
						killMe(true);
					}
				}
			}
		}
	}
}