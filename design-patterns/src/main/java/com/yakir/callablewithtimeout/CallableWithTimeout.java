package com.yakir.callablewithtimeout;

import static com.yakir.callablewithtimeout.MainClass.logMessage;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
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
 *    - One instance of CallableWithTimeoutEnforcer must be provided [shared between all submitted callables] to the constructor<br>
 *      in order to enforce timeout without using the getOrWait<br>
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
	
	private T result = null;
	private long startTimeMillis;
	private long timeoutMillis;
	private AtomicBoolean started = new AtomicBoolean(false);
	private AtomicBoolean finishedSuccessfully = new AtomicBoolean(false);
	private AtomicBoolean killed = new AtomicBoolean(false);
	private AtomicBoolean timedout = new AtomicBoolean(false);
	private AtomicReference<Throwable> exception = new AtomicReference<>();
	private AtomicReference<Future<?>> innerFuture = new AtomicReference<>();
	private boolean readyForWait = false;
	
	public CallableWithTimeout(long timeout, TimeUnit unit, final CallableWithTimeoutEnforcer timeoutEnforcer) {
		this.timeoutMillis = unit.toMillis(timeout);
		timeoutEnforcer.start();
		timeoutEnforcer.register(this);
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

	public boolean didStart() {
		return started.get();
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

	public Throwable getException() {
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
		return didStart() && !isFinishedSuccessfully() && !isExecutionError();
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
		started.set(true);
		readyForWait = true;
		try {
			result = callInternal();
			if(!isKilled()) {
				finishedSuccessfully.set(true);
			}
		} catch(Throwable e) {
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
	
	public static class CallableWithTimeoutEnforcer implements Runnable {

		private long sleepIntervalsMS;
		private AtomicInteger registerId = new AtomicInteger(0);
		private AtomicBoolean startedRunning = new AtomicBoolean(false);
		private AtomicBoolean shutdown = new AtomicBoolean(false);
		private Map<Integer, CallableWithTimeout<?>> running = new ConcurrentHashMap<>();
		
		public CallableWithTimeoutEnforcer(long sleepInterval, TimeUnit unit) {
			this.sleepIntervalsMS = unit.toMillis(sleepInterval);
		}
		
		private void start() {
			if(startedRunning.compareAndSet(false, true)) {
				logMessage("starting enforcer");
				new Thread(this).start();
			}
		}
		
		public void register(CallableWithTimeout<?> cwt) {
			running.put(registerId.incrementAndGet(), cwt);
		}
		
		public boolean isRunning() {
			return startedRunning.get();
		}
		
		public boolean isShutdown() {
			return shutdown.get();
		}
		
		public void shutdown() {
			this.shutdown.set(true);
		}
		
		@Override
		public void run() {
			while(!isShutdown() || (isRunning() && !running.isEmpty())) {
				Utils.sleep(sleepIntervalsMS);
				Set<Integer> done = new HashSet<>();
				for(Entry<Integer, CallableWithTimeout<?>> e : running.entrySet()) {
					Integer id = e.getKey();
					CallableWithTimeout<?> cwt = e.getValue();
					if(cwt.isStillRunning()) {
						long timeLeft = cwt.calcTimeLeft();
						if(timeLeft <= 0) {
							logMessage(cwt.myId + " killing using enforcer");
							cwt.killMe(true);
							done.add(id);
						}
					} else if(cwt.didStart()) {
						done.add(id);
					}
				}
				
				running.keySet().removeAll(done);
			}
			
			logMessage("enforcer finished!");
		}
	}
}