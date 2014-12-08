package com.ndl.android.babymaster.viewgroup;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.judeau.util.UnitUtil;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.interfaces.OnDateSelectedListener;
import com.ndl.android.babymaster.view.CalendarView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class CalendarContainer extends FrameLayout implements OnDateSelectedListener {

	public CalendarContainer(Context context) {
		super(context);
		initialize(context, null);
	}

	public CalendarContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public CalendarContainer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private Context mContext;
	
	private CalendarView mCurrentView; // 현재 보여지는 캘린더뷰
	
	private CalendarView mView1;
	private CalendarView mView2;
	
	private int mScreenWidth;
	
	private int mDir = 0;
	private int mTempDir = 1; // 드래그 시작시 임시로 저장될 방향
	
	private float mPrevX = 0;
	
	private VelocityTracker mVelocityTracker = null;
	private static final int DRAG_VELOCITY = 100;
	
	public boolean isDragging = false;
	public boolean isOpen = false;
	
	private ValueAnimator mAni;
	
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		mScreenWidth = UnitUtil.getScreenWidth(context);
		
		mView2 = new CalendarView(mContext);
		mView2.setOnDateSelectedListener(this);
		this.addView(mView2);
		
		mView1 = new CalendarView(mContext);
		mView1.setOnDateSelectedListener(this);
		this.addView(mView1);
		
		mCurrentView = mView1;
	}
	
	private DateModel mDateModel;
	
	public DateModel getDateModel(){
		return mDateModel;
	}
	
	public void setDateModel(DateModel model){
//		mDateModel = model;
		mCurrentView.setDateModel(model);
		mDateModel = mCurrentView.getDateModel();
		
		if(mDateSelectedListener != null){
			mDateSelectedListener.onDateSelected(model);
		}
	}
	
	private OnDateSelectedListener mDateSelectedListener;
	
	public void setOnDateSelectedListener(OnDateSelectedListener listener){
		mDateSelectedListener = listener;
	}
	
	@Override
	public void onDateSelected(DateModel model) {
		if(model.dateOfMonth == 0){
			setDateModel(model);
		}else{
			mDir = model.dateOfMonth;
			beforeDraggingPosition(mDir, model.date);
			
			startAnimation();
		}
	}
	
	// 드래그 시작 직전에 캘런더뷰 포지션 및 데이터 셋팅..
	private void beforeDraggingPosition(int dir, int date){ // prev: -1, next: 1
		mTempDir = dir;
		
		mView1.setTranslationX(0);
		mView1.setDateModel(mDateModel);
		
		mView2.setDateModel(getDateModelOfMonth(dir, date));
		mView2.setTranslationX(mScreenWidth*dir);
	}
	
	// 이전 혹은 다음 월의 데이트 모델 반환
	private DateModel getDateModelOfMonth(int dir, int date){
		GregorianCalendar calendar = new GregorianCalendar(mDateModel.year, mDateModel.month, 1);
		calendar.add(Calendar.MONTH, dir);
		return new DateModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), date);
	}
	
	private void startAnimation(){
		mCurrentView = (mDir == 0) ? mView1 : mView2;
		setDateModel(mCurrentView.getDateModel());
//		mDateModel = mCurrentView.getDateModel();
		
		mAni = ValueAnimator.ofFloat(mView1.getTranslationX(), -1*mScreenWidth*mDir);
		mAni.setDuration(350);
		mAni.setInterpolator(new DecelerateInterpolator());
		/*mAni.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd( Animator animation ){
				if(mDateSelectedListener != null) mDateSelectedListener.onDateSelected(mDateModel);
			}
		});*/
		mAni.addUpdateListener(animationUpdateListener);
		mAni.start();
	}
	
	ValueAnimator.AnimatorUpdateListener animationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			float tx = (Float) animation.getAnimatedValue();
			mView1.setTranslationX(tx);
			
			if(mDir == 0){
				mView2.setTranslationX(mView1.getTranslationX() + (mScreenWidth*mTempDir));
			}else{
				mView2.setTranslationX(mView1.getTranslationX() + (mScreenWidth*mDir));
			}
		}
	};
	
	private void setCalendarViewTranslationX(float dx){
		mView1.setTranslationX(mView1.getTranslationX() + dx);
		mView2.setTranslationX(mView2.getTranslationX() + dx);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		if(mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		
		mVelocityTracker.addMovement(ev);
		
		int action = ev.getAction();
		float x = ev.getX();
		
		if(action == MotionEvent.ACTION_DOWN){
			mPrevX = x;
		}
		else if(action == MotionEvent.ACTION_MOVE){
			float dx = x - mPrevX;
			setCalendarViewTranslationX(dx);
			
			mPrevX = x;
		}
		else if(action == MotionEvent.ACTION_UP){
			mVelocityTracker.computeCurrentVelocity(1000);
			int velocity = (int) mVelocityTracker.getXVelocity();
			
			if(velocity > DRAG_VELOCITY ){
				mDir = -1;
			}else if(velocity < -DRAG_VELOCITY){
				mDir = 1;
			}else{
				mDir = -1 * (Math.round( (mView1.getTranslationX() + mScreenWidth) / mScreenWidth ) - 1);
			}
			
			startAnimation();
			
			mVelocityTracker.recycle(); 
	        mVelocityTracker = null; 
	        
	        isDragging = false;
		}
		
		return true;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		float x = ev.getX();
		
		if(action == MotionEvent.ACTION_DOWN){
			mPrevX = x;
		}
		else if(action == MotionEvent.ACTION_MOVE){
			if(!isOpen) return false; 
			
			float distance = x - mPrevX;
			
			if(Math.abs(distance) > 20){
				if(mAni != null){
					if(mAni.isRunning()) return false;
				}
				
				int value = 1;
				
				if(distance > 0)
					value = -1;
				
				beforeDraggingPosition(value, 1);
				
				mPrevX = x;
				
				isDragging = true;
				
				return true;
			}
		}
		
		return false;
	}
	
}
