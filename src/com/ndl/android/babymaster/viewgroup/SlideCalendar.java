package com.ndl.android.babymaster.viewgroup;

import com.judeau.util.NumberUtil;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.adapter.CalendarWeekAdapter;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.interfaces.OnDateSelectedListener;
import com.ndl.android.babymaster.interfaces.OnSlideCalendarListener;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.LinearLayout;

public class SlideCalendar extends LinearLayout implements OnDateSelectedListener {

	public SlideCalendar(Context context) {
		super(context);
		initialize(context, null);
	}

	public SlideCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}
	
	private CalendarContainer mContainer;
	private GridView mWeekView;
	
	private float mOpenY = 0;
	private float mSpaceY = 0;
	
	private float mOpenH = 0;
	private float mCloseH = 0;
	
	public boolean isOpen = false;
	
	private PointF mLastPoint = null;
	
	private int mPaddingTop = 0;
	private int mVspacing = 0;
	
	private void initialize(Context context, AttributeSet attrs){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.slide_calendar, this, true);
		
		mPaddingTop = getResources().getDimensionPixelSize(R.dimen.calendar_padding_top);
		mVspacing = getResources().getDimensionPixelSize(R.dimen.calendar_vspacing);
		
		mWeekView = (GridView) findViewById(R.id.grid_week);
		CalendarWeekAdapter weekAdapter = new CalendarWeekAdapter(context);
		mWeekView.setAdapter(weekAdapter);
		
		mContainer = (CalendarContainer) findViewById(R.id.calendar);
		mContainer.setOnDateSelectedListener(this);
	}
	
	private OnDateSelectedListener mDateSelectedListener;
	
	public void setOnDateSelectedListener(OnDateSelectedListener listener){
		mDateSelectedListener = listener;
	}
	
	@Override
	public void onDateSelected(DateModel model) {
		if(mDateSelectedListener != null) {
			mDateSelectedListener.onDateSelected(model);
		}
	}
	
	public DateModel getDateModel(){
		return mContainer.getDateModel();
	}
	
	public void setDateModel(DateModel model){
		mContainer.setDateModel(model);
	}
	
	private OnSlideCalendarListener mSlideCalendarListener;
	
	public void setOnSlideCalendarListener(OnSlideCalendarListener listener){
		mSlideCalendarListener = listener;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		if(mSpaceY == 0){
			mSpaceY = (mContainer.getHeight() - mPaddingTop)/6;
			
			mOpenH = mContainer.getHeight() + mWeekView.getHeight();
			mCloseH = mOpenH - mContainer.getHeight() + mSpaceY;
			
			calendarOpen(isOpen);
		}
	}
	
	private float getCloseY(){
		float value = -1 * (mContainer.getDateModel().weekIndex*mSpaceY) - mPaddingTop + mVspacing/2;
		return value;
	}
	
	public void calendarOpen(boolean open){
		isOpen = open;
		
		float ty = (open) ? mOpenY : getCloseY();
		mContainer.isOpen = open;
		
		ValueAnimator ani = ValueAnimator.ofFloat(mContainer.getTranslationY(), ty);
		ani.setDuration(200);
		ani.setInterpolator(new DecelerateInterpolator());
		ani.addUpdateListener(animationUpdateListener);
		ani.start();
		
		float th = (isOpen) ? mOpenH : mCloseH;
		mSlideCalendarListener.onSlideUpdate(th);
	}
	
	public float getCalendarHeight(){
		return (isOpen) ? mOpenH : mCloseH;
	}

	ValueAnimator.AnimatorUpdateListener animationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
		
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			float ty = (Float) animation.getAnimatedValue();
			setContainerY(ty);
		}
	};
	
	private void setContainerY(float ty){
		mContainer.setTranslationY(ty);
		
		float th = NumberUtil.getLinearFunctionResult(ty, getCloseY(), mOpenY, mCloseH, mOpenH);
		mSlideCalendarListener.onSlideUpdate(th);
	}
	
	private float mPrevY = 0;
	
	private VelocityTracker mVelocityTracker = null;
	private static final int DRAG_VELOCITY = 100;
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		if(mVelocityTracker == null)
			mVelocityTracker = VelocityTracker.obtain();
		
		mVelocityTracker.addMovement(ev);
		
		int action = ev.getAction();
		float y = ev.getY();
		
		/*if(action == MotionEvent.ACTION_DOWN){
			mPrevY = y;
		}
		else if(action == MotionEvent.ACTION_MOVE){
			float dy = y - mPrevY;
			float ty = mContainer.getTranslationY() + dy;
			
			if(ty < getCloseY()){
				ty = getCloseY();
			}else if(ty > mOpenY){
				ty = mOpenY;
			}
			
			setContainerY(ty);
			
			mPrevY = y;
		}
		else*/ 
		if(action == MotionEvent.ACTION_UP){
			mVelocityTracker.computeCurrentVelocity(1000);
			int velocity = (int) mVelocityTracker.getYVelocity();
			
			if(velocity > DRAG_VELOCITY ){
				calendarOpen(true);
			}else if(velocity < -DRAG_VELOCITY){
				calendarOpen(false);
			}else{
				int dir = Math.round((mContainer.getTranslationY() - getCloseY())/(mOpenY - getCloseY()));
				boolean open = (dir == 1) ? true : false;
				calendarOpen(open);
			}
			
			mVelocityTracker.recycle(); 
	        mVelocityTracker = null; 
		}
		
		return true;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev){
		int action = ev.getAction();
		float y = ev.getY();
		
		if(action == MotionEvent.ACTION_DOWN){
			mPrevY = y;
		}
		else if(action == MotionEvent.ACTION_MOVE){
			if(!mContainer.isDragging){
				float distance = y - mPrevY;
				
				if(Math.abs(distance) > 20){
					mPrevY = y;
					return true;
				}
			}
		}
		
		return false;
	}

}
