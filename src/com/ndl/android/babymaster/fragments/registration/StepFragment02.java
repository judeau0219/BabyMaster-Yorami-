package com.ndl.android.babymaster.fragments.registration;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.judeau.util.LogUtil;
import com.judeau.util.NumberUtil;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.RegistrationActivity;
import com.ndl.android.babymaster.interfaces.ICheckRegistration;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

public class StepFragment02 extends Fragment implements ICheckRegistration {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_registration_step02, container, false);
		return view;
	}
	
	private Button mSelectBirthday;
	private String mDateGuide;
	private String mBirthDay;
	
	private RegistrationActivity mActivity;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (RegistrationActivity) getActivity();
		
		mDateGuide = getResources().getString(R.string.guide_registration_date);
		mSelectBirthday = (Button) getView().findViewById(R.id.btn_select_birthday);
		
		if(mActivity.getBaby().birthDay != null){
			mBirthDay = mActivity.getBaby().birthDay;
			String[] ary = mBirthDay.split("-");
			mSelectBirthday.setText(ary[0] + "년 " + NumberUtil.digit(Integer.parseInt(ary[1]), 2) + "월 " + NumberUtil.digit(Integer.parseInt(ary[2]), 2) + "일");
		}else{
			mSelectBirthday.setText(mDateGuide);
		}
		
		final Calendar calendar = new GregorianCalendar();
		mSelectBirthday.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
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
	
	@Override
	public boolean checkInput() {
		if(mSelectBirthday.getText().equals(mDateGuide)){
			LogUtil.toast(getActivity(), "생년월일을 입력해 주세요.");
			return false;
		}
		
		mActivity.getBaby().birthDay = mBirthDay;
		
		return true;
	}

}
