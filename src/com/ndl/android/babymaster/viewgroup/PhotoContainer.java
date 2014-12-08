package com.ndl.android.babymaster.viewgroup;

import java.util.ArrayList;

import com.judeau.util.NumberUtil;
import com.judeau.util.UnitUtil;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.SelectActivity;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.view.PhotoView;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class PhotoContainer extends LinearLayout implements View.OnClickListener {

	public PhotoContainer(Context context) {
		super(context);
		initialize(context, null);
	}

	public PhotoContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public PhotoContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private Context mContext;
	private BabyDb mDb;
	
	private int mSpaceX = 0;
	
	private VelocityTracker mVelocityTracker = null;
	
	private Scroller mScroller = null;
	private PointF mLastPoint = null;
	
	private int mCurrentPhoto = 0;
	
	private int mDragDistance = 10; 
	
	private int mTouchState;
	
    private static final int TOUCH_STATE_SCROLLING = 0;
    private static final int TOUCH_STATE_NORMAL = 1;
    
    private static final int DRAG_DISTANCE = 10;
    private static final int DRAG_VELOCITY = 100;
    
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		
		mDb = BabyDb.getInstance(context);
		
		mSpaceX = (int)(UnitUtil.getScreenWidth(mContext) * 0.5);
		
		mScroller = new Scroller(context);
		mLastPoint = new PointF();
		
		aryPhoto = new ArrayList<PhotoView>();
		
		setPhotoList();
	}
	
	private void setPhotoList(){
		Cursor cursor = mDb.selectBabys();
		
		while(cursor.moveToNext()){
			addPhotoView(cursor.getString(cursor.getColumnIndex(BabyData.PICTURE_PATH)));
		}
		
		cursor.close();
		
		invalidatePhotoView();
	}
	
	private ArrayList<PhotoView> aryPhoto;
	
	private void addPhotoView(String path){
		PhotoView pv = new PhotoView(mContext);
		
		BitmapDrawable drawable;
		
		if(path == null){
			drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_photo_bear);
			pv.setBitmap(drawable.getBitmap());
		}else{
			Bitmap bitmap;
			
			try{
				bitmap = BitmapFactory.decodeFile(path);
				pv.setBitmap(bitmap);
			}catch(Exception e){
				drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_photo_bear);
				pv.setBitmap(drawable.getBitmap());
			}
		}
		
		pv.setOnClickListener(this);
		
		aryPhoto.add(pv);
		this.addView(pv);
	}
	
	@Override
	public void onClick(View v) {
		SelectActivity activity = (SelectActivity) getContext();
		activity.onClick(this);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		for(int i=0; i<getChildCount(); i++){
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		for(int i=0; i<getChildCount(); i++){
			getChildAt(i).layout(mSpaceX*i, 0, mSpaceX*i + getChildAt(i).getMeasuredWidth(), 
					getChildAt(i).getMeasuredHeight());
		}
	}
	
	public void initPosition(int position){
		mCurrentPhoto = position;
		setScrollX(mSpaceX*mCurrentPhoto);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		
		switch(action){
			case MotionEvent.ACTION_DOWN:
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_NORMAL : TOUCH_STATE_SCROLLING;
				mLastPoint.set(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				int distance = Math.abs(x - (int) mLastPoint.x);
				
				if(distance > DRAG_DISTANCE){
					mTouchState = TOUCH_STATE_SCROLLING;
					mLastPoint.set(x, y);
				}
				
				break;
		}
		
		return mTouchState == TOUCH_STATE_SCROLLING;
	}
	
	public boolean onTouchEvent(MotionEvent ev){
		if(mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		
		mVelocityTracker.addMovement(ev);
		
		int action = ev.getAction();
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(!mScroller.isFinished()){
				mScroller.abortAnimation();
			}
			mLastPoint.set(x, y);
			break;
		case MotionEvent.ACTION_MOVE:
			int distance = (int) (x - mLastPoint.x);
			
			if(mCurrentPhoto == 0) if(distance > 0) distance *= 0.4;
			if(mCurrentPhoto == aryPhoto.size()-1) if(distance < 0) distance *= 0.4;
			
			scrollBy(-distance, 0);
			invalidate();
			
			mLastPoint.set(x, y);
			break;
		case MotionEvent.ACTION_UP:
			mVelocityTracker.computeCurrentVelocity(1000);
			int velocity = (int) mVelocityTracker.getXVelocity();
			
			if(velocity > DRAG_VELOCITY ){
				mCurrentPhoto--;
			}else if(velocity < -DRAG_VELOCITY){
				mCurrentPhoto++;
			}else{
				mCurrentPhoto = Math.round((float)getScrollX()/mSpaceX);
			}
			
			setCurrentPhoto(mCurrentPhoto);
			
			mTouchState = TOUCH_STATE_NORMAL;
			
			mVelocityTracker.recycle(); 
	        mVelocityTracker = null; 
	        
			break;
		}
		
		return true;
	}
	
	private void setCurrentPhoto(int position){
		if(position < 0) mCurrentPhoto = 0;
		if(position > aryPhoto.size()-1) mCurrentPhoto = aryPhoto.size()-1;
		
		int move = mSpaceX * mCurrentPhoto - getScrollX();
		
		mScroller.startScroll(getScrollX(), 0, move, 0, 300);
		invalidate();
		
		SelectActivity activity = (SelectActivity) getContext();
		activity.setContents(mCurrentPhoto);
	}
	
	@Override
	public void computeScroll(){
		super.computeScroll();
		
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		}
		
		invalidatePhotoView();
	}
	
	private void invalidatePhotoView(){
		
		for(int i=0; i<aryPhoto.size(); i++){
			PhotoView pv = aryPhoto.get(i);
			
			int x = Math.abs(getScrollX() - mSpaceX*i);
			int a = 0;
			int b = mSpaceX;
			float c = 1f;
			float d = 0.3f;
			float e = 0.8f;
			
			float resultA = NumberUtil.getLinearFunctionResult(x, a, b, c, d);
			pv.setAlpha(resultA);
			
			float resultS = NumberUtil.getLinearFunctionResult(x, a, b, c, e);
			pv.setScaleX(resultS);
			pv.setScaleY(resultS);
		}
	}
}
