package com.yakir.threads.waitnotify;

public class Data {
    private String packet;
    
    // True if receiver should wait
    // False if sender should wait
    private boolean transfer = true;
 
    public synchronized void send(String packet) {
        while (!transfer) {
            try { 
            	System.out.println("S waiting...");
                wait();
                System.out.println("S finished waiting");
                System.out.println();
            } catch (InterruptedException e)  {
                Thread.currentThread().interrupt(); 
                System.out.println("Thread interrupted");
                e.printStackTrace();
            }
        }
        transfer = false;
        
        this.packet = packet;
        notifyAll();
    }
 
    public synchronized String receive() {
        while (transfer) {
            try {
            	System.out.println("R waiting...");
                wait();
                System.out.println("R finished waiting");
                System.out.println();
            } catch (InterruptedException e)  {
                Thread.currentThread().interrupt(); 
                System.out.println("Thread interrupted");
                e.printStackTrace(); 
            }
        }
        transfer = true;

        notifyAll();
        return packet;
    }
}
