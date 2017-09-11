package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.Stack;

/** @author syrzhn */
public class MNode implements Comparable<MNode>, Cloneable {

	public String mID = "";
	public Object mData;
	public Stack<MNode> mChildren;
	public int mRow;
	public Stack<MNode> mAncestors;
	public MTree mTree;
	
	public String leave() {
		for (MNode child : mChildren) {
			child.leave();
		}
		mAncestors.clear();
		mChildren.clear();
		mTree.mAllNodes.remove(path);
		String str = mID.concat(" has leaved the tree");
		Model.messBuff.add(str);
		return str;
	}
	
	public String path;
	public String getPath() {
		return path;
	}
	
	public MNode setPath() {
		for (MNode child : mChildren) {
			child.setPath();
		}
		mTree.mAllNodes.remove(path);
		path = "";
		for (int i = 0; i < mAncestors.size(); i++) {
			MNode ancestor = mAncestors.get(i);
			String level = Model.getLevelName( ancestor.getLevel() ),
					 row = String.valueOf(ancestor.mRow);
			path = path.concat(level).concat(row);
		}
		path = path.concat(Model.getLevelName( getLevel() )).concat( String.valueOf(mRow) );
		mTree.mAllNodes.put(path, this);
		return this;
	}
	
	public MNode(MTree parent, int row) {
		mTree = parent;
		mChildren = new Stack<MNode>();
		mAncestors = new Stack<MNode>();
		if (parent == null) return;
		mRow = row;
		path = Model.getLevelName( getLevel() ).concat( String.valueOf(mRow) );
		mID = path.concat(" -").concat( String.valueOf(mTree.generation) );
	}

	public MNode(MNode ancestor) {
		if (ancestor == null) return;
		mTree = ancestor.mTree;
		mChildren = new Stack<MNode>();
		mAncestors = new Stack<MNode>();
		if (ancestor.mAncestors.size() > 0) 
			mAncestors.addAll(ancestor.mAncestors);
		mAncestors.push(ancestor);
		mID = ancestor.path;
		if (ancestor.mChildren.size() > 0)
			mRow = ancestor.mChildren.peek().mRow + 1;
		else
			mRow = 0;
		mID = mID.concat(Model.getLevelName( getLevel() )).concat( String.valueOf(mRow) ).concat(" -").concat( String.valueOf(mTree.generation) );
		ancestor.mChildren.push(this);
		setPath();
	}

	public Stack<MNode> getChildren() {
		return mChildren;
	}
	
	public String addChild(MNode child) {
		//mChildren.add(child);
		return child.mID.concat(" has appeared in the tree");
	}
	
	@Override
	public int compareTo(MNode arg0) {
		if (this.mID.equals(arg0.mID))
			return 0;
		else {
			int l1 = this.getLevel(), l2 = arg0.getLevel();
			int l = l1 - l2;
			if (l != 0)
				return l;
			else 
				return this.mRow - arg0.mRow;
		}	
	}
	
	public int getLevel() {
		if (mAncestors == null) return 0;
		return mAncestors.size();
	}

	@Override
	public String toString() {
		return mID;
	}

	@Override
	public MNode clone() throws CloneNotSupportedException{
		MNode node = (MNode)super.clone();
		node.mRow = mRow + 1;
		node.mID  = mID.substring(0, mID.length() - 1).concat(String.valueOf(node.mRow));
		return node;
	}
}