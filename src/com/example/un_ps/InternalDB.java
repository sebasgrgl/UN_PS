package com.example.un_ps;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InternalDB {
	
	private DBHelper ourHelper; //Create a Helper object
	private final Context ourContext; //Create a Context object
	
	public static final int DB_VERSION = 1; //Version number, can be any number
	public static final String DB_NAME = "massey.db"; //Name of the database
	
	
	public class DBHelper extends SQLiteOpenHelper { //Helps create DB		
		//Constructor
		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) { //Called once and only once for a User to create a DB
			String query = "CREATE TABLE Subjects"
					+ "(SName TEXT PRIMARY KEY," //SName - Paper number 
					+ "STeacher TEXT," //STeacher - Paper Name
					+ "SCode TEXT," //SCode - Paper Code 
					+ "SColor TEXT" //color
					+ ");";
				db.execSQL(query);
				query = "CREATE TABLE Hours"
						+ "(_id INTEGER AUTO_INCREMENT PRIMARY KEY," //id
						+ "SName TEXT," //FK
						+ "HType TEXT," //type
						+ "HDay INTEGER," // Day
						+ "HClass INTEGER," //Class & Type
						+ "HStart TEXT," //Hour Start
						+ "HEnd TEXT" //Hour End
						+ ");";
				db.execSQL(query);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //Runs only if a new version of DB (maybe you've added new tables or rows in an update)

			db.execSQL("DROP TABLE IF EXISTS Hours");
			db.execSQL("DROP TABLE IF EXISTS Subjects");
	
		    this.onCreate(db);
		}

	}
	//Class Constructor
	public InternalDB(Context c) {
		ourContext = c; //Initialise the context with passed in context
		
	}
	
	//Open method
	public InternalDB open() throws SQLException {
		ourHelper = new DBHelper(ourContext);
		return this;
	}
	
	public void close() {
		ourHelper.close();
	}
	
	//METHODS FOR DATA INSERTION****************************************************
	
	//METHODS FOR DATA RETRIEVAL****************************************************
	
	//***********************************************************************************
	
	public void insertIntoSubjects(String SName, String SCode, String STeacher, String SColor) {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("SName", SName);
			values.put("SCode", SCode);
			values.put("STeacher", STeacher);
			values.put("SColor", SColor);
			db.insert("Subjects", "", values);
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
	public void updateSubjects(String SName, String SCode, String STeacher, String SColor) {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("SCode", SCode);
			values.put("STeacher", STeacher);
			values.put("SColor", SColor);
			db.update("Subjects", values, "SName='"+SName+"'", null);
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
	public void deleteSubjects(String SName) {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			db.delete("Subjects", "SName='"+SName+"'", null);
			db.delete("Hours", "SName='"+SName+"'", null);
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
	public void updateHours(String OLDSName, String OLDHType, String OLDHClass, int OLDHDay, String OLDHStart, String OLDHEnd,
			String SName, int HDay, String HType, String HClass, String HStart, String HEnd) {
		
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("SName", SName);
			values.put("HDay", HDay);
			values.put("HType", HType);
			values.put("HClass", HClass);
			values.put("HStart", HStart);
			values.put("HEnd", HEnd);
			db.update("Hours", values, "SName='"+OLDSName+"'" +
					"AND HType='"+OLDHType+"'" +
					"AND HClass='"+OLDHClass+"'" +
					"AND HDay='"+OLDHDay+"'" +
					"AND HStart='"+OLDHStart+"'" +
					"AND HEnd='"+OLDHEnd+"'", null);
		} finally {
			if(db!= null)
				db.close();
		}
		
	}
	
	public ArrayList<HashMap<String, Object>> selectSubjects() {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			ArrayList<HashMap<String, Object>> results =
				new ArrayList<HashMap<String, Object>>();
			Cursor c = db.rawQuery("select * from Subjects",null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					HashMap<String,Object> resultsMap =
						new HashMap<String, Object>();
					resultsMap.put("SName" , c.getString(c.getColumnIndex("SName")));
					resultsMap.put("SCode" , c.getString(c.getColumnIndex("SCode")));
					resultsMap.put("STeacher" , c.getString(c.getColumnIndex("STeacher")));
					resultsMap.put("SColor" , c.getString(c.getColumnIndex("SColor")));
					results.add(resultsMap);
				} while(c.moveToNext());
			}
			c.close();
			return results;
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
	public void insertIntoHours(String SName, int HDay, String HType, String HClass, String HStart, String HEnd) {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put("SName", SName);
			values.put("HDay", HDay);
			values.put("HType", HType);
			values.put("HClass", HClass);
			values.put("HStart", HStart);
			values.put("HEnd", HEnd);
			db.insert("Hours", "", values);
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
	public void deleteHour(String SName, int HDay, String HType, String HClass, String HStart, String HEnd) {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			db.delete("Hours",
					"SName = '"+SName+"' " +
					"AND HDay = '"+HDay+"' " +
					"AND HType = '"+HType+"' " +
					"AND HClass = '"+HClass+"' " +
					"AND HStart = '"+HStart+"' " +
					"AND HEnd = '"+HEnd+"' ", null);
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
	public void deleteAllHours() {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			db.delete("Hours", null, null);
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
	public ArrayList<HashMap<String, Object>> selectAllFromDay(int HDay) {
		SQLiteDatabase db = ourHelper.getWritableDatabase();
		try {
			ArrayList<HashMap<String, Object>> results =
				new ArrayList<HashMap<String, Object>>();
			Cursor c = db.rawQuery("select * from Subjects, Hours where Subjects.SName=Hours.SName and HDay="+HDay+" order by HStart",null);			
			if (c.getCount() > 0) {
			c.moveToFirst();
				do {
					HashMap<String,Object> resultsMap =
						new HashMap<String, Object>();
					resultsMap.put("SName" , c.getString(c.getColumnIndex("SName")));
					resultsMap.put("SCode" , c.getString(c.getColumnIndex("SCode")));
					resultsMap.put("STeacher" , c.getString(c.getColumnIndex("STeacher")));
					resultsMap.put("HType" , c.getString(c.getColumnIndex("HType")));
					resultsMap.put("HClass" , c.getString(c.getColumnIndex("HClass")));
					resultsMap.put("HStart" , c.getString(c.getColumnIndex("HStart")));
					resultsMap.put("HEnd" , c.getString(c.getColumnIndex("HEnd")));
					resultsMap.put("SColor" , c.getString(c.getColumnIndex("SColor")));
					results.add(resultsMap);
				} while(c.moveToNext());
			}
			c.close();
			return results;
		} finally {
			if(db!= null)
				db.close();
		}
	}
	
}
	
