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
		Controller getController();
		class State {
			public String caption;
			public State(String[] args) {
				if (args == null || args.length == 0) return;
				caption = args[0];
			}
		}
	}
	
	public IForm mForm;
	
	public TreeItem mCurrentItem;

	public Viewer(IForm form) {
		mForm = form;
	}

	public Listener getTableEventListener(final int eventType) {
		return new Listener() {

			private int mEventType = eventType;
			
			@Override
			public void handleEvent(Event event) {
				mCurrentItem = (TreeItem) event.item;
				switch (mEventType) {
				case SWT.Collapse:
					mForm.getController().setDataOnCollapse(Viewer.this);
					break;
				case SWT.Expand:
					mForm.getController().setDataOnExpand(Viewer.this);
					break;
				case SWT.Selection:
					if (event.detail == SWT.CHECK) 
						mForm.getController().setDataOnCheck(Viewer.this);
					else
						mForm.getController().setDataOnSelection(Viewer.this);
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
		IData data = mForm.getController().setData(this);
		newItem.setText(data.toString());
	}

	public SelectionAdapter getDeleteSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (mCurrentItem == null) return;
				TreeItem parent = mCurrentItem.getParentItem();
				disposeData(mCurrentItem);
				mCurrentItem.dispose();
				parent.removeAll();
				getData(parent);
			}
		};
	}
	
	public void getData(final TreeItem Item) {
		IData data0[];
		data0 = mForm.getController().getNodeData(Item.getText(0));
		for (IData d0 : data0) {
			TreeItem item = new TreeItem(Item, 0);
			item.setText(d0.toString());
			getData(item, d0);
		}
	}
	
	private void disposeData(TreeItem mCurrentItem) {
		mForm.getController().disposeData(this);
	}
	
	public void getData(final Tree tree) {
		IData data0[];
		data0 = mForm.getController().getData(null);
		for (IData d0 : data0) {
			TreeItem item = new TreeItem(tree, 0);
			item.setText(d0.toString());
			getData(item, d0);
		}
	}
	
	private void getData(final TreeItem item, IData data) {
		IData data1[] = data.getChildren(data);
		for (IData d : data1) {
			TreeItem dItem = new TreeItem(item, 0);
			dItem.setText(d.toString());
			getData(dItem, d);
		}
	}
}
