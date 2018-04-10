package ru.syrzhn.samples.mvc.tree_view1.model;

import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Stack;

/** @author syrzhn */
public class MTree extends MANode {
	
	public int mAllNodesCount;
	public volatile boolean isBusy;
	
	public MTree() { mPath = "theTree";	}
	/**test constructor is for performance evaluation and other*/
	public MTree(final int levels, final int rows) {
		mPath = "tesTree"; isBusy = true;
		Stack<MXmlNode> level = new Stack<MXmlNode>(),
				nodesI = new Stack<MXmlNode>();
		for (int i = 0; i < rows; i++) {
			MXmlNode newNode = new MXmlNode(this);
			newNode.putData("xmlNodeName", newNode.mPath);
			newNode.putData("xmlNodeValue", newNode.mPath.concat(" - ").concat(Model.currentTime()));
			newNode.putData("xmlNodeType", "text");
			level.push(newNode);
			mAllNodesCount++;
		}
		for (int l = 1; l < levels; l++) {
			while (!level.isEmpty()) {
				// Size of each new level is equivalent to
				// l-th member of geometric progression
				// where denominator and first member are 
				// equivalent rows count.
				// L(l) = L1*(rows^(l-1)) & L1 = rows => L(l) = rows^l;
				MXmlNode node = level.pop(); // add new level
				for (int i = 0; i < rows; i++) {
					MXmlNode newNode = new MXmlNode(node);
					newNode.putData("xmlNodeName", newNode.mPath);
					newNode.putData("xmlNodeValue", newNode.mPath.concat(" - ").concat(Model.currentTime()));
					newNode.putData("xmlNodeType", "text");
					nodesI.push(newNode);
					mAllNodesCount++;
				}
			}
			level.addAll(nodesI);
			nodesI.clear();
		}
		isBusy = false;
	}	
	
	public MANode findNodeByPath(String pathToFind) {
		class Path {
			private final static String levelSymbols = Model.ALPHABET;//"abcdefghijklmnopqrstuvwxyz";
			private final static String   rowSymbols = "0123456789";
			
			public String mLevel;
			public int      mRow;
			public Stack<Path> mPaths;
			public Path() {	mPaths = new Stack<Path>(); }
			public Path(String path, Stack<Path> paths) {
				mLevel = path.substring(0, 1);
				if (levelSymbols.indexOf(mLevel) < 0) throw new UnsupportedCharsetException("Illegal symbol - \"".concat(mLevel).concat( "\" in path of tree node!" ));
				String s = path.substring(1, path.length());
				try {
					mRow = Integer.valueOf(s);
				} catch (NumberFormatException e) {
					throw new NumberFormatException("Illegal symbol - \"".concat(s).concat( "\" in path of tree node!" ));
				}
				if (paths != null)	mPaths = paths;
			}
			public Stack<Path> parse(String path) {
				if (path == null || path.length() == 0) return mPaths;
				int i = 1;
				for (i = 1; i < path.length(); i++) { 
					char c = path.charAt(i); 
					if (rowSymbols.indexOf(c) < 0) break;
				}
				String p = path.substring(0, i);
				String rest = path.substring(i, path.length());
				mPaths.push(new Path(p, mPaths));
				parse(rest);
				return mPaths; 
			}
			@Override
			public String toString() {
				return mLevel.concat( String.valueOf(mRow) );
			}
		}
		
		List<Path> path = new Path().parse(pathToFind);
		MXmlNode node = null; int n = path.size();
		for (int i = 0; i < n; i++) {
			int row = path.get(i).mRow;
			List<MANode> children = null;
			if (i == 0)
				children = mChildren;
			else 
				children = node.mChildren;
			if (row > -1 && row < children.size())
				node = (MXmlNode) children.get(row);
			else
				return null;
			if ( i == n - 1 && node.mPath.equals(pathToFind) ) return node;
		}
		return null;
	}	

	public Stack<MANode> disposeNode(MANode node) {
		Stack<MANode> brothers = null, dependents = null;
		brothers = node.mAncestors.peek().mChildren;
		int nodeRow = node.mRow;
		dependents = node.getDependents(dependents);
		node.leave(Model.messBuff);
		mAllNodesCount = mAllNodesCount - Model.messBuff.size();
		for (int i = nodeRow + 1; i < brothers.size(); i++) {
			MANode n = brothers.get(i);
			--n.mRow;
			n.setPath();
		}
		brothers.remove(nodeRow);
		return dependents;
	}
	
	public MANode addNode(MANode ancestor) {
		MXmlNode newNode = new MXmlNode(ancestor);
		Model.messBuff.add( newNode.toString().concat(" has appeared in the tree") );
		mAllNodesCount++;
		return newNode;
	}
}