package ru.syrzhn.samples.mvc.tree_view1.model;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlUtils {
	/**
	 * Start empty XML Document.
	 * @param rootName The name of the Document root Element (created here)
	 * @return the Document
	 */
	public static Document createXmlDocument() {
		return createXmlDocument("root");
	}
	/**
	 * Start a new XML Document.
	 * @param rootName The name of the Document root Element (created here)
	 * @return the Document
	 */
	public static Document createXmlDocument(String rootName) {
		Document document = null; 
		DocumentBuilder builder = getXmlDocumentBuilder();
		builder.setErrorHandler(new DumpErrorHandler());
		document = builder.newDocument();
		Element root = document.createElement(rootName);
		document.appendChild(root);
		return document;
	}
    /**
     * Load xml <b>document</b> from file <b>fileName</b>
     * @param fileName
     * @return the Document
     */
    public static Document loadFromFile(final String fileName) {
		Document document = null;
		try {
			DocumentBuilder builder = getXmlDocumentBuilder();
			builder.setErrorHandler(new DumpErrorHandler());
			document = builder.parse(new InputSource(fileName));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		Node root = document.getDocumentElement();
		root.normalize();
		return document;
    }
	/**
	 * Get a DOM Document builder.
	 * @return the DocumentBuilder
	 */
	private static DocumentBuilder getXmlDocumentBuilder() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		factory.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new DumpErrorHandler());
			return builder;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
    /**
     * Save xml <b>document</b> into file <b>fileName</b>
     * @param document
     * @param fileName
     */
    public static void saveToFile(Document document, String fileName) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream(fileName);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void parseXml(MANode node, Object doc) {
    	dumpLoop((Node) doc, node, "");
    }

	private static void dumpLoop(Node node, MANode treeParent, String shift) {
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
	/** Plays the contents of a ATTRIBUTE_NODE */
	private static void dumpAttributeNode(Attr node, MANode treeParent, String shift) {
		System.out.println(shift + "ATTRIBUTE " + node.getName() + "=\"" + node.getValue() + "\"");
		new MXMLNode(treeParent).putData("xmlNodeName",  node.getName())
								.putData("xmlNodeValue", node.getValue())
								.putData("xmlNodeType",  "ATTRIBUTE_NODE");
		((MXMLNode)treeParent).putData(node.getName(), node.getValue());
	}
	/** Plays the contents of a CDATA_SECTION_NODE */
	private static void dumpCDATASectionNode(CDATASection node, MANode treeParent, String shift) {
		System.out.println(shift + "CDATA SECTION length=" + node.getLength());
		System.out.println(shift + "\"" + node.getData() + "\"");
	}
	/** Plays the contents of a COMMENT_NODE */
	private static void dumpCommentNode(Comment node, MANode treeParent, String shift) {
		System.out.println(shift + "COMMENT length=" + node.getLength());
		System.out.println(shift + node.getData());
	}
	/** Plays the contents of a DOCUMENT_NODE */
	private static void dumpDocument(Document node, MANode treeParent, String shift) {
		System.out.println(shift + "DOCUMENT");
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			dumpLoop(list.item(i), treeParent, shift + "\t");
	}
	/** Plays the contents of a DOCUMENT_FRAGMENT_NODE */
	private static void dumpDocumentFragment(DocumentFragment node, MANode treeParent, String shift) {
		System.out.println(shift + "DOCUMENT FRAGMENT");
	}
	/** Plays the contents of a DOCUMENT_TYPE_NODE */
	private static void dumpDocumentType(DocumentType node, MANode treeParent, String shift) {
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
		MXMLNode treeNode = new MXMLNode(treeParent).putData("xmlNodeName",  node.getName())
													.putData("xmlNodeValue", nodeValue)
													.putData("xmlNodeType",  "DOCUMENT_TYPE_NODE");
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
	/** Plays the contents of a ELEMENT_NODE */
	private static void dumpElement(Element node, MANode treeParent, String shift) {
		System.out.println(shift + "ELEMENT: " + node.getTagName());
		MXMLNode treeNode = new MXMLNode(treeParent).putData("xmlNodeName",  node.getTagName())
													.putData("xmlNodeType",  "ELEMENT_NODE");
		NamedNodeMap nm = node.getAttributes();
		for (int i = 0; i < nm.getLength(); i++)
			dumpLoop(nm.item(i), treeNode, shift + "\t");
		if (!node.hasChildNodes()) return;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++)
			dumpLoop(list.item(i), treeNode, shift + "\t");
	}
	/** Plays the contents of a ENTITY_NODE */
	private static void dumpEntityNode(Entity node, MANode treeParent, String shift) {
		System.out.println(shift + "ENTITY: " + node.getNodeName());
	}
	/** Plays the contents of a ENTITY_REFERENCE_NODE */
	private static void dumpEntityReferenceNode(EntityReference node, MANode treeParent, String shift) {
		System.out.println(shift + "ENTITY REFERENCE: " + node.getNodeName());
	}
	/** Plays the contents of a NOTATION_NODE */
	private static void dumpNotationNode(Notation node, MANode treeParent, String shift) {
		System.out.println(shift + "NOTATION");
		System.out.print(shift + node.getNodeName() + "=");
		if (node.getPublicId() != null)
			System.out.println(node.getPublicId());
		else
			System.out.println(node.getSystemId());
	}
	/** Plays the contents of a PROCESSING_INSTRUCTION_NODE */
	private static void dumpProcessingInstructionNode(ProcessingInstruction node, MANode treeParent, String shift) {
		System.out.println(shift + "PI: target=" + node.getTarget());
		System.out.println(shift + node.getData());
	}
	/** Plays the contents of a TEXT_NODE */
	private static void dumpTextNode(Text node, MANode treeParent, String shift) {
		System.out.println(shift + "TEXT length=" + node.getLength());
		System.out.println(shift + node.getData());
		((MXMLNode)treeParent).putData("xmlNodeValue", node.getData());
	}
}

class DumpErrorHandler implements ErrorHandler {
	public void warning(SAXParseException e) throws SAXException {
		show("Warning", e);
		throw (e);
	}

	public void error(SAXParseException e) throws SAXException {
		show("Error", e);
		throw (e);
	}

	public void fatalError(SAXParseException e) throws SAXException {
		show("Fatal Error", e);
		throw (e);
	}

	private void show(String type, SAXParseException e) {
		System.err.println(type + ": " + e.getMessage());
		System.err.println("Line " + e.getLineNumber() + " Column " + e.getColumnNumber());
		System.err.println("System ID: " + e.getSystemId());
	}
}
