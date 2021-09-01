package com.yakir.design.patterns.command;

public class Stock { // acts as request
	
	private String name;
	private int quantity;
	
	public Stock(String name, int quantity) {
		this.name = name;
		this.quantity = quantity;
	}

	public void buy() {
		System.out.println("Buying " + toString());
	}
	
	public void sell() {
		System.out.println("Selling " + toString());
	}
	
	@Override
	public String toString() {
		return "Stock [name=" + name + ", quantity=" + quantity + "]";
	}
	
	
}
