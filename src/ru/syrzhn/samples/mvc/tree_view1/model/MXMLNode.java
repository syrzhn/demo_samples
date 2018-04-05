package ru.syrzhn.samples.mvc.tree_view1.model;

import java.util.HashMap;
import java.util.Map;

public class MXMLNode extends MANode implements Comparable<MXMLNode> {

	private Map<String, Object> mData;
	
	public MXMLNode(MANode nodeParent) { super(nodeParent); }

	public MXMLNode putData(String key, Object data) {
		if (mData == null) 
			mData = new HashMap<>();
		if (data != null)
			mData.put(key, data);
		return this;
	}
	
	public Object getData(String ...key) {
		if (mData == null) return mPath;
		for (int i = 0; i < key.length; i++)
			if (mData.containsKey(key[i]))
				return mData.get(key[i]);
		return "8-0DEADMEAT:-/";//mData.values();
	}
	
	@Override
	public int compareTo(MXMLNode arg0) {
		if (this.mPath.equals(arg0.mPath))
			return 0;
		else {
			int l1 = this.getLevel(), l2 = arg0.getLevel(),
				l = l1 - l2;
			if (l != 0)
				return l;
			else {
				MXMLNode p1 = (MXMLNode) this.mAncestors.peek(),
					  p2 = (MXMLNode) arg0.mAncestors.peek();
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
		if (mData.containsKey("xmlNodeName"))
			return mData.get("xmlNodeName").toString();
		if (mData.containsKey("ID"))
			return mData.get("ID").toString();
		return mData.keySet().toString();
	}
}