package ru.syrzhn.samples.mvc.tree_view1;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Stack;

import ru.syrzhn.samples.mvc.tree_view1.model.ISource;

public class HTMLViewer {
	private List<String> body;
	private IController mForm;
	private SourceAdapter mAdapter;
	
	public HTMLViewer(IController form) {
		body = new Stack<String>();
		mForm = form;
		mAdapter = mForm.getSourceAdapter();
	}
	
	public Thread getRowsFromMTree() {
		abstract class GetItemsFromMTreeTask extends Task {
			public GetItemsFromMTreeTask(String name, IController form) { super(name, form); }
			protected void getChildren(ISource source) {
				ISource children[] = source.getChildren(source);
				for (ISource child : children) {
					Object o = child.getData();
					String s = mAdapter.getHtmlDataFromTreeNode(o);
					if (s.equals("table_row"))
						addRow("<tr>");
					if (s.equals("table_cell"))
						addRow("<td>" + mAdapter.getData(o, "xmlNodeValue") + "</td>");
					getChildren(child);
					if (s.equals("table_row"))
						addRow("</tr>");
				}
			}
		}
		String taskName = "Filling the HTML view";
		return new GetItemsFromMTreeTask(taskName, mForm) {
			@Override
			protected void doTask() {
				mForm.waitForWritingToMTree(); 
				ISource children[] = mAdapter.getSource(null);
				for (ISource child : children) {
					Object o = child.getData();
					String s = mAdapter.getHtmlDataFromTreeNode(o);
					if (s.equals("table_row"))
						addRow("<tr>");
					if (s.equals("table_cell"))
						addRow("<td>" + mAdapter.getData(o, "xmlNodeValue") + "</td>");
					getChildren(child);
					if (s.equals("table_row"))
						addRow("</tr>");
				} print();
			}
		};
	}
	
	public void addRow(String s) { ((Stack<String>)body).push(s); }
	
	private String bodyBuild() {
		StringBuilder sb = new StringBuilder();
		for (String s : body)
			sb.append(s.concat("\r\n"));
		return sb.toString();
	}
	
	public void print() {
		String theString = this.toString();
		StringSelection selection = new StringSelection(theString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		mForm.setBrowser(theString);
	}
	
	@Override
	public String toString() {
		return    "<html>"
				+ "<head>" 
				+ "<title>HTML Test</title>"
				+ "</head>"
				+ "<body>"
				+ "<table>"
				+ bodyBuild()
				+ "</table>"
				+ "</body>"
				+ "</html>";
	}
}
//
//class Html {
//	private boolean rowStarted;
//	public String table_row(List<String> cells) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<tr>");
//		for (String s : cells) 
//			sb.append(table_cell(s));
//		sb.append("</tr>");
//		return sb.toString();
//	}
//
//	private String table_cell(String cell) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<td>");
//		sb.append(cell);
//		sb.append("</td>");
//		return sb.toString();
//	}
//}
//
