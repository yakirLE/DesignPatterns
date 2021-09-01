package com.yakir.design.patterns.abstractfactory;

public class ColorFactory implements IFactory<IColor>{

	@Override
	public IColor getItem(String color) {
		if(color == null)
			return null;
		if(color.equalsIgnoreCase("red"))
			return new Red();
		else if(color.equalsIgnoreCase("green"))
			return new Green();
		else if(color.equalsIgnoreCase("blue"))
			return new Blue();
		
		return null;
	}
}
