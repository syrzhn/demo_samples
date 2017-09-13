package ru.syrzhn.samples.mvc.tree_view1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.syrzhn.samples.mvc.tree_view1.Controller.ISource;
import ru.syrzhn.samples.mvc.tree_view1.model.MNode;

public class Viewer {
	
	public interface IForm {
		void showMessage(String msg);
		void printMessage(String msg);
		void printMessage(String[] msgs);
		void updateState(State state);
		Controller getController();
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
				switch (mEventType) {
				case SWT.Collapse:
					mForm.getController().setDataOnCollapse();
					break;
				case SWT.Expand:
					mForm.getController().setDataOnExpand();
					break;
				case SWT.Selection:
					if (event.detail == SWT.CHECK) 
						mForm.getController().setDataOnCheck();
					else
						mForm.getController().setDataOnSelection();
					break;
				}
			}		
		};
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
	
	private void insertItem(final TreeItem treeItem) {
		mForm.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				ISource source = mForm.getController().setData();
				TreeItem newItem = new TreeItem(treeItem, 0);
				newItem.setData(source.getData());
				newItem.setText( new String[] { newItem.getData().toString(), ((MNode)newItem.getData()).mPath } );
				mCurrentItem.setExpanded(true);
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
			isBusy = false;
			long end = System.currentTimeMillis();
			mForm.printMessage("Time to execute the task \"".concat(mName).concat("\" in millis: ").concat(String.valueOf(end - start)));
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
						childItem.setData(child.getData());
						childItem.setText( new String[] { childItem.getData().toString(), ((MNode)childItem.getData()).mPath } );						
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
						ISource children[] = mForm.getController().getSource(Item.getText(1));
						for (ISource child : children) {
							TreeItem childItem = new TreeItem(Item, 0);
							childItem.setData(child.getData());
							childItem.setText( new String[] { childItem.getData().toString(), ((MNode)childItem.getData()).mPath } );							
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
							childItem.setData(child.getData());
							childItem.setText( new String[] { childItem.getData().toString(), ((MNode)childItem.getData()).mPath } );							
							getData(childItem, child);
						}
					}
				});
			}			
		}; t.start();
	}
}
