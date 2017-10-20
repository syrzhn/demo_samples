package ru.syrzhn.samples.mvc.tree_view1;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.SWTResourceManager;

public class MainForm implements Viewer.IForm {

	public Shell shlMainForm;
	private Tree tree;
	
	private Viewer viewer;
	private Display display;

	@Override
	public void updateState(State state) {
		final String caption = state.caption;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				shlMainForm.setText("Tree sample ".concat(caption));
			}
		});
	}

	@Override
	public void showMessage(String msg) {
		final String mMsg = msg;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox msgBox = new MessageBox(shlMainForm, SWT.ICON_INFORMATION);
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

	@Override
	public Display getDisplay() {
		return display;
	}
	
	@Override
	public Controller getController() {
		return controller;
	}

	@Override
	public String getSearch() {
		return comboSearch.getText().trim();
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MainForm window = new MainForm();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Controller controller;
	private Combo comboSearch;

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		
		viewer = new Viewer(this); 
		controller = new Controller(viewer);

		createContents();
		
		viewer.getItemsFromMTree(tree);
		
		TreeColumn trclmnAncestors = new TreeColumn(tree, SWT.NONE);
		trclmnAncestors.setMoveable(true);
		trclmnAncestors.setWidth(300);
		trclmnAncestors.setText("Ancestors");
		
		shlMainForm.open();
		shlMainForm.layout();
		while (!shlMainForm.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
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
		tltmNewItem.addSelectionListener(viewer.getNewItemSelectionAdapter());
		tltmNewItem.setImage(SWTResourceManager.getImage(MainForm.class, "/ru/syrzhn/samples/mvc/tree_view1/res/new1.png"));
		
		ToolItem tltmDeleteItem = new ToolItem(toolBar, SWT.NONE);
		tltmDeleteItem.setImage(SWTResourceManager.getImage(MainForm.class, "/ru/syrzhn/samples/mvc/tree_view1/res/delete1.png"));
		tltmDeleteItem.addSelectionListener(viewer.getDeleteItemSelectionAdapter());
		
		comboSearch = new Combo(shlMainForm, SWT.NONE);
		comboSearch.addKeyListener(viewer.comboSearchHandler.getKeyAdapter());
		comboSearch.addSelectionListener(viewer.comboSearchHandler.getSelectionAdapter());
		comboSearch.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		GridData gd_comboSearch = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboSearch.heightHint = 35;
		comboSearch.setLayoutData(gd_comboSearch);
		
		ToolBar toolBarSearch = new ToolBar(shlMainForm, SWT.FLAT | SWT.RIGHT);
		
		ToolItem tltmGo = new ToolItem(toolBarSearch, SWT.NONE);
		tltmGo.addSelectionListener(viewer.getSearchSelectionAdapter());
		tltmGo.setImage(SWTResourceManager.getImage(MainForm.class, "/ru/syrzhn/samples/mvc/tree_view1/res/search1.png"));
		
		tree = new Tree(shlMainForm, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.MULTI);
		tree.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		tree.addListener(SWT.Collapse, viewer.getTableEventListener(SWT.Collapse));
		tree.addListener(SWT.Expand, viewer.getTableEventListener(SWT.Expand));
		//tree.addListener(SWT.CHECK, viewer.getTableEventListener(SWT.CHECK));
		tree.addListener(SWT.Selection, viewer.getTableEventListener(SWT.Selection));
		
		TreeColumn trclmnId = new TreeColumn(tree, SWT.NONE);
		trclmnId.setMoveable(true);
		trclmnId.setWidth(400);
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
	}
}
