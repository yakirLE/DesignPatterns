package com.yakir.design.patterns.builder.functional.standalone;

import java.util.Optional;

import lombok.ToString;

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
	
	public static Builder builderOf(String name) {
		return new Builder(name);
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
	
	public static class Builder {
		private String name;
		private int age;
		private String department;
		
		private Builder(String name) {
			this.name = name;
		}
		
		public Builder setAge(int age) {
			this.age = age;
			return this;
		}
		
		public Builder setDepartment(String department) {
			this.department = department;
			return this;
		}
		
		public Employee build() {
			return new Employee(this);
		}
	}
}
