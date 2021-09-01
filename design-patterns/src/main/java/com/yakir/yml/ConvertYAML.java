package com.yakir.yml;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Data;

public class ConvertYAML {

	/*
	 * article: https://www.baeldung.com/jackson-yaml 
	 */
	public static void main(String[] args) {
		System.out.println(getYAML());
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();
		try {
			Order order = mapper.readValue(getYAML(), Order.class);
			System.out.println(order.toString());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	private static final String getYAML() {
		return "orderNo: A001\n"
			 + "date: 2019-04-17\n"
			 + "customerName: Customer, Joe\n"
			 + "orderLines:\n"
			 + "    - item: No. 9 Sprockets\n"
			 + "      quantity: 12\n"
			 + "      unitPrice: 1.23\n"
			 + "    - item: Widget (10mm)\n"
			 + "      quantity: 4\n"
			 + "      unitPrice: 3.45";
	}
	
	@Data
	public static class Order {
		private String orderNo;
	    private LocalDate date;
	    private String customerName;
	    private List<OrderLine> orderLines;
	}
	
	@Data
	public static class OrderLine {
	    private String item;
	    private int quantity;
	    private BigDecimal unitPrice;

	    // Constructors, Getters, Setters and toString
	}
}
