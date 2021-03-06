package com.yakir.sandbox.bofa;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Data;

@Data 
public class CheckExecutionInfo {
	private String checkName;
	private Integer tickets;
	private long durationSeconds;
	private String type;
	private String line;
	private String system;

	private CheckExecutionInfo(CheckExecutionInfo.CheckExecutionInfoBuilder builder) {
		this.checkName = builder.checkName;
		this.durationSeconds = builder.calcDuration();
		this.tickets = builder.tickets;
		this.type = builder.type;
		this.line = builder.line;
		this.system = builder.system;
	}
	
	public static CheckExecutionInfo.CheckExecutionInfoBuilder of(String checkName) {
		return new CheckExecutionInfoBuilder(checkName);
	}
	
	public static class CheckExecutionInfoBuilder {
		/*
		 * 1 - pack
		 * 2 - check id
		 * 3 - check name
		 */
		private static final Pattern REGEX_BOFA_CHECK = Pattern.compile("(\\[.+?\\]): '\\s*(\\d+.\\d+):?\\s*(.+)'");
		
		private String checkName;
		private Integer tickets;
		private String duration;
		private boolean calcDuration = true;
		private String type;
		private String line;
		private String system;
		
		private CheckExecutionInfoBuilder(String checkName) {
			Matcher m = REGEX_BOFA_CHECK.matcher(checkName);
			if(m.find()) {
				this.checkName = String.format("%s: %s: %s", m.group(1).trim(), m.group(2).trim(), m.group(3).trim());
			} else {
				this.checkName = checkName;
			}
		}
		
		public CheckExecutionInfo build() {
			return new CheckExecutionInfo(this);
		}

		private long calcDuration() {
			if(calcDuration) {
				String[] parts = duration.split(" ");
				CheckExecutionInfoBuilder.DurationUnit u = DurationUnit.findByName(parts[1]);
				return u.calcDurationInSeconds(parts[0]);
			}
			
			return Long.parseLong(duration);
		}

		public CheckExecutionInfo.CheckExecutionInfoBuilder setTickets(String tickets) {
			this.tickets = Integer.parseInt(tickets);
			return this;
		}

		public CheckExecutionInfo.CheckExecutionInfoBuilder setDuration(String duration) {
			this.duration = duration;
			return this;
		}
		
		public CheckExecutionInfo.CheckExecutionInfoBuilder disableCalcDuration() {
			this.calcDuration = false;
			return this;
		}

		public CheckExecutionInfo.CheckExecutionInfoBuilder setType(String type) {
			this.type = type;
			return this;
		}

		public CheckExecutionInfo.CheckExecutionInfoBuilder setLine(String line) {
			this.line = line;
			return this;
		}
		
		public CheckExecutionInfo.CheckExecutionInfoBuilder setSystem(String system) {
			this.system = system;;
			return this;
		}
		
		private static enum DurationUnit {
			second,
			minute,
			hour
			;
			
			private static Map<String, DurationUnit> reverseMap = new HashMap<>();
			
			static {
				for(DurationUnit v : values()) {
					reverseMap.put(v.name(), v);
					reverseMap.put(v.name() + "s", v);
				}
			}
			
			public String fixDurationString(String duration) {
				String fixed = "";
				String[] parts = duration.split(":");
				if(parts.length == 1) {
					fixed += "00:00:";
				} else if(parts.length == 2) {
					fixed += "00:";
				}
				
				fixed += duration;
				
				return Arrays.stream(fixed.split(":")).map(v -> v.length() == 1? "0"+v : v).collect(Collectors.joining(":"));
			}
			
			public long calcDurationInSeconds(String durationString) {
				return LocalTime.parse(fixDurationString(durationString)).toSecondOfDay();
			}
			
			public static DurationUnit findByName(String name) {
				return reverseMap.get(name.toLowerCase());
			}
		}
	}
}