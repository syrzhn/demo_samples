package ru.syrzhn.samples.mvc.tree_view1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.syrzhn.samples.mvc.tree_view1.Controller.IData;

public class Viewer {
	
	public interface IForm {
		void showMessage(String msg);
		void printMessage(String msg);
		void printMessage(String[] msgs);
		void updateState(State state);
		class State {
			public String caption;
			public State(String[] args) {
				if (args == null || args.length == 0) return;
				caption = args[0];
			}
		}
	}
	
	public IForm mForm;
	
	private Controller mController;

	public TreeItem mCurrentItem;

	public Viewer(IForm form, Controller controller) {
		mForm = form;
		mController = controller;
	}

	public Listener getTableEventListener(final int eventType) {
		return new Listener() {

			private int mEventType = eventType;
			
			@Override
			public void handleEvent(Event event) {
				mCurrentItem = (TreeItem) event.item;
				switch (mEventType) {
				case SWT.Collapse:
					mController.setDataOnCollapse(Viewer.this);
					break;
				case SWT.Expand:
					mController.setDataOnExpand(Viewer.this);
					break;
				case SWT.Selection:
					if (event.detail == SWT.CHECK) 
						mController.setDataOnCheck(Viewer.this);
					else
						mController.setDataOnSelection(Viewer.this);
					break;
				}
			}		
		};
	}
	
	public SelectionAdapter getNewSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (mCurrentItem == null) return;
				insertTestData(mCurrentItem);
				mCurrentItem.setExpanded(true);
			}
		};
	}
	
	private void insertTestData(final TreeItem treeItem) {
		TreeItem newItem = new TreeItem(treeItem, 0);
		IData data = mController.setData(this);
		newItem.setText(data.toString());
	}

	public SelectionAdapter getDeleteSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (mCurrentItem == null) return;
				disposeData(mCurrentItem);
				mCurrentItem.dispose();
			}
		};
	}
	
	private void disposeData(TreeItem mCurrentItem) {
		mController.disposeData(this);
	}
	
	public void getTestData(final Tree tree) {
		IData data0[];
		data0 = mController.getData(null);
		for (IData d0 : data0) {
			TreeItem item = new TreeItem(tree, 0);
			item.setText(d0.toString());
			getTestData(item, d0);
		}
	}
	
	private void getTestData(final TreeItem item, IData data) {
		IData data1[] = data.getChildren(data);
		for (IData d : data1) {
			TreeItem dItem = new TreeItem(item, 0);
			dItem.setText(d.toString());
			getTestData(dItem, d);
		}
	}
}
