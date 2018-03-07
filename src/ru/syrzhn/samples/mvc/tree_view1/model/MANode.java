package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.NoSuchElementException;
import java.util.Stack;

public abstract class MANode {
	public int mRow;
	public String mPath, mType = "Abstract node";
	public Stack<MANode> mChildren, mAncestors;

	public Stack<MANode> getDescendants(Stack<MANode> descendants) {
		if (descendants == null) 
			descendants = new Stack<MANode>();		
		descendants.addAll(mChildren);
		for (MANode child : mChildren) 
			child.getDescendants(descendants);
		return descendants;
	}
	
	public MANode() {
		mChildren = new Stack<MANode>();
		mAncestors = new Stack<MANode>();
	}
	
	public MANode(MANode ancestor) {
		if (ancestor == null) 
			throw new NoSuchElementException();
		mChildren = new Stack<MANode>();
		mAncestors = new Stack<MANode>();
		mAncestors.addAll(ancestor.mAncestors);
		mAncestors.push(ancestor);
		mRow = ancestor.mChildren.size();
		ancestor.mChildren.push(this);
		setPath();
	}

	public MANode setPath() {
		mPath = (mAncestors.size() > 1) ? ((MXMLNode) mAncestors.peek()).mPath : "";
		mPath = mPath.concat(Model.getLevelName( getLevel() )).concat( String.valueOf(mRow) );
		for (MANode child : mChildren)
			child.setPath();
		return this;
	}

	public Stack<String> leave(Stack<String> messBuff) {
		for (MANode child : mChildren) 
			child.leave(messBuff);
		mAncestors.clear();
		mChildren.clear();
		messBuff.add(this.toString().concat(" has leaved the tree"));
		return messBuff;
	}
	
	public int getLevel() {
		return mAncestors.size() - 1;
	}
	
	public Stack<MANode> getYoungerBrothers() {
		Stack<MANode> brothers = mAncestors.peek().mChildren;
		Stack<MANode> youngerBrothers = new Stack<MANode>();
		for (int i = mRow + 1; i < brothers.size(); i++) {
			MANode n = brothers.get(i);
			youngerBrothers.push(n);
		}
		return youngerBrothers;
	}
	
	public Stack<MANode> getDependents(Stack<MANode> dependents) {
		if (dependents == null) dependents = new Stack<MANode>();		
		Stack<MANode> youngBrothers = getYoungerBrothers();
		dependents.addAll(youngBrothers);
		for (MANode node : youngBrothers)
			dependents.addAll(node.getDescendants(dependents));
		return dependents;
	}

	@Override
	public String toString() {
		return mPath;
	}
}
