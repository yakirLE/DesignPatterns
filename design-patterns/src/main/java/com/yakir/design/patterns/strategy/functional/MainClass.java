package com.yakir.design.patterns.strategy.functional;

import java.math.BigDecimal;

import com.yakir.design.patterns.strategy.oldway.Item;

public class MainClass {

	public static void main(String[] args) {
		Item itm = new Item(1L, new BigDecimal("12.99"));
		System.out.println("Delivery price for Basic is " + PaymentPlan.BASIC.deliveryPrice.apply(itm));
		System.out.println("Delivery price for Premium is " + PaymentPlan.PREMIUM.deliveryPrice.apply(itm));
		System.out.println("Delivery price for Business is " + PaymentPlan.BUSINESS.deliveryPrice.apply(itm));
	}
}
