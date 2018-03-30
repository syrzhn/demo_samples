package ru.syrzhn.samples.mvc.tree_view1.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ru.syrzhn.samples.mvc.tree_view1.model.XmlUtils;

public class Sqlite {
	
	 /** Connect to a sample database */
    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:src/ru/syrzhn/samples/mvc/tree_view1/data/chinook.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

	public Stack<String> messages;
	public int progress;
	
	public Sqlite[] mChildren;

	public List<Map<String, String>> mXmlNodeData;
	public Document mDoc;
	
	public Sqlite(Object data) {
		String name = "!!!EMPTYNESS!!!", ID = name;
		// TODO Auto-generated constructor stub
		mXmlNodeData = new ArrayList<Map<String, String>>();
		HashMap<String, String> tagID = new HashMap<String, String>();
		tagID.put("tagName", "ID");
		tagID.put("tagValue", ID);
		tagID.put("tagType", "text");
		mXmlNodeData.add(tagID);
		HashMap<String, String> tagName = new HashMap<String, String>();
		tagName.put("tagName", "name");
		tagName.put("tagValue", name);
		tagName.put("tagType", "text");
		mXmlNodeData.add(tagName);
	}
	
	private Element createRow(final String row_number) {
		Element elRow  = mDoc.createElement("row");
		for (Map<String, String> map : mXmlNodeData) {
			Element el = mDoc.createElement(map.get("tagName"));
			elRow.appendChild(el);
			el.setTextContent(map.get("tagValue"));
			for (String key : map.keySet()) {
				if (key.indexOf("tagName") > -1 || key.indexOf("tagValue") > -1) continue;
				el.setAttribute(key, map.get(key));
			}
		}
		elRow.setAttribute("row_number", row_number);
		return elRow;
	}

	public Document getDocument() {
		connect();
		mDoc = XmlUtils.createXmlDocument();
		Element root = mDoc.getDocumentElement();
		Element fstEl = createRow("main row");
		root.appendChild(fstEl);
		Object[] children = null;
		// TODO Auto-generated method stub
		int size = children.length;
		mChildren = new Sqlite[size];
		for (int i = 0; i < size; i++) {
			mChildren[i] = new Sqlite(children[i]);
			mChildren[i].mDoc = mDoc;
			Element rowI = mChildren[i].createRow(i + "");
			fstEl.appendChild(rowI);
		}
		XmlUtils.saveToFile(mDoc, "src\\ru\\syrzhn\\samples\\mvc\\tree_view1\\xml\\output.xml");
		
		return mDoc;
	}
}
