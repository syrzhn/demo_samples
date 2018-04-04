package ru.syrzhn.samples.mvc.tree_view1.data;

import org.w3c.dom.Document;

import ru.syrzhn.samples.mvc.tree_view1.model.ISource;
import ru.syrzhn.samples.mvc.tree_view1.model.XmlUtils;

public class XmlFile implements ISource {
	
	private Document mDoc;
	private String mFileName;
	
	public XmlFile(String fileName) {
		mFileName = fileName;
	}
	
	private Document getDocument() {
		mDoc = XmlUtils.loadFromFile(mFileName);
		return mDoc;
	}

	@Override
	public ISource[] getChildren(ISource parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getData() {
		return getDocument();
	}

}
