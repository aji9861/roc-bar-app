package com.agile.rocbarfinder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.provider.BaseColumns;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BarInfoStorage extends SQLiteOpenHelper {

	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "BAR_DATA";
	private static final String DB_TABLE_NAME = "BAR_TABLE_DATA";

	private static final String DB_COL_NAME = "NAME";
	private static final String DB_COL_LAT = "LATITUDE";
	private static final String DB_COL_LONG = "LONGITUDE";
	private static final String DB_COL_VICINITY = "VICINITY";
	private static final String DB_COL_IMAGE = "IMAGE";
	private static final String DB_COL_BAR_ID= "BAR_ID";
	private static final String DB_COL_TYPE = "TYPE";
	
	private static final String[] DB_ALL_COLS = new String[]{DB_COL_NAME, DB_COL_LAT, DB_COL_LONG,
		DB_COL_VICINITY,DB_COL_IMAGE,DB_COL_BAR_ID,DB_COL_TYPE};
	
	private static final String CREATE_TABLE_COMMAND = 
			"CREATE TABLE " + DB_TABLE_NAME + " (" +
			BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			DB_COL_NAME + " TEXT, " +
			DB_COL_LAT + " TEXT, " +
			DB_COL_LONG + " TEXT, " +
			DB_COL_VICINITY + " TEXT, " +
			DB_COL_IMAGE + " TEXT, " +
			DB_COL_BAR_ID + " TEXT, " +
			DB_COL_TYPE + " TEXT, " +
			"UNIQUE (" + DB_COL_BAR_ID + ")" +
			");";
	
	private static BarInfoStorage self = null;
	
	private BarInfoStorage(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	public synchronized static BarInfoStorage getInstance(Context c){
		if (self == null){
			self = new BarInfoStorage(c);
		}
		return self;
	}
	
	public synchronized static BarInfoStorage getInstance(){
		return self;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_COMMAND);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// No upgrading is done
	}
	
	/**
	 * Add a bar to the local database
	 * @param bi
	 * @return true if successful, false otherwise
	 */
	public synchronized boolean addBar(BarInformation bi){
		ContentValues values = getContentValues(bi);
		if (values == null){
			return false;
		}
		SQLiteDatabase db = getWritableDatabase();
		long result = db.insert(DB_TABLE_NAME, null, values);
		db.close();
		// If result is -1 an error occurred
		return result != -1;
	}
	
	/**
	 * Add a list of bars to the database
	 * @param lbi
	 * @return true if they all succeed, false if 1 or more fail
	 */
	public synchronized boolean addAllBars(List<BarInformation> lbi){
		boolean success = true;
		for (BarInformation bi : lbi){
			boolean res = addBar(bi);
			if (!res){
				success = false;
			}
		}
		return success;
	}
	
	public synchronized List<BarInformation> getAllBars(){
		return getAllBars(BarSortingOption.None, 0, 0);
	}
	
	public synchronized List<BarInformation> getAllBars(BarSortingOption sortingOptions, double lat, double lon){
		List<BarInformation> lbi = new LinkedList<BarInformation>();
		SQLiteDatabase db = getReadableDatabase();

		try{
			Cursor c = db.query(DB_TABLE_NAME, DB_ALL_COLS, null, null , null, null, null);
			if(c.moveToFirst()){
				while(c.moveToNext()){
					BarInformation bi = new BarInformation(
							getStringValueFromName(c, DB_COL_NAME),
							getStringValueFromName(c, DB_COL_VICINITY),
							getStringValueFromName(c, DB_COL_IMAGE),
							getStringValueFromName(c, DB_COL_BAR_ID),
							getDoubleValueFromName(c, DB_COL_LAT),
							getDoubleValueFromName(c, DB_COL_LONG));
					lbi.add(bi);
				}
			}
			if(sortingOptions != BarSortingOption.None){
				if(sortingOptions == BarSortingOption.DistanceToBar){
					Collections.sort(lbi, new BarComparatorDistance(lat, lon));
				}
				else if(sortingOptions == BarSortingOption.Name){
					Collections.sort(lbi, new BarComparatorName());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.close();
		}

		return lbi;
	}
	
	public synchronized BarInformation getBarByName(String name){
		BarInformation bi = null;
		SQLiteDatabase db = getReadableDatabase();
		
		try{
			Cursor c = db.query(DB_TABLE_NAME, DB_ALL_COLS, DB_COL_NAME + "=?", new String[]{name}, null, null, null);
			if(c.moveToFirst()){
				bi = new BarInformation(
						getStringValueFromName(c, DB_COL_NAME),
						getStringValueFromName(c, DB_COL_VICINITY),
						getStringValueFromName(c, DB_COL_IMAGE),
						getStringValueFromName(c, DB_COL_BAR_ID),
						getDoubleValueFromName(c, DB_COL_LAT),
						getDoubleValueFromName(c, DB_COL_LONG));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{

			db.close();
		}
		return bi;
	}
	
	private String getStringValueFromName(Cursor c, String colName){
		return c.getString(c.getColumnIndex(colName));
	}
	
	private Double getDoubleValueFromName(Cursor c, String colName){
		return c.getDouble(c.getColumnIndex(colName));
	}
	
	private ContentValues getContentValues(BarInformation bi){
		ContentValues cv = new ContentValues();
		cv.put(DB_COL_NAME, bi.name.toString());
		cv.put(DB_COL_LAT, bi.latitude.toString());
		cv.put(DB_COL_LONG, bi.longitude.toString());
		cv.put(DB_COL_VICINITY, bi.vicinity.toString());
		cv.put(DB_COL_IMAGE, bi.image.toString());
		cv.put(DB_COL_BAR_ID, bi.id.toString());
		
		return cv;
	}
}
