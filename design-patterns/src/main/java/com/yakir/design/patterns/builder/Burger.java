package com.yakir.design.patterns.builder;

public abstract class Burger implements IItem {

	@Override
	public IPacking packing() {
		return new Wrapper();
	}

}
