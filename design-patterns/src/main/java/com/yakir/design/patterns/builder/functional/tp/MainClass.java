package com.yakir.design.patterns.builder.functional.tp;

import java.util.Arrays;

public class MainClass {

	public static void main(String[] args) {
		Employee.Builder builder = new Employee.Builder();
//		long salaryInNIS = 10000L;
		Employee employee = builder.name("yakir")
								   .age(32)
//								   .department("bla")
								   .nickname("pita")
//								   .mapSalaryInUSD(sal -> salaryInNIS / 3.4)
								   .salaryInUSD(2500)
								   .email("mail@gmail.com")
								   .addNums(1)
								   .addNums(2, 3)
								   .addAllNums(Arrays.asList(4, 5))
								   .putKeyValueMap("key1", "val1")
								   .putKeyValueMap("key2", "val2")
								   .build();
		System.out.println(employee);
//		System.out.println(employee.salaryInUSD().get());
	}
	
}
