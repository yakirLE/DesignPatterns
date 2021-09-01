package com.yakir.threads.waitnotify;

public class MainClass {

	public static void main(String[] args) {
		// https://www.baeldung.com/java-wait-notify
	    Data data = new Data();
	    Thread sender = new Thread(new Sender(data));
	    Thread receiver = new Thread(new Receiver(data));
	    
	    sender.start();
	    receiver.start();
	}
}
