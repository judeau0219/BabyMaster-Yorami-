package com.ndl.android.babymaster;

import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class IntroActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		BabyDb db = BabyDb.getInstance(this);
		
		// db.insertBaby("이규봉", "Lee kyubong", null, DateUtil.getCurrentDate(), BabyConst.MALE, "177", "74");
		
		// 날짜시간 : 2014-03-21 02:13:21
		
		// db.deleteRecords(1);
		
		/*
		db.insertRecord(1, RecordConst.DIAPER_BIG, null, "묽은똥");
		
		Cursor cursor = db.selectRecords(1);
		
		while(cursor.moveToNext()){
			LogUtil.trace("" + cursor.getInt(cursor.getColumnIndexOrThrow(BabyData.BABY_ID)));
			LogUtil.trace("" + cursor.getString(cursor.getColumnIndexOrThrow(BabyData.TYPE)));
			LogUtil.trace("" + cursor.getString(cursor.getColumnIndexOrThrow(BabyData.REGISTRATION_DATE)));
			LogUtil.trace("" + cursor.getString(cursor.getColumnIndexOrThrow(BabyData.RECORD_DATA)));
			LogUtil.trace("------------------------------------------------");
		}
		
		cursor.close();
		*/
		/*
		// 등록하기 테스트 임시
		for(int i=0; i<db.countBabys(); i++){
			Cursor cursor = db.selectBabys();
			cursor.moveToFirst();
			db.deleteBabys(cursor.getInt(cursor.getColumnIndexOrThrow(BabyData._ID)));
		}
		*/
		
		Intent intent;
		
		if(db.countBabys() > 0){
			intent = new Intent(this, SelectActivity.class);
		}else{
			Constants.initialize = false;
			intent = new Intent(this, RegistrationActivity.class);
		}
		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		startActivity(intent);
		
		/*
		LogUtil.trace("등록 전 아이 수 : " + db.countBabys());
		
		db.insertBaby("류태욱", "RYU TAEWOOK", null, DateUtil.getCurrentDate(), BabyConst.MALE, "172", "67");
		
		LogUtil.trace("등록 후 아이 수 : " + db.countBabys());
		
		Cursor cursor = db.selectBabys();
		cursor.moveToPosition(0);
		db.deleteBabys(cursor.getInt(cursor.getColumnIndexOrThrow(BabyData._ID)));
		
		LogUtil.trace("삭제 후 아이 수 : " + db.countBabys());
		*/
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		finish();
	}
	
}
