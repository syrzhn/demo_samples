package ru.syrzhn.samples.mvc.tree_view1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.syrzhn.samples.mvc.tree_view1.Controller.ISource;

public class Viewer {
	
	public interface IForm {
		void showMessage(String msg);
		void printMessage(String msg);
		void printMessage(String[] msgs);
		void updateState(State state);
		Controller getController();
		String getSearch();
		Display getDisplay();
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
	private volatile boolean isBusy;

	public Viewer(IForm form) {
		mForm = form;
	}

	public Listener getTableEventListener(final int eventType) {
		return new Listener() {

			private int mEventType = eventType;
			
			@Override
			public void handleEvent(Event event) {
				if (isBusy) return;
				mCurrentItem = (TreeItem) event.item;
				Controller c = mForm.getController();
				switch (mEventType) {
				case SWT.Collapse:
					c.setDataOnCollapse();
					break;
				case SWT.Expand:
					c.setDataOnExpand();
					break;
				case SWT.Selection:
					if (event.detail == SWT.CHECK) 
						c.setDataOnCheck();
					else
						c.setDataOnSelection();
					break;
				}
			}		
		};
	}
	
	public SelectionAdapter getSearchSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isBusy) return;
				String s = mForm.getSearch();
				mForm.getController().searchByPath(s).setChecked(true);
			}
		};
	}
	ComboSearchHandler comboSearchHandler = new ComboSearchHandler();
	public class ComboSearchHandler extends KeyAdapter implements Listener {
		
		private final String mValidSymbols = "0123456789" 
				+ "abcdefghijklmnopqrstuvwxyz" 
				+ "יצףךוםדרשחץתפûגאןנמכהז‎ÿקסלטעüב‏¸"
				+ "-_+=|\\/*\"'"
				+ ",.:;!?"
				+ "(){}[]"
				+ "@#$%^&*"
				+ '\u0008' // backspace
				+ '\u007F' // delete
				+ '\r'
				;
		
		private boolean isValid(KeyEvent keyevent) {
			if (mValidSymbols.toUpperCase().indexOf(String.valueOf(keyevent.character).toUpperCase()) < 0)
				return false;
			return true;
		}
		
		@Override
		public void keyPressed(KeyEvent keyevent) {}
		
		@Override
		public void keyReleased(KeyEvent keyevent) {
			if (isBusy) return;
			if (!isValid(keyevent))	return;
			Combo src = (Combo) keyevent.getSource();
			String str = src.getText();
			if (keyevent.character == '\r')
				search(str).setChecked(true);
		}
		
		@Override
		public void handleEvent(Event event) {
			if (isBusy) return;
			Combo src = (Combo) event.widget;
			String str = src.getText();
			search(str).setChecked(true);
		}
		
		public TreeItem search(String str) {
			Controller c = mForm.getController();
			TreeItem item = c.searchByPath(str);
			TreeItem items[] = c.getAncestors(item);
			expandTreeItems(items);
			return item;
		}
	}
	
	public SelectionAdapter getNewItemSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isBusy) return;
				if (mCurrentItem == null) return;
				insertItem(mCurrentItem);
			}
		};
	}
	
	private void expandTreeItems(TreeItem items[]) {
		for (TreeItem item : items) 
			item.setExpanded(true);
	}
	
	private void insertItem(final TreeItem treeItem) {
		mForm.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				Controller c = mForm.getController();
				ISource source = c.addNewData();
				TreeItem newItem = new TreeItem(treeItem, 0);
				Object o = source.getData();
				newItem.setData(o);
				newItem.setText(c.parseDataToItemColumns(o));
				treeItem.setExpanded(true);
			}
		});
	}

	public SelectionAdapter getDeleteItemSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isBusy) return;
				if (mCurrentItem == null) return;
				disposeItem(mCurrentItem);
				TreeItem parent = mCurrentItem.getParentItem();
				Tree tree = mCurrentItem.getParent();
				mCurrentItem.dispose();
				if (parent != null) {
					parent.removeAll();
					getItemsFromMTree(parent);
				}
				else {
					if (tree != null) {
						tree.removeAll();
						getItemsFromMTree(tree);
					}
				}					
			}
		};
	}
	
	private void disposeItem(TreeItem mCurrentItem) {
		if (isBusy) return;
		mForm.getController().disposeData();
	}
	
	private abstract class Task extends Thread {
		private String mName;
		protected abstract void doTask();
		public Task(String name) { mName = name; }
		public void run() {
			isBusy = true;
			long start = System.currentTimeMillis();
			doTask();
			long end = System.currentTimeMillis();
			mForm.printMessage("Time to execute the task \"".concat(mName).concat("\" in millis: ").concat(String.valueOf(end - start)));
			isBusy = false;
		}
	}
	
	private abstract class GetItemsFromMTreeTask extends Task {
		public GetItemsFromMTreeTask(String name) { super(name); }
		protected void getData(final TreeItem item, ISource source) {
			ISource children[] = source.getChildren(source);
			mForm.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					Controller c = mForm.getController();
					for (ISource child : children) {
						TreeItem childItem = new TreeItem(item, 0);
						Object o = child.getData();
						childItem.setData(o);
						childItem.setText(c.parseDataToItemColumns(o));
						c.setData(childItem);
						getData(childItem, child);
					}
				}
			});				
		}
	}
	
	public void getItemsFromMTree(final TreeItem Item) {
		Task t = new GetItemsFromMTreeTask("fill the item - ".concat(Item.toString())) {
			@Override
			protected void doTask() {
				mForm.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						Controller c = mForm.getController();
						ISource children[] = c.getSource(Item.getData());
						for (ISource child : children) {
							TreeItem childItem = new TreeItem(Item, 0);
							Object o = child.getData();
							childItem.setData(o);
							childItem.setText(c.parseDataToItemColumns(o));
							c.setData(childItem);
							getData(childItem, child);
						}
					}
				});
			}			
		}; t.start();
	}
	
	public void getItemsFromMTree(final Tree tree) {
		Task t = new GetItemsFromMTreeTask("fill the tree") {
			@Override
			protected void doTask() {
				mForm.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						ISource children[] = mForm.getController().getSource();
						for (ISource child : children) {
							TreeItem childItem = new TreeItem(tree, 0);
							Object o = child.getData();
							childItem.setData(o);
							childItem.setText(mForm.getController().parseDataToItemColumns(o));
							mForm.getController().setData(childItem);
							getData(childItem, child);
						}
					}
				});
			}			
		}; t.start();
	}
}