package com.yakir.design.patterns.builder;

public abstract class ColdDrink implements IItem {

	@Override
	public IPacking packing() {
		return new Bottle();
	}

}
