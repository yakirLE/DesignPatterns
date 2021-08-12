package com.yakir.design.patterns.strategy.functional;

import java.math.BigDecimal;
import java.util.function.Function;

import com.yakir.design.patterns.strategy.oldway.Item;

public enum PaymentPlan {

	BASIC(deliveryPriceWithPercentageSurplus("0.025")),
	PREMIUM(deliveryPriceWithPercentageSurplus("0.015")),
	BUSINESS(deliveryPriceWithPercentageSurplus("0.0"))
	;
	
	private static Function<Item, BigDecimal> deliveryPriceWithPercentageSurplus(String percentageSurplus) {
		return item -> item.getPrice().multiply(new BigDecimal(percentageSurplus))
				  					  .add(new BigDecimal("1.0"));
	}
	
	public final Function<Item, BigDecimal> deliveryPrice;
	
	private PaymentPlan(Function<Item, BigDecimal> deliveryPrice) {
		this.deliveryPrice = deliveryPrice;
	}
}
