package com.yakir.design.patterns.abstractfactory;

public class FactoryGenerator {
	public static IFactory<?> getFactory(String choice) {
		if(choice.equalsIgnoreCase("shape"))
			return new ShapeFactory();
		else if(choice.equalsIgnoreCase("color"))
			return new ColorFactory();
		
		return null;
	}
}
