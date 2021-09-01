package com.yakir.design.patterns.abstractfactory;

public interface IFactory<T> {
	public T getItem(String itemType);
}
