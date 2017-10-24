package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.Stack;

public abstract class ANode {
	public String mID;
	
	public Stack<ANode> mChildren;
	public Stack<ANode> mAncestors;
	
	public Stack<ANode> getDescendants(Stack<ANode> descendants) {
		descendants.addAll(mChildren);
		for (ANode child : mChildren) 
			child.getDescendants(descendants);
		return descendants;
	}
	
	public ANode() {
		mChildren = new Stack<ANode>();
		mAncestors = new Stack<ANode>();
	}	
	
	public Stack<String> leave( Stack<String> messBuff) {
		for (ANode child : mChildren) 
			child.leave(messBuff);
		mAncestors.clear();
		mChildren.clear();
		String str = mID.concat(" has leaved the tree");
		messBuff.add(str);
		return messBuff;
	}

	@Override
	public String toString() {
		return mID;
	}
}
