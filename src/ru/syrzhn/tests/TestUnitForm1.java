package ru.syrzhn.tests;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import ru.syrzhn.samples.mvc.tree_view1.MainForm;
import ru.syrzhn.samples.mvc.tree_view1.data.Sqlite;
import ru.syrzhn.samples.mvc.tree_view1.data.XmlFile;

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
		
		Button btnRunSqlite = new Button(shell, SWT.NONE);
		GridData gd_btnRunSqlite = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_btnRunSqlite.heightHint = 44;
		gd_btnRunSqlite.widthHint = 533;
		btnRunSqlite.setLayoutData(gd_btnRunSqlite);
		btnRunSqlite.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnRunSqlite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Sqlite data = new Sqlite("src/ru/syrzhn/samples/mvc/tree_view1/data/chinook.db");
					MainForm window = new MainForm(shell, SWT.APPLICATION_MODAL, data);
					window.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnRunSqlite.setText("Run the\r\n \"src/ru/syrzhn/samples/mvc/tree_view1/data/chinook.db\"");
		
		Button btnRunXmlFile = new Button(shell, SWT.NONE);
		btnRunXmlFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				XmlFile data = new XmlFile("src\\ru\\syrzhn\\samples\\mvc\\tree_view1\\xml\\input.xml");
				MainForm window = new MainForm(shell, SWT.APPLICATION_MODAL, data);
				window.open();
			}
		});
		btnRunXmlFile.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		btnRunXmlFile.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.NORMAL));
		btnRunXmlFile.setText("Run the\r\n \"src\\ru\\syrzhn\\samples\\mvc\\tree_view1\\xml\\input.xml\"");
	}

}
