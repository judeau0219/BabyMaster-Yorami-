package com.ndl.android.babymaster.datamodel;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Parcel;
import android.os.Parcelable;

public class DateModel implements Parcelable, Comparable<DateModel>{

	public DateModel(){
		
	}
	
	public DateModel(int year, int month, int date){
		this.year = year;
		this.month = month;
		this.date = date;
	}
	
	public static final int PREV_MONTH = 0;
	public static final int CURRENT_MONTH = 1;
	public static final int NEXT_MONTH = 2;
	
	public int year;
	public int month;
	public int date;
	public int day; // 요일(1~7)
	public int weekIndex; // 월의 주차 번호 (0~5)
	public int dateOfMonth = 0; // -1: 이전월의 날짜, 0: 현재월의 날짜, 1: 다음월의 날짜
	
	@Override
	public String toString(){
		return year + "-" + (month+1) + "-" + date;
	}
	
	@Override
	public int compareTo(DateModel another) {
		if(this.toString().equals(another.toString())){
			return 1;
		}
		
		return 0;
	}
	
	public Date getDate(){
		return new GregorianCalendar(year, month, date).getTime();
	}
	
	public GregorianCalendar getCalendar(){
		return new GregorianCalendar(year, month, date);
	}
	
	public void setDateModel(Calendar calendar){
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		date = calendar.get(Calendar.DATE);
	}
	
	
	// Parcelable
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(year);
		dest.writeInt(month);
		dest.writeInt(date);
		dest.writeInt(day);
		dest.writeInt(weekIndex);
		dest.writeInt(dateOfMonth);
	}
	
	public static final Parcelable.Creator<DateModel> CREATOR = new Parcelable.Creator<DateModel>() {
		public DateModel createFromParcel( Parcel source ){
			DateModel model = new DateModel();
			model.year = source.readInt();
			model.month = source.readInt();
			model.date = source.readInt();
			model.day = source.readInt();
			model.weekIndex = source.readInt();
			model.dateOfMonth = source.readInt();
			return model;
		}
		
		public DateModel[] newArray( int size )
		{
			return new DateModel[size];
		}
	};
}
