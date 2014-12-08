package com.ndl.android.babymaster.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.judeau.util.NumberUtil;
import com.judeau.util.UnitUtil;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.RecordConst;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.util.DateUtil;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {

	public GraphView(Context context) {
		super(context);
		initialize(context, null);
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs);
	}
	
	// 구분기준
	public static final String BY_DATE = "byDate";
	public static final String BY_MONTH = "byMonth";
	
	// 단위
	private static final String FOR_COUNT = "forCount";
	private static final String FOR_TIME = "forTime";
	private static final String FOR_VOLUME = "forVolume";
	
	// 구분기준
	private static final int COLOR_FILL_01 = 0x8881e0ec;
	private static final int COLOR_FILL_02 = 0x88f9bf04;
	private static final int COLOR_LINE = 0xff231f20;
	private static final int COLOR_BG_THIN = 0x11000000;
	private static final int COLOR_BG_THICK = 0x44000000;
	private static final int COLOR_BG_TEXT = 0xffbebebe;
	
	private BabyDb mDb;
	
	private int mWidth = 300;
	private int mHeight = 300;
	
	private int mPaddingTop;
	private int mPaddingBottom;
	private int mPaddingLeft;
	private int mPaddingRight;
	
	private int mTextSize;
	
	private int mLimitX = 0; // x축 최대개수
	private int mLimitY = 0; // y축 최대개수
	
	private float mGraphW = 0; // 그래프 가로크기
	private float mGraphH = 0; // 그래프 세로크기
	
	private float mStartX = 0;
	private float mStartY = 0;
	private float mSpaceX = 0;
	private float mSpaceY = 0;
	
	private String mUnit; // 기록의 단위
	private int mGap = 1; // y축 라인 간격수치
	
	private void initialize(Context context, AttributeSet attrs){
		mDb = BabyDb.getInstance(context);
		
		mPaddingTop = UnitUtil.dpiToPixel(context, 8);
		mPaddingRight = UnitUtil.dpiToPixel(context, 12);
		mPaddingLeft = UnitUtil.dpiToPixel(context, 30);
		mPaddingBottom = UnitUtil.dpiToPixel(context, 14);
		
		mTextSize = UnitUtil.dpiToPixel(context, 11);
	}
	
	private String[] mTypes;
	private String mPeriod;
	
	private ArrayList<String> mListX = new ArrayList<String>(); // x축 단위 이름
	
	private ArrayList<Integer> mFirstRecords = new ArrayList<Integer>();
	private ArrayList<Integer> mTotalRecords = new ArrayList<Integer>();
	
	public void setGraph(String period, int babyId, DateModel startDate, DateModel endDate, String[] types){
		mLimitY = 0;
		
		mListX.clear();
		mFirstRecords.clear();
		mTotalRecords.clear();
		
		mPeriod = period;
		mTypes = types;
		mUnit = getUnit(mTypes[0]);
		mGap = 1;
		
		if(mPeriod.equals(BY_DATE)){
			mLimitX = DateUtil.elapsedDays(startDate.toString(), endDate.toString());
		}else{
			mLimitX = DateUtil.elapsedMonths(startDate.toString(), endDate.toString());
		}
		
		GregorianCalendar calendar = startDate.getCalendar();
		
		DateModel dateModel = new DateModel();
		DateModel dateModel2 = new DateModel();
		
		for(int i=0; i<mLimitX; i++){
			dateModel.year = calendar.get(Calendar.YEAR);
			dateModel.month = calendar.get(Calendar.MONTH);
			
			if(mPeriod.equals(BY_DATE)){
				dateModel.date = calendar.get(Calendar.DATE);
			}else{
				dateModel.date = 1;
				
				dateModel2.year = calendar.get(Calendar.YEAR);
				dateModel2.month = calendar.get(Calendar.MONTH);
				dateModel2.date = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			}
			
			int totalCount = 0;
			int count = 0;
			
			for(int j=0; j<mTypes.length; j++){
				Cursor cursor;
				
				if(mPeriod.equals(BY_DATE)){
					if(mUnit.equals(FOR_TIME) || mUnit.equals(FOR_VOLUME)){
						cursor = mDb.sumRecords(babyId, dateModel, null, mTypes[j]);
						if(cursor.moveToFirst()) count = cursor.getInt(0);
					}else{ // unit.equals(FOR_COUNT)
						cursor = mDb.selectRecords(babyId, dateModel, null, mTypes[j]);
						count = cursor.getCount();
					}
				}else{ // BY_MONTH
					if(mUnit.equals(FOR_TIME) || mUnit.equals(FOR_VOLUME)){
						cursor = mDb.sumRecords(babyId, dateModel, dateModel2, mTypes[j]);
						if(cursor.moveToFirst()) count = cursor.getInt(0);
					}else{ // unit.equals(FOR_COUNT)
						cursor = mDb.selectRecords(babyId, dateModel, dateModel2, mTypes[j]);
						count = cursor.getCount();
					}
				}
				
				if(j == 0)
					mFirstRecords.add(count);
				
				totalCount += count;
			}
			
			mTotalRecords.add(totalCount);
			mLimitY = Math.max(mLimitY, totalCount);
			
			if(mPeriod.equals(BY_DATE)){
				mListX.add(Integer.toString(dateModel.date));
				calendar.add(Calendar.DATE, 1);
			}else{
				mListX.add(Integer.toString(dateModel.month+1)); 
				calendar.add(Calendar.MONTH, 1);
			}
		}
		
		if(mUnit.equals(FOR_VOLUME)){ // MILK, DRY_MILK
			mGap = (int) (Math.ceil(mLimitY/500.0) * 10);
		}else if(mUnit.equals(FOR_TIME)){ // BREASTFEEDING, SLEEP
			if(mLimitY > 300){ // 최대 값이 300분 이상이면 300분마다 Y축 간격이 1시간 단위씩 증가한다 
				mGap = (int)(mLimitY/300) * 60;
			}else{
				mGap = 30;
			}
		}else{
			if(mPeriod.equals(BY_MONTH)){ // DIAPER(MONTH)
				mGap = 10;
			}
		}
		
		if(mLimitY > 0){
			mLimitY = (int)(mLimitY/mGap);
			mLimitY += 2; //(int)(mLimitY * 0.1);
		}
		
		invalidate();
	}
	
	private String getUnit(String type){
		String unit = FOR_COUNT;
		
		if(type.equals(RecordConst.BREASTFEEDING_LEFT) || type.equals(RecordConst.BREASTFEEDING_RIGHT) || type.equals(RecordConst.SLEEP)){
			unit = FOR_TIME;
		}else if(type.equals(RecordConst.DRY_MILK) || type.equals(RecordConst.MILK)){
			unit = FOR_VOLUME;
		}
		
		return unit;
	}
	
	protected void onDraw(Canvas canvas){
		canvas.drawColor(Color.WHITE);
		
		mGraphW = mWidth - mPaddingLeft - mPaddingRight;
		mGraphH = mHeight - mPaddingTop - mPaddingBottom;
		
		mStartX = mPaddingLeft;
		mStartY = mHeight - mPaddingBottom;
		mSpaceX = mGraphW/(mLimitX-1);
		mSpaceY = mGraphH/mLimitY;
		
		drawBackground(canvas);
		
		if(mLimitY > 0){
			drawFill(canvas);
			drawLine(canvas);
		}
	}
	
	private void drawBackground(Canvas canvas){
		Paint paint = new Paint();
		paint.setTextSize(mTextSize);
		paint.setTextAlign(Paint.Align.CENTER);
		
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
		
		int mode = 1;
		
		if(mLimitX <= 7){
			mode = 1;
		}else if(mLimitX <= 12){
			mode = 3;
		}else if(mLimitX <= 31){
			mode = 5;
		}
		
		for(int i=0; i<mLimitX; i++){
			float x = mStartX+mSpaceX*i;
			
			if(i == 0 || (i+1)%mode == 0){
				String txt = mListX.get(i);
				
				if(mPeriod.equals(BY_DATE)){
					txt += "일";
				}else if(mPeriod.equals(BY_MONTH)){
					txt += "월";
				}
				
				paint.setColor(COLOR_BG_TEXT);
				canvas.drawText(txt, x, mStartY+mTextSize, paint);
				
				paint.setColor(COLOR_BG_THICK);
			}else{
				paint.setColor(COLOR_BG_THIN);
			}
			
			canvas.drawLine(x, mStartY, x, mStartY-mGraphH, paint);
		}
		
		if(mLimitY == 0){
			paint.setColor(COLOR_BG_THICK);
			canvas.drawLine(mStartX, mStartY, mStartX+mGraphW, mStartY, paint);
			return;
		}
		
		// Y축 그리기
		paint.setTextAlign(Paint.Align.RIGHT);
		
		mode = 5;
		
		if(mUnit.equals(FOR_COUNT)){
			mode = (int) (Math.ceil(mLimitY/50.0) * 5);
		}else if(mUnit.equals(FOR_VOLUME)){
			mode = 10;
		}else if(mUnit.equals(FOR_TIME)){
			mode = 2;
		}
		
		if(mLimitY < 5) mode = 1;
		
		for(int i=0; i<mLimitY+1; i++){ // 0부터 시작이기 때문에 +1
			float y = mStartY-mSpaceY*i;
			
			if(i%mode == 0){
				paint.setColor(COLOR_BG_TEXT);
				
				float txtY = (i == 0) ? y : (float)  (y+mTextSize*0.5)-3;
				
				String unit = "개";
				String txt = Integer.toString(i*mGap);
				
				if(mUnit.equals(FOR_TIME)){
					if(mGap == 30){
						unit = "분";
					}else{
						unit = "시간";
						
						float value = (float) ((i*mGap)/60.0);
						txt = NumberUtil.decimalFormat(value, 0);
					}
				}else if(mUnit.equals(FOR_VOLUME)){
					unit = "ml";
				}
				
				canvas.drawText(txt, mStartX-4, txtY, paint);
				
				if(i > 0) canvas.drawText(unit, mStartX-4, txtY+mTextSize, paint);
				
				paint.setColor(COLOR_BG_THICK);
			}else{
				paint.setColor(COLOR_BG_THIN);
			}
			
			canvas.drawLine(mStartX, y, mStartX+mGraphW, y, paint);
		}
	}
	
	private void drawFill(Canvas canvas){
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		
		for(int i=0; i<mTypes.length; i++){
			int clr = (i == 0) ? COLOR_FILL_01 : COLOR_FILL_02;
			paint.setColor(clr);
			
			Path path = new Path();
			
			float x = 0;
			float y = 0;
			
			path.moveTo(mStartX, mStartY);
			
			int size = mFirstRecords.size();
			
			if(i == 0){
				for(int j=0; j<size; j++){
					x = mStartX+mSpaceX*j;
					y = mStartY-mSpaceY*(mFirstRecords.get(j)/(float)mGap);
					path.lineTo(x, y);
				}
				
				path.lineTo(x, mStartY);
			}else{
				for(int j=0; j<size*2; j++){
					if(j < size){
						x = mStartX+mSpaceX*j;
						y = mStartY-mSpaceY*(mTotalRecords.get(j)/(float)mGap);
					}else{
						int idx = size-1-(j-size);
						x = mStartX+mSpaceX*idx;
						y = mStartY-mSpaceY*(mFirstRecords.get(idx)/(float)mGap);
					}
					
					path.lineTo(x, y);
				}
			}
			
			canvas.drawPath(path, paint);
		}
	}
	
	private void drawLine(Canvas canvas){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1.5f);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(COLOR_LINE);
		
		Path path = new Path();
		
		ArrayList<Integer> ary = (mTotalRecords.size() == 0) ? mFirstRecords : mTotalRecords;
		for(int i=0; i<ary.size(); i++){
			float x = mStartX+mSpaceX*i;
			float y = mStartY-mSpaceY*(ary.get(i)/(float)mGap);
			
			if(i == 0){
				path.moveTo(x, y);
			}else{
				path.lineTo(x, y);
			}
		}
		
		canvas.drawPath(path, paint);
	}
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		int	 wMode = MeasureSpec.getMode(widthMeasureSpec);
		int wSize = MeasureSpec.getSize(widthMeasureSpec);
		int hMode = MeasureSpec.getMode(heightMeasureSpec);
		int hSize = MeasureSpec.getSize(heightMeasureSpec);
		
		switch(wMode)
		{
			case MeasureSpec.AT_MOST:
				mWidth = Math.min(wSize, mWidth);
				break;
			case MeasureSpec.EXACTLY:
				mWidth = wSize;
				break;
			case MeasureSpec.UNSPECIFIED:
				break;
		}
		
		switch(hMode){
			case MeasureSpec.AT_MOST:
				mHeight = Math.min(hSize, mHeight);
				break;
			case MeasureSpec.EXACTLY:
				mHeight = hSize;
				break;
			case MeasureSpec.UNSPECIFIED:
				break;
		}
		
		setMeasuredDimension(mWidth, mHeight);
	}

}
