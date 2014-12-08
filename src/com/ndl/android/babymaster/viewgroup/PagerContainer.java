package com.ndl.android.babymaster.viewgroup;

import java.util.ArrayList;

import com.judeau.util.UnitUtil;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.BabyDb;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PagerContainer extends LinearLayout {

	public PagerContainer(Context context) {
		super(context);
		initialize(context, null);
	}

	public PagerContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private Context mContext;
	
	private int mDotWidth = 0;
	
	private BabyDb mDb;
	
	private ArrayList<ImageView> aryImage;
	
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		mDotWidth = UnitUtil.dpiToPixel(context, 8);
		
		aryImage = new ArrayList<ImageView>();
		
		mDb = BabyDb.getInstance(context);
	}
	
	public void initPage(int position){
		int len = mDb.countBabys();
		
		for(int i=0; i<len; i++){
			ImageView iv = new ImageView(mContext);
			aryImage.add(iv);
			
			LayoutParams params = new LinearLayout.LayoutParams(mDotWidth, mDotWidth);
			iv.setImageResource(R.drawable.page_circle_normal);
			
			if(i != 0) params.setMargins(mDotWidth*2, 0, 0, 0);
			
			iv.setLayoutParams(params);
			
			this.addView(iv);
		}
		
		setPage(position);
	}
	
	public void setPage(int position){
		for(int i=0; i<aryImage.size(); i++){
			ImageView iv = aryImage.get(i);
			if(i == position){
				iv.setImageResource(R.drawable.page_circle_selected);
			}else{
				iv.setImageResource(R.drawable.page_circle_normal);
			}
		}
	}

}
