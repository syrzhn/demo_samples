package ru.syrzhn.samples.mvc.tree_view1;

import java.util.Stack;

import org.eclipse.swt.widgets.TreeItem;

import ru.syrzhn.samples.mvc.tree_view1.Viewer.IForm.States;
import ru.syrzhn.samples.mvc.tree_view1.model.ANode;
import ru.syrzhn.samples.mvc.tree_view1.model.MNode;
import ru.syrzhn.samples.mvc.tree_view1.model.Model;

public class SourceController {
	
	private Viewer mViewer;
	
	private Model mModel;
	
	public SourceController(Viewer viewer) {
		mModel = new Model(3, 3);
		//mModel = new Model("src\\ru\\syrzhn\\samples\\mvc\\tree_view1\\xml\\BookCatalogue.xml");
		mViewer = viewer;
	}
	
	public void setViewer(Viewer viewer) {
		mViewer = viewer;
	}	

	public void setState(TreeItem item) {
		MNode node = (MNode) item.getData();
		node.mState = item;
	}
	
	public ISource addNewData() {
		String str = mViewer.mCurrentItem.toString();
		mViewer.mForm.printMessage("Adding new node to ".concat(str));
		MNode parent = (MNode) mViewer.mCurrentItem.getData();
		if (parent == null) throw new RuntimeException("Empty data in the item ".concat(str));
		MNode node = mModel.mDataTree.addNode(parent); 
		mViewer.mForm.printMessage(Model.messBuff);
		mViewer.mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		return new TreeSource(node);
	}

	public void disposeData() {
		String str = mViewer.mCurrentItem.toString();
		mViewer.mForm.printMessage("Disposing the node ".concat(str));
		MNode node = (MNode) mViewer.mCurrentItem.getData();
		if (node == null) throw new RuntimeException("Empty data in the item ".concat(str));
		mModel.mDataTree.disposeNode(node);
		mViewer.mForm.printMessage(Model.messBuff);
		mViewer.mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
	}

	public String[] parseDataToItemColumns(Object data) {
		MNode node = (MNode) data;
		return new String[] { data.toString(), node.mPath, node.mData.toString(), node.mType, node.mAncestors.toString() } ;
	}

	public void setDataOnCollapse() {
		String str = mViewer.mCurrentItem.toString() + " was collapsed";
		mViewer.mForm.printMessage(str);
	}

	public void setDataOnExpand() {
		String str = mViewer.mCurrentItem.toString() + " was expanded";
		mViewer.mForm.printMessage(str);
	}

	public void setDataOnSelection() {
		String str = mViewer.mCurrentItem.toString() + " was selected";
		mViewer.mForm.printMessage(str);
	}

	public void setDataOnCheck() {
		String str = mViewer.mCurrentItem.toString() + (mViewer.mCurrentItem.getChecked() ? " was checked" : " was unchecked");
		mViewer.mForm.printMessage(str);
	}
	
	public interface ISource {
		ISource[] getChildren(ISource parent);
		Object getData();
	}
	
	public ISource[] getSource() {
		mViewer.mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		return new TreeSource().getBeginDataSet();
	}

	public ISource[] getSource(Object node) {
		mViewer.mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		return new TreeSource((MNode) node).getBeginDataSet();
	}
	
	public class TreeSource implements ISource {
		private MNode mChildren[];
		private MNode mSource;
		
		public TreeSource() {
			mChildren = mModel.getDataTreeData(null);
		}
		
		public TreeSource(MNode node) {
			if (node == null) throw new RuntimeException("Empty node!");
			mSource = node;
			mChildren = mModel.getDataTreeData(mSource);
		}
		
		public MNode getData() {return mSource;}
		
		public ISource[] getBeginDataSet() {
			TreeSource arg[] = new TreeSource[mChildren.length];
			for (int i = 0; i < mChildren.length; i++) {
				arg[i] = new TreeSource(mChildren[i]);
			}
			return arg;
		}
		
		@Override
		public ISource[] getChildren(ISource parent) {
			MNode node = (MNode) parent.getData();
			if (node == null) return null;
			mChildren = mModel.getDataTreeData(node);
			TreeSource ret[] = new TreeSource[mChildren.length];
			for (int i = 0; i < mChildren.length; i++) {
				ret[i] = new TreeSource(mChildren[i]);
			}
			return ret;
		}
		
		@Override
		public String toString() {
			String s = mSource.toString();
			if (mChildren == null || mChildren.length == 0) return s;
			s = s.concat(" and children: ");
			for (int i = 0; i < mChildren.length; i++) {
				MNode child = mChildren[i];
				if (i == 0)
					s.concat(child.toString());
				else
					s.concat(",").concat(child.toString());
			}
			return s;
		}
	}

	public TreeItem searchByPath(String path) {
		MNode node = mModel.mDataTree.findNodeByPath(path);
		if (node == null) return null;
		return (TreeItem) node.mState;
	}

	public TreeItem[] getAncestors(TreeItem item) {
		MNode node = (MNode) item.getData();
		Stack<ANode> ancestors = node.mAncestors;
		TreeItem items[] = new TreeItem[ancestors.size()];
		int i = 0;
		for (ANode ancestor : ancestors) {
			Object data = ((MNode) ancestor).mState;
			if (data == null) continue;
			items[i++] = (TreeItem) data;
		}
		return items;
	}
	
	public TreeItem[] getDescendants(TreeItem item) {
		MNode node = (MNode) item.getData();
		Stack<ANode> descendants = node.mChildren;
		TreeItem items[] = new TreeItem[descendants.size()];
		int i = 0;
		for (ANode descendant : descendants) {
			Object data = ((MNode) descendant).mState;
			if (data == null) continue;
			items[i++] = (TreeItem) data;
		}
		return items;
	}
}


