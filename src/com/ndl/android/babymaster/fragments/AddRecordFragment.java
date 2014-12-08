package com.ndl.android.babymaster.fragments;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.DrawerActivity;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.database.Constants.RecordConst;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.datamodel.RecordModel;
import com.ndl.android.babymaster.util.DateUtil;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddRecordFragment extends Fragment implements OnClickListener, DialogInterface.OnClickListener, OnDateSetListener, OnTimeSetListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_add_record, container, false);
		return view;
	}
	
	private DrawerActivity mActivity;
	private BabyDb mDb;
	
	private Cursor mCursor;
	private Cursor mRecordCursor;
	
	private Button mBtnType;
	
	private TextView mTxtDate;
	private TextView mTxtTime;
	
	private TextView mTxtTitle;
	private EditText mEditText;
	private EditText mEditNum;
	private Button mBtnElapsed;
	
	private String[] mTypes = {RecordConst.DIAPER_SMALL,
											RecordConst.DIAPER_BIG,
											RecordConst.BREASTFEEDING_LEFT,
											RecordConst.BREASTFEEDING_RIGHT,
											RecordConst.DRY_MILK,
											RecordConst.MILK,
											RecordConst.BABY_FOOD,
											RecordConst.SLEEP};
	
	private RecordModel mRecordModel = new RecordModel();
	
	private Calendar mCalendar;
	
	private int mPosition = 0;
	private int mTimePickerState = 0; // 0: 일자/시간, 1: 모유수유시간, 2: 수면시간
	
	private String[] mTypeNames;
	
	private String mElapsedGuide;
	
	private int mRecordId = -1;
	private int mTypeIndex = 0;
	private int mRecordInt = -1;
	private String mRecordStr;
	
	private boolean isInit = false;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (DrawerActivity) getActivity();
		mDb = BabyDb.getInstance(getActivity());
		
		mTypeNames = getResources().getStringArray(R.array.type_selector);
		mElapsedGuide = getResources().getString(R.string.guide_btn_elapsed);
		
		mActivity.selectCursor();
		mCursor = mActivity.mCursor;
		
		mBtnType = (Button) getView().findViewById(R.id.btn_type);
		mBtnType.setOnClickListener(this);
		
		mTxtDate = (TextView) getView().findViewById(R.id.txt_date);
		mTxtTime = (TextView) getView().findViewById(R.id.txt_time);
		mTxtDate.setOnClickListener(this);
		mTxtTime.setOnClickListener(this);
		
		mTxtTitle = (TextView) getView().findViewById(R.id.txt_title);
		mEditText = (EditText) getView().findViewById(R.id.edit_text);
		mEditNum = (EditText) getView().findViewById(R.id.edit_num);
		mBtnElapsed = (Button) getView().findViewById(R.id.btn_elapsed);
		mBtnElapsed.setOnClickListener(this);
		
		Button btnAddRecord = (Button) getView().findViewById(R.id.btn_add_record);
		btnAddRecord.setOnClickListener(this);
		
		mRecordModel.babyId = mCursor.getInt(mCursor.getColumnIndex(BabyData._ID));
		
		mCalendar = new GregorianCalendar();
		
		mTypeIndex = 0;
		
		Bundle args = getArguments();
		
		mActivity.setDrawerTitle("로그 추가");
		btnAddRecord.setText("등록하기");
		
		if(args != null){
			mRecordId = args.getInt("recordId", -1);
			
			if(mRecordId == -1){
				DateModel dateModel = args.getParcelable("dateModel");
				
				if(dateModel != null){
					mCalendar.set(Calendar.YEAR, dateModel.year);
					mCalendar.set(Calendar.MONTH, dateModel.month);
					mCalendar.set(Calendar.DATE, dateModel.date);
				}
			}else{
				int babyId = mCursor.getInt(mCursor.getColumnIndex(BabyData._ID));
				mRecordCursor = mDb.selectRecord(babyId, mRecordId);
				
				if(mRecordCursor.getCount() > 0){
					mRecordCursor.moveToFirst();
					
					String timeStr = mRecordCursor.getString(mRecordCursor.getColumnIndex(BabyData.REGISTRATION_DATE));
					mCalendar.setTime(DateUtil.getStringToDate(timeStr));
					
					String typeStr = mRecordCursor.getString(mRecordCursor.getColumnIndex(BabyData.TYPE));
					mTypeIndex = Arrays.asList(RecordConst.ARRAY_TYPE).indexOf(typeStr);
					
					mRecordInt = mRecordCursor.getInt(mRecordCursor.getColumnIndex(BabyData.RECORD_INT));
					mRecordStr = mRecordCursor.getString(mRecordCursor.getColumnIndex(BabyData.RECORD_STR));
				}
				
				mActivity.setDrawerTitle("로그 수정");
				btnAddRecord.setText("수정하기");
			}
		}
		
		isInit = true;
		mCalendar.set(Calendar.SECOND, 0);
		
		setDateTime();
		onClick(null, mTypeIndex);
	}
	
	private void setDateTime(){
		Date calendarDate = mCalendar.getTime();
		
		mTxtDate.setText(DateUtil.getDateToDisplayString(calendarDate));
		mTxtTime.setText(DateUtil.getDateToDisplayTime(calendarDate));
		
		mRecordModel.registrationDate = DateUtil.getDateToTime(mCalendar.getTime());
	}
	
	private boolean checkRecord(){
		String str;
		
		if(mPosition == 0 || mPosition == 1 || mPosition == 6){ // 기저귀(대변,소변), 이유식
			str = mEditText.getText().toString();
			
			if(!str.equals("")) mRecordModel.recordStr = str;
		}
		else if(mPosition == 4 || mPosition == 5){ // 분유, 우유
			str = mEditNum.getText().toString();
			
			if(str.equals("")){
				mRecordModel.recordInt = -1;
				LogUtil.toast(getActivity(), getResources().getString(R.string.toast_add_record_milk));
				return false;
			}else{
				mRecordModel.recordInt = Integer.parseInt(str);
			}
		}
		else if(mPosition == 2 || mPosition == 3 || mPosition == 7){ // 모유(좌,우), 수면시간
			if(mBtnElapsed.getText().equals(mElapsedGuide)){
				LogUtil.toast(getActivity(), getResources().getString(R.string.toast_add_record_elapsed));
				return false;
			}
		}
		
		return true;
	}
	
	private void saveRecord(){
		boolean success = mDb.insertRecord(mRecordModel.babyId, mRecordModel.type, mRecordModel.registrationDate, mRecordModel.recordInt, mRecordModel.recordStr);
		
		if(success){
//			LogUtil.trace("save record! : " + mRecordModel.toString());
			
			Bundle args = new Bundle();
			args.putString("initDate", mRecordModel.registrationDate);
			mActivity.switchFragment(2, false, args);
		}
	}
	
	private void updateRecord(){
		boolean success = mDb.updateRecord(mRecordId, mRecordModel.type, mRecordModel.registrationDate, mRecordModel.recordInt, mRecordModel.recordStr);
		
		if(success){
//			LogUtil.trace("update record! : " + mRecordModel.toString());
			
			Bundle args = new Bundle();
			args.putString("initDate", mRecordModel.registrationDate);
			mActivity.switchFragment(2, false, args);
		}
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		mPosition = which;
		mRecordModel.type = mTypes[mPosition];
		mBtnType.setText(mTypeNames[mPosition]);
		
		if(isInit) { 
			if(mRecordInt != -1) mRecordModel.recordInt = mRecordInt; 
			if(mRecordStr != null) mRecordModel.recordStr = mRecordStr; 
		}else{
			mRecordInt = mRecordModel.recordInt = -1;
			mRecordStr = mRecordModel.recordStr = null;
		}
		
		mEditText.setText("");
		mEditNum.setText("");
		mBtnElapsed.setText(mElapsedGuide);
		
		mBtnElapsed.setVisibility(View.GONE);
		mEditText.setVisibility(View.GONE);
		mEditNum.setVisibility(View.GONE);
		
		// 기저귀(소변,대변)
		if(which == 0 || which == 1){ 
			int hintResId = (which == 0) ? R.string.input_guide_diaper_small : R.string.input_guide_diaper_big;
			
			mTxtTitle.setText(getResources().getString(R.string.input_title_diaper));
			mEditText.setHint(getResources().getString(hintResId));
			mEditText.setVisibility(View.VISIBLE);
			
			if(isInit && mRecordStr != null) mEditText.setText(mRecordStr); 
		}
		// 분유, 우유
		else if(which == 4 || which == 5){ 
			mTxtTitle.setText(getResources().getString(R.string.input_title_milk));
			mEditNum.setHint(getResources().getString(R.string.input_guide_milk));
			mEditNum.setVisibility(View.VISIBLE);
			
			if(isInit && mRecordInt != -1) mEditNum.setText(Integer.toString(mRecordInt)); 
		}
		// 이유식
		else if(which == 6){ 
			mTxtTitle.setText(getResources().getString(R.string.input_title_babyfood));
			mEditText.setHint(getResources().getString(R.string.input_guide_babyfood));
			mEditText.setVisibility(View.VISIBLE);
			
			if(isInit && mRecordStr != null) mEditText.setText(mRecordStr); 
		}
		// 모유(좌,우)
		else if(which == 2 || which == 3){ 
			mTxtTitle.setText(getResources().getString(R.string.input_title_breastfeeding));
			mBtnElapsed.setVisibility(View.VISIBLE);
			
			if(isInit && mRecordInt != -1) mBtnElapsed.setText(DateUtil.getMinuteToHour(mRecordInt, true)); 
		}
		// 수면시간
		else if(which == 7){ 
			mTxtTitle.setText(getResources().getString(R.string.input_title_sleep));
			mBtnElapsed.setVisibility(View.VISIBLE);
			
			if(isInit && mRecordInt != -1) mBtnElapsed.setText(DateUtil.getMinuteToHour(mRecordInt, true)); 
		}
		
		isInit = false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		switch(id){
			case R.id.btn_type:
				showTypeDialog();
				break;
			case R.id.txt_date:
				showDatePicker();
				break;
			case R.id.txt_time:
				mTimePickerState = 0;
				showTimePicker(false);
				break;
			case R.id.btn_elapsed:
				mTimePickerState = (mPosition == 7) ? 2 : 1;
				showTimePicker();
				break;
			case R.id.btn_add_record:
				if(checkRecord()){
					if(mRecordId == -1){
						saveRecord();
					}else{
						updateRecord();
					}
				}
				
				break;
		}
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		mCalendar.set(Calendar.YEAR, year);
		mCalendar.set(Calendar.MONTH, monthOfYear);
		mCalendar.set(Calendar.DATE, dayOfMonth);
		
		setDateTime();
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if(mTimePickerState == 0){ // 일자/시간
			mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			mCalendar.set(Calendar.MINUTE, minute);
			
			setDateTime();
		}
		else{
			int min = hourOfDay*60 + minute;
			
			mRecordModel.recordInt = min;
			mBtnElapsed.setText(DateUtil.getMinuteToHour(min, true));
		}
	}
	
	private void showTypeDialog(){
		AlertDialog alert = new AlertDialog.Builder(getActivity())
								.setItems(mTypeNames, this)
								.setCancelable(false)
								.create();
		
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
	
	private void showDatePicker(){
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), 
											this, 
											mCalendar.get(Calendar.YEAR), 
											mCalendar.get(Calendar.MONTH), 
											mCalendar.get(Calendar.DATE));
		
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	private void showTimePicker(boolean is24HourView){
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);
		
		TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, is24HourView);
		
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	private void showTimePicker(){
		int hour = 0;
		int minute = 10;
		
		if(mTimePickerState == 2){
			hour = 2;
			minute = 0;
		}
		
		if(mRecordModel.recordInt != -1){
			hour = mRecordModel.recordInt/60;
			minute = mRecordModel.recordInt%60;
		}
		
		TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute, true);
		
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
}

