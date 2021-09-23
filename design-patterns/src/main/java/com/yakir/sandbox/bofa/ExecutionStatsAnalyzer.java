package com.yakir.sandbox.bofa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExecutionStatsAnalyzer {

	private String gap2105;
	private String gap2100;
	
	public void execute() throws IOException {
		/*
		 * 1 - tickets
		 * 2 - duration
		 * 3 - type
		 * 4 - check
		 */
		final Pattern REGEX_2100 = Pattern.compile("TICKETS \\[(\\d+)\\]\\s+DURATION \\[([\\d:]+ .+?)\\]\\s+SIGNATURE: \\[(.+?)\\]\\s+(\\[.+')");
		final Pattern REGEX_2105 = Pattern.compile("PASS \\[\\d+\\]\\s+FAIL \\[(\\d+)\\]\\s+INSUFFICIENT INFORMATION \\[\\d+\\]\\s+DURATION \\[([\\d:]+ .+?)\\]\\s+CHECK: \\[(.+?)\\]\\s+(\\[.+')");
		Map<String, List<CheckExecutionInfo>> results = new TreeMap<>();
		Matcher m = REGEX_2105.matcher(gap2105);
		while(m.find()) {
			fillMap(m, results, "DSA");
		}
		
		m = REGEX_2100.matcher(gap2100);
		while(m.find()) {
			fillMap(m, results, "AG");
		}
		
		StringJoiner sjContent = new StringJoiner("\n");
		printMap(new TreeMap<>(results.entrySet().stream().filter(e -> e.getValue().size() == 2).collect(Collectors.toMap(Entry::getKey, Entry::getValue))), sjContent);
		printMap(new TreeMap<>(results.entrySet().stream().filter(e -> e.getValue().size() == 1 && e.getValue().iterator().next().getSystem().equals("DSA")).collect(Collectors.toMap(Entry::getKey, Entry::getValue))), sjContent);
		printMap(new TreeMap<>(results.entrySet().stream().filter(e -> e.getValue().size() == 1 && e.getValue().iterator().next().getSystem().equals("AG")).collect(Collectors.toMap(Entry::getKey, Entry::getValue))), sjContent);
		
		Files.writeString(Path.of("checksStats.txt"), sjContent.toString());
	}
	
	private static void printMap(Map<String, List<CheckExecutionInfo>> map, StringJoiner sjContent) {
		map.forEach((c, info) -> {
			StringJoiner sj = new StringJoiner("\t");
			sj.add(c);
			if(info.size() == 1 && info.get(0).getSystem().equals("AG")) {
				sj.add("\t\t\t");
			}
			
			info.forEach(i -> {
				sj.add(i.getType());
				sj.add(i.getTickets()+"");
				sj.add(i.getDurationSeconds()+"");
				sj.add("");
			});
			
			if(info.size() == 2) {
				sj.add((info.get(0).getTickets() - info.get(1).getTickets())+"");
				sj.add((info.get(0).getDurationSeconds() - info.get(1).getDurationSeconds())+"");
			}
			
			sjContent.add(sj.toString());
		});
	}

	private static void fillMap(Matcher m, Map<String, List<CheckExecutionInfo>> results, String system) {
		CheckExecutionInfo info = CheckExecutionInfo.of(m.group(4))
													.setTickets(m.group(1))
													.setDuration(m.group(2))
													.setType(m.group(3))
													.setLine(m.group())
													.setSystem(system)
													.build();
		results.computeIfAbsent(info.getCheckName(), k -> new ArrayList<>()).add(info);
	}
}
