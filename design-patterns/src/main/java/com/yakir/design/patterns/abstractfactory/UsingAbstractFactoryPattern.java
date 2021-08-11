package com.yakir.design.patterns.abstractfactory;

public class UsingAbstractFactoryPattern {
	public static void main(String[] args) {
		ShapeFactory shapeFactory = (ShapeFactory)FactoryGenerator.getFactory("shape");
		ColorFactory colorFactory = (ColorFactory)FactoryGenerator.getFactory("color");
		IShape circle = shapeFactory.getItem("circle");
		IShape rectangle = shapeFactory.getItem("rectangle");
		IShape square = shapeFactory.getItem("square");
		IColor red = colorFactory.getItem("red");
		IColor green = colorFactory.getItem("green");
		IColor blue = colorFactory.getItem("blue");
		circle.draw();
		rectangle.draw();
		square.draw();
		red.fill();
		green.fill();
		blue.fill();
	}
}
