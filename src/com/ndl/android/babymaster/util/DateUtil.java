package com.ndl.android.babymaster.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.text.format.DateFormat;

import com.judeau.util.NumberUtil;
import com.ndl.android.babymaster.datamodel.DateModel;

public class DateUtil {

	public static String getMinuteToHour(int minute, boolean hasHour){
		int hour = (int) minute/60;
		int min = (int) minute%60;
		
		if(!hasHour && hour == 0){
			return NumberUtil.digit(min, 2) + "분";
		}else{
			return NumberUtil.digit(hour, 2) + "시간 " + NumberUtil.digit(min, 2) + "분";
		}
	}
	
	public static String getMinuteToEngHour(int minute){
		return NumberUtil.digit(minute/60, 2) + "h " + NumberUtil.digit(minute%60, 2) + "m";
	}
	
	public static String getDateToTime(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static DateModel getCurrentDateModel(){
		GregorianCalendar calendar = new GregorianCalendar();
		return new DateModel(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
	}
	
	public static String getDateToDisplayTime(Date date){
		String apm = DateFormat.format("a", date).toString();
		
		if(apm.equals("오전")){
			apm = "AM";
		}else if(apm.equals("오후")){
			apm = "PM";
		}else{
			apm = apm.toUpperCase();
		}
		
		return apm + " " + DateFormat.format("hh:mm", date).toString();
	}
	
	public static Date getStringToDate(String strDate){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		
		try {
			date = dateFormat.parse(strDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	
	public static String getDateToString(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
	
	public static String getDateToDisplayString(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		return dateFormat.format(date);
	}
	
	public static int elapsedDays(String startDay, String endDay){
		String[] ary = startDay.split("-");
		
		Calendar startCalendar = new GregorianCalendar(Integer.parseInt(ary[0]), Integer.parseInt(ary[1])-1, Integer.parseInt(ary[2]));
		Calendar endCalendar;
		
		if(endDay == null){
			endCalendar = new GregorianCalendar();
		}else{
			ary = endDay.split("-");
			endCalendar = new GregorianCalendar(Integer.parseInt(ary[0]), Integer.parseInt(ary[1])-1, Integer.parseInt(ary[2]));
		}
		
		long milli_sec = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
		int day = 0;
		
		if(milli_sec >= 0){
			day = (int)(milli_sec/1000/60/60/24) + 1;
		}
		
		return day;
	}
	
	public static int elapsedMonths(String startDay, String endDay){
		String[] ary = startDay.split("-");
		GregorianCalendar startCalendar = new GregorianCalendar(Integer.parseInt(ary[0]), Integer.parseInt(ary[1])-1, Integer.parseInt(ary[2]));
		
		ary = endDay.split("-");
		GregorianCalendar endCalendar = new GregorianCalendar(Integer.parseInt(ary[0]), Integer.parseInt(ary[1])-1, Integer.parseInt(ary[2]));
		
		long milli_sec = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
		if(milli_sec <= 0) return 0;
		
		int months=0;
		
		while(true){
			months++;
			
			if(startCalendar.get(Calendar.YEAR) == endCalendar.get(Calendar.YEAR) && startCalendar.get(Calendar.MONTH) == endCalendar.get(Calendar.MONTH)){
				break;
			}else{
				startCalendar.add(Calendar.MONTH, 1);
			}
		}
		
		return months;
	}

}
