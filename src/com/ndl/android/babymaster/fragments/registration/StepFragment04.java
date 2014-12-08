package com.ndl.android.babymaster.fragments.registration;

import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.RegistrationActivity;
import com.ndl.android.babymaster.interfaces.ICheckRegistration;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class StepFragment04 extends Fragment implements ICheckRegistration {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_registration_step04, container, false);
		return view;
	}
	
	private EditText mHeightText;
	private EditText mWeightText;
	
	private RegistrationActivity mActivity;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mHeightText = (EditText) getView().findViewById(R.id.edit_height);
		mWeightText = (EditText) getView().findViewById(R.id.edit_weight);
		
		mActivity = (RegistrationActivity) getActivity();
		
		if(mActivity.getBaby().height != null){
			mHeightText.setText(mActivity.getBaby().height);
		}
		
		if(mActivity.getBaby().weight != null){
			mWeightText.setText(mActivity.getBaby().weight);
		}
		
		mHeightText.setOnEditorActionListener(mActivity);
		mWeightText.setOnEditorActionListener(mActivity);
	}
	
	public boolean checkInput(){
		if(mHeightText.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "키를 입력해 주세요.");
			return false;
		}else if(mWeightText.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "몸무게를 입력해 주세요.");
			return false;
		}
		
		mActivity.getBaby().height = mHeightText.getText().toString();
		mActivity.getBaby().weight = mWeightText.getText().toString();
		
		return true;
	}

}
