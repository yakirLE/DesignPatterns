package com.yakir.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterMap <T> extends ConcurrentHashMap<T, AtomicInteger> {

	private static final long serialVersionUID = -4990243938318098192L;

	private int floor = 0;
	
	public CounterMap() {
		super();
	}
	
	public CounterMap(int floor) {
		super();
		this.floor = floor;
	}
	
	public CounterMap(Map<T, Integer> m) {
		m.entrySet().stream().filter(e -> e.getKey() != null).forEach(e -> put(e.getKey(), e.getValue()));
	}
	
	public Integer increase(T key) {
		if(key == null) {
			return null;
		}
		
		return compute(key, (k, v) -> (v == null) ? new AtomicInteger(0) : v).incrementAndGet();
	}
	
	public Integer decrease(T key) {
		if(key == null) {
			return null;
		}
		
		return compute(key, (k, v) -> (v == null || v.get()==floor) ? new AtomicInteger(floor+1) : v).decrementAndGet();
	}
	
	public Integer put(T key, Integer value) {
		if(key == null) {
			return null;
		}
		
		return compute(key, (k, v) -> new AtomicInteger(value)).get();
	}
	
	public Map<T, Integer> getMap() {
		Map<T, Integer> map = new HashMap<>();
		this.forEach((k, c) -> map.put(k, c.get()));
		
		return map;
	}
}