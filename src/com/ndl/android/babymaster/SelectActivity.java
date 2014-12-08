package com.ndl.android.babymaster;

import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.util.DateUtil;
import com.ndl.android.babymaster.viewgroup.PagerContainer;
import com.ndl.android.babymaster.viewgroup.PhotoContainer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

public class SelectActivity extends Activity {

	private int mCursorPosition = 0;
	private BabyDb mDb;
	
	private PhotoContainer mPhoto;
	private PagerContainer mPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select);
		
		mDb = BabyDb.getInstance(this);
		
		Intent intent = getIntent();
		mCursorPosition = intent.getIntExtra("cursorPosition", 0);
		
		mPhoto = (PhotoContainer) findViewById(R.id.photo_container);
		mPhoto.initPosition(mCursorPosition);
		
		mPager = (PagerContainer) findViewById(R.id.pager_container);
		mPager.initPage(mCursorPosition);
		
		setContents(mCursorPosition);
	}
	
	public void setContents(int cursorPosition){
		mCursorPosition = cursorPosition;
		
		mPager.setPage(mCursorPosition);
		
		Cursor cursor = mDb.selectBabys();
		cursor.moveToPosition(mCursorPosition);
		
		((TextView) findViewById(R.id.txt_name_kor)).setText(cursor.getString(cursor.getColumnIndex(BabyData.NAME_KOR)));
		((TextView) findViewById(R.id.txt_name_eng)).setText(cursor.getString(cursor.getColumnIndex(BabyData.NAME_ENG)));
		
		((TextView) findViewById(R.id.txt_months)).setText(Long.toString(DateUtil.elapsedDays(cursor.getString(cursor.getColumnIndex(BabyData.BIRTHDAY)), null)) + "days");
		
		((TextView) findViewById(R.id.txt_height)).setText(cursor.getString(cursor.getColumnIndex(BabyData.HEIGHT)) + "cm");
		((TextView) findViewById(R.id.txt_weight)).setText(cursor.getString(cursor.getColumnIndex(BabyData.WEIGHT)) + "kg");
		
		cursor.close();
	}
	
	public void onClick(View v){
		int id = v.getId();
		
		Intent intent;
		
		switch(id){
			case R.id.btn_stats:
				intent = new Intent(this, DrawerActivity.class);
				intent.putExtra("menuIndex", 3);
				intent.putExtra("cursorPosition", mCursorPosition);
				startActivity(intent);
				
				break;
			case R.id.btn_recorder:
				intent = new Intent(this, DrawerActivity.class);
				intent.putExtra("menuIndex", 1);
				intent.putExtra("cursorPosition", mCursorPosition);
				startActivity(intent);
				
				break;
			case R.id.btn_log:
				intent = new Intent(this, DrawerActivity.class);
				intent.putExtra("menuIndex", 2);
				intent.putExtra("cursorPosition", mCursorPosition);
				startActivity(intent);
				
				break;
			case R.id.btn_add:
				Constants.initialize = false;
				intent = new Intent(this, RegistrationActivity.class);
				startActivity(intent);
				
				break;
				
			case R.id.photo_container:
				intent = new Intent(this, DrawerActivity.class);
				intent.putExtra("menuIndex", 4);
				intent.putExtra("cursorPosition", mCursorPosition);
				startActivity(intent);
				
				break;
		}
	}
	
	private int mBackCount = 0;
	
	@Override
	public void onResume(){
		super.onResume();
		mBackCount = 0;
	}
	
	@Override
	public void onBackPressed(){
		mBackCount++;
		
		if(mBackCount == 1){
			LogUtil.toast(this, "'뒤로' 버튼 한번 더 누르시면 종료됩니다.");
		}else{
			Constants.initialize = true;
			finish();
		}
	}

}
