package ru.syrzhn.samples.mvc.tree_view1.data;

import ru.syrzhn.samples.mvc.tree_view1.model.ISource;

public class TestData implements ISource {
	private int mLevels, mRows;
	
	public TestData(int levels, int rows) {
		mLevels = levels; mRows = rows;
	}

	@Override
	public ISource[] getChildren(ISource parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getData() {
		return new int[] { mLevels, mRows };
	}
}
