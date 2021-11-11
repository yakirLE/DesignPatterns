package com.yakir.binarytree;

public class BinaryTree {
	private Node root;
	
	public void create() {
		root = new Node(1);
		root.setLeft(new Node(2));
		root.setRight(new Node(3));
		root.getLeft().setLeft(new Node(4));
		root.getLeft().setRight(new Node(5));
		root.getRight().setRight(new Node(6));
		root.getRight().getRight().setRight(new Node(7));
	}
	
	public void printlnFirst() {
		printRec(root, "F");
		System.out.println();
	}
	
	public void printlnMiddle() {
		printRec(root, "M");
		System.out.println();
	}
	
	public void printlnLast() {
		printRec(root, "L");
		System.out.println();
	}
	
	private void printRec(Node node, String position) {
		if(node == null) {
			return;
		}
		
		if("F".equals(position)) 
			System.out.print(node.getNum() + " ");
		
		printRec(node.getLeft(), position);
		
		if("M".equals(position)) 
			System.out.print(node.getNum() + " ");
		
		printRec(node.getRight(), position);
		
		if("L".equals(position)) 
			System.out.print(node.getNum() + " ");
	}
	
	public void mirror() {
		root = mirrorRec(root);
	}
	
	private Node mirrorRec(Node node) {
		if(node == null)
			return node;
		
		Node left = mirrorRec(node.getLeft());
		Node right = mirrorRec(node.getRight());
		
		node.setLeft(right);
		node.setRight(left);
		
		return node;
	}
	
	public void convertToComplete() {
		root = convertToCompleteRec(root);
	}
	
	private Node convertToCompleteRec(Node node) {
		if(node == null)
			return null;
		
		node.setLeft(convertToCompleteRec(node.getLeft()));
		node.setRight(convertToCompleteRec(node.getRight()));
		
		if(node.isLeaf() || node.isFull()) {
			return node;
		}
		
		return node.getLeft() == null ? node.getRight() : node.getLeft();	
	}
	
	public static void main(String[] args) {
		BinaryTree binaryTree = new BinaryTree();
		binaryTree.create();
		binaryTree.printlnFirst();
		binaryTree.printlnMiddle();
		binaryTree.printlnLast();
		binaryTree.mirror();
		
		System.out.println();
		binaryTree.printlnFirst();
		binaryTree.printlnMiddle();
		binaryTree.printlnLast();
		
		System.out.println();
		binaryTree.convertToComplete();
		binaryTree.printlnFirst();
		binaryTree.printlnMiddle();
		binaryTree.printlnLast();
	}
}