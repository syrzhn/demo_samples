package ru.syrzhn.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ru.syrzhn.samples.mvc.tree_view1.MainForm;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridData;

public class TestUnitForm1 {

	protected Shell shell;

	/** Launch the application.
	 ** @param args */
	public static void main(String[] args) {
		try {
			TestUnitForm1 window = new TestUnitForm1();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Open the window */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/** Create contents of the window */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(560, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));
		
		Button btnRunTestTree = new Button(shell, SWT.NONE);
		GridData gd_btnRunTestTree = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_btnRunTestTree.heightHint = 44;
		gd_btnRunTestTree.widthHint = 533;
		btnRunTestTree.setLayoutData(gd_btnRunTestTree);
		btnRunTestTree.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnRunTestTree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					MainForm window = new MainForm(shell, SWT.APPLICATION_MODAL, "src/ru/syrzhn/samples/mvc/tree_view1/data/chinook.db");
					window.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnRunTestTree.setText("Run the\r\n \"src/ru/syrzhn/samples/mvc/tree_view1/data/chinook.db\"");
	}

}
