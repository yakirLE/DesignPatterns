package com.yakir.functional.programming.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConsumerExamples {

	/*
	 * article: https://www.geeksforgeeks.org/java-8-consumer-interface-in-java-with-examples/
	 */
	public static void main(String[] args) {
		Consumer<Integer> display = a -> System.out.println(a);
		display.accept(10);
		
		List<Integer> lst = new ArrayList<>();
		lst.add(2);
		lst.add(1);
		lst.add(3);
		
		Consumer<List<Integer> > multiplyListValuesBy2 = list -> {
            for (int i = 0; i < list.size(); i++)
                list.set(i, 2 * list.get(i));
        };
        
        Consumer<List<Integer>> printList = list -> {
        	System.out.println("======= print ========");
        	list.forEach(a -> System.out.print(a + " "));
        	System.out.println("\n======================\n");
        };
        
        // modify and print list in 2 steps
        multiplyListValuesBy2.accept(lst);
        printList.accept(lst);
        
        // modify and print list in one steps
        multiplyListValuesBy2.andThen(printList).accept(lst);
        
	}
}
