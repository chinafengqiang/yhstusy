package com.smartlearning.db;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 执行创建数据表结构
 */
public class SQLiteHelper extends SQLiteOpenHelper {

	private File dbf = null;
	
	public SQLiteHelper(Context context) {
		super(context, DB.DATABASE_NAME, null, DB.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB.TABLES.SYSMESSAGE.SQL.CREATE);
		db.execSQL(DB.TABLES.TESTPAPER_CATEGORY.SQL.CREATE);
		db.execSQL(DB.TABLES.TESTPAPER.SQL.CREATE);
		db.execSQL(DB.TABLES.TEST_PAPER_QUESTION.SQL.CREATE);
		db.execSQL(DB.TABLES.USER_TEST_PAPER.SQL.CREATE); 
		db.execSQL(DB.TABLES.USERINFO.SQL.CREATE);
		db.execSQL(DB.TABLES.EBOOK.SQL.CREATE);
		db.execSQL(DB.TABLES.EVIDEOS.SQL.CREATE);
		db.execSQL(DB.TABLES.BOOKPART.SQL.CREATE);
		db.execSQL(DB.TABLES.BOOKCHAPTER.SQL.CREATE);
		db.execSQL(DB.TABLES.ONLINEMSG.SQL.CREATE);
		db.execSQL(DB.TABLES.COURSEPLAN.SQL.CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DB.TABLES.BOOKPART.SQL.CREATE);
		db.execSQL(DB.TABLES.BOOKCHAPTER.SQL.CREATE);
		db.execSQL(DB.TABLES.ONLINEMSG.SQL.CREATE);
		db.execSQL(DB.TABLES.COURSEPLAN.SQL.CREATE);
	}

	

	
//	@Override
//	public synchronized SQLiteDatabase getWritableDatabase() {
//		File dbp = new File(DB.dbPath);
//		dbf = new File(DB.DATABASE_NAME);
//		if (!dbp.exists()) {
//			dbp.mkdir();
//		}
//		
//		// 数据库文件是否创建成功
//		boolean isFileCreateSuccess = false;
//		if (!dbf.exists()) {
//			try {
//				isFileCreateSuccess = dbf.createNewFile();
//				if (isFileCreateSuccess) {
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.SYSMESSAGE.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.TESTPAPER_CATEGORY.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.TESTPAPER.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.TEST_PAPER_QUESTION.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.USER_TEST_PAPER.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.USERINFO.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.EBOOK.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.EVIDEOS.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.BOOKPART.SQL.CREATE);
//					SQLiteDatabase.openOrCreateDatabase(dbf, null).execSQL(DB.TABLES.BOOKCHAPTER.SQL.CREATE);
//					
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else {
//			isFileCreateSuccess = true;
//		}
//		if (isFileCreateSuccess) {
//			return SQLiteDatabase.openOrCreateDatabase(dbf, null);
//		} else {
//			return null;
//		}
//	}

	/*public void close() {
		if (dbf.exists()) {
			SQLiteDatabase.openOrCreateDatabase(dbf, null).close();
		}

	}*/
	
	public void close(SQLiteDatabase db){
		if(db != null){  
            db.close();  
        }  
	}

}