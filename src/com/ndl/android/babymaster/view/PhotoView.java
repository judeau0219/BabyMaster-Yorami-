package com.ndl.android.babymaster.view;

import com.judeau.graphics.drawable.RoundedDrawable;
import com.ndl.android.babymaster.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class PhotoView extends FrameLayout {

	public PhotoView(Context context) {
		super(context);
		initialize(context, null);
	}

	public PhotoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public PhotoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	public static final int PICK_FROM_CAMERA = 0;
	public static final int PICK_FROM_ALBUM = 1;
	public static final int CROP_FROM_CAMERA = 2;
	
	private Context mContext;
	
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_photo, this, true);
	}
	
	public void setBitmap(Bitmap bitmap){
		ImageView iv = (ImageView) findViewById(R.id.img_profile);
		iv.setImageDrawable(new RoundedDrawable(bitmap));
	}

}
