package com.ndl.android.babymaster.adapter;

import com.judeau.util.ViewHolder;
import com.ndl.android.babymaster.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarWeekAdapter extends BaseAdapter {

	private Context mContext;
	
	private String[] mWeekNames = { "일", "월", "화", "수", "목", "금", "토" };
	
	public CalendarWeekAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mWeekNames.length;
	}

	@Override
	public Object getItem(int position) {
		return mWeekNames[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.week_item, parent, false);
		}
		
		TextView week = (TextView) ViewHolder.get(convertView, R.id.txt_week);
		week.setText(mWeekNames[position]);
		
		return convertView;
	}
}
