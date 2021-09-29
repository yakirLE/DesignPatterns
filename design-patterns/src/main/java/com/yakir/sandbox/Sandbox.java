package com.yakir.sandbox;

import static com.yakir.sandbox.bofa.ExecutionStatsAnalyzer.Product.DSA;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.yakir.sandbox.bofa.ExecutionStatsAnalyzer;
import com.yakir.utils.CounterMap;

public class Sandbox {

	public static void main(String[] args) throws IOException, URISyntaxException {
		new ExecutionStatsAnalyzer(ExecutionStatsAnalyzer.get2105_2009(), "UAT_20.9", ExecutionStatsAnalyzer.get2105_2809(), "UAT_28.9").execute(DSA, DSA);
		System.out.println("done");
		
		System.exit(0);
		findHostsWithData();
	}

	/**
	 *
	 * find which hosts has raw data AND opened a ticket of type 004.050 in UAT BUT didn't open a ticket in Prod
	 * 
	 */
	private static void findHostsWithData() throws IOException, URISyntaxException {
		CounterMap<String> map = new CounterMap<>();
		Set<String> prod = new HashSet<>();
		Set<String> uat = new HashSet<>();
		Set<String> data = new HashSet<>();
		Files.readString(Path.of(Sandbox.class.getClassLoader().getResource("prod004050.txt").toURI())).lines().forEach(s -> {
			prod.add(s);
			map.increase(s);
		});
		
		Files.readString(Path.of(Sandbox.class.getClassLoader().getResource("uat004050.txt").toURI())).lines().forEach(s -> {
			uat.add(s);
			map.increase(s);
		});
		
		Files.readString(Path.of(Sandbox.class.getClassLoader().getResource("data004050.txt").toURI())).lines().forEach(data::add);
		
		for(Entry<String, AtomicInteger> e : map.entrySet()) {
			String hostname = e.getKey();
			if(e.getValue().get() == 1 && uat.contains(hostname) && data.contains(hostname)) {
				System.out.println(hostname);
			}
		}
	}
	
}
