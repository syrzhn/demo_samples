package ru.syrzhn.samples.mvc.tree_view1.model;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;

import ru.syrzhn.samples.mvc.tree_view1.HTMLViewer;

public class Model {
	
	final static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	public HTMLViewer html;
	
	public static String getLevelName(int level) {
		return String.valueOf( ALPHABET.charAt(level) );
	}

	public static String getNameLevel(String name) {
		return String.valueOf( ALPHABET.indexOf(name.charAt(0)) );
	}
	
	public void createData(Object doc) {
		if (doc instanceof int[]) {
			int a[] = (int[])doc;
			mDataTree = new MTree(a[0], a[1]);
		}
		else
			mDataTree = new MXmlTree(doc);
		html = new HTMLViewer();
		"".toCharArray();
	}

	
	public MANode[] getDataFromTree(MANode parent) {
		MXmlNode arg[] = null;
		List<MANode> level = null;
		if (parent != null) 
			level = parent.mChildren;
		else
			level = mDataTree.mChildren;
		arg = new MXmlNode[level.size()];
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