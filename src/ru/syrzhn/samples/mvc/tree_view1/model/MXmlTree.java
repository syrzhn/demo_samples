package ru.syrzhn.samples.mvc.tree_view1.model;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class MXmlTree extends MTree {
	public MXmlTree(Object doc) {
		mPath = "xmlDocument";
		parseXml(doc);
		isLoaded = true;
	}
    public void parseXml(Object doc) {
    	dumpLoop((Node) doc, this, "");
    }

	private void dumpLoop(Node node, MANode treeParent, String shift) {
		switch (node.getNodeType()) {
		case Node.ATTRIBUTE_NODE:              dumpAttributeNode             ((Attr)                  node, treeParent, shift); break;
		case Node.CDATA_SECTION_NODE:          dumpCDATASectionNode          ((CDATASection)          node, treeParent, shift); break;
		case Node.COMMENT_NODE:	               dumpCommentNode               ((Comment)               node, treeParent, shift); break;
		case Node.DOCUMENT_NODE:               dumpDocument                  ((Document)              node, treeParent, shift);	break;
		case Node.DOCUMENT_FRAGMENT_NODE:      dumpDocumentFragment          ((DocumentFragment)      node, treeParent, shift); break;
		case Node.DOCUMENT_TYPE_NODE:          dumpDocumentType              ((DocumentType)          node, treeParent, shift); break;
		case Node.ELEMENT_NODE:	               dumpElement                   ((Element)               node, treeParent, shift); break;
		case Node.ENTITY_NODE:                 dumpEntityNode                ((Entity)                node, treeParent, shift); break;
		case Node.ENTITY_REFERENCE_NODE:       dumpEntityReferenceNode       ((EntityReference)       node, treeParent, shift); break;
		case Node.NOTATION_NODE: 		       dumpNotationNode              ((Notation)              node, treeParent, shift); break;
		case Node.PROCESSING_INSTRUCTION_NODE: dumpProcessingInstructionNode ((ProcessingInstruction) node, treeParent, shift); break;
		case Node.TEXT_NODE:                   dumpTextNode                  ((Text)                  node, treeParent, shift); break;
		default:  System.out.println(shift + "Unknown node"); break;
		}
	}
	/** Plays the contents of a ELEMENT_NODE */
	private void dumpElement(Element node, MANode treeParent, String shift) {
		System.out.println(shift + "ELEMENT: " + node.getTagName());
		MXmlNode treeNode = new MXmlNode(treeParent).putData("xmlNodeName",  node.getTagName())
													.putData("xmlNodeType",  "ELEMENT_NODE");
		((MTree)treeNode.mAncestors.get(0)).mAllNodesCount++;

		NamedNodeMap nm = node.getAttributes();
		for (int i = 0; i < nm.getLength(); i++)
			dumpLoop(nm.item(i), treeNode, shift + "\t");
		if (!node.hasChildNodes()) return;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			dumpLoop(list.item(i), treeNode, shift + "\t");
	}
	/** Plays the contents of a ATTRIBUTE_NODE */
	private void dumpAttributeNode(Attr node, MANode treeParent, String shift) {
		System.out.println(shift + "ATTRIBUTE " + node.getName() + "=\"" + node.getValue() + "\"");
		new MXmlNode(treeParent).putData("xmlNodeName",  node.getName())
								.putData("xmlNodeValue", node.getValue())
								.putData("xmlNodeType",  "ATTRIBUTE_NODE");
		MXmlNode parent = ((MXmlNode)treeParent); 
		MTree tree = (MTree) parent.mAncestors.get(0);
		tree.mAllNodesCount++;
		parent.putData(node.getName(), node.getValue());
	}
	/** Plays the contents of a DOCUMENT_TYPE_NODE */
	private void dumpDocumentType(DocumentType node, MANode treeParent, String shift) {
		System.out.println(shift + "DOCUMENT_TYPE: " + node.getName());
		String nodeValue = null;
		if (node.getPublicId() != null) {
			System.out.println(shift + " Public ID: " + node.getPublicId());
			nodeValue  = node.getPublicId();
		}
		if (node.getSystemId() != null) {
			System.out.println(shift + " System ID: " + node.getSystemId());
			nodeValue = node.getSystemId();
		}
		MXmlNode treeNode = new MXmlNode(treeParent).putData("xmlNodeName",  node.getName())
													.putData("xmlNodeValue", nodeValue)
													.putData("xmlNodeType",  "DOCUMENT_TYPE_NODE");
		((MTree)treeNode.mAncestors.get(0)).mAllNodesCount++;

		NamedNodeMap entities = node.getEntities();
		if (entities.getLength() > 0) {
			for (int i = 0; i < entities.getLength(); i++) {
				dumpLoop(entities.item(i), treeNode, shift + "\t");
			}
		}
		NamedNodeMap notations = node.getNotations();
		if (notations.getLength() > 0) {
			for (int i = 0; i < notations.getLength(); i++)
				dumpLoop(notations.item(i), treeNode, shift + "\t");
		}
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			dumpLoop(list.item(i), treeParent, shift + "\t");
	}
	/** Plays the contents of a CDATA_SECTION_NODE */
	private void dumpCDATASectionNode(CDATASection node, MANode treeParent, String shift) {
		System.out.println(shift + "CDATA SECTION length=" + node.getLength());
		System.out.println(shift + "\"" + node.getData() + "\"");
	}
	/** Plays the contents of a COMMENT_NODE */
	private void dumpCommentNode(Comment node, MANode treeParent, String shift) {
		System.out.println(shift + "COMMENT length=" + node.getLength());
		System.out.println(shift + node.getData());
	}
	/** Plays the contents of a DOCUMENT_NODE */
	private void dumpDocument(Document node, MANode treeParent, String shift) {
		System.out.println(shift + "DOCUMENT");
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			dumpLoop(list.item(i), treeParent, shift + "\t");
	}
	/** Plays the contents of a DOCUMENT_FRAGMENT_NODE */
	private void dumpDocumentFragment(DocumentFragment node, MANode treeParent, String shift) {
		System.out.println(shift + "DOCUMENT FRAGMENT");
	}
	/** Plays the contents of a ENTITY_NODE */
	private void dumpEntityNode(Entity node, MANode treeParent, String shift) {
		System.out.println(shift + "ENTITY: " + node.getNodeName());
	}
	/** Plays the contents of a ENTITY_REFERENCE_NODE */
	private void dumpEntityReferenceNode(EntityReference node, MANode treeParent, String shift) {
		System.out.println(shift + "ENTITY REFERENCE: " + node.getNodeName());
	}
	/** Plays the contents of a NOTATION_NODE */
	private void dumpNotationNode(Notation node, MANode treeParent, String shift) {
		System.out.println(shift + "NOTATION");
		System.out.print(shift + node.getNodeName() + "=");
		if (node.getPublicId() != null)
			System.out.println(node.getPublicId());
		else
			System.out.println(node.getSystemId());
	}
	/** Plays the contents of a PROCESSING_INSTRUCTION_NODE */
	private void dumpProcessingInstructionNode(ProcessingInstruction node, MANode treeParent, String shift) {
		System.out.println(shift + "PI: target=" + node.getTarget());
		System.out.println(shift + node.getData());
	}
	/** Plays the contents of a TEXT_NODE */
	private void dumpTextNode(Text node, MANode treeParent, String shift) {
		System.out.println(shift + "TEXT length=" + node.getLength());
		System.out.println(shift + node.getData());
		((MXmlNode)treeParent).putData("xmlNodeValue", node.getData());
	}
}

