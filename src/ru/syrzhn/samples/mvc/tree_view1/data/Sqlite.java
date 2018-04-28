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
/* test code

public class TceData implements ISource {
	public TCComponentBOMLine mBOMLine;
	public TCComponentItemRevision mItemRevision;
	public TCComponentForm mItemRevisionForm;
	public TCComponentItem mItem;
	public TCComponentForm mItemForm;
	public TceDataLine mTceDataLine;
	
	public Stack<String> messages;
	public int progress;
	
	private TceData[] mChildren;

	public List<Map<String, String>> mXmlNodeAttribtes;
	public Document mDoc;
	
	public TceData(Object data) {
		mXmlNodeAttribtes = new ArrayList<Map<String, String>>();
		if (data == null) return;
		if (data instanceof String) {
			HashMap<String, String> tagID = new HashMap<String, String>();			
			tagID.put("tagName", "ID");
			tagID.put("tagValue", data.toString());
			tagID.put("html", "table_row");
			tagID.put("html", "table_cell");
			mXmlNodeAttribtes.add(tagID);
			return;
		}
		mBOMLine = (TCComponentBOMLine)data;
		try {
			mItemRevision     = mBOMLine.getItemRevision();
			mItemRevisionForm = CommonUtils.FormComp(mItemRevision);
			mItem             = mItemRevision.getItem();
			mItemForm         = CommonUtils.FormComp(mItem);
		} catch (TCException e) {
			errorMessage("Не удалось получить ревизию из BOM линии \"" + data + "\"!");
			e.printStackTrace();
		}
	}
	
	private void setLine() {
		mTceDataLine = new TceDataLine(this);
		HashMap<String, String> tagID = new HashMap<String, String>();
		tagID.put("tagName", "ID");
		tagID.put("tagValue", mTceDataLine.get("Идентификатор"));
		tagID.put("html", "table_cell");
		mXmlNodeAttribtes.add(tagID);
		HashMap<String, String> tagName = new HashMap<String, String>();
		tagName.put("tagName", "name");
		tagName.put("tagValue", mTceDataLine.get("Наименование"));
		tagName.put("html", "table_cell");
		mXmlNodeAttribtes.add(tagName);
		HashMap<String, String> tagType = new HashMap<String, String>();
		tagType.put("tagName", "type");
		tagType.put("tagValue", mTceDataLine.get("Тип"));
		tagType.put("html", "table_cell");
		mXmlNodeAttribtes.add(tagType);
	}
	
	private Element createRow(final String row_number) {
		Element elRow = mDoc.createElement("row");
		Element rowNumber = mDoc.createElement("row_number");
		rowNumber.setTextContent(row_number);
		rowNumber.setAttribute("html", "table_cell");
		elRow.appendChild(rowNumber);
		for (Map<String, String> map : mXmlNodeAttribtes) {
			Element el = mDoc.createElement(map.get("tagName"));
			elRow.appendChild(el);
			el.setTextContent(map.get("tagValue"));
			for (String key : map.keySet()) {
				if (key.indexOf("tagName") > -1 || key.indexOf("tagValue") > -1) continue;
				el.setAttribute(key, map.get(key));
			}
		}
		elRow.setAttribute("html", "table_row");
		return elRow;
	}
	
	public Document getDocument() {
		setLine();
		mDoc = MXmlUtils.createXmlDocument();
		Element root = mDoc.getDocumentElement();
		Element fstEl = createRow("main row");
		fstEl.setAttribute("swt_actions", "select");
		root.appendChild(fstEl);
		
		TceData tceDoc= new TceData("Документация");
		tceDoc.mDoc = mDoc;
		Element elDoc = tceDoc.createRow("documents");
		fstEl.appendChild(elDoc);

		if (!mBOMLine.hasChildren()) return mDoc;
		AIFComponentContext[] children = null;
		try {
			children = mBOMLine.getChildren();
		} catch (TCException e) {
			errorMessage("Не удалось получить структуру из \"" + mBOMLine + "\"");
			e.printStackTrace();
		}
		int size = children.length;
		mChildren = new TceData[size];
		for (int i = 0; i < size; i++) {
			mChildren[i] = new TceData(children[i].getComponent());
			mChildren[i].setLine();
			mChildren[i].mDoc = mDoc;
			Element rowI = mChildren[i].createRow(i + "");
			fstEl.appendChild(rowI);
		}
		MXmlUtils.saveToFile(mDoc, "C:\\temp\\output.xml");
		
		return mDoc;
	}

	private void errorMessage(String string) { messages.push(string); }

	@Override
	public ISource[] getChildren(ISource parent) { return mChildren; }

	@Override
	public Object getData() { return getDocument(); }
}

public class TceDataLine {
	public int mNumber;
	public int mSize;
	public Map<String, TceDataCell> mRusIndexCells;
	public Map<String, TceDataCell> mTceIndexCells;
	public TceDataLine(TceData dataSource) {
		mRusIndexCells = new HashMap<String, TceDataCell>();
		mTceIndexCells = new HashMap<String, TceDataCell>();
		mCells = new ArrayList<TceDataCell>();
		mCells.add(new TceDataCell("Наименование",  "avid_DSE_NAME", TcePropertyStorePlace.ItemForm).set(dataSource));
		mCells.add(new TceDataCell("Идентификатор", "avid_DSE_ID",   TcePropertyStorePlace.ItemForm).set(dataSource));
		mCells.add(new TceDataCell("Тип",           "object_type",   TcePropertyStorePlace.Item)    .set(dataSource));
		for(TceDataCell cell : mCells) {
			mRusIndexCells.put(cell.mRusName, cell );
			mTceIndexCells.put(cell.mTceProprtyName, cell );
		}		
	}
	public List<TceDataCell> mCells;
	public String get(String key) {
		if (mRusIndexCells.containsKey(key))
			return mRusIndexCells.get(key).mValue;
		if (mTceIndexCells.containsKey(key))
			return mTceIndexCells.get(key).mValue;
		return null;
	}
}

class TceDataCell {
	public String mRusName;
	public String mTceProprtyName;
	public TcePropertyStorePlace mTceStorePlace;
	public TceDataCell(String rusName, String tcePropertyName, TcePropertyStorePlace place) {
		mRusName = rusName;
		mTceProprtyName = tcePropertyName;
		mTceStorePlace = place;
	}
	public TceDataCell set(TceData dataSource) {
		TCComponent source= null; 
		try {
			switch (mTceStorePlace) {
			case Item: 
				source = dataSource.mItem;
				mValue = ((TCComponentItem)source).getStringProperty(mTceProprtyName);
				break;
			case ItemForm: 
				source = dataSource.mItemForm; 
				mValue = ((TCComponentForm)source).getFormTCProperty(mTceProprtyName).getStringValue();
				break;
			case ItemRevision: 
				source = dataSource.mItemRevision; 
				mValue = ((TCComponentItemRevision)source).getStringProperty(mTceProprtyName);
				break;
			case ItemRevisionForm: 
				source = dataSource.mItemRevisionForm; 
				mValue = ((TCComponentForm)source).getFormTCProperty(mTceProprtyName).getStringValue();
				break;
			case BOMLine: 
				source = dataSource.mBOMLine; 
				mValue = ((TCComponentBOMLine)source).getTCProperty(mTceProprtyName).getStringValue();
				break;
			default:
				break;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return this;
	}
	public String mValue;
}

enum TcePropertyStorePlace { 
	Item,
	ItemRevision,
	ItemForm,
	ItemRevisionForm,
	BOMLine
}

*/