package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.ArrayList;

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
	
	public MNode[] getTreeData(MNode parent) {
		MNode arg[] = null;
		if (parent == null) {
			arg = new MNode[mTree.getFirstLevel().size()];
			mTree.getFirstLevel().toArray(arg);
		}
		else {
			arg = new MNode[parent.getChildren().size()];
			parent.getChildren().toArray(arg);
		}		
		return arg;
	}
	
	static {
		messBuff = new ArrayList<String>();
	}

	public static ArrayList<String> messBuff;

	public MTree mTree;
}
