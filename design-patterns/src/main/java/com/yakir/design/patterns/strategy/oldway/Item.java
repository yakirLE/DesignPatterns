package com.yakir.design.patterns.strategy.oldway;

import java.math.BigDecimal;

public class Item {

	private long id;
	private BigDecimal price;
	
	public Item(long id, BigDecimal price) {
		super();
		this.id = id;
		this.price = price;
	}

	public long getId() {
		return id;
	}

	public BigDecimal getPrice() {
		return price;
	}
}
