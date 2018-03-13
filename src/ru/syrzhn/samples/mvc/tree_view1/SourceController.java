package ru.syrzhn.samples.mvc.tree_view1;

import java.util.NoSuchElementException;
import java.util.Stack;

import org.eclipse.swt.widgets.TreeItem;

import ru.syrzhn.samples.mvc.tree_view1.Viewer.IForm.States;
import ru.syrzhn.samples.mvc.tree_view1.model.MANode;
import ru.syrzhn.samples.mvc.tree_view1.model.MXMLNode;
import ru.syrzhn.samples.mvc.tree_view1.model.Model;

public class SourceController {
	
	private Viewer mViewer;
	
	private Model mModel;
	
	public SourceController(Viewer viewer) {
		mModel = new Model();
		//mModel = new Model(3, 3);
		//mModel = new Model("src\\ru\\syrzhn\\samples\\mvc\\tree_view1\\xml\\input.xml");
		mViewer = viewer;
	}
	
	public void setViewer(Viewer viewer) {
		mViewer = viewer;
	}	

	public void setState(TreeItem item) {
		MXMLNode node = (MXMLNode) item.getData();
		node.putData("TreeItem", item);
	}
	
	public ISource addNewData(Object o) {
		MXMLNode parentNode = (MXMLNode) o;
		if (parentNode == null) 
			throw new NoSuchElementException("Empty data in the item ".concat(o.toString()));
		String str = o.toString();
		mViewer.mForm.printMessage("Adding new node to ".concat(str));
		MXMLNode node = mModel.mDataTree.addNode(parentNode); 
		mViewer.mForm.printMessage(Model.messBuff);
		mViewer.mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		return new TreeSource(node);
	}

	public TreeItem[] disposeData(Object o) {
		MXMLNode parentNode = (MXMLNode) o;
		if (parentNode == null) 
			throw new NoSuchElementException("Empty data in the item ".concat(o.toString()));
		String str = o.toString();
		mViewer.mForm.printMessage("Disposing the node ".concat(str));
		Stack<MANode> dependents = mModel.mDataTree.disposeNode(parentNode);
		mViewer.mForm.printMessage(Model.messBuff);
		mViewer.mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		TreeItem items[] = new TreeItem[dependents.size()];
		int i = 0;
		for (MANode depend : dependents) {
			Object data = ((MXMLNode) depend).mData.get("TreeItem");
			if (data == null) continue;
			items[i++] = (TreeItem) data;
		}
		return items;
	}

	public String[] parseDataToItemColumns(Object data) {
		MXMLNode node = (MXMLNode) data;
		return new String[] { 
				node.toString(), 
				node.mPath, 
				node.getData("all").toString(),
				node.getData("xmlNodeType").toString(), 
				node.mAncestors.toString() 
		};
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
	
	public ISource[] getSource(Object node) {
		mViewer.mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		if (node == null)
			return new TreeSource().getBeginDataSet();
		else
			return new TreeSource((MXMLNode) node).getBeginDataSet();
	}
	
	public class TreeSource implements ISource {
		private MXMLNode mChildren[];
		private MXMLNode mSource;
		
		public TreeSource() {
			mChildren = mModel.getDataTreeData(null);
		}
		
		public TreeSource(MXMLNode node) {
			if (node == null) throw new RuntimeException("Empty node!");
			mSource = node;
			mChildren = mModel.getDataTreeData(mSource);
		}
		
		public MXMLNode getData() {return mSource;}
		
		public ISource[] getBeginDataSet() {
			TreeSource arg[] = new TreeSource[mChildren.length];
			for (int i = 0; i < mChildren.length; i++) {
				arg[i] = new TreeSource(mChildren[i]);
			}
			return arg;
		}
		
		@Override
		public ISource[] getChildren(ISource parent) {
			MXMLNode node = (MXMLNode) parent.getData();
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
				MXMLNode child = mChildren[i];
				if (i == 0)
					s.concat(child.toString());
				else
					s.concat(",").concat(child.toString());
			}
			return s;
		}
	}

	public TreeItem searchByPath(String path) {
		MXMLNode node = mModel.mDataTree.findNodeByPath(path);
		if (node == null) return null;
		return (TreeItem) node.mData.get("TreeItem");
	}

	public TreeItem[] getAncestors(TreeItem item) {
		MXMLNode node = (MXMLNode) item.getData();
		Stack<MANode> ancestors = node.mAncestors;
		int size = ancestors.size();
		TreeItem items[] = new TreeItem[size - 1];
		for (int i = 1; i < size; i++) {
			Object data = ((MXMLNode) ancestors.get(i)).mData.get("TreeItem");
			if (data == null) continue;
			items[i - 1] = (TreeItem) data;
		}
		return items;
	}
	
	public TreeItem[] getDescendants(TreeItem item) {
		MXMLNode node = (MXMLNode) item.getData();
		Stack<MANode> descendants = new Stack<MANode>();
		descendants = node.getDescendants(descendants);
		TreeItem items[] = new TreeItem[descendants.size()];
		int i = 0;
		for (MANode descendant : descendants) {
			Object data = ((MXMLNode) descendant).mData.get("TreeItem");
			if (data == null) continue;
			items[i++] = (TreeItem) data;
		}
		return items;
	}
}


