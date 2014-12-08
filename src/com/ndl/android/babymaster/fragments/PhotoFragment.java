package com.ndl.android.babymaster.fragments;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.view.PhotoView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;


public class PhotoFragment extends Fragment implements View.OnClickListener, DialogInterface.OnClickListener {
	
	protected PhotoView mPhotoView;
	
	private Uri mImageUri;
	private Uri mCropUri;
	
	private Bitmap mBitmap;
	
	private String[] selectors;
	
	private MediaScannerConnection mMediaScanner;
	private String mScanPath;
	
	private String mCropDirPath;
	protected String mCropFilePath;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		selectors = getResources().getStringArray(R.array.camera_selector);
		
		mMediaScanner = new MediaScannerConnection(getActivity(), mScannerClient);
		mCropDirPath = Environment.getExternalStorageDirectory() + "/Yorami";
	}
	
	private MediaScannerConnectionClient mScannerClient = new MediaScannerConnectionClient() {
		
		@Override
		public void onScanCompleted(String path, Uri uri) {
			mMediaScanner.disconnect();
		}
		
		@Override
		public void onMediaScannerConnected() {
			mMediaScanner.scanFile(mScanPath, null);
		}
	};
	
	@Override
	public void onClick(View v) {
		openSelectDialog();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which)
		{
			case 0:
				takePhotoAlbum();
				break;
			case 1:
				takeCamera();
				break;
			case 2:
				setDefaultPhoto();
				// deleteCropImage();
				mCropFilePath = null;
				break;
		}
	}
	
	protected void setDefaultPhoto(){
		BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_photo_default);
		mPhotoView.setBitmap(drawable.getBitmap());
	}
	
	private void takeCamera(){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		// 카메라로 찍은 최초 이미지 임시파일 설정 및 저장
		String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
		// mImageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
		mImageUri = Uri.fromFile(new File(mCropDirPath, url));
		
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
		
		startActivityForResult(intent, PhotoView.PICK_FROM_CAMERA);
	}
	
	private void takePhotoAlbum(){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
		startActivityForResult(intent, PhotoView.PICK_FROM_ALBUM);
	}
	
	public void onActivityResult (int requestCode, int resultCode, Intent data){
		if(resultCode != Activity.RESULT_OK) 
			return;
		
		switch(requestCode){
			case PhotoView.PICK_FROM_ALBUM:
				mImageUri = data.getData();
			case PhotoView.PICK_FROM_CAMERA:
				imageCrop();
				break;
			case PhotoView.CROP_FROM_CAMERA: // 이미지 크롭 후..
				final Bundle extra = data.getExtras();
				
				if(extra != null){
					Bitmap bitmap = extra.getParcelable("data");
					saveCropImage(bitmap);
					
					mPhotoView.setBitmap(bitmap);
					// deleteTempImage();
					mImageUri = null;
				}
				
				break;
		}
	}
	
	protected void saveCropImage(Bitmap bitmap){
		File dir = new File(mCropDirPath);
		if(!dir.exists())	dir.mkdir();
		
		mCropFilePath = mCropDirPath + "/" + System.currentTimeMillis() + ".jpg";
		
		File cropFile = new File(mCropFilePath);
		
		BufferedOutputStream bos = null;
		
		try{
			cropFile.createNewFile();
			
			bos = new BufferedOutputStream(new FileOutputStream(cropFile));
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			
			fileScanning(mCropFilePath);
			
			bos.flush();
			bos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		/*
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		intent.setData(Uri.parse("file://" + mCropFilePath));
		getActivity().sendBroadcast(intent);
		*/
	}
	
	private void imageCrop(){
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		cropIntent.setDataAndType(mImageUri, "image/*");
		
		// crop 한 이미지 사이즈 (300*300)
		cropIntent.putExtra("outputX", 300);
		cropIntent.putExtra("outputY", 300);
		// 가로세로 비율
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		// 꽉찬 비율
		cropIntent.putExtra("scale", true);
		// camera.face
		cropIntent.putExtra("noFaceDetection", true);
		// JPG 파일형식 저장
		cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		// 번들용량 제한으로 크기가 큰 이미지는 넘겨줄 수 없다
		cropIntent.putExtra("return-data", true);
		
		startActivityForResult(cropIntent, PhotoView.CROP_FROM_CAMERA);
	}
	
	private void openSelectDialog(){
		AlertDialog alert = new AlertDialog.Builder(getActivity())
		.setTitle("사진선택")
		.setItems(selectors, this)
		.setCancelable(true)
		.create();
		
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
	
	private void deleteTempImage(){
		if(mImageUri != null){
			String path = mImageUri.getPath();
			File file = new File(path);
			
			if(file.exists()){
				file.delete();
				fileScanning(path);
				
				mImageUri = null;
			}
		}
	}
	
	protected void deleteCropImage(){
		if(mCropFilePath != null){
			File file = new File(mCropFilePath);
			
			if(file.exists()){
				file.delete();
				fileScanning(mCropFilePath);
				
				mCropFilePath = null;
			}
		}
	}
	
	private void fileScanning(String path){
		mScanPath = path;
		
		if(!mMediaScanner.isConnected()){
			mMediaScanner.connect();
		}
	}
	/*
	private void dirScanning(){
		Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
		intent.setData(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/Yorami"));
		getActivity().sendBroadcast(intent);
	}
	*/
}
