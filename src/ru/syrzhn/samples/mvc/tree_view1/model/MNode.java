package ru.syrzhn.samples.mvc.tree_view1.model;

/** @author syrzhn */
public class MNode extends ANode implements Comparable<MNode>, Cloneable {

	public String mPath;
	public Object mData;
	public int mRow;
	
	public String leave() {
		for (ANode child : mChildren) 
			((MNode)child).leave();
		mTree.mAllNodesCount--;
		mAncestors.clear();
		mChildren.clear();
		String str = mID.concat(" has leaved the tree");
		Model.messBuff.add(str);
		return str;
	}
	
	public MNode setPath() {
		if (mAncestors.size() > 1) {
			mPath = ((MNode) mAncestors.peek()).mPath;
		}
		else
			mPath = "";
		mPath = mPath.concat(Model.getLevelName( getLevel() )).concat( String.valueOf(mRow) );
		for (ANode child : mChildren)
			((MNode) child).setPath();
		return this;
	}
	
	private int getLevel() {
		return mAncestors.size() - 1;
	}
	
	public MNode(ANode ancestor) {
		super();
		if (ancestor == null) return;
		mAncestors.addAll(ancestor.mAncestors);
		mAncestors.push(ancestor);
		mRow = ancestor.mChildren.size();
		mID = setPath().mPath.concat(" - ").concat(Model.currentTime());
		ancestor.mChildren.push(this);
		mTree = (MTree) mAncestors.firstElement(); 
		mTree.mAllNodesCount++;
	}

	private MTree mTree;

	@Override
	public int compareTo(MNode arg0) {
		if (this.mPath.equals(arg0.mPath))
			return 0;
		else {
			int l1 = this.getLevel(), l2 = arg0.getLevel(),
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
	public MNode clone() throws CloneNotSupportedException{
		MNode node = (MNode)super.clone();
		node.mRow = mRow + 1;
		node.mID = node.setPath().mPath.concat(" - ").concat(Model.currentTime());
		return node;
	}
}