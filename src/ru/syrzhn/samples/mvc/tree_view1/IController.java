package ru.syrzhn.samples.mvc.tree_view1;

import org.eclipse.swt.widgets.Display;

public interface IController {
	SourceAdapter getSourceAdapter();
	void printMessage(Object m);
	void showMessage(String msg);
	void setBusy(boolean busy);
	boolean getBusy();
	void waitForWritingToMTree(Thread t);
	Display getDisplay();
	Object getData();
	String getSearch();
	void setBrowser(String html);
	enum States {
		CAPTION, 
		TREE_ITEM
	}
	void updateState(States state, Object o);
}
