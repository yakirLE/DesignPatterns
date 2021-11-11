package com.yakir.binarytree;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node {
	private int num;
	private Node left;
	private Node right;

	public Node(int num) {
		this(num, null, null);
	}
	
	public boolean isLeaf() {
		return left == null && right == null;
	}
	
	public boolean isFull() {
		return left != null && right != null;
	}
	
	@Override
	public String toString() {
		String l = "";
		if(left != null) {
			l = " L" + left.num;
		}
		
		String r = "";
		if(right != null) {
			r = " R" + right.num;
		}
		
		return num + l + r; 
	}
}