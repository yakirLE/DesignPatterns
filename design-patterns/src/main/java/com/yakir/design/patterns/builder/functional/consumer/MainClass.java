package com.yakir.design.patterns.builder.functional.consumer;

public class MainClass {

	public static void main(String[] args) {
		Employee employee = Employee.builderOf("yakir")
									.with(builder -> {
										builder.age = 32;
										builder.department = "Alon 10, Yavne";
									})
								   .build();
		System.out.println(employee);
	}
}
