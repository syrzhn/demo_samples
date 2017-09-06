package ru.syrzhn.samples.mvc.tree_view1;

import ru.syrzhn.samples.mvc.tree_view1.model.MNode;
import ru.syrzhn.samples.mvc.tree_view1.model.Model;

public class Controller {
	
	private Viewer mViewer;
	
	private Model mModel;
	
	public Controller(Viewer viewer) {
		mModel = new Model(3, 3);
		mViewer = viewer;
	}
	
	public void setViewer(Viewer viewer) {
		mViewer = viewer;
	}
	
	public ISource setData() {
		String str = mViewer.mCurrentItem.toString();
		mViewer.mForm.printMessage("Adding new node to ".concat(str));
		str = ((MNode)mViewer.mCurrentItem.getData()).path;
		MNode node = mModel.mTree.addChild(str);
		mViewer.mForm.printMessage(Model.messBuff.toArray( new String[ Model.messBuff.size() ] )); Model.messBuff.clear();
		mViewer.mForm.updateState(new Viewer.IForm.State( new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")} ));
		return new TreeSource(node.mID);
	}

	public void disposeData() {
		String str = mViewer.mCurrentItem.toString();
		mViewer.mForm.printMessage("Disposing the node ".concat(str));
		mViewer.mForm.printMessage(mModel.mTree.disposeChild(((MNode)mViewer.mCurrentItem.getData()).path)); Model.messBuff.clear();
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
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
		Object  getData();
	}
	
	public ISource[] getSource(ISource parent) {
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
		return new TreeSource(parent).getBeginDataSet();
	}

	public ISource[] getSource() {
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
		return new TreeSource().getBeginDataSet();
	}

	public ISource[] getSource(String ID) {
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
		return new TreeSource(ID).getBeginDataSet();
	}
	
	public class TreeSource implements ISource {
		private String    mID;
		private MNode mData[];
		private MNode data;
		public TreeSource(ISource parent) {
			if (parent == null) {
				mData = mModel.getTreeData(null);
			}
			else {
				MNode node = mModel.mTree.findNode(parent.toString());
				if (node == null) return;
				mData = mModel.getTreeData(node);
			}
		}
		public TreeSource() {
			mData = mModel.getTreeData(null);
		}
		public TreeSource(String ID) {
			if (ID == null) return;
			mID = ID;
			data = mModel.mTree.findNode(mID);
			if (data == null) return;
			mData = mModel.getTreeData(data);
		}
		public MNode getData() {return data;}
		public ISource[] getBeginDataSet() {
			TreeSource arg[] = new TreeSource[mData.length];
			for (int i = 0; i < mData.length; i++) {
				arg[i] = new TreeSource(mData[i].toString());
			}
			return arg;
		}
		@Override
		public ISource[] getChildren(ISource parent) {
			MNode node = mModel.mTree.findNode(parent.toString());
			if (node == null) return null;
			mData = mModel.getTreeData(node);
			TreeSource ret[] = new TreeSource[mData.length];
			for (int i = 0; i < mData.length; i++) {
				ret[i] = new TreeSource(mData[i].toString());
			}
			return ret;
		}
		@Override
		public String toString() {
			return mID;
		}
	}
}


