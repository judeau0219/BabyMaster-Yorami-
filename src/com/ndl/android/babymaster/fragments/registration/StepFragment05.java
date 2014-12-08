package com.ndl.android.babymaster.fragments.registration;

import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.RegistrationActivity;
import com.ndl.android.babymaster.database.Constants.BabyConst;
import com.ndl.android.babymaster.fragments.PhotoFragment;
import com.ndl.android.babymaster.interfaces.ICheckRegistration;
import com.ndl.android.babymaster.view.PhotoView;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StepFragment05 extends PhotoFragment implements ICheckRegistration, View.OnClickListener, DialogInterface.OnClickListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_registration_step05, container, false);
		return view;
	}
	
	private RegistrationActivity mActivity;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mPhotoView = (PhotoView) getView().findViewById(R.id.photo_view);
		mPhotoView.setOnClickListener(this);
		
		mActivity = (RegistrationActivity) getActivity();
		
		((TextView) getView().findViewById(R.id.txt_name_kor)).setText(mActivity.getBaby().nameKor);
		((TextView) getView().findViewById(R.id.txt_name_eng)).setText(mActivity.getBaby().nameEng);
		
		String[] ary = mActivity.getBaby().birthDay.split("-");
		((TextView) getView().findViewById(R.id.txt_birthday)).setText(ary[0] + "년 " + ary[1] + "월 " + ary[2] + "일");
		
		String sex = (mActivity.getBaby().sex == BabyConst.MALE) ? "남자아이" : "여자아이";
		((TextView) getView().findViewById(R.id.txt_sex)).setText(sex);
		
		((TextView) getView().findViewById(R.id.txt_hweight)).setText(mActivity.getBaby().height + "cm / " + mActivity.getBaby().weight + "kg");
		
		setDefaultPhoto();
	}
	
	@Override
	protected void saveCropImage(Bitmap bitmap){
		super.saveCropImage(bitmap);
		mActivity.getBaby().picturePath = mCropFilePath;
	}
	
	@Override
	public boolean checkInput() {
		return false;
	}
}
