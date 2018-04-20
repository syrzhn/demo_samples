package ru.syrzhn.samples.mvc.tree_view1;

import java.util.NoSuchElementException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.syrzhn.samples.mvc.tree_view1.model.ISource;

public class SwtTreeViewer {
	
	public IController mForm;
	public TreeItem mCurrentItem;
	private SourceAdapter mAdapter;

	public SwtTreeViewer(IController form) {
		mForm = form;
		comboSearchHandler = new ComboSearchHandler();
		mAdapter = mForm.getSourceAdapter();
	}
	
	public Thread getItemsFromMTree(Object parent) {
		abstract class GetItemsFromMTreeTask extends Task {
			public GetItemsFromMTreeTask(String name, IController form) { super(name, form); }
			protected void getChildren(final TreeItem item, ISource source) {
				ISource children[] = source.getChildren(source);
				for (ISource child : children) {
					TreeItem childItem = new TreeItem(item, 0);
					Object o = child.getData();
					childItem.setText(mAdapter.getTextDataFromTreeNode(o));
					if (mAdapter.getSelectFromTreeNode(o))
						childItem.getParent().setSelection(childItem);
					childItem.setData(o);
					mAdapter.setState(childItem);
					getChildren(childItem, child);
				}
			}
		}
		final boolean kindOfParentIsItem = parent instanceof TreeItem; 
		String taskName = "Filling the ", name = "tree";
		if (kindOfParentIsItem) name = "item - ".concat(parent.toString());  
		taskName = taskName.concat(name);			
		final Object data = kindOfParentIsItem ? ((TreeItem) parent).getData() : null;
		return new GetItemsFromMTreeTask(taskName, mForm) {
			@Override
			protected void doTask() {
				mForm.waitForWritingToMTree();
				ISource children[] = mAdapter.getSource(data);
				mForm.getDisplay().asyncExec(() -> {
						for (ISource child : children) {
							TreeItem childItem = kindOfParentIsItem ? new TreeItem((TreeItem) parent, 0) : new TreeItem((Tree) parent, 0);
							Object o = child.getData();
							childItem.setText(mAdapter.getTextDataFromTreeNode(o));
							if (mAdapter.getSelectFromTreeNode(o))
								childItem.getParent().setSelection(childItem);
							childItem.setData(o);
							mAdapter.setState(childItem);
							getChildren(childItem, child);
						}
					}
				);
			}			
		};
	}
	
	public SelectionAdapter getNewItemSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (mCurrentItem == null) return;
				Object parentNode = mCurrentItem.getData();
				if (parentNode == null)	throw new NoSuchElementException();
				String taskName = "inserting the item - ".concat(mCurrentItem.toString());
				Task t = new Task(taskName, mForm) {
					@Override
					protected void doTask() {
						ISource source = mAdapter.addNewData(parentNode);
						mForm.getDisplay().asyncExec(() -> {
								TreeItem newItem = new TreeItem(mCurrentItem, 0);
								Object newNode = source.getData();
								newItem.setText(mAdapter.getTextDataFromTreeNode(newNode));
								newItem.setData(newNode);
								mAdapter.setState(newItem);
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
				if (mCurrentItem == null) return;
				Object parentNode = mCurrentItem.getData();
				if (parentNode == null)	throw new NoSuchElementException();
				String taskName = "deleting the item - ".concat(mCurrentItem.toString());
				Task t = new Task(taskName, mForm) {
					@Override
					protected void doTask() {
						TreeItem[] items = mAdapter.disposeData(parentNode);
						mForm.getDisplay().asyncExec(() -> {
								mCurrentItem.dispose();
								for (TreeItem item : items) {
									Object o = item.getData();
									item.setText(mAdapter.getTextDataFromTreeNode(o));
									item.setData(o);
								}
							}
						);
					}
				}; t.start();				
			}
		};
	}
	
	public Listener getTableEventListener(final int eventType) {
		return new Listener() {

			private int mEventType = eventType;
			
			@Override
			public void handleEvent(Event event) {
				if (mForm.getBusy()) return;
				mCurrentItem = (TreeItem) event.item;
				Object data = mCurrentItem.getData();
				switch (mEventType) {
				case SWT.Collapse:
					mAdapter.setDataOnCollapse(data);
					break;
				case SWT.Expand:
					mAdapter.setDataOnExpand(data);
					break;
				case SWT.Selection:
					if (event.detail == SWT.CHECK) 
						mAdapter.setDataOnCheck(data);
					else
						mAdapter.setDataOnSelection(data);
					break;
				}
			}		
		};
	}
	
	public SelectionAdapter getSearchSelectionAdapter() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (mForm.getBusy()) return;
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
					if (mForm.getBusy()) return;
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
					if (mForm.getBusy()) return;
					sourceWidget = (Combo) event.widget;
					String str = sourceWidget.getText();
					if (str.length() < 3) return;
					searchAndCheckItem(str);
				}
			};
		}
		
		public void searchAndCheckItem(String str) {
			Task t = new Task("searching the item - ".concat(str), mForm) {
				@Override
				protected void doTask() {
					mForm.getDisplay().asyncExec(() -> {
							SourceAdapter c = mAdapter;
							TreeItem item = c.searchByPath(str);
							if (item == null) return;
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
	
	@Override
	public String toString() {
		return "Viewer";
	}
}