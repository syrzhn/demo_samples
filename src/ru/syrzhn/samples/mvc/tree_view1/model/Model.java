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
		mTestTree = new MTree(levels, rows);
	}
	
	public MNode[] getTreeData() {
		MNode arg[] = null;
		List<MNode> level = mTestTree.mChildren;
		arg = new MNode[level.size()];
		level.toArray(arg);
		return arg;
	}
	
	public MNode[] getTreeData(MNode parent) {
		if (parent == null) return null;
		MNode arg[] = null;
		List<MNode> level = parent.mChildren;
		arg = new MNode[level.size()];
		level.toArray(arg);
		return arg;
	}
	
	static {
		messBuff = new Stack<String>();
	}

	public static List<String> messBuff;

	public MTree mTestTree;
	
	public static String currentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS", Locale.GERMANY);
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+5:00"));
		calendar.setTimeInMillis(System.currentTimeMillis());
		return sdf.format(calendar.getTime());
	}
}