package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.List;
import java.util.Stack;

public class Model {
	
	final static String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	public static String getLevelName(int level) {
		return String.valueOf( alphabet.charAt(level) );
	}

	public static String getNameLevel(String name) {
		return String.valueOf( alphabet.indexOf(name.charAt(0)) );
	}
	
	public Model(final int levels, final int rows) {
		mTree = new MTree(levels, rows);
	}
	
	public MNode[] getTreeData() {
		MNode arg[] = null;
		List<MNode> level = mTree.getFirstLevel();
		arg = new MNode[level.size()];
		level.toArray(arg);
		return arg;
	}
	
	public MNode[] getTreeData(MNode parent) {
		if (parent == null) return null;
		MNode arg[] = null;
		List<MNode> level = parent.getChildren();
		arg = new MNode[level.size()];
		level.toArray(arg);
		return arg;
	}
	
	static {
		messBuff = new Stack<String>();
	}

	public static List<String> messBuff;

	public MTree mTree;
}