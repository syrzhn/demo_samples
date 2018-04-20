package ru.syrzhn.samples.mvc.tree_view1.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.syrzhn.samples.mvc.tree_view1.model.ISource;
import ru.syrzhn.samples.mvc.tree_view1.model.MXmlUtils;

public class Sqlite implements ISource {
	public List<String> messages;
	public int progress;	
	private List<Sqlite> mChildren;
	public List<Map<String, String>> mXmlNodeData;
	private Document mDoc;
	private String mDatabaseName;
	
	public Sqlite(Object data) {
		mXmlNodeData = new ArrayList<Map<String, String>>();
		mChildren    = new ArrayList<Sqlite>();
		if (data instanceof String) {
			mDatabaseName = data.toString();
			File file = new File(mDatabaseName);
			HashMap<String, String> tagName = new HashMap<String, String>();
			tagName.put("tagName", "database_name");
			tagName.put("tagValue", file.getName());
			tagName.put("tagType", "text");
			mXmlNodeData.add(tagName);
			HashMap<String, String> tagPath = new HashMap<String, String>();
			tagPath.put("tagName", "database_path");
			tagPath.put("tagValue", file.getAbsolutePath());
			tagPath.put("tagType", "text");
			mXmlNodeData.add(tagPath);
		} 
		else if (data instanceof ResultSet) {
			ResultSet resultSet = (ResultSet)data;
			try {
				HashMap<String, String> tagTabName = new HashMap<String, String>();
				tagTabName.put("tagName", "table_name");
				tagTabName.put("tagValue", resultSet.getString("name"));
				tagTabName.put("tagType", "text");
				tagTabName.put("tbl_name", resultSet.getString("tbl_name"));
				tagTabName.put("rootpage", resultSet.getInt("rootpage") + "");
				tagTabName.put("sql", resultSet.getString("sql"));
				mXmlNodeData.add(tagTabName);
				HashMap<String, String> tagTabType = new HashMap<String, String>();
				tagTabType.put("tagName", "table_type");
				tagTabType.put("tagValue", resultSet.getString("type"));
				tagTabType.put("tagType", "text");
				mXmlNodeData.add(tagTabType);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Element createRow(final String row_number) {
		Element elRow  = mDoc.createElement("row");
		elRow.setAttribute("row_number", row_number);
		for (Map<String, String> map : mXmlNodeData) {
			Element el = mDoc.createElement(map.get("tagName"));
			elRow.appendChild(el);
			el.setTextContent(map.get("tagValue"));
			for (String key : map.keySet()) {
				if (key.indexOf("tagName") > -1 || key.indexOf("tagValue") > -1) continue;
				el.setAttribute(key, map.get(key));
			}
		}
		return elRow;
	}

	private Document getDocument() {
		mDoc = MXmlUtils.createXmlDocument();
		Element root = mDoc.getDocumentElement();
		Element fstEl = createRow("main row");
		fstEl.setAttribute("tree_actions", "select");
		root.appendChild(fstEl);
		connect(mDatabaseName);
		int size = mChildren.size();
		for (int i = 0; i < size; i++) {
			mChildren.get(i).mDoc = mDoc;
			Element rowI = mChildren.get(i).createRow(i + "");
			fstEl.appendChild(rowI);
		}
		MXmlUtils.saveToFile(mDoc, "src\\ru\\syrzhn\\samples\\mvc\\tree_view1\\xml\\output.xml");
		
		return mDoc;
	}

	/** Connect to a database */
	public void connect(String dbName) {
		Connection conn = null;
		try {
			// db parameters
			String url = "jdbc:sqlite:" + dbName;
			// create a connection to the database
			conn = DriverManager.getConnection(url);
			Statement statement = conn.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			ResultSet resultSet = statement.executeQuery("select * from sqlite_master where type = 'table'");
			while (resultSet.next()) { // iterate & read the result set
				mChildren.add(new Sqlite(resultSet));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public ISource[] getChildren(ISource parent) {
		Sqlite arg[] = new Sqlite[mChildren.size()];
		mChildren.toArray(arg);
		return arg;
	}

	@Override
	public Object getData() {
		return getDocument();
	}
}
