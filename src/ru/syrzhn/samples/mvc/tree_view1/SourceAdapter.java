package ru.syrzhn.samples.mvc.tree_view1;

import java.util.NoSuchElementException;
import java.util.Stack;

import org.eclipse.swt.widgets.TreeItem;

import ru.syrzhn.samples.mvc.tree_view1.IController.States;
import ru.syrzhn.samples.mvc.tree_view1.model.ISource;
import ru.syrzhn.samples.mvc.tree_view1.model.MANode;
import ru.syrzhn.samples.mvc.tree_view1.model.MXmlNode;
import ru.syrzhn.samples.mvc.tree_view1.model.Model;

public class SourceAdapter {
	
	private IController mForm;	
	private Model mModel;
	private ISource mDatabaseData;
	
	public volatile boolean isLoaded;
	
	public SourceAdapter(IController form) {
		mForm = form;
		mDatabaseData = (ISource) mForm.getData();
		mModel = new Model();
	}
	
	public String[] getTextDataFromTreeNode(Object data) {
		MXmlNode node = (MXmlNode) data;
		return new String[] { 
				node.getData("xmlNodeName").toString(), 
				node.mPath, 
				node.getData("xmlNodeValue", "row_number").toString(),
				node.getData("xmlNodeType").toString(), 
				node.mAncestors.toString() 
		};
	}

	public boolean getSelectFromTreeNode(Object o) {
		MXmlNode node = (MXmlNode) o;
		boolean expanded = node.getData("tree_actions").toString().indexOf("select") > -1;
		return expanded;
	}

	public void setState(TreeItem item) {
		MXmlNode node = (MXmlNode) item.getData();
		node.putData("TreeItem", item);
	}	

	public ISource[] getSource(Object node) {
		ISource[] s = null;
		if (node == null)
			s = new TreeSource().getBeginDataSet();
		else
			s = new TreeSource((MXmlNode)node).getBeginDataSet();
		mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		return s;
	}

	public Thread writeDataToMTree() {
		return new Task("Connecting & reading the data", mForm) {
			@Override
			protected void doTask() {
				isLoaded = mModel.createData(mDatabaseData.getData());
				"".toCharArray();
			}
		};
	}
	
	public class TreeSource implements ISource {
		private MANode mChildren[];
		private MANode mSource;
		
		public TreeSource() {
			mChildren = mModel.getDataFromTree(null);
		}
		
		public TreeSource(MANode node) {
			if (node == null) throw new RuntimeException("Empty node!");
			mSource = node;
			mChildren = mModel.getDataFromTree(mSource);
		}
		
		public MANode getData() { return mSource; }
		
		public ISource[] getBeginDataSet() {
			TreeSource arg[] = new TreeSource[mChildren.length];
			for (int i = 0; i < mChildren.length; i++) {
				arg[i] = new TreeSource(mChildren[i]);
			}
			return arg;
		}
		
		@Override
		public ISource[] getChildren(ISource parent) {
			MXmlNode node = (MXmlNode) parent.getData();
			if (node == null) return null;
			mChildren = mModel.getDataFromTree(node);
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
				MANode child = mChildren[i];
				if (i == 0)
					s.concat(child.toString());
				else
					s.concat(",").concat(child.toString());
			}
			return s;
		}
	}

	public TreeItem searchByPath(String path) {
		MXmlNode node = (MXmlNode) mModel.mDataTree.findNodeByPath(path);
		if (node == null) return null;
		return (TreeItem) node.getData("TreeItem");
	}

	public ISource addNewData(Object o) {
		MXmlNode parentNode = (MXmlNode) o;
		if (parentNode == null) 
			throw new NoSuchElementException("Empty data in the item ".concat(o.toString()));
		String str = o.toString();
		mForm.printMessage("Adding new node to ".concat(str));
		MANode node = mModel.mDataTree.addNode(parentNode); 
		mForm.printMessage(Model.messBuff);
		mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		return new TreeSource(node);
	}

	public TreeItem[] disposeData(Object o) {
		MXmlNode parentNode = (MXmlNode) o;
		if (parentNode == null) 
			throw new NoSuchElementException("Empty data in the item ".concat(o.toString()));
		String str = o.toString();
		mForm.printMessage("Disposing the node ".concat(str));
		Stack<MANode> dependents = mModel.mDataTree.disposeNode(parentNode);
		mForm.printMessage(Model.messBuff);
		mForm.updateState(States.CAPTION, String.valueOf(mModel.mDataTree.mAllNodesCount).concat(" nodes in the tree"));
		TreeItem items[] = new TreeItem[dependents.size()];
		int i = 0;
		for (MANode depend : dependents) {
			Object data = ((MXmlNode) depend).getData("TreeItem");
			if (data == null) continue;
			items[i++] = (TreeItem) data;
		}
		return items;
	}

	public void setDataOnCollapse(Object data) {
		String str = data.toString() + " was collapsed";
		mForm.printMessage(str);
	}

	public void setDataOnExpand(Object data) {
		String str = data.toString() + " was expanded";
		mForm.printMessage(str);
	}

	public void setDataOnSelection(Object data) {
		String str = data.toString() + " was selected";
		mForm.printMessage(str);
	}

	public void setDataOnCheck(Object data) {
		MXmlNode node = (MXmlNode) data;
		TreeItem item = (TreeItem) node.getData("TreeItem");
		String str = data.toString() + (item.getChecked() ? " was checked" : " was unchecked");
		mForm.printMessage(str);
	}
}