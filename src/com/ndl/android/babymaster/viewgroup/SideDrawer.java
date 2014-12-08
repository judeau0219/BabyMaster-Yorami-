package com.ndl.android.babymaster.viewgroup;

import com.judeau.util.NumberUtil;
import com.judeau.util.UnitUtil;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.interfaces.OnSideDrawerListener;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

public class SideDrawer extends ViewGroup {

	public SideDrawer(Context context) {
		super(context);
		initialize(context, null);
	}

	public SideDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public SideDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private Context mContext;
	
	private ImageView mCover;
	private ImageView mShadow;
	private ViewGroup mDrawer;
	
	private int mScreenWidth;
	private int mDrawerWidth;
	
	private int mOpenX;
	private int mCloseX;
	private int mPeepX;
	private int mDragDistance;
	
	private int mDrawerHandleWidth;
	
	private int mVeloctyX = 0;
	private int mFlingVelocity = 100;
	
	private boolean mFromLeft = true;
	
	public boolean isOpen = false;
	private boolean isDragging = false;
	
	private VelocityTracker mVelocityTracker;
	
	private ValueAnimator mDrawerAni;
	
	private OnSideDrawerListener mSideDrawerListener;
	
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		
		mScreenWidth = UnitUtil.getScreenWidth(context);
		mDrawerHandleWidth = getResources().getDimensionPixelSize(R.dimen.drawer_handle_width);
		mDragDistance = UnitUtil.dpiToPixel(context, 10);
		
		int defWidth = UnitUtil.dpiToPixel(context, 220);
		int coverColor = 0x00000000;
		int shadowResId = 0;
		
		if(attrs != null){
			TypedArray ary = context.obtainStyledAttributes(attrs, R.styleable.DrawerAttrs);
			
			mDrawerWidth = (int) ary.getDimension(R.styleable.DrawerAttrs_drawer_width, defWidth);
			coverColor = (int) ary.getColor(R.styleable.DrawerAttrs_cover_color, 0x00000000);
			mFromLeft = ary.getBoolean(R.styleable.DrawerAttrs_from_left, true);
			shadowResId = ary.getResourceId(R.styleable.DrawerAttrs_drawer_shadow, 0);
		} 
		
		mCover = new ImageView(context);
		mCover.setBackgroundColor(coverColor);
		this.addView(mCover);
		
		if(shadowResId != 0){
			mShadow = new ImageView(context);
			mShadow.setBackgroundResource(shadowResId);
			this.addView(mShadow);
		}
		
		mDrawer = (ViewGroup) View.inflate(context, R.layout.main_drawer, null);
		this.addView(mDrawer);
		
		mOpenX = (mFromLeft) ? 0 : mScreenWidth - mDrawerWidth;
		mCloseX = (mFromLeft) ? -mDrawerWidth : mScreenWidth;
		
		int temp = (int)(mDrawerHandleWidth * 0.5);
		mPeepX = (mFromLeft) ? temp - mDrawerWidth : mScreenWidth - temp;
		
		mDrawer.setTranslationX((float) mCloseX);
		mCover.setAlpha(0.0f);
		
