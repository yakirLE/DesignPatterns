package com.yakir.design.patterns.builder.functional.consumer;

import java.util.Optional;
import java.util.function.Consumer;

import lombok.ToString;

/*
 * article: https://theboreddev.com/functional-patterns-in-java/
 */

@ToString
public class Employee {

	private final String name;
	private final int age;
	private final String department;
	
	private Employee(Builder builder) {
		this.name = builder.name;
		this.age = builder.age;
		this.department = builder.department;
	}
	
	public String getName() {
		return name;
	}
	
	public Optional<Integer> getAge() {
		return Optional.ofNullable(age);
	}
	
	public Optional<String> getDepartment() {
		return Optional.ofNullable(department);
	}
	
	public static Builder builderOf(String name) {
		return new Builder(name);
	}
	
	public static class Builder {
		private String name;
		public int age;
		public String department;
		
		private Builder(String name) {
			this.name = name;
		}
		
		public Builder with(Consumer<Builder> consumer) {
			consumer.accept(this);
			return this;
		}
		
		public Employee build() {
			return new Employee(this);
		}
	}
}
