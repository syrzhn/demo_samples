package ru.syrzhn.samples.mvc.tree_view1;

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
			int i;
			public GetItemsFromMTreeTask(String name, IController form) { super(name, form); }
			protected void getChildren(ISource source) {
				ISource children[] = source.getChildren(source);
				for (ISource child : children) {
					Object o = child.getData();
					addRow("" + i++);
					getChildren(child);
				}
			}
		}
		String taskName = "Filling the HTML view";
		return new GetItemsFromMTreeTask(taskName, mForm) {
			int i;
			@Override
			protected void doTask() {
				mForm.waitForWritingToMTree(); 
				ISource children[] = mAdapter.getSource(null);
				for (ISource child : children) {
					Object o = child.getData();
					addRow("" + i++);
					getChildren(child);
				}
				print();
			}
		};
	}
	
	public void addRow(String s) {
		((Stack<String>)body).push(s);
	}
	
	private String bodyBuild() {
		StringBuilder sb = new StringBuilder();
		for (String s : body)
			sb.append(s.concat("\r\n"));
		return sb.toString();
	}
	
	public void print() {
		mForm.setBrowser(this.toString());
	}
	
	@Override
	public String toString() {
		return    "<html>"
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
