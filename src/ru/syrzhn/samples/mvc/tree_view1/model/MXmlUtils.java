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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MXmlUtils {
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
