package ru.syrzhn.samples.mvc.tree_view1.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Model {
	
	final static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	
	public static String getLevelName(int level) {
		return String.valueOf( ALPHABET.charAt(level) );
	}

	public static String getNameLevel(String name) {
		return String.valueOf( ALPHABET.indexOf(name.charAt(0)) );
	}
	
	public Model(final int levels, final int rows) {
		mDataTree = new MTree(levels, rows);
	}
	
	public Model (final String fileName) {
		mDataTree = new MTree(fileName);
	}
	
	public Model() {
		mDataTree = new MTree();
	}

	public MXMLNode[] getDataTreeData(MANode parent) {
		MXMLNode arg[] = null;
		List<MANode> level = null;
		if (parent != null) 
			level = parent.mChildren;
		else
			level = mDataTree.mChildren;
		arg = new MXMLNode[level.size()];
		level.toArray(arg);
		return arg;
	}
	
	static {
		messBuff = new Stack<String>();
	}

	public static Stack<String> messBuff;

	public MTree mDataTree;
	
	public static String currentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss,SSS", Locale.GERMANY);
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+5:00"));
		calendar.setTimeInMillis(System.currentTimeMillis());
		return sdf.format(calendar.getTime());
	}
}

class XmlUtils {
	/**
	 * Start a new XML Document.
	 * @param rootName The name of the Document root Element (created here)
	 * @return the Document
	 * @throws DomException
	 */
	public static Document createXmlDocument(String rootName) {
		Document document = getXmlDocumentBuilder().newDocument();
		Element  root     = document.createElement(rootName);

		document.appendChild(root);
		return document;
	}
	/**
	 * Get a DOM Document builder.
	 * @return The DocumentBuilder
	 * @throws DomException
	 */
	public static DocumentBuilder getXmlDocumentBuilder() {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
		
			return factory.newDocumentBuilder();
		
		} catch (Exception e) {}
		return null;
	}
	 
    /**
     * Save document into file "outer.xml"
     * @param document
     * @throws TransformerFactoryConfigurationError
     */
    public static void saveDocument(Document document) throws TransformerFactoryConfigurationError {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream("outer.xml");
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
        } catch (TransformerException | IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
