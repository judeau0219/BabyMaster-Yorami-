package com.ndl.android.babymaster.adapter;

import java.util.ArrayList;

import com.judeau.util.ViewHolder;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.datamodel.DateModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

	private Context mContext;
	
	private ArrayList<DateModel> mDateList = new ArrayList<DateModel>();
	private DateModel mDateModel;
	
	public CalendarAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public int getCount() {
		return mDateList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public DateModel getDate(int position){
		return mDateList.get(position);
	}
	
	public void setDateList(ArrayList<DateModel> dateList, DateModel dateModel){
		mDateList = dateList;
		mDateModel = dateModel;
		
		this.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.day_item, null);
		}
		
		TextView day = (TextView) ViewHolder.get(convertView, R.id.txt_day);
		day.setText(Integer.toString(mDateList.get(position).date));
		
		DateModel model = mDateList.get(position);
		
		if(model.dateOfMonth != 0){
			day.setTextColor(0x22000000);
		}else{
			if(mDateList.get(position).compareTo(mDateModel) == 1){
				day.setTextColor(0xFF000000);
			}else{
				day.setTextColor(0x55000000);
			}
		}
		
		return convertView;
	}

}
