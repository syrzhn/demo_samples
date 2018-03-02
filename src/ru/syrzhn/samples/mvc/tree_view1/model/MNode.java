package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.Stack;

/** @author syrzhn */
public class MNode extends ANode implements Comparable<MNode>, Cloneable {

	public String mID;
	public String mType;
	public Object mData;
	public Object mState;
	
	public Stack<ANode> getDependents(Stack<ANode> dependents) {
		if (dependents == null) 
			dependents = new Stack<ANode>();		
		Stack<ANode> youngBrothers = getYoungBrothers();
		dependents.addAll(youngBrothers);
		for (ANode node : youngBrothers) {
			dependents.addAll(node.getDescendants(dependents));
		}
		return dependents;
	}

	public Stack<ANode> getYoungBrothers() {
		Stack<ANode> brothers = mAncestors.peek().mChildren;
		Stack<ANode> youngBrothers = new Stack<ANode>();
		for (int i = mRow + 1; i < brothers.size(); i++) {
			ANode n = brothers.get(i);
			youngBrothers.push(n);
		}
		return youngBrothers;
	}
	
	public MNode(ANode ancestor, Object data) {
		super(ancestor);
		mID = mPath.concat(" - ").concat(Model.currentTime());
		mData = data;
		mType = data.getClass().getSimpleName();
	}

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
	public String toString() {
		return mID;
	}
	
	@Override
	public MNode clone() throws CloneNotSupportedException{
		MNode node = (MNode) super.clone();
		node.mRow = mRow + 1;
		node.mID = node.setPath().mPath.concat(" - ").concat(Model.currentTime());
		return node;
	}
}