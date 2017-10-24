package ru.syrzhn.samples.mvc.tree_view1.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** @author syrzhn */
public class MTree extends ANode {
	
	public int mAllNodesCount;
	
	public MTree(final String fileName) {
		super();
		File xmlFile = new File(fileName);
		mID = xmlFile.getName();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setIgnoringElementContentWhitespace(true);

		Document document = null;
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			document = builder.parse(fileName);
		} catch (ParserConfigurationException | SAXException | IOException ex) {
			ex.printStackTrace();
		}
		Node rootXML = document.getDocumentElement();
		rootXML.normalize();
		unGordius((Node) document, this, "");
	}
	
	private void unGordius(Node nodeXML, ANode nodeParent, String indent) {
		String tagName = "";
		ANode nodeTree = nodeParent;
        switch(nodeXML.getNodeType()) {
        case Node.CDATA_SECTION_NODE:
            System.out.println(indent + "CDATA_SECTION_NODE");
            break;
        case Node.COMMENT_NODE:
            System.out.println(indent + "COMMENT_NODE");
            break;
        case Node.DOCUMENT_FRAGMENT_NODE:
            System.out.println(indent + "DOCUMENT_FRAGMENT_NODE");
            break;
        case Node.DOCUMENT_NODE:
            System.out.println(indent + "DOCUMENT_NODE");
            break;
        case Node.DOCUMENT_TYPE_NODE:
        	tagName = ((Document) nodeXML).getDocumentElement().getNodeName();
            System.out.println(indent.concat(tagName).concat(" type=DOCUMENT_TYPE_NODE"));
            break;
        case Node.ELEMENT_NODE:
			Element elementXML = (Element) nodeXML;
			nodeTree = new MNode(nodeParent, elementXML);
			tagName  = elementXML.getNodeName(); 
			nodeTree.mID = tagName;
			mAllNodesCount++;
            System.out.println(indent.concat(tagName).concat(" type=ELEMENT_NODE"));
            break;
        case Node.ENTITY_NODE:
            System.out.println(indent + "ENTITY_NODE");
            break;
        case Node.ENTITY_REFERENCE_NODE:
            System.out.println(indent + "ENTITY_REFERENCE_NODE");
            break;
        case Node.NOTATION_NODE:
            System.out.println(indent + "NOTATION_NODE");
            break;
        case Node.PROCESSING_INSTRUCTION_NODE:
            System.out.println(indent + "PROCESSING_INSTRUCTION_NODE");
            break;
        case Node.TEXT_NODE:
            System.out.println(indent + "TEXT_NODE");
            break;
        default:
            System.out.println(indent + "Unknown node");
            break;
        }
        NodeList xmlNodesList = nodeXML.getChildNodes();
        for(int i = 0; i < xmlNodesList.getLength(); i++)
            unGordius(xmlNodesList.item(i), nodeTree, indent + "\t");
    }

	public MTree(final int levels, final int rows) {
		super();
		mID = "testTree";
		Stack<MNode> level = new Stack<MNode>(),
				nodesI = new Stack<MNode>();
		for (int i = 0; i < rows; i++) {
			level.push(new MNode(this, "node"));
			mAllNodesCount++;
		}
		for (int l = 1; l < levels; l++) {
			while (!level.isEmpty()) {
				// Size of each new level is equivalent to
				// l-th member of geometric progression
				// where denominator and first member are 
				// equivalent rows count.
				// L(l) = L1*(rows^(l-1)) & L1 = rows => L(l) = rows^l;
				MNode node = level.pop(); // add new level
				for (int i = 0; i < rows; i++) {
					nodesI.push(new MNode(node, "none"));
					mAllNodesCount++;
				}
			}
			level.addAll(nodesI);
			nodesI.clear();
		}
	}	
	
	public MNode findNodeByPath(String pathToFind) {
		class Path {
			private final static String levelSymbols = Model.ALPHABET;//"abcdefghijklmnopqrstuvwxyz";
			private final static String   rowSymbols = "0123456789";
			
			public String mLevel;
			public int      mRow;
			public Stack<Path> mPaths;
			public Path() {
				mPaths = new Stack<Path>();
			}
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
		MNode node = null; int n = path.size();
		for (int i = 0; i < n; i++) {
			int row = path.get(i).mRow;
			List<ANode> children = null;
			if (i == 0)
				children = mChildren;
			else 
				children = node.mChildren;
			if (row > -1 && row < children.size())
				node = (MNode) children.get(row);
			else
				return null;
			if ( i == n - 1 && node.mPath.equals(pathToFind) ) return node;
		}
		return null;
	}	

	public void disposeNode(MNode node) {
		List<ANode> brothers = null;
		brothers = node.mAncestors.peek().mChildren;
		int nodeRow = node.mRow;

		node.leave(Model.messBuff);
		mAllNodesCount = mAllNodesCount - Model.messBuff.size();
		for (int i = nodeRow + 1; i < brothers.size(); i++) {
			MNode n = (MNode) brothers.get(i);
			--n.mRow;
			n.setPath();
		}
		brothers.remove(nodeRow);
	}
	
	public Stack<ANode> getDescendants(MNode node) {
		Stack<ANode> descendants = new Stack<ANode>();
		return node.getDescendants(descendants);
	}
	
	public MNode addNode(MNode ancestor) {
		MNode newNode = new MNode(ancestor, ancestor.mType);
		Model.messBuff.add( newNode.mID.concat(" has appeared in the tree") );
		mAllNodesCount++;
		return newNode;
	}
}