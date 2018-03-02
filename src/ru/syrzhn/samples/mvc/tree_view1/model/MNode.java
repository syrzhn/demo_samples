package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class MNode extends ANode implements Comparable<MNode> {

	public Map<String, Object> mData;
	
	public MNode(ANode nodeParent) { super(nodeParent); }

	public Stack<ANode> getDependents(Stack<ANode> dependents) {
		if (dependents == null) dependents = new Stack<ANode>();		
		Stack<ANode> youngBrothers = getYoungerBrothers();
		dependents.addAll(youngBrothers);
		for (ANode node : youngBrothers)
			dependents.addAll(node.getDescendants(dependents));
		return dependents;
	}

	public Stack<ANode> getYoungerBrothers() {
		Stack<ANode> brothers = mAncestors.peek().mChildren;
		Stack<ANode> youngerBrothers = new Stack<ANode>();
		for (int i = mRow + 1; i < brothers.size(); i++) {
			ANode n = brothers.get(i);
			youngerBrothers.push(n);
		}
		return youngerBrothers;
	}
	
	public ANode putData(String key, Object data) {
		if (mData == null) 
			mData = new HashMap<>();
		mData.put(key, data);
		return this;
	}
	
	public Object getData(String key) {
		if (mData == null) return mPath;
		if (mData.containsKey(key))
			return (String) mData.get(key);
		return "";
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
		if (mData == null) return mPath;
		if (mData.containsKey("ID"))
			return (String) mData.get("ID").toString();
		return mData.keySet().toString();
	}
}