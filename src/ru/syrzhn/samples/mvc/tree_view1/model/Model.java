package ru.syrzhn.samples.mvc.tree_view1.model;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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

	public MNode[] getDataTreeData(ANode parent) {
		MNode arg[] = null;
		List<ANode> level = null;
		if (parent != null) 
			level = parent.mChildren;
		else
			level = mDataTree.mChildren;
		arg = new MNode[level.size()];
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
}