		menuOpen(false);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		// mDrawer 레이아웃 내의 View 들이 onMeasure 함수를 통해 전달된 Spec 을 참조하여 레이아웃을 구성한다.
		mDrawer.measure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mCover.layout(l, t, r, b);
		if(mShadow != null) mShadow.layout(l, t, 15, b);
		mDrawer.layout(l, t, mDrawerWidth, b);
	}
	
	public void setOnSideDrawerListener(OnSideDrawerListener listener){
		mSideDrawerListener = listener;
	}
	
	public void menuOpen(Boolean open){
		isOpen = open;
		
		int tx = (open) ? mOpenX : mCloseX;
		long duration = 200;
		
		Interpolator interpolator= new DecelerateInterpolator();
		
		mDrawerAni = ValueAnimator.ofInt((int) mDrawer.getX(), tx);
		mDrawerAni.setDuration(duration);
		mDrawerAni.setInterpolator(interpolator);
		mDrawerAni.addUpdateListener(menuAnimationUpdateListener);
		mDrawerAni.start();
		
		if(mSideDrawerListener != null){
			mSideDrawerListener.onSideDrawerOpen(open);
		}
	}
	
	ValueAnimator.AnimatorUpdateListener menuAnimationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			int tx = (Integer) animation.getAnimatedValue();
			setMenuTranslationX(tx);
		}
	};
	
	private void setMenuTranslationX(int tx){
		mDrawer.setTranslationX(tx);
		
		float targetA = NumberUtil.getLinearFunctionResult(tx, mCloseX, mOpenX, 0, 1);
		mCover.setAlpha(targetA);
		
		if(mShadow != null){
			mShadow.setTranslationX(tx + mDrawer.getWidth());
			mShadow.setAlpha(targetA);
		}
	}
	
	private void dragDrawer(float x){
		float tx;
		
		if(mFromLeft){
			tx = mDrawer.getX() + (x - mPrevX);
			if(tx > mOpenX) tx = mOpenX;
		}else{
			tx = mDrawer.getX() - (mPrevX - x);
			if(tx < mOpenX) tx = mOpenX;
		}
		
		setMenuTranslationX((int)tx);
	}
	
	private boolean isDrawerTouch(float x){
		if(mDrawer.getX() <= x && x <= mDrawer.getX() + mDrawer.getWidth()){
			return true;
		}
		
		return false;
	}
	
	private void dragRelease(){
		if(Math.max(mDrawer.getX(), mOpenX) - Math.min(mDrawer.getX(), mOpenX) < mDrawerWidth * 0.5) {
			menuOpen(true);
		}else{
			menuOpen(false);
		}
	}
	
	// false 를 반환하면 down 이벤트 외에 어떤 이벤트도 발생시키지 않는다.
	// true 를 반환해야 move, up 등의 이벤트가 차례로 발생된다.
	@Override
	public boolean onTouchEvent( MotionEvent e ){
		
		if( mVelocityTracker == null ) mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(e);
		
		int action = e.getAction();
		float x = e.getX();
		
		switch(action)
		{
			case MotionEvent.ACTION_DOWN:
				if(isDragging){
					if(mDrawer.getX() == mCloseX) setMenuTranslationX(mPeepX);
					return true;
				}else{
					if(isOpen){
						if(!isDrawerTouch(x)){
							menuOpen(false);
						}
						return true;
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(isDragging){
					dragDrawer(x);
					mPrevX = x;
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				if(isDragging){ // 드래그를 놓았을 때 가까운 지점으로 열리거나 닫히도록..
					isDragging = false;
					
					mVelocityTracker.computeCurrentVelocity(100);
					mVeloctyX = (int) mVelocityTracker.getXVelocity();
					
					if(mFromLeft){
						if(mVeloctyX > mFlingVelocity){
							menuOpen(true);
							return true;
						}else if(mVeloctyX < -mFlingVelocity){
							menuOpen(false);
							return true;
						}
					}else{
						if(mVeloctyX < -mFlingVelocity){
							menuOpen(true);
							return true;
						}else if(mVeloctyX > mFlingVelocity){
							menuOpen(false);
							return true;
						}
					}
					
					dragRelease();
					
					return true;
				}
				break;
		}
		
		return false;
	}
	
	private float mPrevX = 0;
	
	public boolean onInterceptTouchEvent(MotionEvent e){
		int action = e.getAction();
		float x = e.getX();
		
		boolean intercept = false;
		
		switch(action)
		{
			case MotionEvent.ACTION_DOWN:
				if(mDrawerAni.isRunning()){
					if(isDrawerTouch(x)){
						mDrawerAni.cancel();
						isDragging = true;
						intercept = true;
					}
				}else{
					if(!isOpen){
						if(isDragStart(x)){
							isDragging = true;
							intercept = true;
						}
					}
				}
				
				mPrevX = x;
				
				break;
			case MotionEvent.ACTION_MOVE:
				if(isOpen){
					float distance = Math.abs(x - mPrevX);
					if (distance > mDragDistance) {  
						isDragging = true;
						intercept = true;
						mPrevX = x;
					} 
				}

				break;
		}
		
		return intercept;
	}
	
	private boolean isDragStart(float x){
		if(mFromLeft){
			if(x < mDrawerHandleWidth) return true;
		}else{
			if(x > mScreenWidth - mDrawerHandleWidth) return true;
		}
		
		return false;
	}
}
