package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.NoSuchElementException;
import java.util.Stack;

public abstract class ANode {
	public int mRow;
	public String mPath;
	public Stack<ANode> mChildren;
	public Stack<ANode> mAncestors;

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
	}
	
	public ANode(ANode ancestor) {
		if (ancestor == null) 
			throw new NoSuchElementException();
		mChildren = new Stack<ANode>();
		mAncestors = new Stack<ANode>();
		mAncestors.addAll(ancestor.mAncestors);
		mAncestors.push(ancestor);
		mRow = ancestor.mChildren.size();
		ancestor.mChildren.push(this);
		setPath();
	}

	public ANode setPath() {
		mPath = (mAncestors.size() > 1) ? ((MNode) mAncestors.peek()).mPath : "";
		mPath = mPath.concat(Model.getLevelName( getLevel() )).concat( String.valueOf(mRow) );
		for (ANode child : mChildren)
			child.setPath();
		return this;
	}

	public Stack<String> leave(Stack<String> messBuff) {
		for (ANode child : mChildren) 
			child.leave(messBuff);
		mAncestors.clear();
		mChildren.clear();
		messBuff.add(this.toString().concat(" has leaved the tree"));
		return messBuff;
	}
	
	public int getLevel() {
		return mAncestors.size() - 1;
	}
	
	@Override
	public String toString() {
		return mPath;
	}
}
