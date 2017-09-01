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
	
	public IData setData() {
		String str = mViewer.mCurrentItem.toString();
		mViewer.mForm.printMessage("Adding new node to ".concat(str));
		str = mViewer.mCurrentItem.getText(0);
		MNode node = mModel.mTree.addChild(str);
		mViewer.mForm.printMessage(Model.messBuff.toArray( new String[ Model.messBuff.size() ] )); Model.messBuff.clear();
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
		return new TreeData(node.mID);
	}

	public void disposeData() {
		String str = mViewer.mCurrentItem.toString();
		mViewer.mForm.printMessage("Disposing the node ".concat(str));
		mViewer.mForm.printMessage(mModel.mTree.disposeChild(mViewer.mCurrentItem.getText(0))); Model.messBuff.clear();
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
	
	public interface IData {
		IData[] getChildren(IData parent);
	}
	
	public IData[] getData(IData parent) {
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
		return new TreeData(parent).getData();
	}

	public IData[] getData() {
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
		return new TreeData().getData();
	}

	public IData[] getData(String ID) {
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodes.size()).concat(" nodes in the tree")}));
		return new TreeData(ID).getData();
	}
	
	public class TreeData implements IData {
		private String    mID;
		private MNode mData[];		
		public TreeData(IData parent) {
			if (parent == null) {
				mData = mModel.getTreeData(null);
			}
			else {
				MNode node = mModel.mTree.findNode(parent.toString());
				if (node == null) return;
				mData = mModel.getTreeData(node);
			}
		}
		public TreeData() {
			mData = mModel.getTreeData(null);
		}
		public TreeData(String ID) {
			if (ID == null) return;
			mID = ID;
			MNode node = mModel.mTree.findNode(ID);
			if (node == null) return;
			mData = mModel.getTreeData(node);
		}
		public IData[] getData() {
			TreeData arg[] = new TreeData[mData.length];
			for (int i = 0; i < mData.length; i++) {
				arg[i] = new TreeData(mData[i].toString());
			}
			return arg;
		}
		@Override
		public IData[] getChildren(IData parent) {
			MNode node = mModel.mTree.findNode(parent.toString());
			if (node == null) return null;
			mData = mModel.getTreeData(node);
			TreeData ret[] = new TreeData[mData.length];
			for (int i = 0; i < mData.length; i++) {
				ret[i] = new TreeData(mData[i].toString());
			}
			return ret;
		}
		@Override
		public String toString() {
			return mID;
		}
	}
}


