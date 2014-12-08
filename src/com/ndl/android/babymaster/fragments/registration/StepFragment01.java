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

public class StepFragment01 extends Fragment implements ICheckRegistration {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_registration_step01, container, false);
		return view;
	}
	
	private EditText mKorText;
	private EditText mEngText;
	
	private RegistrationActivity mActivity;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mKorText = (EditText) getView().findViewById(R.id.edit_name_kor);
		mEngText = (EditText) getView().findViewById(R.id.edit_name_eng);
		
		mActivity = (RegistrationActivity) getActivity();
		
		if(mActivity.getBaby().nameKor != null){
			mKorText.setText(mActivity.getBaby().nameKor);
		}
		
		if(mActivity.getBaby().nameEng != null){
			mEngText.setText(mActivity.getBaby().nameEng);
		}
		
		mKorText.setOnEditorActionListener(mActivity);
		mEngText.setOnEditorActionListener(mActivity);
	}
	
	public boolean checkInput(){
		if(mKorText.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "한글 이름을 입력해 주세요.");
			return false;
		}else if(mEngText.getText().toString().equals("")){
			LogUtil.toast(getActivity(), "영문 이름을 입력해 주세요.");
			return false;
		}
		
		mActivity.getBaby().nameKor = mKorText.getText().toString();
		mActivity.getBaby().nameEng = mEngText.getText().toString();
		
		return true;
	}

}
