package com.ndl.android.babymaster;

import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants;
import com.ndl.android.babymaster.datamodel.BabyModel;
import com.ndl.android.babymaster.fragments.registration.StepFragment01;
import com.ndl.android.babymaster.fragments.registration.StepFragment02;
import com.ndl.android.babymaster.fragments.registration.StepFragment03;
import com.ndl.android.babymaster.fragments.registration.StepFragment04;
import com.ndl.android.babymaster.fragments.registration.StepFragment05;
import com.ndl.android.babymaster.interfaces.ICheckRegistration;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RegistrationActivity extends Activity implements OnEditorActionListener {

	private InputMethodManager mImm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		if(savedInstanceState == null){
			baby = getBaby();
		}else{
			baby = getBaby();
			
			baby.nameKor = savedInstanceState.getString("nameKor");
			baby.nameEng = savedInstanceState.getString("nameEng");
			baby.birthDay = savedInstanceState.getString("birthDay");
			baby.sex = savedInstanceState.getString("sex");
			baby.height = savedInstanceState.getString("height");
			baby.weight = savedInstanceState.getString("weight");
		}
		
		mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_registration);
		frame.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return false;
			}
		});
		
		switchFragment();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		
		if(baby.nameKor != null) outState.putString("nameKor", baby.nameKor);
		if(baby.nameEng != null) outState.putString("nameEng", baby.nameEng);
		if(baby.birthDay != null) outState.putString("birthDay", baby.birthDay);
		if(baby.sex != null) outState.putString("sex", baby.sex);
		if(baby.height != null) outState.putString("height", baby.height);
		if(baby.weight != null) outState.putString("weight", baby.weight);
	}
	
	// 1~5
	private int mStep = 1;
	
	private BabyModel baby;
	
	public BabyModel getBaby(){
		if(baby == null) baby = new BabyModel();
		return baby;
	}
	
	public void onClick(View v){
		int id = v.getId();
		
		switch(id){
			case R.id.btn_prev:
				mStep--;
				switchFragment();
				
				break;
			case R.id.btn_next:
				Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_registration);
				
				if(fragment != null){
					ICheckRegistration icr = (ICheckRegistration) fragment;
					if(!icr.checkInput()) return;
				}
				
				mStep++;
				switchFragment();
				
				break;
			case R.id.btn_registration:
				saveData();
				break;
		}
	}
	
	private void switchFragment(){
		FragmentManager fm = getFragmentManager();
		Fragment fragment;
		
		switch(mStep){
			case 1:
				fragment = new StepFragment01();
				break;
			case 2:
				fragment = new StepFragment02();
				break;
			case 3:
				fragment = new StepFragment03();
				break;
			case 4:
				fragment = new StepFragment04();
				break;
			case 5:
				fragment = new StepFragment05();
				break;
			default:
				fragment = new StepFragment01();
				break;
		}
		
		FragmentTransaction tr = fm.beginTransaction();
		tr.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		tr.replace(R.id.frame_registration, fragment);
		tr.commit();
	}
	
	private void saveData()
	{
		BabyDb db = BabyDb.getInstance(this);
		boolean success = db.insertBaby(baby.nameKor, baby.nameEng, baby.picturePath, baby.birthDay, baby.sex, baby.height, baby.weight);
		
		if(success){
			Intent intent = new Intent(this, SelectActivity.class);
			
			int cursorPosition = db.countBabys() - 1;
			intent.putExtra("cursorPosition", cursorPosition);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// IME_ACTION_SEARCH , IME_ACTION_GO, IME_ACTION_DONE, IME_ACTION_NEXT	
		if(actionId == EditorInfo.IME_ACTION_DONE){ // 키보드 '완료' 버튼이 클릭 되면
			// 키보드 닫기
			mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			return true;
		}
		else if(actionId == EditorInfo.IME_ACTION_NEXT){
			// 다음버튼
		}

		return false;
	}
	
	@Override
	protected void onRestart()
	{
		super.onRestart();
		
		if(Constants.initialize) {
			Constants.initialize = false;
			finish();
		}
	}
	
	@Override
	public void onBackPressed(){
		if(mStep == 1){
			finish();
		}else{
			mStep--;
			switchFragment();
		}
	}
	
}
