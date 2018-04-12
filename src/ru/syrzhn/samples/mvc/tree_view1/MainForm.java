package ru.syrzhn.samples.mvc.tree_view1;

import java.util.List;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainForm extends Dialog implements IController {
	
	private Object mTarget;
	private volatile boolean isBusy;
	public volatile Thread mReadDataFromSource;
	private List<Thread> mDataReaders;
	
	public MainForm(Shell parent, int style, Object target) {
		super(parent, style);
		setText("Список исключаемых из отчёта узлов");
		mTarget = target;
		mDataReaders = new Stack<Thread>();
	}

	/** Open the window */
	public void open() {
		display = Display.getDefault();
		
		mSource = new SourceAdapter(this);
		mReadDataFromSource = mSource.writeDataToMTree();
		//mReadDataFromSource.start();
		mViewer = new SwtTreeViewer(this);
		mHtml   = new HTMLViewer   (this);
		createContents();
		mDataReaders.add(mViewer.getItemsFromMTree(tree));
		mDataReaders.add(mHtml  .getRowsFromMTree ()    );
		for (Thread t : mDataReaders) 
			t.start();
		"".toCharArray();
		shlMainForm.open();
		shlMainForm.layout();
		while (!shlMainForm.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	@Override
	public void waitForWritingToMTree() {
		Thread t = getWritingThread();
		if (t != null && t.isAlive()) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Thread getWritingThread() { return mReadDataFromSource; }
	
	@Override
	public List<Thread> getReadingThreads() { return mDataReaders; }
	
	private Shell shlMainForm;
	private Tree tree;
	private Browser browser;
	
	private SwtTreeViewer mViewer;
	public HTMLViewer mHtml;
	private Display display;
	SourceAdapter mSource;
	
	@Override
	public SourceAdapter getSourceAdapter() { return mSource; }

	@Override
	public Object getData() { return mTarget; }
	
	@Override
	public void updateState(States state, Object o) {
		switch (state) {
		case CAPTION :
			display.asyncExec(() -> shlMainForm.setText("Tree sample ".concat(o.toString())) );
			break;
		case TREE_ITEM :
			break;
		}
	}

	@Override
	public void showMessage(String msg) {
		display.asyncExec(() -> {
				MessageBox msgBox = new MessageBox(shlMainForm, SWT.ICON_INFORMATION);
				msgBox.setText("Test application for tree №1");
				msgBox.setMessage(msg);
				msgBox.open();	
			}
		);
	}
	
	@Override
	public void setBusy(boolean busy) {	isBusy = busy; }

	@Override
	public boolean getBusy() { return isBusy; }

	@Override
	public void printMessage(Object m) {
		if (m instanceof String)
			System.out.println(m);
		else if (m instanceof String[]) {
			String msgs[] = (String[]) m; 
			for (String msg : msgs)
				System.out.println(msg);
		}
		else if (m instanceof List) {
			List<?> msgs = (List<?>) m;
			for (Object msg : msgs)
				System.out.println(msg);
			msgs.clear();
		}
	}

	@Override
	public Display getDisplay() { return display; }
	
	@Override
	public String getSearch() { return comboSearch.getText().trim(); }
	
	@Override
	public void setBrowser(String html) { 
			display.asyncExec(() -> {
					browser.setText(html); 
			}
		);
	}
	
	private Combo comboSearch;

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlMainForm = new Shell();
		shlMainForm.setImage(SWTResourceManager.getImage(MainForm.class, "/ru/syrzhn/samples/mvc/tree_view1/res/tree1.png"));
		shlMainForm.setSize(1280, 720);
		shlMainForm.setText("Tree sample");
		shlMainForm.setLayout(new GridLayout(3, false));
		
		ToolBar toolBar = new ToolBar(shlMainForm, SWT.FLAT | SWT.RIGHT);
		toolBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.addSelectionListener(mViewer.getNewItemSelectionAdapter());
		tltmNewItem.setImage(SWTResourceManager.getImage(MainForm.class, "/ru/syrzhn/samples/mvc/tree_view1/res/new1.png"));
		
		ToolItem tltmDeleteItem = new ToolItem(toolBar, SWT.NONE);
		tltmDeleteItem.setImage(SWTResourceManager.getImage(MainForm.class, "/ru/syrzhn/samples/mvc/tree_view1/res/delete1.png"));
		tltmDeleteItem.addSelectionListener(mViewer.getDeleteItemSelectionAdapter());
		
		comboSearch = new Combo(shlMainForm, SWT.NONE);
		comboSearch.addKeyListener(mViewer.comboSearchHandler.getKeyAdapter());
		comboSearch.addSelectionListener(mViewer.comboSearchHandler.getSelectionAdapter());
		comboSearch.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		GridData gd_comboSearch = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboSearch.heightHint = 35;
		comboSearch.setLayoutData(gd_comboSearch);
		
		ToolBar toolBarSearch = new ToolBar(shlMainForm, SWT.FLAT | SWT.RIGHT);
		
		ToolItem tltmGo = new ToolItem(toolBarSearch, SWT.NONE);
		tltmGo.addSelectionListener(mViewer.getSearchSelectionAdapter());
		tltmGo.setImage(SWTResourceManager.getImage(MainForm.class, "/ru/syrzhn/samples/mvc/tree_view1/res/search1.png"));
		
		TabFolder tabFolder = new TabFolder(shlMainForm, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		
		TabItem tbtmTree = new TabItem(tabFolder, SWT.NONE);
		tbtmTree.setText("Tree");
		
		tree = new Tree(tabFolder, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		tbtmTree.setControl(tree);
		tree.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		tree.setHeaderVisible(true);
		tree.addListener(SWT.Collapse, mViewer.getTableEventListener(SWT.Collapse));
		tree.addListener(SWT.Expand, mViewer.getTableEventListener(SWT.Expand));
		//tree.addListener(SWT.CHECK, viewer.getTableEventListener(SWT.CHECK));
		tree.addListener(SWT.Selection, mViewer.getTableEventListener(SWT.Selection));
		
		TreeColumn trclmnId = new TreeColumn(tree, SWT.NONE);
		trclmnId.setMoveable(true);
		trclmnId.setWidth(300);
		trclmnId.setText("ID");
		
		TreeColumn trclmnPath = new TreeColumn(tree, SWT.NONE);
		trclmnPath.setMoveable(true);
		trclmnPath.setWidth(300);
		trclmnPath.setText("Path");
		
		TreeColumn trclmnData = new TreeColumn(tree, SWT.NONE);
		trclmnData.setMoveable(true);
		trclmnData.setWidth(200);
		trclmnData.setText("Data");
		
		TreeColumn trclmnType = new TreeColumn(tree, SWT.NONE);
		trclmnType.setMoveable(true);
		trclmnType.setWidth(100);
		trclmnType.setText("Type");		
		
				TreeColumn trclmnAncestors = new TreeColumn(tree, SWT.NONE);
				trclmnAncestors.setMoveable(true);
				trclmnAncestors.setWidth(300);
				trclmnAncestors.setText("Ancestors");		
		
		TabItem tbtmHtml = new TabItem(tabFolder, SWT.NONE);
		tbtmHtml.setText("HTML");
		
		browser = new Browser(tabFolder, SWT.NONE);
		tbtmHtml.setControl(browser);
	}
}
