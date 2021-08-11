package com.yakir.design.patterns.abstractfactory;

public class Red implements IColor {

	@Override
	public void fill() {
		System.out.println("Painting red");
	}

}
