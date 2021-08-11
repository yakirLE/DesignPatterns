package com.yakir.design.patterns.builder.functional.standalone;

public class MainClass {

	public static void main(String[] args) {
		Employee.Builder builder = new Employee.Builder();
		Employee employee = builder.setName("yakir")
								   .setAge(32)
								   .setDepartment("Alon 10, Yavne")
								   .build();
		System.out.println(employee);
	}
}
