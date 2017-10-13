package ru.syrzhn.samples.mvc.tree_view1.model;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;

public class Model {
	
	final static String alphabet = "abcdefghijklmnopqrstuvwxyz";
	
	public static String getLevelName(int level) {
		return String.valueOf( alphabet.charAt(level) );
	}

	public static String getNameLevel(String name) {
		return String.valueOf( alphabet.indexOf(name.charAt(0)) );
	}
	
	public Model(final int levels, final int rows) {
		mDataTree = new MTree(levels, rows);
	}
	
	public Model (final String fileName) {
		mDataTree = new MTree(fileName);
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
	
	public MTree getDataTree() {
		return mDataTree;
	}
	
	static {
		messBuff = new Stack<String>();
	}

	public static List<String> messBuff;

	private MTree mDataTree;
	
	public static String currentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss,SSS", Locale.GERMANY);
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+5:00"));
		calendar.setTimeInMillis(System.currentTimeMillis());
		return sdf.format(calendar.getTime());
	}
}