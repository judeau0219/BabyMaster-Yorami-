package com.ndl.android.babymaster.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.adapter.CalendarAdapter;
import com.ndl.android.babymaster.datamodel.DateModel;
import com.ndl.android.babymaster.interfaces.OnDateSelectedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

public class CalendarView extends LinearLayout {

	public CalendarView(Context context) {
		super(context);
		initialize(context, null);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private Context mContext;
	
	private GridView mCalendarGrid;
	private CalendarAdapter mCalendarAdapter;
	
	private ArrayList<DateModel> mDateList = new ArrayList<DateModel>();
	
	private void initialize(Context context, AttributeSet attrs){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_calendar, this, true);
		
		mCalendarGrid = (GridView) findViewById(R.id.grid_calendar);
		
		mCalendarAdapter = new CalendarAdapter(context);
		
		mCalendarGrid.setAdapter(mCalendarAdapter);
		mCalendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
				DateModel model = mCalendarAdapter.getDate(position);
				mDateSelectedListener.onDateSelected(model);
			}
		});
	}
	
	private DateModel mDateModel;
	
	public DateModel getDateModel(){
		return mDateModel;
	}
	
	public void setDateModel(DateModel model){
		if(mDateModel != null) if(mDateModel.compareTo(model) == 1) 
			return;
		
		mDateModel = model;
		
		Calendar calendar = new GregorianCalendar(model.year, model.month, 1);
		int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		
		if(day == 0){
			calendar.add(Calendar.DATE, -7);
		}else{
			calendar.add(Calendar.DATE, -day);
		}
		
		mDateList.clear();
		
		int dateOfMonth = -1;
		
		for(int i=0; i<42; i++){
			DateModel dateModel = new DateModel();
			dateModel.year = calendar.get(Calendar.YEAR);
			dateModel.month = calendar.get(Calendar.MONTH);
			dateModel.date = calendar.get(Calendar.DATE);
			dateModel.day = calendar.get(Calendar.DAY_OF_WEEK);
			dateModel.weekIndex = (int) (i/7);
			
			if(dateModel.date == 1) dateOfMonth++;
			dateModel.dateOfMonth = dateOfMonth;
			
			mDateList.add(dateModel);
			
			if(mDateModel.compareTo(dateModel) == 1){
				mDateModel = dateModel;
			}
			
			calendar.add(Calendar.DATE, 1);
		}
		
		mCalendarAdapter.setDateList(mDateList, model);
	}
	
	private OnDateSelectedListener mDateSelectedListener;
	
	public void setOnDateSelectedListener(OnDateSelectedListener listener){
		mDateSelectedListener = listener;
	}
	
	public void removeOnDateSelectedListener(){
		mDateSelectedListener = null;
	}
	
}
