package ru.syrzhn.samples.mvc.tree_view1.model;

/** @author syrzhn */
public class MNode extends ANode implements Comparable<MNode>, Cloneable {

	public String mID;
	public String mPath;
	public Object mData;
	public int mRow;
	public MTree mTree;
	public int mLevel;
	
	public String leave() {
		for (ANode child : mChildren) 
			((MNode)child).leave();
		mAncestors.clear();
		mChildren.clear();
		mTree.mAllNodesCount--;
		String str = mID.concat(" has leaved the tree");
		Model.messBuff.add(str);
		return str;
	}
	
	public MNode setPath() {
		if (mAncestors.size() > 0) {
			MNode tmp = (MNode) mAncestors.peek();
			mPath = tmp.mPath;
		}
		else
			mPath = "";
		mLevel = mAncestors.size();
		mPath = mPath.concat(Model.getLevelName( mLevel )).concat( String.valueOf(mRow) );
		for (ANode child : mChildren)
			((MNode) child).setPath();
		return this;
	}
	
	public MNode(MTree ancestor) {
		super();
		if (ancestor == null) return;
		mTree = ancestor;
		mAncestors.addAll(ancestor.mAncestors);
		mRow = ancestor.mChildren.size();
		mID = setPath().mPath.concat(" - ").concat(Model.currentTime());
		ancestor.mChildren.push(this);
	}

	public MNode(MNode ancestor) {
		super();
		if (ancestor == null) return;
		mTree = ancestor.mTree;
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
				MNode p1 = (MNode) this.mAncestors.peek(),
					  p2 = (MNode) arg0.mAncestors.peek();
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