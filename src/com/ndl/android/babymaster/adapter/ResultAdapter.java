package com.ndl.android.babymaster.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import com.judeau.util.ViewHolder;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.Constants.RecordConst;
import com.ndl.android.babymaster.datamodel.RecordModel;
import com.ndl.android.babymaster.util.DateUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

public class ResultAdapter extends BaseAdapter {

	private Context mContext;
	
	private ArrayList<RecordModel> mResults;
	
	public ResultAdapter(Context context, ArrayList<RecordModel> results) {
		mContext = context;
		mResults = results;
	}

	@Override
	public int getCount() {
		return mResults.size();
	}

	@Override
	public Object getItem(int position) {
		return mResults.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private int selectedIndex = -1;
	
	public void setSelectedIndex(int index){
		selectedIndex = index;
	}
	
	public RecordModel getSelectedModel(){
		return mResults.get(selectedIndex);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.result_list_item, parent, false);
		}
		
		RadioButton radio = (RadioButton) ViewHolder.get(convertView, R.id.radio_result);
		
		String type = mResults.get(position).type;
		String record = "";
		
		if(type == RecordConst.BREASTFEEDING_LEFT || type == RecordConst.BREASTFEEDING_RIGHT || type == RecordConst.SLEEP){
			record = DateUtil.getMinuteToHour(mResults.get(position).recordInt, false);
		}
		else if(type == RecordConst.DRY_MILK || type == RecordConst.MILK){
			record = mResults.get(position).recordInt + "ml";
		}
		else{ // mResults.get(position).type == RecordConst.DIAPER_SMALL || mResults.get(position).type == RecordConst.DIAPER_BIG || mResults.get(position).type == RecordConst.BABY_FOOD
			if(mResults.get(position).recordStr != null) record = mResults.get(position).recordStr;
		}
		
		int index = Arrays.asList(RecordConst.ARRAY_TYPE).indexOf(type);
		radio.setText(RecordConst.ARRAY_TYPE_NAME[index] + " " + record);
		
		boolean checked = (selectedIndex == position) ? true : false;
		radio.setChecked(checked);
		
		return convertView;
	}

}
