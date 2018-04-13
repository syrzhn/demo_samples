package ru.syrzhn.samples.mvc.tree_view1;

public abstract class Task extends Thread {
	
	private String mName;
	private IController mContr;
	protected abstract void doTask();
	
	public Task(String name, IController contr) {
		mName = name;
		mContr = contr;
	}
	
	public void run() {
		mContr.setBusy(true);
		long start = System.currentTimeMillis();
		doTask();
		long end = System.currentTimeMillis();
		mContr.printMessage("Time to execute the task \"".concat(mName).concat("\" in millis: ").concat(String.valueOf(end - start)));
		mContr.setBusy(false);
	}
}