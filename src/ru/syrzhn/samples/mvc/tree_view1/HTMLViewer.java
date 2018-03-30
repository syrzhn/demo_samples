package ru.syrzhn.samples.mvc.tree_view1;

import java.util.Stack;

public class HTMLViewer {
	private Stack<String> body;
	
	public HTMLViewer() {
		body = new Stack<String>();
	}
	
	public void addRow(String s) {
		body.push(s);
	}
	
	private String bodyBuild() {
		StringBuilder sb = new StringBuilder();
		for (String s : body)
			sb.append(s.concat("\r\n"));
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return "<html>"
				+ "<head>" 
				+ "<base href=\"http://www.eclipse.org/swt/\" >"
				+ "<title>HTML Test</title>"
				+ "</head>"
				+ "<body>"
				+ bodyBuild()
				+ "</body>"
				+ "</html>";
	}
}
