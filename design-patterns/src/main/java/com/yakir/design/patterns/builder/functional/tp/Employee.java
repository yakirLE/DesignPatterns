package com.yakir.design.patterns.builder.functional.tp;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.inferred.freebuilder.FreeBuilder;

@FreeBuilder
public interface Employee {

	String name();
	int age();
	String department();
	Optional<String> nickname();
	Optional<Double> salaryInUSD();
	String email();
	List<Integer> nums();
	Set<Integer> nums2();
	Map<String, String> keyValueMap();
	Map<String, String> map2();
	Map<String, String> map3();

	public class Builder extends Employee_Builder {
		
		public Builder() {
			department("default department");
		}
		
		@Override
		public Builder email(String email) {
			if(isValidEmail(email)) {
				return super.email(email);
			}
			
			throw new IllegalArgumentException("Invalid email: " + email);
		}
		
		private boolean isValidEmail(String email) {
			return email.contains("@");
		}
	}
}
