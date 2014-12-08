package com.ndl.android.babymaster.fragments;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.judeau.util.LogUtil;
import com.judeau.util.UnitUtil;
import com.ndl.android.babymaster.DrawerActivity;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.adapter.RecordAdapter;
import com.ndl.android.babymaster.database.BabyDb;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.database.Constants.RecordConst;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.interfaces.OnDateSelectedListener;
import com.ndl.android.babymaster.interfaces.OnSlideCalendarListener;
import com.ndl.android.babymaster.util.DateUtil;
import com.ndl.android.babymaster.viewgroup.SlideCalendar;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class LogFragment extends Fragment implements OnSlideCalendarListener, OnItemClickListener, OnItemLongClickListener, OnDateSelectedListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_log, container, false);
		return view;
	}
	
	private BabyDb mDb;
	private DrawerActivity mActivity;
	private Cursor mCursor;
	
	private SlideCalendar mCalendar;
	private LinearLayout mLog;
	private LinearLayout mChart;
	private TextView mEmpty;
	
	private ListView mList;
	private RecordAdapter mAdapter;
	private Cursor mRecordCursor;
	
	private int mFragmentW;
	private int mFragmentH;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mDb = BabyDb.getInstance(getActivity());
		mActivity = (DrawerActivity) getActivity();
		
		mActivity.selectCursor();
		mCursor = mActivity.mCursor;
		
		int topH = (int) getResources().getDimensionPixelSize(R.dimen.main_top_height);
		int statusH = (int) getResources().getDimensionPixelSize(R.dimen.status_bar_height);
		
		mFragmentW = UnitUtil.getScreenWidth(getActivity());
		mFragmentH = UnitUtil.getScreenHeight(getActivity()) - topH - statusH;
		
		mCalendar = (SlideCalendar) getView().findViewById(R.id.slide_calendar);
		mCalendar.setOnSlideCalendarListener(this);
		mCalendar.setOnDateSelectedListener(this);
		
		mLog = (LinearLayout) getView().findViewById(R.id.linear_log);
		mChart = (LinearLayout) getView().findViewById(R.id.linear_chart);
		mEmpty = (TextView) getView().findViewById(R.id.txt_empty);
		
		mList = (ListView) getView().findViewById(R.id.list_log);
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);
		
		Bundle args = getArguments();
		
		if(args != null){
			String initDate = args.getString("initDate");
			
			LogUtil.trace(initDate);
			
			if(initDate != null){
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(DateUtil.getStringToDate(initDate));
				DateModel dateModel = new DateModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
				mCalendar.setDateModel(dateModel);
				return;
			}
		}
		
		mCalendar.setDateModel(DateUtil.getCurrentDateModel());
	}

	@Override
	public void onSlideUpdate(float calendarH) {
		mLog.setTranslationY(calendarH);
		
		int top = (int) mChart.getHeight();
		int bottom = (int) (mFragmentH - calendarH);
		
		mList.layout(0, top, mFragmentW, bottom);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		int recordId = mRecordCursor.getInt(mRecordCursor.getColumnIndex(BabyData._ID));
		
		Bundle args = new Bundle();
		args.putInt("recordId", recordId);
		mActivity.switchFragment(8, true, args);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
		deleteAlert(mRecordCursor.getInt(mRecordCursor.getColumnIndex(BabyData._ID)));
		return true;
	}
	
	private void deleteAlert(int id)
	{
		final int deleteId = id;
		
		AlertDialog alert = new AlertDialog.Builder(getActivity())
		.setMessage("선택한 로그를 삭제하시겠습니까?")
		.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(mDb.deleteRecord(deleteId)){
					onDateSelected(mCalendar.getDateModel());
				}
			}
		})
		.setNegativeButton("취소", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setCancelable(false)
		.create();
		 
		alert.setCanceledOnTouchOutside(true);
		alert.show();
	}
	
	private DateModel mDateModel;
	
	public DateModel getDateModel(){
		return mDateModel;
	}
	
	@Override
	public void onDateSelected(DateModel model) {
		mDateModel = model;
		
		mActivity.setDrawerTitle(DateUtil.getDateToDisplayString(model.getDate()));
		mRecordCursor = mDb.selectRecords(mCursor.getInt(mCursor.getColumnIndex(BabyData._ID)), model, null, null);
		
		if(mAdapter == null){
			mAdapter = new RecordAdapter(getActivity(), mRecordCursor, false);
		}
		
		mList.setAdapter(mAdapter);
		mAdapter.changeCursor(mRecordCursor);
		
		if(mRecordCursor.getCount() > 0){
			mEmpty.setVisibility(View.GONE);
			mList.setVisibility(View.VISIBLE);
		}else{
			mList.setVisibility(View.GONE);
			mEmpty.setVisibility(View.VISIBLE);
		}
		
		mCalendar.calendarOpen(mCalendar.isOpen);
		
		int k = 0;
		int diaper_s = 0;
		int diaper_b = 0;
		int baby_food = 0;
		int breastfeeding = 0;
		int dry_milk = 0;
		int milk = 0;
		int sleep = 0;
		
//		LogUtil.trace("전체개수 : " + mRecordCursor.getCount());
		
		while(mRecordCursor.moveToPosition(k)){
			String type = mRecordCursor.getString(mRecordCursor.getColumnIndex(BabyData.TYPE));
			
			if(type.equals(RecordConst.DIAPER_SMALL)){
				diaper_s++;
			}
			else if(type.equals(RecordConst.DIAPER_BIG)){
				diaper_b++;
			}
			else if(type.equals(RecordConst.BABY_FOOD)){
				baby_food++;
			}
			else if(type.equals(RecordConst.BREASTFEEDING_LEFT) || type.equals(RecordConst.BREASTFEEDING_RIGHT)){
				breastfeeding += mRecordCursor.getInt(mRecordCursor.getColumnIndex(BabyData.RECORD_INT));
			}
			else if(type.equals(RecordConst.DRY_MILK)){
				dry_milk += mRecordCursor.getInt(mRecordCursor.getColumnIndex(BabyData.RECORD_INT));
			}
			else if(type.equals(RecordConst.MILK)){
				milk += mRecordCursor.getInt(mRecordCursor.getColumnIndex(BabyData.RECORD_INT));
			}
			else if(type.equals(RecordConst.SLEEP)){
				sleep += mRecordCursor.getInt(mRecordCursor.getColumnIndex(BabyData.RECORD_INT));
			}
			
			k++;
		}
		
		((TextView) getView().findViewById(R.id.txt_diaper_small)).setText(diaper_s + "회");
		((TextView) getView().findViewById(R.id.txt_diaper_big)).setText(diaper_b + "회");
		((TextView) getView().findViewById(R.id.txt_breastfeeding)).setText(DateUtil.getMinuteToEngHour(breastfeeding));
		((TextView) getView().findViewById(R.id.txt_babyfood)).setText(baby_food + "회");
		((TextView) getView().findViewById(R.id.txt_drymilk)).setText(dry_milk + "ml");
		((TextView) getView().findViewById(R.id.txt_milk)).setText(milk + "ml");
		((TextView) getView().findViewById(R.id.txt_sleep)).setText(DateUtil.getMinuteToEngHour(sleep));
	}

}
