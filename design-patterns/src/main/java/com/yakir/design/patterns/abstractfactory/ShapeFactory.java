package com.yakir.design.patterns.abstractfactory;

public class ShapeFactory implements IFactory<IShape> {
	
	@Override
	public IShape getItem(String shape) {
		if(shape == null)
			return null;
		if(shape.equalsIgnoreCase("circle"))
			return new Circle();
		else if(shape.equalsIgnoreCase("rectangle"))
			return new Rectangle();
		else if(shape.equalsIgnoreCase("square"))
			return new Square();
		
		return null;
	}
}
