package com.ndl.android.babymaster.view;

import com.judeau.widget.Arc;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.interfaces.OnRecorderViewListener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class RecorderView extends FrameLayout {

	public RecorderView(Context context) {
		super(context);
		initialize(context, null);
	}

	public RecorderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public RecorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private Context mContext;
	
	private FrameLayout mFrameRecorder;
	
	private Arc mArcTimer;
	
	private ImageView mImageIcon;
	private TextView mTextName;
	
	private float mZoomInScale = 1f;
	private float mZoomOutScale = 0.78f;
	
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_recorder, this, true);
		
		mFrameRecorder = (FrameLayout) findViewById(R.id.frame_recorder);
		mArcTimer = (Arc) findViewById(R.id.arc_timer);
		
		int iconId = 0;
		
		mImageIcon = (ImageView) findViewById(R.id.img_icon);
		mTextName = (TextView) findViewById(R.id.txt_recorder_name);
		
		if(attrs != null){
			TypedArray ary = context.obtainStyledAttributes(attrs, R.styleable.RecorderViewAttrs);
			
			iconId = ary.getResourceId(R.styleable.RecorderViewAttrs_recorder_icon, 0);
			mTextName.setText(ary.getString(R.styleable.RecorderViewAttrs_recorder_name));
		} 
		
		if(iconId != 0){
			mImageIcon.setBackgroundResource(iconId);
		}
	}
	
	public void setScaleAnimation(boolean zoomIn){
		float ts = (zoomIn) ? mZoomInScale : mZoomOutScale;
		long duration = 200;
		Interpolator interpolator= new DecelerateInterpolator();
		
		mFrameRecorder.animate().setDuration(duration).setInterpolator(interpolator).scaleX(ts).scaleY(ts);
		
		/*
		ObjectAnimator aniX = ObjectAnimator.ofFloat(mFrameRecorder, "scaleX", ts);
		ObjectAnimator aniY = ObjectAnimator.ofFloat(mFrameRecorder, "scaleY", ts);
		
		AnimatorSet aniSet = new AnimatorSet();
		aniSet.setDuration(duration);
		aniSet.setInterpolator(interpolator);
		aniSet.playTogether(aniX, aniY);
		aniSet.start();
		*/
	}
	
	private OnRecorderViewListener mRecorderViewListener;
	
	public void setOnRecorderViewListener(OnRecorderViewListener listener){
		mRecorderViewListener = listener;
	}
	
	private ValueAnimator mAni;
	
	public void drawArc(){
		mAni = ValueAnimator.ofFloat(0, 360);
		mAni.setDuration(5000);
		mAni.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd( Animator animation ){
				mRecorderViewListener.onTimeOver();
			}
		});
		mAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mArcTimer.setEndAngle((Float) animation.getAnimatedValue());
			}
		});
		
		mAni.start();
	}
	
	public void initArc(){
		if(mAni != null){
			if(mAni.isRunning()){
				mAni.cancel();
			}
		}
	}

}
