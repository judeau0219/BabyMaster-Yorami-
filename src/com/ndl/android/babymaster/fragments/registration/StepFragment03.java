package com.ndl.android.babymaster.fragments.registration;

import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.RegistrationActivity;
import com.ndl.android.babymaster.database.Constants.BabyConst;
import com.ndl.android.babymaster.interfaces.ICheckRegistration;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

public class StepFragment03 extends Fragment implements ICheckRegistration {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_registration_step03, container, false);
		return view;
	}
	
	private RegistrationActivity mActivity;
	private RadioGroup mRadioGroup;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mRadioGroup = (RadioGroup) getView().findViewById(R.id.radiogroup_sex);
//		mRadioGroup.setOnCheckedChangeListener(checkChangeListener);
		
		mActivity = (RegistrationActivity) getActivity();
		
		if(mActivity.getBaby().sex != null){
			if(mActivity.getBaby().sex == BabyConst.MALE){
				mRadioGroup.check(R.id.radio_male);
			}else{
				mRadioGroup.check(R.id.radio_female);
			}
		}
	}
	/*
	RadioGroup.OnCheckedChangeListener checkChangeListener = new RadioGroup.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.radio0:
				// 남
				break;
			case R.id.radio1:
				// 여
				break;
			}
		}
	};
	 */
	@Override
	public boolean checkInput() {
		if(mRadioGroup.getCheckedRadioButtonId() == R.id.radio_male){
			mActivity.getBaby().sex = BabyConst.MALE;
		}else{
			mActivity.getBaby().sex = BabyConst.FEMALE;
		}
		
		return true;
	}

}
