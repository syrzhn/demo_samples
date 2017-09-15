package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.List;
import java.util.Stack;

/** @author syrzhn */
public class MTree {
	
	public String mID = "";
	public String mPath = "";
	public Stack<MNode> mChildren;
	public Stack<MNode> mAncestors;
	public int mAllNodesCount;
	
	public int generation;

	public MTree(final int levels, final int rows) {
		mChildren = new Stack<MNode>();
		mAncestors = new Stack<MNode>();
		MNode root = new MNode(this, -1);
		mAncestors.push(root);
		mAllNodesCount = 0;
		mID = "All tree";
		
		Stack<MNode> level = new Stack<MNode>();
		for (int j = 0; j < rows; j++) {
			MNode nodeJ = new MNode(this, j);
			level.push(nodeJ);
			mChildren.push(nodeJ);
			mAllNodesCount++;
		}
		Stack<MNode> nodesI = new Stack<MNode>();
		for (int i = 0; i < levels - 1; i++) {
			MNode node = null;
			while (!level.isEmpty()) {
				node = level.pop(); // add new level
				for (int j = 0; j < rows; j++) {
					MNode nodeJ = new MNode(node);
					nodesI.push(nodeJ);
					mAllNodesCount++;
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
	
	public MNode findNode(String pathFind) {
		List<Path> path = new Path(null).parse(pathFind);
		MNode node = null; int n = path.size();
		for (int i = 0; i < n; i++) {
			int row = Integer.valueOf( path.get(i).mRow );
			if (i == 0) 
				node = mChildren.get(row); 
			else {
				List<MNode> children = node.getChildren();
				node = children.get(row);
			}
			if ( i == n - 1 && node.mPath.equals(pathFind) ) return node;
		}
		return null;
	}	

	public String[] disposeChild(String path) {
		MNode node = findNode(path);
		if (node == null) throw new RuntimeException("Can't find the node by identifier - \"".concat(path).concat("\"!"));
		disposeNode(node);
		return Model.messBuff.toArray(new String[Model.messBuff.size()]);
	}
	
	private void disposeNode(MNode node) {
		List<MNode> brothers = null;
		if (node.getLevel() > 0) {
			brothers = node.mAncestors.peek().mChildren;
		}
		else {
			brothers = node.mTree.mChildren;			
		}
		int nodeRow = node.mRow;

		node.leave();

		for (int i = nodeRow; i < brothers.size(); i++) {
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
		mAllNodesCount++;
		return newNode;
	}
	
	public List<MNode> getFirstLevel() {
		return mChildren;
	}

	@Override
	public String toString() {
		return mID;
	}
}