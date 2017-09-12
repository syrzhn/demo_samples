package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.List;
import java.util.Stack;

/** @author syrzhn */
public class MTree {
	
	public int generation;

	private String mName;
	private int mLevels, mRows;
	public Stack<MNode> firstLevel;
	public int mAllNodes;
	
	public MNode findNode(String pathFind) {
		List<Path> path = new Path(null).parse(pathFind);
		MNode node = null; int n = path.size();
		for (int i = 0; i < n; i++) {
			int row = Integer.valueOf( path.get(i).mRow );
			if (i == 0) 
				node = firstLevel.get(row); 
			else {
				List<MNode> children = node.getChildren();
				node = children.get(row);
			}
			if ( i == n - 1 && node.path.equals(pathFind) ) return node;
		}
		return null;
	}	

	public MTree(final int levels, final int rows) {
		mAllNodes = 0;
		mLevels = levels;
		mRows = rows;
		mName = "All tree";
		
		Stack<MNode> level = new Stack<MNode>();
				firstLevel = new Stack<MNode>();
		for (int j = 0; j < mRows; j++) {
			MNode nodeJ = new MNode(this, j);
			level.push(nodeJ);
			firstLevel.push(nodeJ);
			mAllNodes++;
		}
		Stack<MNode> nodesI = new Stack<MNode>();
		for (int i = 0; i < mLevels - 1; i++) {
			MNode node = null;
			while (!level.isEmpty()) {
				node = level.pop(); // add new level
				for (int j = 0; j < mRows; j++) {
					MNode nodeJ = new MNode(node);
					nodesI.push(nodeJ);
					mAllNodes++;
				}
			}
			level.addAll(nodesI);
			nodesI.clear();
		}
	}
	
	private class Path {
		private final static String levelSymbols = Model.alphabet;//"abcdefghijklmnopqrstuvwxyz";
		private final static String   rowSymbols = "0123456789";
		
		public String mLevel;
		public String   mRow;
		public Path(String path) {
			if (path != null) {
				mLevel = String.valueOf( path.charAt(0) );
				if (levelSymbols.indexOf(mLevel) < 0) throw new RuntimeException("Illegal symbol - \"".concat( mLevel ).concat( "\" in identifier of tree node!" ));
				mRow = String.valueOf( path.charAt(1) );
				if (rowSymbols.indexOf(mRow) < 0) throw new RuntimeException("Illegal symbol - \"".concat( mRow ).concat( "\" in identifier of tree node!" ));
			}
		}
		public List<Path> parse(String path) {
			List<Path> ret = new Stack<Path>();
			for (int i = 0; i < path.length() - 1; i += 2) {
				ret.add( new Path(path.substring(i, i + 2)) );
			}
			return ret; 
		}
	}
	
	public String[] disposeChild(String path) {
		MNode node = findNode(path);
		if (node == null) throw new RuntimeException("Can't find the node by identifier - \"".concat(path).concat("\"!"));
		disposeNode(node);
		return Model.messBuff.toArray(new String[Model.messBuff.size()]);
	}
	
	private void disposeNode(MNode node) {
		setBrothersPaths(node);
		node.leave();
	}
	
	private void setBrothersPaths(MNode node) {
		List<MNode> brothers = null;
		if (node.getLevel() > 0) {
			MNode parent = node.mAncestors.peek();
			brothers = parent.mChildren;
		}
		else {
			brothers = node.mTree.getFirstLevel();			
		}
		int nodeRow = node.mRow;
		for (int i = nodeRow + 1; i < brothers.size(); i++) {
			MNode n = brothers.get(i);
			--n.mRow;
			n.setPath();
		}
		brothers.remove(nodeRow);
	}

	public MNode addChild(String path) {
		MNode node = findNode(path);
		if (node == null) return null;
		MNode newNode = new MNode(node);
		newNode.mTree = this;
		Model.messBuff.add( node.addChild(newNode) );
		mAllNodes++;
		return newNode;
	}
	
	public List<MNode> getFirstLevel() {
		return firstLevel;
	}

	@Override
	public String toString() {
		return mName;
	}
}