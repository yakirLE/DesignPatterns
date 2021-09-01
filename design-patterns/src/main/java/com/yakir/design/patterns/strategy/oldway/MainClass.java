package com.yakir.design.patterns.strategy.oldway;

import java.math.BigDecimal;

public class MainClass {

	public static void main(String[] args) {
		Item itm = new Item(1L, new BigDecimal("12.99"));
		System.out.println("Delivery price for Basic is " + PaymentPlan.BASIC.deliveryPriceFor(itm));
		System.out.println("Delivery price for Premium is " + PaymentPlan.PREMIUM.deliveryPriceFor(itm));
		System.out.println("Delivery price for Business is " + PaymentPlan.BUSINESS.deliveryPriceFor(itm));
	}
}
