package com.ndl.android.babymaster.fragments;

import com.judeau.util.LogUtil;
import com.ndl.android.babymaster.DrawerActivity;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.BabyConst;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.view.PhotoView;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends PhotoFragment implements View.OnClickListener, DialogInterface.OnClickListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);
		return view;
	}
	
	private DrawerActivity mActivity;
	private Cursor mCursor;
	private BabyDb mDb;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mDb = BabyDb.getInstance(getActivity());
		mActivity = (DrawerActivity) getActivity();
		
		mActivity.selectCursor();
		mCursor = mActivity.mCursor;
		
		mPhotoView = (PhotoView) getView().findViewById(R.id.photo_view);
		mPhotoView.setOnClickListener(this);
		
		mActivity.setDrawerTitle(getResources().getStringArray(R.array.menu)[4]);
		
		String strNameKor = mCursor.getString(mCursor.getColumnIndex(BabyData.NAME_KOR));
		String strNameEng = mCursor.getString(mCursor.getColumnIndex(BabyData.NAME_ENG));
		
		String[] ary = mCursor.getString(mCursor.getColumnIndex(BabyData.BIRTHDAY)).split("-");
		String strBirthDay = ary[0] + "년 " + ary[1] + "월 " + ary[2] + "일";
		
		String strSex = (mCursor.getString(mCursor.getColumnIndex(BabyData.SEX)).equals(BabyConst.MALE)) ? "남자아이" : "여자아이";
		
		String strHeight = mCursor.getString(mCursor.getColumnIndex(BabyData.HEIGHT)) + "cm / " + mCursor.getString(mCursor.getColumnIndex(BabyData.WEIGHT)) + "kg";
		
		((TextView) getView().findViewById(R.id.txt_name_kor)).setText(strNameKor);
		((TextView) getView().findViewById(R.id.txt_name_eng)).setText(strNameEng);
		((TextView) getView().findViewById(R.id.txt_birthday)).setText(strBirthDay);
		((TextView) getView().findViewById(R.id.txt_sex)).setText(strSex);
		((TextView) getView().findViewById(R.id.txt_hweight)).setText(strHeight);
		
		initPhoto();
	}
	
	private void initPhoto(){
		String path = mCursor.getString(mCursor.getColumnIndex(BabyData.PICTURE_PATH));
		
		if(path == null){
			setDefaultPhoto();
		}else{
			try{
				mCropFilePath = path;
				mPhotoView.setBitmap(BitmapFactory.decodeFile(path));
			}catch(Exception e){
				setDefaultPhoto();
			}
		}
	}
	
	@Override
	protected void deleteCropImage(){
		super.deleteCropImage();
		
		int id = mCursor.getInt(mCursor.getColumnIndex(BabyData._ID));
		
		if(mDb.updateBabyOfPicturePath(null, id))
			mActivity.setThumb(null);
	}
	
	@Override
	protected void saveCropImage(Bitmap bitmap){
		super.saveCropImage(bitmap);
		
		int id = mCursor.getInt(mCursor.getColumnIndex(BabyData._ID));
		
		if(mDb.updateBabyOfPicturePath(mCropFilePath, id))
			mActivity.setThumb(mCropFilePath);
	}
	
}
