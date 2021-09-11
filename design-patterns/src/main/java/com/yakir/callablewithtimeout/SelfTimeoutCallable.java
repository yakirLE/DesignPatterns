package com.yakir.callablewithtimeout;

import static com.yakir.callablewithtimeout.MainClass.logMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.yakir.utils.Utils;

public class SelfTimeoutCallable extends CallableWithTimeout<Integer> {

	private long sleepMS;
	private long startSleepingMS;
	
	public SelfTimeoutCallable(long sleepSeconds, long timeoutSeconds, ExecutorService timeBombsPool) {
		super(timeoutSeconds, TimeUnit.SECONDS, timeBombsPool);
		this.sleepMS = sleepSeconds * 1000;
	}

	@Override
	protected Integer callInternal() {
		logMessage(myId + " starting sleep of " + sleepMS / 1000);
		startSleepingMS = System.currentTimeMillis();
		long sleptTime = 0;
		while (shouldKeepSleeping()) {
			Utils.sleep(250);
			sleptTime += 250;
			if(isKilled()) {
				throw new RuntimeException(myId + " timedout");
//				break;
			}
		}
		
		logMessage(String.format("%d finished sleeping %d ms", myId, sleptTime));
		return myId;
	}
	
	private boolean shouldKeepSleeping() {
		return (startSleepingMS + sleepMS - System.currentTimeMillis()) > 0;
	}
	
	public int getMyId() {
		return myId;
	}

	@Override
	protected void executeIfTimedout() {
	}
}
