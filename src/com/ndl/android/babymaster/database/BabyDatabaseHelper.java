package com.ndl.android.babymaster.database;

import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.database.Constants.BabyData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BabyDatabaseHelper extends SQLiteOpenHelper {

	public BabyDatabaseHelper(Context context){
		super(context, BabyData.DB_NAME, null, BabyData.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.execSQL("CREATE TABLE " + BabyData.BABYS + "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					BabyData.NAME_KOR + " TEXT NOT NULL, " +
					BabyData.NAME_ENG + " TEXT NOT NULL, " +
					BabyData.PICTURE_PATH + " TEXT," + 
					BabyData.BIRTHDAY + " DATE NOT NULL," + 
					BabyData.SEX + " TEXT NOT NULL," +
					BabyData.HEIGHT + " TEXT NOT NULL," + 
					BabyData.WEIGHT + " TEXT NOT NULL);");
		}catch(Exception e){
			LogUtil.trace("Failed to create a " + BabyData.BABYS + " table!");
		}
		
		/*
		 * CURRENT_TIME : 시간만
		 * CURRENT_DATE : 날짜만
		 * CURRENT_TIMESTAMP : 날짜,시간 둘다
		 */
		try{
			db.execSQL("CREATE TABLE " + BabyData.RECORDS + "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					BabyData.BABY_ID + " INTEGER NOT NULL, " +
					BabyData.TYPE + " TEXT NOT NULL, " + 
					BabyData.REGISTRATION_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + 
					BabyData.RECORD_INT + " INTEGER DEFAULT -1, " + 
					BabyData.RECORD_STR + " TEXT);");
		}catch(Exception e){
			LogUtil.trace("Failed to create a " + BabyData.RECORDS + " table!");
		}
	}

	// DB버전이 이전에 생성된 버전과 다른 경우 호출됨. 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + BabyData.BABYS);
		db.execSQL("DROP TABLE IF EXISTS " + BabyData.RECORDS);
		
		LogUtil.trace("Drop table!");
		
		onCreate(db);
	}
	
}
