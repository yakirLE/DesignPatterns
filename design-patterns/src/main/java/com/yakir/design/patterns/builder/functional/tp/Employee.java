package com.yakir.design.patterns.builder.functional.tp;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.inferred.freebuilder.FreeBuilder;

/*
 * article: https://www.baeldung.com/java-builder-pattern-freebuilder 
 */

@FreeBuilder
public interface Employee {

	String getName();
	int getAge();
	String getDepartment();
	Optional<String> getNickname();
	Optional<Double> getSalaryInUSD();
	String getEmail();
	List<Integer> getNums();
	Set<Integer> getNums2();
	Map<String, String> getKeyValueMap();
	Map<String, String> getMap2();
	Map<String, String> getMap3();

	public class Builder extends Employee_Builder {
		
		public Builder() {
			setDepartment("default department");
		}
		
		@Override
		public Builder setEmail(String email) {
			if(isValidEmail(email)) {
				return super.setEmail(email);
			}
			
			throw new IllegalArgumentException("Invalid email: " + email);
		}
		
		private boolean isValidEmail(String email) {
			return email.contains("@");
		}
	}
}
