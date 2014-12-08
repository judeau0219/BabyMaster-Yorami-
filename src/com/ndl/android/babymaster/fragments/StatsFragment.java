package com.ndl.android.babymaster.fragments;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ndl.android.babymaster.DrawerActivity;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.database.Constants.RecordConst;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.util.DateUtil;
import com.ndl.android.babymaster.view.GraphView;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StatsFragment extends Fragment implements OnClickListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_stats, container, false);
		return view;
	}
	
	private BabyDb mDb;
	private DrawerActivity mActivity;
	private Cursor mCursor;
	private int mBabyId;
	
	private LinearLayout mChart;
	private GraphView mGraph;
	private Button mBtnType;
	private Button mBtnPeriod;
	private TextView mTxtTitle;
	
	private static final String[] mTypeNames = {"기저귀", "모유", "분유", "우유", "수면시간"};
	private static final String[] mPeriodNames = {"최근 7일", "최근 30일", "최근 6개월", "최근 1년"};
	private static final String[] mTotalNames = {"총 사용량", "총 수유시간", "총 분유용량", "총 우유용량", "총 수면시간"};
	
	private int[] mPeriods = {7, 30, 6, 12};
	
	private String[][] mTypeArrays = {{RecordConst.DIAPER_SMALL, RecordConst.DIAPER_BIG},
											{RecordConst.BREASTFEEDING_LEFT, RecordConst.BREASTFEEDING_RIGHT},
											{RecordConst.DRY_MILK},
											{RecordConst.MILK},
											{RecordConst.SLEEP}};
	
	private int mTypeIndex = 0;
	private int mPeriodIndex = 0;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mDb = BabyDb.getInstance(getActivity());
		mActivity = (DrawerActivity) getActivity();
		
		mActivity.selectCursor();
		mCursor = mActivity.mCursor;
		
		mBabyId = mCursor.getInt(mCursor.getColumnIndex(BabyData._ID));
		
		mChart = (LinearLayout) getView().findViewById(R.id.linear_chart);
		mGraph = (GraphView) getView().findViewById(R.id.view_graph);
		mTxtTitle = (TextView) getView().findViewById(R.id.txt_graph_title);
		
		mBtnType = (Button) getView().findViewById(R.id.btn_type);
		mBtnType.setOnClickListener(this);
		
		mBtnPeriod = (Button) getView().findViewById(R.id.btn_period);
		mBtnPeriod.setOnClickListener(this);
		
		mBtnType.setText(mTypeNames[mTypeIndex]);
		mBtnPeriod.setText(mPeriodNames[mPeriodIndex]);
		
		setGraph();
	}
	
	private void setGraph(){
		GregorianCalendar calendar = new GregorianCalendar();
		
		DateModel startDate = new DateModel();
		DateModel endDate = new DateModel();
		endDate.setDateModel(calendar);
		
		String period = GraphView.BY_DATE;
		
		for(int i=0; i<mPeriods[mPeriodIndex]; i++){
			if(mPeriodIndex > 1){
				period = GraphView.BY_MONTH;
				calendar.add(Calendar.MONTH, -1);
			}else{
				period = GraphView.BY_DATE;
				calendar.add(Calendar.DATE, -1);
			}	
		}
		
		startDate.setDateModel(calendar);
		
		mGraph.setGraph(period, mBabyId, startDate, endDate, mTypeArrays[mTypeIndex]);
		setChart(startDate, endDate);
		setLegend(mTypeArrays[mTypeIndex]);
		
		mTxtTitle.setText(mPeriodNames[mPeriodIndex] + " " + mTypeNames[mTypeIndex] + " 통계");
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		switch(id){
			case R.id.btn_type:
				showTypeDialog();
				break;
			case R.id.btn_period:
				showPeriodDialog();
				break;
		}
	}
	
	private void showTypeDialog(){
		AlertDialog alert = new AlertDialog.Builder(getActivity())
								.setItems(mTypeNames, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mBtnType.setText(mTypeNames[which]);
										mTypeIndex = which;
										setGraph();
									}
								})
								.setCancelable(false)
								.create();
		
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}

	private void showPeriodDialog(){
		AlertDialog alert = new AlertDialog.Builder(getActivity())
								.setItems(mPeriodNames, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mBtnPeriod.setText(mPeriodNames[which]);
										mPeriodIndex = which;
										setGraph();
									}
								})
								.setCancelable(false)
								.create();
		
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
	
	private void setChart(DateModel startDate, DateModel endDate){
		Cursor cursor;
		int count, total = 0;
		
		if(mPeriodIndex > 1){ // 개월 단위 차트인 경우
			startDate.date = 1;
			endDate.date = endDate.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		
		ImageView iv1 = (ImageView) getView().findViewById(R.id.icon_type_01);
		ImageView iv2 = (ImageView) getView().findViewById(R.id.icon_type_02);
		
		TextView tv = (TextView) getView().findViewById(R.id.txt_total);
		TextView tv1 = (TextView) getView().findViewById(R.id.txt_type_01);
		TextView tv2 = (TextView) getView().findViewById(R.id.txt_type_02);
		
		if(mTypeIndex < 2){
			tv.setVisibility(View.VISIBLE);
			iv1.setVisibility(View.VISIBLE);
			iv2.setVisibility(View.VISIBLE);
			tv1.setVisibility(View.VISIBLE);
			tv2.setVisibility(View.VISIBLE);
		}else{
			tv.setVisibility(View.GONE);
			iv1.setVisibility(View.GONE);
			iv2.setVisibility(View.GONE);
			tv1.setVisibility(View.VISIBLE);
			tv2.setVisibility(View.GONE);
		}
		
		if(mTypeIndex == 0){ // 기저귀
			cursor = mDb.selectRecords(mBabyId, startDate, endDate, RecordConst.DIAPER_SMALL);
			count = cursor.getCount();
			total += count;
			iv1.setImageResource(R.drawable.ic_diaper_small);
			tv1.setText(count + "회");
			
			cursor = mDb.selectRecords(mBabyId, startDate, endDate, RecordConst.DIAPER_BIG);
			count = cursor.getCount();
			total += count;
			iv2.setImageResource(R.drawable.ic_diaper_big);
			tv2.setText(count + "회");
			
			tv.setText(mTotalNames[mTypeIndex] + ": " + total + "회");
		}
		else if(mTypeIndex == 1){ // 모유
			cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.BREASTFEEDING_LEFT);
			count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
			total += count;
			iv1.setImageResource(R.drawable.ic_breastfeeding);
			tv1.setText("모유(좌)\n" + DateUtil.getMinuteToEngHour(count));
			
			cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.BREASTFEEDING_RIGHT);
			count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
			total += count;
			iv2.setImageResource(R.drawable.ic_breastfeeding);
			tv2.setText("모유(우)\n" + DateUtil.getMinuteToEngHour(count));
			
			tv.setText(mTotalNames[mTypeIndex] + ": " + DateUtil.getMinuteToEngHour(total));
		}
		else if(mTypeIndex == 2){ // 분유
			cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.DRY_MILK);
			count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
			tv1.setText(mTotalNames[mTypeIndex] + ": " + count + "ml");
		}
		else if(mTypeIndex == 3){ // 우유
			cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.MILK);
			count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
			tv1.setText(mTotalNames[mTypeIndex] + ": " + count + "ml");
		}
		else if(mTypeIndex == 4){ // 수면시간
			cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.SLEEP);
			count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
			tv1.setText(mTotalNames[mTypeIndex] + ": " +DateUtil.getMinuteToEngHour(count));
		}
	}
	/*
	private void setChart(DateModel startDate, DateModel endDate){
		Cursor cursor;
		int count = 0;
		
		if(mPeriodIndex > 1){ // 개월 단위 차트인 경우
			startDate.date = 1;
			endDate.date = endDate.getCalendar().getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		
		cursor = mDb.selectRecords(mBabyId, startDate, endDate, RecordConst.DIAPER_SMALL);
		count = cursor.getCount();
		((TextView) getView().findViewById(R.id.txt_diaper_small)).setText(count + "회");
		
		cursor = mDb.selectRecords(mBabyId, startDate, endDate, RecordConst.DIAPER_BIG);
		count = cursor.getCount();
		((TextView) getView().findViewById(R.id.txt_diaper_big)).setText(count + "회");
		
		cursor = mDb.selectRecords(mBabyId, startDate, endDate, RecordConst.BABY_FOOD);
		count = cursor.getCount();
		((TextView) getView().findViewById(R.id.txt_babyfood)).setText(count + "회");
		
		String[] ary = {RecordConst.BREASTFEEDING_LEFT, RecordConst.BREASTFEEDING_RIGHT};
		cursor = mDb.sumRecords(mBabyId, startDate, endDate, ary);
		count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
		((TextView) getView().findViewById(R.id.txt_breastfeeding)).setText(DateUtil.getMinuteToEngHour(count));
		
		cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.DRY_MILK);
		count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
		((TextView) getView().findViewById(R.id.txt_drymilk)).setText(count + "ml");
		
		cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.MILK);
		count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
		((TextView) getView().findViewById(R.id.txt_milk)).setText(count + "ml");
		
		cursor = mDb.sumRecords(mBabyId, startDate, endDate, RecordConst.SLEEP);
		count = (cursor.moveToFirst()) ? cursor.getInt(0) : 0;
		((TextView) getView().findViewById(R.id.txt_sleep)).setText(DateUtil.getMinuteToEngHour(count));
	}
	*/
	private void setLegend(String[] types){
		for(int i=0; i<types.length; i++){
			int index = Arrays.asList(RecordConst.ARRAY_TYPE).indexOf(types[i]);
			String nameType = RecordConst.ARRAY_TYPE_NAME[index];
			
			int textId = (i == 0) ? R.id.txt_legend01 : R.id.txt_legend02;
			((TextView) getView().findViewById(textId)).setText(nameType);
		}
		
		TextView tv = (TextView) getView().findViewById(R.id.txt_legend02);
		ImageView iv = (ImageView) getView().findViewById(R.id.icon_02);
		
		if(types.length < 2){
			tv.setVisibility(View.GONE);
			iv.setVisibility(View.GONE);
		}else{
			tv.setVisibility(View.VISIBLE);
			iv.setVisibility(View.VISIBLE);
		}
	}
	
}
