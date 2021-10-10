package com.yakir.sandbox;

import static com.yakir.sandbox.bofa.ExecutionStatsAnalyzer.Product.DSA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.yakir.sandbox.bofa.ExecutionStatsAnalyzer;
import com.yakir.utils.CounterMap;

public class Sandbox {

	private static final Pattern REGEX_DATE = Pattern.compile("^\\d+/\\d+/\\d+ \\d+:\\d+:\\d+");
	private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	public static void main(String[] args) throws IOException, URISyntaxException, ParseException {
		new ExecutionStatsAnalyzer(ExecutionStatsAnalyzer.get2105_2809(), "UAT_28.9", "C:\\del\\BofA\\upgrade\\CA2_After_Hotfix.txt", "UAT_4.10").execute(DSA, true, DSA, false);
		System.exit(0);
		System.out.println("--DA_20-9_Prod");
		findTimeGapsSuggestingGC("C:\\del\\BofA\\upgrade\\DA_20-9_Prod.txt");
		System.out.println();
		System.out.println();
		
		System.out.println("--CA_20-9_Before_Hotfix");
		findTimeGapsSuggestingGC("C:\\del\\BofA\\upgrade\\CA_20-9_Before_Hotfix.txt");
		System.out.println();
		System.out.println();
		
		System.out.println("--CA1_After_Hotfix");
		findTimeGapsSuggestingGC("C:\\del\\BofA\\upgrade\\CA1_After_Hotfix.txt");
		System.out.println();
		System.out.println();
		
		System.out.println("--CA2_After_Hotfix");
		findTimeGapsSuggestingGC("C:\\del\\BofA\\upgrade\\CA2_After_Hotfix.txt");
		System.out.println();
		System.out.println();
		findHostsWithData();
//		new ExecutionStatsAnalyzer(ExecutionStatsAnalyzer.get2105_2009(), "UAT_20.9", ExecutionStatsAnalyzer.get2105_2809(), "UAT_28.9").execute(DSA, DSA);
		System.out.println("done");
	}
	
	private static void findTimeGapsSuggestingGC(String file) throws FileNotFoundException, IOException, ParseException {
		SortedSet<Date> set = new TreeSet<>(); 
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			while(line != null) {
				Matcher m = REGEX_DATE.matcher(line);
				if(m.find()) {
					set.add(SDF.parse(m.group()));
				}
				
				line = br.readLine();
			}
		}
		
		Date d0 = set.first();
		long sum = 0;
		for(Date d : set) {
			if(d.getTime() - d0.getTime() > 15*1000) {
				sum += d.getTime() - d0.getTime();
				System.out.println(String.format("%s missing time=%d, (%d - %d)", SDF.format(d), d.getTime() - d0.getTime(), d.getTime(), d0.getTime()));
			}
			
			d0 = d;
		}
		
		System.out.println("Total missing time = " + sum / 1000 / 60 + " minutes");
	}

	@SuppressWarnings("unused")
	private static void findHighMemoryUsage() throws IOException {
		Pattern p = Pattern.compile("Used: (\\d+)MB \\[(\\d+)%\\], Total: (\\d+)MB");
		Stream<String> lines = Files.lines(Paths.get("C:\\del\\BofA\\upgrade\\CA2_After_Hotfix.txt"));
		lines.forEach(l -> {
			Matcher m = p.matcher(l);
			if(m.find()) {
				if(Integer.parseInt(m.group(2)) > 80 && (Double.parseDouble(m.group(1)) / Double.parseDouble(m.group(3))) > 0.95) {
					System.out.println(l);
				}
			}
		});
		
		lines.close();
	}

	@SuppressWarnings("unused")
	private static void findNewScripts() {
		File d1 = new File("C:\\del\\BofA\\upgrade\\scripts\\7.3.3.10");
		File d2 = new File("C:\\del\\BofA\\upgrade\\scripts\\8.4.0.9");
		Set<String> s1 = new HashSet<>();
		Arrays.stream(d1.listFiles()).forEach(f -> s1.add(f.getName()));
		Arrays.stream(d2.listFiles()).forEach(f -> {
			if(!s1.contains(f.getName()))
				System.out.println(f.getName());
		});
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
		
		Set<String> sorted = new TreeSet<>();
		for(Entry<String, AtomicInteger> e : map.entrySet()) {
			String hostname = e.getKey();
			if (e.getValue().get() == 2 && prod.contains(hostname)/* && data.contains(hostname) */) {
				sorted.add(hostname);
			}
		}
		
		sorted.forEach(System.out::println);
	}
	
}
