package com.ndl.android.babymaster.adapter;

import java.util.Arrays;

import com.judeau.util.ViewHolder;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.Constants.BabyData;
import com.ndl.android.babymaster.database.Constants.RecordConst;
import com.ndl.android.babymaster.util.DateUtil;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordAdapter extends CursorAdapter {

	public RecordAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	public RecordAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
	}

	// Cursor 가 가리키는 데이터를 대입하는 기능
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView imgIcon = (ImageView) ViewHolder.get(view, R.id.img_icon);
		TextView txtTime = (TextView) ViewHolder.get(view, R.id.txt_time);
		TextView txtType = (TextView) ViewHolder.get(view, R.id.txt_type);
		TextView txtData = (TextView) ViewHolder.get(view, R.id.txt_data);
		
		txtType.setText("");
		txtData.setText("");
		
		String date = cursor.getString(cursor.getColumnIndex(BabyData.REGISTRATION_DATE));
		txtTime.setText(DateUtil.getDateToDisplayTime(DateUtil.getStringToDate(date)));
		
		String type = cursor.getString(cursor.getColumnIndex(BabyData.TYPE));
		int recordInt = cursor.getInt(cursor.getColumnIndex(BabyData.RECORD_INT));
		String recordStr = cursor.getString(cursor.getColumnIndex(BabyData.RECORD_STR));
		
		int index = Arrays.asList(RecordConst.ARRAY_TYPE).indexOf(type);
		
		int iconRes = RecordConst.ARRAY_RESOURCE[index];
		String nameType = RecordConst.ARRAY_TYPE_NAME[index];
		
		if(type.equals(RecordConst.BREASTFEEDING_LEFT) || type.equals(RecordConst.BREASTFEEDING_RIGHT) || type.equals(RecordConst.SLEEP)){
			if(recordInt != -1) txtData.setText(DateUtil.getMinuteToHour(recordInt, false));
		}
		else if(type.equals(RecordConst.DRY_MILK) || type.equals(RecordConst.MILK)){
			if(recordInt != -1) txtData.setText(recordInt + "ml");
		}
		else{
			if(recordStr != null) txtData.setText(recordStr);
		}
		
		imgIcon.setBackgroundResource(iconRes);
		txtType.setText(nameType);
	}
	
	// Cursor 가 가리키는 데이터를 표시할 뷰를 생성하여 리턴
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.record_list_item, parent, false);
		return v;
	}

}
