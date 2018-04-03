package ru.syrzhn.samples.mvc.tree_view1.model;

public interface ISource {
	ISource[] getChildren(ISource parent);
	Object getData();
}

