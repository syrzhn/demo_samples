package ru.syrzhn.samples.mvc.tree_view1;

import java.util.List;
import java.util.NoSuchElementException;

import javax.lang.model.type.UnknownTypeException;

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
	public class ComboSearchHandler {
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
		
		public void searchAndCheckItem(String str) {
			Task t = new Task("searching the item - ".concat(str)) {
				@Override
				protected void doTask() {
					mForm.getDisplay().asyncExec(() -> {
							SourceController c = mController;
							TreeItem item = c.searchByPath(str);
							if (item == null) return;
//							TreeItem items[] = c.getAncestors(item);
//							expandTreeItems(items);
							if (item != null) {
								item.setChecked(true);
								item.getParent().setSelection(item); 
							}
							addItem(str);
						}
					);
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
	
//	private void expandTreeItems(TreeItem items[]) {
//		for (TreeItem item : items) 
//			item.setExpanded(true);
//	}
	
	public SelectionAdapter getNewItemSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isBusy || mCurrentItem == null) return;
				Object parentNode = mCurrentItem.getData();
				if (parentNode == null)	throw new NoSuchElementException();
				String taskName = "inserting the item - ".concat(mCurrentItem.toString());
				Task t = new Task(taskName) {
					@Override
					protected void doTask() {
						ISource source = mController.addNewData(parentNode);
						mForm.getDisplay().asyncExec(() -> {
								TreeItem newItem = new TreeItem(mCurrentItem, 0);
								Object newNode = source.getData();
								newItem.setText(mController.parseDataToItemColumns(newNode));
								newItem.setData(newNode);
								mController.setState(newItem);
								mCurrentItem.setExpanded(true);
							}
						);
					}			
				}; t.start();
			}
		};
	}

	public SelectionAdapter getDeleteItemSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isBusy || mCurrentItem == null) return;
				Object parentNode = mCurrentItem.getData();
				if (parentNode == null)	throw new NoSuchElementException();
				String taskName = "deleting the item - ".concat(mCurrentItem.toString());
				Task t = new Task(taskName) {
					@Override
					protected void doTask() {
						TreeItem[] items = mController.disposeData(parentNode);
						mForm.getDisplay().asyncExec(() -> {
								mCurrentItem.dispose();
								for (TreeItem item : items) {
									Object o = item.getData();
									item.setText(mController.parseDataToItemColumns(o));
									item.setChecked(true);
									item.setData(o);
								}
							}
						);
					}
				}; t.start();				
			}
		};
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
	
	public void getItemsFromMTree(Object parent) {
		abstract class GetItemsFromMTreeTask extends Task {
			public GetItemsFromMTreeTask(String name) { super(name); }
			protected void getData(final TreeItem item, ISource source) {
				ISource children[] = source.getChildren(source);
				mForm.getDisplay().asyncExec(() -> {
						for (ISource child : children) {
							TreeItem childItem = new TreeItem(item, 0);
							Object o = child.getData();
							childItem.setText(mController.parseDataToItemColumns(o));
							childItem.setData(o);
							mController.setState(childItem);
							getData(childItem, child);
						}
					}
				);				
			}
		}
		String taskName = "filling the ";
		if (parent instanceof TreeItem) 
			taskName = taskName.concat("item - ").concat(parent.toString());
		else if (parent instanceof Tree)
			taskName = taskName.concat("tree");
		else 
			throw new UnknownTypeException(null, parent);
			
		Object data = parent instanceof TreeItem ? ((TreeItem) parent).getData() : null;
		Task t = new GetItemsFromMTreeTask(taskName) {
			@Override
			protected void doTask() {
				ISource children[] = mController.getSource(data);
				mForm.getDisplay().asyncExec(() -> {
						for (ISource child : children) {
							TreeItem childItem = parent instanceof TreeItem ? new TreeItem((TreeItem) parent, 0) : new TreeItem((Tree) parent, 0);
							Object o = child.getData();
							childItem.setText(mController.parseDataToItemColumns(o));
							childItem.setData(o);
							mController.setState(childItem);
							getData(childItem, child);
						}
					}
				);
			}			
		}; t.start();
	}
}