package ru.syrzhn.samples.mvc.tree_view1.model;

import java.nio.charset.UnsupportedCharsetException;
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
	
	public MNode findNodeByPath(String pathToFind) {
		class Path {
			private final static String levelSymbols = Model.alphabet;//"abcdefghijklmnopqrstuvwxyz";
			private final static String   rowSymbols = "0123456789";
			
			public String mLevel;
			public int      mRow;
			public Stack<Path> mPath;
			public Path() {
				mPath = new Stack<Path>();
			}
			public Path(String path, Stack<Path> passes) {
				mLevel = path.substring(0, 1);
				if (levelSymbols.indexOf(mLevel) < 0) throw new UnsupportedCharsetException("Illegal symbol - \"".concat(mLevel).concat( "\" in path of tree node!" ));
				String s = path.substring(1, path.length());
				try {
					mRow = Integer.valueOf(s);
				} catch (NumberFormatException e) {
					throw new NumberFormatException("Illegal symbol - \"".concat(s).concat( "\" in path of tree node!" ));
				}
				if (passes != null)	mPath = passes;
			}
			public Stack<Path> parse(String path) {
				if (path == null || path.length() == 0) return mPath;
				int i = 1;
				for (i = 1; i < path.length(); i++) { 
					char c = path.charAt(i); 
					if (rowSymbols.indexOf(c) < 0) break;
				}
				String p = path.substring(0, i);
				String rest = path.substring(i, path.length());
				mPath.push(new Path(p, mPath));
				parse(rest);
				return mPath; 
			}
		}
		
		List<Path> path = new Path().parse(pathToFind);
		MNode node = null; int n = path.size();
		for (int i = 0; i < n; i++) {
			int row = path.get(i).mRow;
			if (i == 0) 
				node = mChildren.get(row); 
			else {
				List<MNode> children = node.getChildren();
				node = children.get(row);
			}
			if ( i == n - 1 && node.mPath.equals(pathToFind) ) return node;
		}
		return null;
	}	

	public String[] disposeChild(MNode node) {
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
	
	public MNode addNode(MNode ancestor) {
		MNode newNode = new MNode(ancestor);
		newNode.mTree = this;
		Model.messBuff.add( newNode.mID.concat(" has appeared in the tree") );
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