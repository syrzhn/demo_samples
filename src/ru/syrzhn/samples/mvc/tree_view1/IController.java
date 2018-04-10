package ru.syrzhn.samples.mvc.tree_view1;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

public interface IController {
	SourceController getSourceController();
	void printMessage(Object m);
	void showMessage(String msg);
	Display getDisplay();
	Object getData();
	String getSearch();
	Browser getBrowser();
	enum States {
		CAPTION, 
		TREE_ITEM
	}
	void updateState(States state, Object o);
}
