package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/** @author syrzhn */
public class MTree {

	private String mName;
	public Map<String, MNode> mAllNodes;
	private int mLevels, mRows;
	
	public MNode findNode(String ID) {
		List<IDentifier> path = new IDentifier(null).parse(ID);
		MNode node = null; int n = path.size();
		for (int i = 0; i < n; i++) {
			int row = Integer.valueOf( path.get(i).mRow );
			if (i == 0) 
				node = mAllNodes.get(path.get(i).getID());
			else {
				List<MNode> children = node.getChildren();
				node = children.get(row);
			}
			if (i == n - 1 && node.mID.equals(ID)) return node;
		}
		return null;
	}	

	public MTree(final int levels, final int rows) {
		mAllNodes = new HashMap<String, MNode>();
		mLevels = levels;
		mRows = rows;
		mName = "All tree";
		
		Stack<MNode> level = new Stack<MNode>();
		for (int j = 0; j < mRows; j++) {
			MNode nodeJ = new MNode(null, j);
			nodeJ.parent = this;
			level.push(nodeJ);
			mAllNodes.put(nodeJ.mID, nodeJ);
		}
		Stack<MNode> nodesI = new Stack<MNode>();
		for (int i = 0; i < mLevels - 1; i++) {
			MNode node = null;
			while (!level.isEmpty()) {
				node = level.pop(); // add new level
				for (int j = 0; j < mRows; j++) {
					MNode nodeJ = new MNode(node, j);
					nodesI.push(nodeJ);
					nodeJ.parent = this;
					node.addChild(nodeJ);
					mAllNodes.put(nodeJ.mID, nodeJ);
				}
			}
			level.addAll(nodesI);
			nodesI.clear();
		}
	}
	
	private class IDentifier {
		private final static String levelSymbols = Model.alphabet;//"abcdefghijklmnopqrstuvwxyz";
		private final static String   rowSymbols = "0123456789";
		
		public String mLevel;
		public String   mRow;
		public IDentifier(String ID) {
			if (ID != null) {
				mLevel = String.valueOf( ID.charAt(0) );
				if (levelSymbols.indexOf(mLevel) < 0) throw new RuntimeException("Illegal symbol - \"".concat( mLevel ).concat( "\" in identifier of tree node!" ));
				mRow = String.valueOf( ID.charAt(1) );
				if (rowSymbols.indexOf(mRow) < 0) throw new RuntimeException("Illegal symbol - \"".concat( mRow ).concat( "\" in identifier of tree node!" ));
			}
		}
		public List<IDentifier> parse(String ID) {
			List<IDentifier> ret = new Stack<IDentifier>();
			for (int i = 0; i < ID.length() - 1; i += 2) {
				ret.add( new IDentifier(ID.substring(i, i + 2)) );
			}
			return ret; 
		}
		public String getID() {
			return mLevel.concat(mRow);
		}
	}
	
	public String[] disposeChild(String ID) {
		MNode node = findNode(ID);
		if (node == null) throw new RuntimeException("Can't find the node by identifier - \"".concat(ID).concat("\"!"));
		disposeNode(node);
		return Model.messBuff.toArray(new String[Model.messBuff.size()]);
	}
	
	private String disposeNode(MNode node) {
		if (node.getLevel() > 1) {
			MNode parent = node.mAncestors.peek();
			int nodeRow = node.mRow;
			Stack<MNode> children = node.mAncestors.peek().mChildren;
			children.remove(node.mRow);
			for (int i = nodeRow; i < parent.mChildren.size(); i++) {
				MNode n = parent.mChildren.get(i);
				n.mRow--;
				n.mID = n.getPath();
			}
		}
		return node.leave();
	}

	public MNode addChild(String ID) {
		MNode node = findNode(ID);
		if (node == null) return null;
		MNode newNode = new MNode(node);
		newNode.parent = this;
		Model.messBuff.add( node.addChild(newNode) );
		mAllNodes.put(newNode.mID, newNode);
		return newNode;
	}
	
	public List<MNode> getFirstLevel() {
		if (mRows <= 0) return null;
		List<MNode> firstLevel = new Stack<MNode>();
		for (int i = 0; i < mRows; i++)
			firstLevel.add( mAllNodes.get(Model.getLevelName(0).concat(String.valueOf(i))) );
		return firstLevel;
	}

	@Override
	public String toString() {
		return mName;
	}
}