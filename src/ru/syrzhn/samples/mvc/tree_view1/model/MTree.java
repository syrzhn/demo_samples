package ru.syrzhn.samples.mvc.tree_view1.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/** @author syrzhn */
public class MTree extends MANode {
	
	public int mAllNodesCount;
	
	public MTree(final String fileName) {
		File xmlFile = new File(fileName);
		mPath = xmlFile.getName();

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
	
	public MTree (final Document document) {
		unGordius((Node) document, this, "");		
	}
	
	private void unGordius(Node nodeXML, MANode nodeParent, String tab) {
		String nodeName = "", nodeValue = "", nodeType = "";
		MANode nodeTree = nodeParent;
		switch (nodeXML.getNodeType()) {
		case Node.CDATA_SECTION_NODE:
			nodeType = "CDATA_SECTION_NODE";
			break;
		case Node.COMMENT_NODE:
			nodeType = "COMMENT_NODE";
			break;
		case Node.DOCUMENT_FRAGMENT_NODE:
			nodeType = "DOCUMENT_FRAGMENT_NODE";
			break;
		case Node.DOCUMENT_NODE:
			nodeType = "DOCUMENT_NODE";
			break;
		case Node.DOCUMENT_TYPE_NODE:
			nodeType = "DOCUMENT_TYPE_NODE";
			break;
		case Node.ELEMENT_NODE:
			nodeType = "ELEMENT_NODE";
			nodeName  = nodeXML.getNodeName();
			nodeValue = nodeXML.getNodeValue();
			nodeTree  = new MXMLNode(nodeParent)
					.putData("xmlNodeName",  nodeName)
					.putData("xmlNodeValue", nodeValue)
					.putData("xmlNodeType",  nodeType);
			mAllNodesCount++;
			break;
		case Node.ENTITY_NODE:
			nodeType = "ENTITY_NODE";
			break;
		case Node.ENTITY_REFERENCE_NODE:
			nodeType = "ENTITY_REFERENCE_NODE";
			break;
		case Node.NOTATION_NODE:
			nodeType = "NOTATION_NODE";
			break;
		case Node.PROCESSING_INSTRUCTION_NODE:
			nodeType = "PROCESSING_INSTRUCTION_NODE";
			break;
		case Node.TEXT_NODE:
			nodeType = "TEXT_NODE";
			break;
		default:
			nodeType = "Unknown node";
			break;
		}
		System.out.println(tab + nodeName + " " + nodeValue + " " + nodeType);
		if (nodeXML.hasAttributes()) {
			NamedNodeMap attributes = nodeXML.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Attr attrXML = (Attr) attributes.item(i);
				nodeName  = attrXML.getName();
				nodeValue = attrXML.getValue();
				nodeType  = "ATTRIBUTE_NODE";
				new MXMLNode(nodeTree)
						.putData("xmlNodeName",  nodeName)
						.putData("xmlNodeValue", nodeValue)
						.putData("xmlNodeType",  nodeType);
				System.out.println(tab + '\t' + nodeName + " " + nodeValue + " " + nodeType);
				mAllNodesCount++;
			}
		}
		if (!nodeXML.hasChildNodes()) return;
		NodeList xmlNodesList = nodeXML.getChildNodes();
		for (int i = 0; i < xmlNodesList.getLength(); i++)
			unGordius(xmlNodesList.item(i), nodeTree, tab.concat("\t"));
	}

	public MTree() {
		Document doc = XmlUtils.createXmlDocument("TestTree");
        Node root = doc.getDocumentElement();
 
        Element fstEl = doc.createElement("First");
        fstEl.setTextContent("First text content");
        fstEl.setNodeValue("First node value");
        fstEl.setAttribute("ID", "First id");
        fstEl.setAttribute("type", "type text");
        root.appendChild(fstEl);
        
        Element sndEl1 = doc.createElement("SecondOne");
        sndEl1.setTextContent("Second 1 text content");
        sndEl1.setAttribute("ID", "Second 1 id");
        fstEl.appendChild(sndEl1);

        Element sndEl2 = doc.createElement("SecondTwo");
        sndEl2.setAttribute("ID", "Second 2 id");
        sndEl2.setAttribute("type", "type text");
        fstEl.appendChild(sndEl2);

        unGordius((Node) doc, this, "");
        
        XmlUtils.saveDocument(doc);
	}
	
	public MTree(final int levels, final int rows) {
		mPath = "tesTree";
		Stack<MXMLNode> level = new Stack<MXMLNode>(),
				nodesI = new Stack<MXMLNode>();
		for (int i = 0; i < rows; i++) {
			MXMLNode newNode = new MXMLNode(this);
			newNode.putData("ID", newNode.mPath.concat(" - ").concat(Model.currentTime()));
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
				MXMLNode node = level.pop(); // add new level
				for (int i = 0; i < rows; i++) {
					MXMLNode newNode = new MXMLNode(node);
					newNode.putData("ID", newNode.mPath.concat(" - ").concat(Model.currentTime()));
					nodesI.push(newNode);
					mAllNodesCount++;
				}
			}
			level.addAll(nodesI);
			nodesI.clear();
		}
	}	
	
	public MXMLNode findNodeByPath(String pathToFind) {
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
		MXMLNode node = null; int n = path.size();
		for (int i = 0; i < n; i++) {
			int row = path.get(i).mRow;
			List<MANode> children = null;
			if (i == 0)
				children = mChildren;
			else 
				children = node.mChildren;
			if (row > -1 && row < children.size())
				node = (MXMLNode) children.get(row);
			else
				return null;
			if ( i == n - 1 && node.mPath.equals(pathToFind) ) return node;
		}
		return null;
	}	

	public Stack<MANode> disposeNode(MXMLNode node) {
		Stack<MANode> brothers = null, dependents = null;
		brothers = node.mAncestors.peek().mChildren;
		int nodeRow = node.mRow;
		dependents = node.getDependents(dependents);
		node.leave(Model.messBuff);
		mAllNodesCount = mAllNodesCount - Model.messBuff.size();
		for (int i = nodeRow + 1; i < brothers.size(); i++) {
			MXMLNode n = (MXMLNode) brothers.get(i);
			--n.mRow;
			n.setPath();
		}
		brothers.remove(nodeRow);
		return dependents;
	}
	
	public Stack<MANode> getDescendants(MXMLNode node) {
		Stack<MANode> descendants = new Stack<MANode>();
		return node.getDescendants(descendants);
	}
	
	public MXMLNode addNode(MXMLNode ancestor) {
		MXMLNode newNode = new MXMLNode(ancestor);
		Model.messBuff.add( newNode.toString().concat(" has appeared in the tree") );
		mAllNodesCount++;
		return newNode;
	}
}