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
		mModel.mTree.generation++;
		str = ((MNode)mViewer.mCurrentItem.getData()).mPath;
		MNode node = mModel.mTree.addChild(str); 
		mViewer.mForm.printMessage(Model.messBuff.toArray( new String[ Model.messBuff.size() ] )); Model.messBuff.clear();
		mViewer.mForm.updateState(new Viewer.IForm.State( new String[] {String.valueOf(mModel.mTree.mAllNodesCount).concat(" nodes in the tree")} ));
		return new TreeSource(node.mPath);
	}

	public void disposeData() {
		String str = mViewer.mCurrentItem.toString();
		mViewer.mForm.printMessage("Disposing the node ".concat(str));
		mViewer.mForm.printMessage(mModel.mTree.disposeChild(((MNode)mViewer.mCurrentItem.getData()).mPath)); Model.messBuff.clear();
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodesCount).concat(" nodes in the tree")}));
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
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodesCount).concat(" nodes in the tree")}));
		return new TreeSource().getBeginDataSet();
	}

	public ISource[] getSource(String path) {
		mViewer.mForm.updateState(new Viewer.IForm.State(new String[] {String.valueOf(mModel.mTree.mAllNodesCount).concat(" nodes in the tree")}));
		return new TreeSource(path).getBeginDataSet();
	}
	
	public class TreeSource implements ISource {
		private String mPath;
		private MNode mChildren[];
		private MNode mSource;
		
		public TreeSource() {
			mChildren = mModel.getTreeData(null);
		}
		
		public TreeSource(String path) {
			if (path == null) return;
			mPath = path;
			mSource = mModel.mTree.findNode(mPath);
			if (mSource == null) return;
			mChildren = mModel.getTreeData(mSource);
		}
		
		public MNode getData() {return mSource;}
		
		public ISource[] getBeginDataSet() {
			TreeSource arg[] = new TreeSource[mChildren.length];
			for (int i = 0; i < mChildren.length; i++) {
				arg[i] = new TreeSource(mChildren[i].mPath);
			}
			return arg;
		}
		
		@Override
		public ISource[] getChildren(ISource parent) {
			MNode node = mModel.mTree.findNode(parent.toString());
			if (node == null) return null;
			mChildren = mModel.getTreeData(node);
			TreeSource ret[] = new TreeSource[mChildren.length];
			for (int i = 0; i < mChildren.length; i++) {
				ret[i] = new TreeSource(mChildren[i].mPath);
			}
			return ret;
		}
		
		@Override
		public String toString() {
			return mPath;
		}
	}
}


