package com.yakir.design.patterns.abstractfactory;

public class Rectangle implements IShape {

	@Override
	public void draw() {
		System.out.println("Drawing rectangle.");
	}

}
