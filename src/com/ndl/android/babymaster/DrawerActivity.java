package com.ndl.android.babymaster;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.judeau.graphics.drawable.RoundedDrawable;
import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.adapter.DrawerAdapter;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.datamodel.RecordModel;
import com.ndl.android.babymaster.fragments.AddRecordFragment;
import com.ndl.android.babymaster.fragments.GuideFragment;
import com.ndl.android.babymaster.fragments.LogFragment;
import com.ndl.android.babymaster.fragments.ProfileFragment;
import com.ndl.android.babymaster.fragments.ProfileModifyFragment;
import com.ndl.android.babymaster.fragments.RecorderFragment;
import com.ndl.android.babymaster.fragments.SettingFragment;
import com.ndl.android.babymaster.fragments.StatsFragment;
import com.ndl.android.babymaster.interfaces.OnSideDrawerListener;
import com.ndl.android.babymaster.util.DateUtil;
import com.ndl.android.babymaster.viewgroup.RecorderResult;
import com.ndl.android.babymaster.viewgroup.SideDrawer;

import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DrawerActivity extends Activity implements OnItemClickListener, OnSideDrawerListener {

	private RelativeLayout mContents;
	
	private SideDrawer mDrawer;
	private ListView mDrawerList;
	private String[] mAryTitle;
	
	private BabyDb mDb;
	
	private int mCursorPosition = 0;
	public Cursor mCursor;
	
	private TextView mTitle;
	private ImageView mThumb;
	private RecorderResult mResult;
	private ImageView mToggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawer);
		
		final InputMethodManager mImm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		LinearLayout linear = (LinearLayout) findViewById(R.id.linear_drawer);
		linear.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return false;
			}
		});
		
		mDb = BabyDb.getInstance(this);
		
		Intent intent = getIntent();
		mCursorPosition = intent.getIntExtra("cursorPosition", 0);
		
		selectCursor();
		
		mAryTitle = getResources().getStringArray(R.array.menu);
		
		mContents = (RelativeLayout) findViewById(R.id.main_contents);
		mResult = (RecorderResult) findViewById(R.id.record_result);
		
		mDrawer = (SideDrawer) findViewById(R.id.side_drawer);
		mDrawer.setOnSideDrawerListener(this);
		
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
		mThumb = (ImageView) findViewById(R.id.img_profile_thumb);
		mToggle = (ImageView) findViewById(R.id.img_toggle);
		
		mTitle = (TextView) findViewById(R.id.main_title);
		
		DrawerAdapter adapter = new DrawerAdapter(this);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(this);
		
		setThumb(mCursor.getString(mCursor.getColumnIndex(BabyData.PICTURE_PATH)));
		switchFragment(intent.getIntExtra("menuIndex", 1));
	}
	/*
	private ProgressDialog mProgress;
	
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			mProgress.dismiss();
		}
	};
	
	public void showProgressBar(){
		mProgress = new ProgressDialog(this);
		mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgress.setMessage("잠시만 기다려주세요..");
		mProgress.setCancelable(false);
		mProgress.show();
		
		mHandler.sendEmptyMessageDelayed(0, 400);
	}
	*/
	public void selectCursor(){
		mCursor = mDb.selectBabys();
		mCursor.moveToPosition(mCursorPosition);
	}
	
	public void setThumb(String path){
		BitmapDrawable drawable;
		
		if(path == null){
			drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_photo_bear_s);
			mThumb.setImageDrawable(new RoundedDrawable(drawable.getBitmap()));
		}else{
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			
			Bitmap bitmap;
			
			try{
				bitmap = BitmapFactory.decodeFile(path, options);
				mThumb.setImageDrawable(new RoundedDrawable(Bitmap.createScaledBitmap(bitmap, 64, 64, true)));
			}catch(Exception e){
				drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_photo_bear_s);
				bitmap = drawable.getBitmap();
				mThumb.setImageDrawable(new RoundedDrawable(Bitmap.createScaledBitmap(bitmap, 64, 64, true)));
			}
		}
		
	}
	
	private void saveResult(){
		RecordModel model = mResult.getSelectedModel();
		model.babyId = mCursor.getInt(mCursor.getColumnIndex(BabyData._ID));
		model.registrationDate = DateUtil.getDateToTime(new GregorianCalendar().getTime());
		
		boolean success = mDb.insertRecord(model.babyId, model.type, model.registrationDate, model.recordInt, model.recordStr);
		
		if(success){
			LogUtil.trace("save record! : " + model.toString());
			
			hideResult();
			switchFragment(2);
		}
	}
	
	public void showResult(ArrayList<RecordModel> texts){
		final ArrayList<RecordModel> results = texts;
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mResult.setResult(results);
				mResult.setAlpha(0);
				mResult.setVisibility(View.VISIBLE);
				
				mResult.animate().setDuration(200).setInterpolator(new DecelerateInterpolator()).alpha(1);
			}
		});
	}
	
	public void hideResult(){
		ValueAnimator mAni = ValueAnimator.ofFloat(1, 0);
		mAni.setDuration(200);
		mAni.setInterpolator(new DecelerateInterpolator());
		mAni.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd( Animator animation ){
				mResult.setVisibility(View.GONE);
			}
		});
		mAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mResult.setAlpha((Float) animation.getAnimatedValue());
			}
		});
		
		mAni.start();
	}
	
	public void recordError(String msg){
		final String errorMsg = msg;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtil.toast(DrawerActivity.this, errorMsg);
			}
		});
	}
	
	public void onClick(View v){
		int id = v.getId();
		
		switch(id){
			case R.id.btn_toggle:
				mDrawer.menuOpen(!mDrawer.isOpen);
				break;
			case R.id.btn_modify: // 프로필 수정
				switchFragment(7, true);
				break;
			case R.id.btn_cancel: // 녹음결과 취소
				hideResult();
				break;
			case R.id.btn_confirm: // 녹음결과 확인
				saveResult();
				break;
			case R.id.btn_add_record: // 로그 추가
				if(mDrawer.isOpen) mDrawer.menuOpen(false);
				
				FragmentManager fm = getFragmentManager();
				Fragment fragment = fm.findFragmentById(R.id.main_frame);
				
				if(fragment != null) {
					if(fragment.getClass().getName().equals(LogFragment.class.getName())){ // 로그페이지에서 이동되는 경우 현재 선택된 날짜를 가지고 이동
						Bundle args = new Bundle();
						
						LogFragment logFragment = (LogFragment) fragment;
						args.putParcelable("dateModel", logFragment.getDateModel());
						
						switchFragment(8, true, args);
					}else{
						switchFragment(8, true);
					}
				}else{
					switchFragment(8);
				}
				
				break;
		}
	}
	
	public String getDrawerTitle(){
		return (String) mTitle.getText();
	}
	
	public void setDrawerTitle(String title){
		mTitle.setText(title);
	}
	
	private DateModel mSelectModel;
	
	public void switchFragment(int index){
		switchFragment(index, false, null);
	}
	
	public void switchFragment(int index, boolean backStack){
		switchFragment(index, backStack, null);
	}
	
	public void switchFragment(int index, boolean backStack, Bundle args){
		if(index == 0){
			Intent intent = new Intent(this, SelectActivity.class);
			
			intent.putExtra("cursorPosition", mCursorPosition);
			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			startActivity(intent);
			
			return;
		}
		
		if(index < mAryTitle.length){
			setDrawerTitle(mAryTitle[index]);
		}
		
		FragmentManager fm = getFragmentManager();
		
		// 백스택에 
		fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		
		Fragment fragment = fm.findFragmentById(R.id.main_frame);
		
		if(index == 1){
			fragment = new RecorderFragment();
		}
		else if(index == 2){
			fragment = new LogFragment();
		}
		else if(index == 3){
			fragment = new StatsFragment();
		}
		else if(index == 4){
			fragment = new ProfileFragment();
		}
		else if(index == 5){
			fragment = new GuideFragment();
		}
		else if(index == 6){
			fragment = new SettingFragment();
		}
		else if(index == 7){
			fragment = new ProfileModifyFragment();
		}
		else if(index == 8){
			fragment = new AddRecordFragment();
		}
		
		if(args != null) fragment.setArguments(args);
		
		if(fragment != null){
			FragmentTransaction tr = fm.beginTransaction();
			tr.replace(R.id.main_frame, fragment);
			if(backStack){
				tr.addToBackStack(null);
			}
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			tr.commit();
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
		mDrawer.menuOpen(false);
		switchFragment(position);
	}

	@Override
	public void onSideDrawerOpen(boolean open) {
		int tx = (open) ? (int)(-mToggle.getWidth()*0.5) : 0;
		mToggle.animate().setDuration(200).setInterpolator(new DecelerateInterpolator()).x(tx).start();
	}

}
