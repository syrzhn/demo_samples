package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.NoSuchElementException;
import java.util.Stack;

public abstract class ANode {
	public Stack<ANode> mChildren;
	public Stack<ANode> mAncestors;
	public String mID;

	public Stack<ANode> getDescendants(Stack<ANode> descendants) {
		if (descendants == null) 
			descendants = new Stack<ANode>();		
		descendants.addAll(mChildren);
		for (ANode child : mChildren) 
			child.getDescendants(descendants);
		return descendants;
	}
	
	public ANode() {
		mChildren = new Stack<ANode>();
		mAncestors = new Stack<ANode>();
		mID ="ANode";		
	}
	
	public ANode(ANode ancestor) {
		if (ancestor == null) 
			throw new NoSuchElementException();
		mChildren = new Stack<ANode>();
		mAncestors = new Stack<ANode>();
		mAncestors.addAll(ancestor.mAncestors);
		mAncestors.push(ancestor);
		ancestor.mChildren.push(this);
		mID ="ANode";		
	}

	@Override
	public String toString() {
		return mID;
	}
}
