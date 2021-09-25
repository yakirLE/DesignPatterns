package com.yakir.sandbox;

import java.io.IOException;

import com.yakir.sandbox.bofa.ExecutionStatsAnalyzer;

public class Sandbox {

	public static void main(String[] args) throws IOException {
		new ExecutionStatsAnalyzer(ExecutionStatsAnalyzer.get2105(), ExecutionStatsAnalyzer.get2100()).execute();
		System.out.println("done");
	}
}
