package com.sparkzi.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.sparkzi.model.Essential;

public class EssentialDbManager {

	@SuppressWarnings("unused")
	private static final String TAG = EssentialDbManager.class.getSimpleName();

	private static String TABLE_ESSENTIAL_LIST = "essential_list_table";

	public static final String TABLE_PRIMARY_KEY = "_id";

	private static String ESSENTIAL_TYPE = "essential_type";
	private static String ID = "id";
	private static String VALUE = "value";

	private static final String CREATE_TABLE_ESSENTIAL_LIST = "create table "
			+ TABLE_ESSENTIAL_LIST + " ( " + TABLE_PRIMARY_KEY
			+ " integer primary key autoincrement, " + ESSENTIAL_TYPE
			+ " integer, " + ID + " integer, " + VALUE + " text);";

	public static void createTable(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_ESSENTIAL_LIST);
	}

	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESSENTIAL_LIST);
	}

	public static long insert(SQLiteDatabase db, Essential essential,
			int essentialType) throws SQLException {

		ContentValues cv = new ContentValues();

		cv.put(ESSENTIAL_TYPE, essentialType);
		cv.put(ID, essential.getId());
		cv.put(VALUE, essential.getValue());

		return db.insert(TABLE_ESSENTIAL_LIST, null, cv);
	}

	public static Essential retrieve(SQLiteDatabase db, int essentialType,
			int id) throws SQLException {
		Essential essential = new Essential();

		Cursor c = db.query(TABLE_ESSENTIAL_LIST, null, ESSENTIAL_TYPE + "="
				+ essentialType + " AND " + ID + "=" + id, null, null, null,
				null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			String value = c.getString(c.getColumnIndex(VALUE));
			essential = new Essential(id, value);
		}
		return essential;
	}

	public static List<Essential> retrieve(SQLiteDatabase db, int essentialType)
			throws SQLException {
		List<Essential> essentialList = new ArrayList<Essential>();

		Cursor c = db.query(TABLE_ESSENTIAL_LIST, null, ESSENTIAL_TYPE + "="
				+ essentialType, null, null, null, null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				int id = c.getInt(c.getColumnIndex(ID));
				String value = c.getString(c.getColumnIndex(VALUE));

				Essential essential = new Essential(id, value);
				essentialList.add(essential);

				c.moveToNext();
			}
		}
		return essentialList;
	}

	public static List<Essential> retrieve(SQLiteDatabase db)
			throws SQLException {
		List<Essential> essentialList = new ArrayList<Essential>();

		Cursor c = db.query(TABLE_ESSENTIAL_LIST, null, null, null, null, null,
				null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				int id = c.getInt(c.getColumnIndex(ID));
				String value = c.getString(c.getColumnIndex(VALUE));

				Essential essential = new Essential(id, value);
				essentialList.add(essential);

				c.moveToNext();
			}
		}
		return essentialList;
	}

	public static long update(SQLiteDatabase db, Essential essential,
			int essentialType) throws SQLException {

		ContentValues cv = new ContentValues();
		cv.put(VALUE, essential.getValue());

		return db.update(TABLE_ESSENTIAL_LIST, cv, ESSENTIAL_TYPE + "="
				+ essentialType + " AND " + ID + "=" + essential.getId(), null);
	}

	public static boolean isExist(SQLiteDatabase db, int id, int essentialType)
			throws SQLException {
		boolean itemExist = false;

		Cursor c = db.query(TABLE_ESSENTIAL_LIST, null, ESSENTIAL_TYPE + "="
				+ essentialType + " AND " + ID + "=" + id, null, null, null,
				null);
		if ((c != null) && (c.getCount() > 0)) {
			itemExist = true;
		}
		return itemExist;
	}

	public static void insertOrupdate(SQLiteDatabase db, Essential essential,
			int essentialType) throws SQLException {
		if (isExist(db, essential.getId(), essentialType)) {
			update(db, essential, essentialType);
		} else {
			insert(db, essential, essentialType);
		}
	}

	public static void deleteAll(SQLiteDatabase db) throws SQLException {
		db.delete(TABLE_ESSENTIAL_LIST, null, null);
	}

}
