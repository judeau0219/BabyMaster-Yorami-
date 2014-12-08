package com.ndl.android.babymaster.database;

import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.util.DateUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BabyDb {

	private static BabyDb sInstance;
	private Context mContext;
	private SQLiteDatabase mDb;
	
	private BabyDb(Context context){
		mContext = context;
	}
	
	public synchronized static BabyDb getInstance(Context context){
		
		if(sInstance != null && sInstance.mDb != null){
			return sInstance;
		}
		
		sInstance = new BabyDb(context);
		
		if(!sInstance.open(context))
			sInstance = null;
		
		return sInstance;
	}
	
	private boolean open(Context context){
		BabyDatabaseHelper dbHelper = new BabyDatabaseHelper(context);
		
		try{
			mDb = dbHelper.getWritableDatabase();
		}catch(Exception e){
			mDb = null;
		}
		
		return (mDb == null) ? false : true;
	}
	
	public boolean insertBaby(String nameKor, String nameEng, String picturePath, String birthDay, String sex, String height, String weight){
		/*
		String sql = "INSERT INTO " + BabyData.BABYS + 
				"(" + BabyData.NAME_KOR + ", " + BabyData.NAME_ENG + ", " + BabyData.PICTURE_PATH + ", " + 
				BabyData.BIRTHDAY + ", " + BabyData.SEX + ", " + BabyData.HEIGHT + ", " +BabyData.WEIGHT + ") VALUES('" +
				nameKor + "', '" + nameEng + "', '" + picturePath + "', '" +
				birthDay + "', '" + sex + "', '" + height + "', '" + weight + "');";
		
		try{
			mDb.execSQL(sql);
			return true;
		}catch(Exception e){
			return false;
		}
		*/
		
		ContentValues values = new ContentValues();
		
		values.put(BabyData.NAME_KOR, nameKor);
		values.put(BabyData.NAME_ENG, nameEng);
		
		if(picturePath == null){
			values.putNull(BabyData.PICTURE_PATH);
		}else{
			values.put(BabyData.PICTURE_PATH, picturePath);
		}
		
		values.put(BabyData.BIRTHDAY, birthDay);
		values.put(BabyData.SEX, sex);
		values.put(BabyData.HEIGHT, height);
		values.put(BabyData.WEIGHT, weight);
		
		try{
			mDb.insert(BabyData.BABYS, null, values);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	// picturePath 는 업데이트 하지 않음..
	public boolean updateBaby(int id, String nameKor, String nameEng, String birthDay, String sex, String height, String weight){
		ContentValues values = new ContentValues();
		
		values.put(BabyData.NAME_KOR, nameKor);
		values.put(BabyData.NAME_ENG, nameEng);
		
		/*
		if(picturePath == null){
			values.putNull(BabyData.PICTURE_PATH);
		}else{
			values.put(BabyData.PICTURE_PATH, picturePath);
		}
		*/
		
		values.put(BabyData.BIRTHDAY, birthDay);
		values.put(BabyData.SEX, sex);
		values.put(BabyData.HEIGHT, height);
		values.put(BabyData.WEIGHT, weight);
		
		try{
			mDb.update(BabyData.BABYS, values, "_id='" + id + "'", null);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean updateBabyOfPicturePath(String newValue, int id){
		ContentValues values = new ContentValues();
		
		if(newValue == null){
			values.putNull(BabyData.PICTURE_PATH);
		}else{
			values.put(BabyData.PICTURE_PATH, newValue);
		}
		
		try{
			mDb.update(BabyData.BABYS, values, "_id='" + id + "'", null);
			return true;
		}catch(Exception e){
			return false;
		}
		
		/*
		String sql = "UPDATE " + BabyData.BABYS + " SET " + BabyData.PICTURE_PATH + "='" + 
				newValue + "' WHERE _id='" + id + "';";
		try{
			mDb.execSQL(sql);
			return true;
		}catch(Exception e){
			return false;
		}
		*/
	}
	
	public boolean insertRecord(int babyId, String type, String registrationDate, int recordInt, String recordStr){
		ContentValues values = new ContentValues();
		
		values.put(BabyData.BABY_ID, babyId);
		values.put(BabyData.TYPE, type);
		values.put(BabyData.REGISTRATION_DATE, registrationDate);
		values.put(BabyData.RECORD_INT, recordInt);
		
		if(recordStr == null){
			values.putNull(BabyData.RECORD_STR);
		}else{
			values.put(BabyData.RECORD_STR, recordStr);
		}
		
		try{
			mDb.insert(BabyData.RECORDS, null, values);
			return true;
		}catch(Exception e){
			return false;
		}
		/*
		long id = mDb.insert(BabyData.RECORDS, null, values);
		return (id == -1) ? false : true;
		*/
	}
	
	public boolean updateRecord(int id, String type, String registrationDate, int recordInt, String recordStr){
		ContentValues values = new ContentValues();
		
		values.put(BabyData.TYPE, type);
		values.put(BabyData.REGISTRATION_DATE, registrationDate);
		values.put(BabyData.RECORD_INT, recordInt);
		
		if(recordStr == null){
			values.putNull(BabyData.RECORD_STR);
		}else{
			values.put(BabyData.RECORD_STR, recordStr);
		}
		
		try{
			mDb.update(BabyData.RECORDS, values, "_id='" + id + "'", null);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public int countBabys(){
		String sql = "SELECT COUNT(*) FROM " + BabyData.BABYS;
		
		Cursor cursor = mDb.rawQuery(sql, null);
		cursor.moveToFirst();
		return cursor.getInt(0);
	}
	
	public Cursor selectBabys(){
		String sql = "SELECT * FROM " + BabyData.BABYS;
		return mDb.rawQuery(sql, null);
	}
	
	public Cursor selectBabys(int id){
		String sql = "SELECT * FROM " + BabyData.BABYS + " WHERE _id='" + id + "'";
		return mDb.rawQuery(sql, null);
	}
	
	public boolean deleteBabys(int id){
		String sql = "DELETE FROM " + BabyData.BABYS + " WHERE _id='" + id + "'";
		
		try{
			mDb.execSQL(sql);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public Cursor selectRecord(int babyId, int id){
		String sql = "SELECT * FROM " + BabyData.RECORDS + " WHERE _id='" + id + "' AND " + BabyData.BABY_ID + "='" + babyId + "'";
		return mDb.rawQuery(sql, null);
	}
	
	public Cursor selectRecords(int babyId){
		String sql = "SELECT * FROM " + BabyData.RECORDS + " WHERE " + BabyData.BABY_ID + "='" + babyId + "'";
		return mDb.rawQuery(sql, null);
	}
	
	public Cursor selectRecords(int babyId, DateModel startDate, DateModel endDate, String type){
		DateModel startModel = startDate;
		DateModel endModel = (endDate == null) ? startDate : endDate;
		
		String sql = "SELECT * FROM " + BabyData.RECORDS + " WHERE " + BabyData.BABY_ID + "='" + babyId + "' AND " + 
							BabyData.REGISTRATION_DATE + " BETWEEN '" + 
							DateUtil.getDateToString(startModel.getDate()) + " 00:00:00" + "' AND '" + 
							DateUtil.getDateToString(endModel.getDate()) + " 23:59:59'";
		
		if(type != null && !type.equals("")) sql += " AND " + BabyData.TYPE + "='" + type +"'";
		
		return mDb.rawQuery(sql, null);
	}
	
	public Cursor sumRecords(int babyId, DateModel startDate, DateModel endDate, String type){
		DateModel startModel = startDate;
		DateModel endModel = (endDate == null) ? startDate : endDate;
		
		String sql = "SELECT SUM(" + BabyData.RECORD_INT + ") FROM " + BabyData.RECORDS + " WHERE " + BabyData.BABY_ID + "='" + babyId + "' AND " + 
							BabyData.REGISTRATION_DATE + " BETWEEN '" + 
							DateUtil.getDateToString(startModel.getDate()) + " 00:00:00" + "' AND '" + 
							DateUtil.getDateToString(endModel.getDate()) + " 23:59:59'";
		
		if(type != null) sql += " AND " + BabyData.TYPE + "='" + type +"'";
		
		return mDb.rawQuery(sql, null);
	}
	
	public Cursor sumRecords(int babyId, DateModel startDate, DateModel endDate, String[] types){
		DateModel startModel = startDate;
		DateModel endModel = (endDate == null) ? startDate : endDate;
		
		String sql = "SELECT SUM(" + BabyData.RECORD_INT + ") FROM " + BabyData.RECORDS + " WHERE " + BabyData.BABY_ID + "='" + babyId + "' AND " + 
							BabyData.REGISTRATION_DATE + " BETWEEN '" + 
							DateUtil.getDateToString(startModel.getDate()) + " 00:00:00" + "' AND '" + 
							DateUtil.getDateToString(endModel.getDate()) + " 23:59:59'";
		
		if(types != null){
			if(types.length > 0){
				sql += " AND (";
				
				for(int i=0; i<types.length; i++){
					sql += BabyData.TYPE + "='" + types[i] + "'";
					if(i < types.length-1){
						sql += " OR ";
					}
				}
				
				sql += ");";
			}
		}
		
		return mDb.rawQuery(sql, null);
	}
	
	/*
	public Cursor selectRecords(int babyId, DateModel startDate, DateModel endDate, String[] types){
		DateModel startModel = startDate;
		DateModel endModel = (endDate == null) ? startDate : endDate;
		
		String sql = "SELECT * FROM " + BabyData.RECORDS + " WHERE " + BabyData.BABY_ID + "='" + babyId + "' AND " + 
							BabyData.REGISTRATION_DATE + " BETWEEN '" + 
							DateUtil.getDateToString(startModel.getDate()) + " 00:00:00' AND '" + 
							DateUtil.getDateToString(endModel.getDate()) + " 23:59:59'";
		
		if(types != null){
			if(types.length > 0){
				sql += " AND (";
				
				for(int i=0; i<types.length; i++){
					sql += BabyData.TYPE + "='" + types[i] + "'";
					if(i < types.length-1){
						sql += " OR ";
					}
				}
				
				sql += ");";
			}
		}
		
		return mDb.rawQuery(sql, null);
	}
	*/
	public boolean deleteRecord(int id){
		String sql = "DELETE FROM " + BabyData.RECORDS + " WHERE _id='" + id + "'";
		
		try{
			mDb.execSQL(sql);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean deleteRecords(int babyId){
		String sql = "DELETE FROM " + BabyData.RECORDS + " WHERE " + BabyData.BABY_ID + "='" + babyId + "'";
		
		try{
			mDb.execSQL(sql);
			return true;
		}catch(Exception e){
			return false;
		}
	}

}
