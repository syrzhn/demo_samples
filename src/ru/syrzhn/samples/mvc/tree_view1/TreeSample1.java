package ru.syrzhn.samples.mvc.tree_view1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;

public class TreeSample1 implements Viewer.IForm {

	public Shell shlTreeSample;
	private Tree tree;
	
	private Viewer viewer;
	private Display display;
	private Text txtSearch;

	@Override
	public void updateState(State state) {
		final String caption = state.caption;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				TreeSample1.this.shlTreeSample.setText("Tree sample ".concat(caption));
			}
		});
	}

	@Override
	public void showMessage(String msg) {
		final String mMsg = msg;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox msgBox = new MessageBox(shlTreeSample, SWT.ICON_INFORMATION);
				msgBox.setText("Test application for tree ¹1");
				msgBox.setMessage(mMsg);
				msgBox.open();	
			}
		});
	}

	@Override
	public void printMessage(String[] msgs) {
		for (String msg : msgs)
			System.out.println(msg);
	}

	@Override
	public void printMessage(String msg) {
		System.out.println(msg);
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TreeSample1 window = new TreeSample1();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Controller controller;

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		
		controller = new Controller(this);
		viewer = controller.getViewer();
 
		long start = System.currentTimeMillis();
		createContents();
		long end = System.currentTimeMillis();
		printMessage("Time to fill the tree in millis: ".concat(String.valueOf(end - start)));
		
		viewer.getTestData(tree);
		
		shlTreeSample.open();
		shlTreeSample.layout();
		while (!shlTreeSample.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlTreeSample = new Shell();
		shlTreeSample.setImage(SWTResourceManager.getImage(TreeSample1.class, "/ru/syrzhn/samples/mvc/tree_view1/res/tree1.png"));
		shlTreeSample.setSize(1280, 720);
		shlTreeSample.setText("Tree sample");
		shlTreeSample.setLayout(new GridLayout(3, false));
		
		ToolBar toolBar = new ToolBar(shlTreeSample, SWT.FLAT | SWT.RIGHT);
		toolBar.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		
		ToolItem tltmNew = new ToolItem(toolBar, SWT.NONE);
		tltmNew.addSelectionListener(viewer.getNewSelectionAdapter());
		tltmNew.setImage(SWTResourceManager.getImage(TreeSample1.class, "/ru/syrzhn/samples/mvc/tree_view1/res/new1.png"));
		
		ToolItem tltmDelete = new ToolItem(toolBar, SWT.NONE);
		tltmDelete.setImage(SWTResourceManager.getImage(TreeSample1.class, "/ru/syrzhn/samples/mvc/tree_view1/res/delete1.png"));
		tltmDelete.addSelectionListener(viewer.getDeleteSelectionAdapter());
		
		txtSearch = new Text(shlTreeSample, SWT.BORDER);
		txtSearch.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		GridData gd_txtSearch = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtSearch.heightHint = 24;
		txtSearch.setLayoutData(gd_txtSearch);
		
		ToolBar toolBarSearch = new ToolBar(shlTreeSample, SWT.FLAT | SWT.RIGHT);
		
		ToolItem tltmGo = new ToolItem(toolBarSearch, SWT.NONE);
		tltmGo.setImage(SWTResourceManager.getImage(TreeSample1.class, "/ru/syrzhn/samples/mvc/tree_view1/res/search1.png"));
		
		tree = new Tree(shlTreeSample, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		tree.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		tree.addListener(SWT.Collapse, viewer.getTableEventListener(SWT.Collapse));
		tree.addListener(SWT.Expand, viewer.getTableEventListener(SWT.Expand));
		//tree.addListener(SWT.CHECK, viewer.getTableEventListener(SWT.CHECK));
		tree.addListener(SWT.Selection, viewer.getTableEventListener(SWT.Selection));
		
		TreeColumn trclmnId = new TreeColumn(tree, SWT.NONE);
		trclmnId.setWidth(450);
		trclmnId.setText("ID");
		
		TreeColumn trclmnData = new TreeColumn(tree, SWT.NONE);
		trclmnData.setWidth(200);
		trclmnData.setText("Data");
		
		TreeColumn trclmnType = new TreeColumn(tree, SWT.NONE);
		trclmnType.setWidth(100);
		trclmnType.setText("Type");		
	}
}
