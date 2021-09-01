package com.yakir.design.patterns.builder;

public class UsingBuilderPattern {

	public static void main(String[] args) {
		MealBuilder mealBuilder = new MealBuilder();
		Meal vegMeal = mealBuilder.prepareVegMeal();
		Meal nonVegMeal = mealBuilder.prepareNonVegMeal();
		
		System.out.println("Vegan Meal:");
		vegMeal.showItems();
		System.out.println("Total Cost: " + vegMeal.getCost());
		System.out.println("\nNon-Vegan Meal:");
		nonVegMeal.showItems();
		System.out.println("Total Cost: " + nonVegMeal.getCost());
	}

}
