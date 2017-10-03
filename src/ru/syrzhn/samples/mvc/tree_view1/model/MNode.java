package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.Stack;

/** @author syrzhn */
public class MNode implements Comparable<MNode>, Cloneable {

	public String mID;
	public String mPath;
	public Stack<MNode> mChildren;
	public Stack<MNode> mAncestors;
	public Object mData;
	public int mRow;
	public MTree mTree;
	public int mLevel;
	
	public String leave() {
		for (MNode child : mChildren) {
			child.leave();
		}
		mAncestors.clear();
		mChildren.clear();
		mTree.mAllNodesCount--;
		String str = mID.concat(" has leaved the tree");
		Model.messBuff.add(str);
		return str;
	}
	
	public MNode setPath() {
		if (mAncestors.size() > 0)
			mPath = mAncestors.peek().mPath;
		else
			mPath = "";
		mLevel = mAncestors.size();
		mPath = mPath.concat(Model.getLevelName( mLevel )).concat( String.valueOf(mRow) );
		for (MNode child : mChildren) {
			child.setPath();
		}
		return this;
	}
	
	public MNode(MTree parent, int row) {
		if (parent == null) return;
		mTree = parent;
		mChildren = new Stack<MNode>();
		mAncestors = new Stack<MNode>();
		mRow = row;
		mID = setPath().mPath.concat(" - ").concat(Model.currentTime());
	}

	public MNode(MNode ancestor) {
		if (ancestor == null) return;
		mTree = ancestor.mTree;
		mChildren = new Stack<MNode>();
		mAncestors = new Stack<MNode>();
		mAncestors.addAll(ancestor.mAncestors);
		mAncestors.push(ancestor);
		mRow = ancestor.mChildren.size();
		mID = setPath().mPath.concat(" - ").concat(Model.currentTime());
		ancestor.mChildren.push(this);
	}

	@Override
	public int compareTo(MNode arg0) {
		if (this.mPath.equals(arg0.mPath))
			return 0;
		else {
			int l1 = this.mLevel, l2 = arg0.mLevel,
				l = l1 - l2;
			if (l != 0)
				return l;
			else {
				MNode p1 = this.mAncestors.peek(),
					  p2 = arg0.mAncestors.peek();
				if (p1.equals(p2))
					return this.mRow - arg0.mRow;
				else
					return p1.mRow - p2.mRow;
			}
		}	
	}
	
	@Override
	public String toString() { return mID; }

	@Override
	public MNode clone() throws CloneNotSupportedException{
		MNode node = (MNode)super.clone();
		node.mRow = mRow + 1;
		node.mID = node.setPath().mPath.concat(" - ").concat(Model.currentTime());
		return node;
	}
}