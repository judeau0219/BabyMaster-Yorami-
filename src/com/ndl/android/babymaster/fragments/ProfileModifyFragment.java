package com.ndl.android.babymaster.fragments;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.judeau.util.LogUtil;
import com.judeau.util.NumberUtil;
import com.ndl.android.babymaster.DrawerActivity;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.BabyConst;
import com.ndl.android.babymaster.database.Constants.BabyData;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.DatePickerDialog.OnDateSetListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

public class ProfileModifyFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile_modify, container, false);
		return view;
	}
	
	private DrawerActivity mActivity;
	private BabyDb mDb;
	private Cursor mCursor;
	
	private EditText mTxtNameKor;
	private EditText mTxtNameEng;
	private EditText mTxtHeight;
	private EditText mTxtWeight;
	
	private Button mSelectBirthday;
	private String mBirthDay;
	
	private RadioGroup mRadioGroup;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (DrawerActivity) getActivity();
		mDb = BabyDb.getInstance(getActivity());
		
		mActivity.selectCursor();
		mActivity.setDrawerTitle("아기 프로필 수정");
		
		mCursor = mActivity.mCursor;
		
		mTxtNameKor = (EditText) getView().findViewById(R.id.edit_name_kor);
		mTxtNameEng = (EditText) getView().findViewById(R.id.edit_name_eng);
		mTxtHeight = (EditText) getView().findViewById(R.id.edit_height);
		mTxtWeight = (EditText) getView().findViewById(R.id.edit_weight);
		
		mTxtNameKor.setText(mCursor.getString(mCursor.getColumnIndex(BabyData.NAME_KOR)));
		mTxtNameEng.setText(mCursor.getString(mCursor.getColumnIndex(BabyData.NAME_ENG)));
		mTxtHeight.setText(mCursor.getString(mCursor.getColumnIndex(BabyData.HEIGHT)));
		mTxtWeight.setText(mCursor.getString(mCursor.getColumnIndex(BabyData.WEIGHT)));
		
		mBirthDay = mCursor.getString(mCursor.getColumnIndex(BabyData.BIRTHDAY));
		String[] ary = mBirthDay.split("-");
		
		mSelectBirthday = (Button) getView().findViewById(R.id.btn_select_birthday);
		mSelectBirthday.setText(ary[0] + "년 " + ary[1] + "월 " + ary[2] + "일");
		
		final Calendar calendar = new GregorianCalendar();
		mSelectBirthday.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
				dialog.show();
			}
		});
		
		mRadioGroup = (RadioGroup) getView().findViewById(R.id.radiogroup_sex);
		String sex = mCursor.getString(mCursor.getColumnIndex(BabyData.SEX));
		
		if(sex.equals(BabyConst.MALE)){
			mRadioGroup.check(R.id.radio_male);
		}else{
			mRadioGroup.check(R.id.radio_female);
		}
		
		Button btnModifyComp = (Button) getView().findViewById(R.id.btn_modify_comp);
		btnModifyComp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				modifyData();
			}
		});
	}
	
	OnDateSetListener dateSetListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			mSelectBirthday.setText(year + "년 " + 
					NumberUtil.digit(monthOfYear+1, 2) + "월 " + 
					NumberUtil.digit(dayOfMonth, 2) + "일");
			
			mBirthDay = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
		}
	};
	
	private void modifyData()
	{
		if(mTxtNameKor.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "한글 이름을 입력해 주세요.");
			return;
		}else if(mTxtNameEng.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "영문 이름을 입력해 주세요.");
			return;
		}else if(mSelectBirthday.getText().equals(getResources().getString(R.string.guide_registration_date)) || mSelectBirthday.getText().equals("")){
			LogUtil.toast(getActivity(), "생년월일을 입력해 주세요.");
			return;
		}else if(mTxtHeight.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "키를 입력해 주세요.");
			return;
		}else if(mTxtWeight.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "몸무게를 입력해 주세요.");
			return;
		}
		
		String sex;
		
		if(mRadioGroup.getCheckedRadioButtonId() == R.id.radio_male){
			sex = BabyConst.MALE;
		}else{
			sex = BabyConst.FEMALE;
		}
		
		int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(BabyData._ID));
		
		boolean success = mDb.updateBaby(id, mTxtNameKor.getText().toString(), 
														mTxtNameEng.getText().toString(), 
														mBirthDay, 
														sex, 
														mTxtHeight.getText().toString(), 
														mTxtWeight.getText().toString());
		
		if(success){
			mActivity.switchFragment(4);
		}else{
			LogUtil.toast(getActivity(), "수정실패");
		}
	}

}
