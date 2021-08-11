package com.yakir.design.patterns.abstractfactory;

public class Circle implements IShape {

	@Override
	public void draw() {
		System.out.println("Drawing circle.");
	}

}
