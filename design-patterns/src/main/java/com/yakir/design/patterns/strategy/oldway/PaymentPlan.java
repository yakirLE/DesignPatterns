package com.yakir.design.patterns.strategy.oldway;

import java.math.BigDecimal;

public enum PaymentPlan {

	BASIC {
		@Override
		BigDecimal deliveryPriceFor(Item item) {
			return item.getPrice().multiply(new BigDecimal("0.025"))
								  .add(new BigDecimal("1.0"));
		}
	},
	PREMIUM {
		@Override
		BigDecimal deliveryPriceFor(Item item) {
			return item.getPrice().multiply(new BigDecimal("0.015"))
					  .add(new BigDecimal("1.0"));
		}
	},
	BUSINESS {
		@Override
		BigDecimal deliveryPriceFor(Item item) {
			return new BigDecimal("1.0");
		}
	}
	;
	
	abstract BigDecimal deliveryPriceFor(Item item);
}
