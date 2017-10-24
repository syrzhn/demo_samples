package ru.syrzhn.samples.mvc.tree_view1;

import java.util.List;

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

import ru.syrzhn.samples.mvc.tree_view1.SourceController.ISource;

public class Viewer {
	
	public interface IForm {
		void showMessage(String msg);
		void printMessage(String msg);
		void printMessage(String[] msgs);
		void printMessage(List<String> msgs);
		void updateState(States state, Object o);
		String getSearch();
		Display getDisplay();
		enum States { 
			CAPTION, 
			TREE_ITEM 
		};
	}
	
	public IForm mForm;
	public TreeItem mCurrentItem;
	private volatile boolean isBusy;
	private SourceController mController;

	public Viewer(IForm form) {
		mForm = form;
		comboSearchHandler = new ComboSearchHandler();
		mController = new SourceController(this);
	}

	public Listener getTableEventListener(final int eventType) {
		return new Listener() {

			private int mEventType = eventType;
			
			@Override
			public void handleEvent(Event event) {
				if (isBusy) return;
				mCurrentItem = (TreeItem) event.item;
				switch (mEventType) {
				case SWT.Collapse:
					mController.setDataOnCollapse();
					break;
				case SWT.Expand:
					mController.setDataOnExpand();
					break;
				case SWT.Selection:
					if (event.detail == SWT.CHECK) 
						mController.setDataOnCheck();
					else
						mController.setDataOnSelection();
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
				comboSearchHandler.searchAndCheckItem(s);
			}
		};
	}
	ComboSearchHandler comboSearchHandler;
	public class ComboSearchHandler{
		Combo sourceWidget;
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
		public KeyAdapter getKeyAdapter() {
			return new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent keyevent) {}
				
				@Override
				public void keyReleased(KeyEvent keyevent) {
					if (isBusy) return;
					if (!isValid(keyevent))	return;
					sourceWidget = (Combo) keyevent.getSource();
					String str = sourceWidget.getText();
					if (keyevent.character == '\r') 
						searchAndCheckItem(str);
				}
			};
		}
		
		public SelectionAdapter getSelectionAdapter() {
			return new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) { search(event); }
				@Override
				public void widgetDefaultSelected(SelectionEvent event) { search(event); }
				
				private void search(SelectionEvent event) {
					if (isBusy) return;
					sourceWidget = (Combo) event.widget;
					String str = sourceWidget.getText();
					if (str.length() < 3) return;
					searchAndCheckItem(str);
				}
			};
		}
		
		public void searchAndCheckItem(final String str) {
			Task t = new Task("searching the item - ".concat(str)) {
				@Override
				protected void doTask() {
					mForm.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							SourceController c = mController;
							TreeItem item = c.searchByPath(str);
							if (item == null) return;
							TreeItem items[] = c.getAncestors(item);
							expandTreeItems(items);
							if (item != null)
								item.setChecked(true);
							addItem(str);
						}
					});
				}			
			}; t.start();
		}
		
		private void addItem(String str) {
			if (sourceWidget == null) return;
			for (String item : sourceWidget.getItems())
				if (str.equals(item)) return;
			sourceWidget.add(str);
		}
	}
	
	private void expandTreeItems(TreeItem items[]) {
		for (TreeItem item : items) 
			item.setExpanded(true);
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
	
	private void insertItem(final TreeItem item) {
		Task t = new Task("inserting the item - ".concat(item.toString())) {
			@Override
			protected void doTask() {
				mForm.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						ISource source = mController.addNewData();
						TreeItem newItem = new TreeItem(item, 0);
						Object o = source.getData();
						newItem.setText(mController.parseDataToItemColumns(o));
						newItem.setData(o);
						mController.setState(newItem);
						item.setExpanded(true);
					}
				});
			}			
		}; t.start();
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
		mController.disposeData();
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
					for (ISource child : children) {
						TreeItem childItem = new TreeItem(item, 0);
						Object o = child.getData();
						childItem.setText(mController.parseDataToItemColumns(o));
						childItem.setData(o);
						mController.setState(childItem);
						getData(childItem, child);
					}
				}
			});				
		}
	}
	
	public void getItemsFromMTree(final TreeItem Item) {
		Object data = Item.getData();
		Task t = new GetItemsFromMTreeTask("filling the item - ".concat(Item.toString())) {
			@Override
			protected void doTask() {
				ISource children[] = mController.getSource(data);
				mForm.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						for (ISource child : children) {
							TreeItem childItem = new TreeItem(Item, 0);
							Object o = child.getData();
							childItem.setText(mController.parseDataToItemColumns(o));
							childItem.setData(o);
							mController.setState(childItem);
							getData(childItem, child);
						}
					}
				});
			}			
		}; t.start();
	}
	
	public void getItemsFromMTree(final Tree tree) {
		Task t = new GetItemsFromMTreeTask("filling the tree") {
			@Override
			protected void doTask() {
				ISource children[] = mController.getSource();
				mForm.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						for (ISource child : children) {
							TreeItem childItem = new TreeItem(tree, 0);
							Object o = child.getData();
							childItem.setText(mController.parseDataToItemColumns(o));
							childItem.setData(o);
							mController.setState(childItem);
							getData(childItem, child);
						}
					}
				});
			}			
		}; t.start();
	}
}