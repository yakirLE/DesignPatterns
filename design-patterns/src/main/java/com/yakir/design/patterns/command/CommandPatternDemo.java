package com.yakir.design.patterns.command;

public class CommandPatternDemo {

	/*
	 * A request is wrapped under an object as command and passed to invoker object. 
	 * Invoker object looks for the appropriate object, which can handle this command and passes the command to the corresponding object, which executes the command
	 * 
	 * In this example:
	 * Order = Command
	 * Stock = Request
	 * Broker = Command Invoker
	 * 
	 * The Broker takes and executes the Orders (Commands)
	 * The Request operation is wrapped as a Command
	 */
	
	public static void main(String[] args) {
		Stock stock = new Stock("myStock", 10);
		
		Order buyStockOrder = new BuyStock(stock);
		Order sellStockOrder = new SellStock(stock);
		
		Broker broker = new Broker();
		broker.takeOrder(buyStockOrder);
		broker.takeOrder(sellStockOrder);
		
		broker.placeOrders();
	}
}
