package com.yakir.design.patterns.builder.functional.standalone;

public class MainClass {

	public static void main(String[] args) {
		Employee employee = Employee.builderOf("yakir")
								   .setAge(32)
								   .setDepartment("Alon 10, Yavne")
								   .build();
		System.out.println(employee);
	}
}
