package com.ndl.android.babymaster.database;

import com.ndl.android.babymaster.R;

import android.provider.BaseColumns;

public class Constants implements BaseColumns {

	public Constants() {
		
	}
	
	public static boolean initialize = false;
	
	public static final class SpeechRecognizerConst{
		public static final String apiKey = "b882800ec9ca1ba0e54f317f4ac273a7";
	}
	
	public static final class BabyConst{
		public static final String MALE = "male";
		public static final String FEMALE = "female";
	}
	
	public static final class RecordConst{
		public static final String DIAPER_SMALL = "diaper_small"; 
		public static final String DIAPER_BIG = "diaper_big"; 
		public static final String BREASTFEEDING_LEFT = "breastfeeding_left"; 
		public static final String BREASTFEEDING_RIGHT = "breastfeeding_right";
		public static final String DRY_MILK = "dryMilk"; 
		public static final String MILK = "milk"; 
		public static final String BABY_FOOD = "babyFood"; 
		public static final String SLEEP = "sleep"; 
		
		public static final String NAME_DIAPER_SMALL = "기저귀(소변)"; 
		public static final String NAME_DIAPER_BIG = "기저귀(대변)"; 
		public static final String NAME_BREASTFEEDING_LEFT = "모유(좌)"; 
		public static final String NAME_BREASTFEEDING_RIGHT = "모유(우)"; 
		public static final String NAME_DRY_MILK = "분유"; 
		public static final String NAME_MILK = "우유"; 
		public static final String NAME_BABY_FOOD = "이유식"; 
		public static final String NAME_SLEEP = "수면"; 
		
		public static final String[] ARRAY_TYPE = {DIAPER_SMALL, DIAPER_BIG, BREASTFEEDING_LEFT, BREASTFEEDING_RIGHT, DRY_MILK, MILK, BABY_FOOD, SLEEP};
		
		public static final String[] ARRAY_TYPE_NAME = {NAME_DIAPER_SMALL, NAME_DIAPER_BIG, NAME_BREASTFEEDING_LEFT, NAME_BREASTFEEDING_RIGHT, 
			NAME_DRY_MILK,NAME_MILK, NAME_BABY_FOOD, NAME_SLEEP};
		
		public static final int[] ARRAY_RESOURCE = {R.drawable.ic_diaper_small_l, R.drawable.ic_diaper_big_l, R.drawable.ic_breastfeeding_l, R.drawable.ic_breastfeeding_l, 
			R.drawable.ic_drymilk_l, R.drawable.ic_milk_l, R.drawable.ic_babyfood_l, R.drawable.ic_sleep_l};
		
		
		public static final String[] FILTERS_DIAPERS_SMALL = {"소변", "오줌", "소변의", "소변에", "소변은", "소변을", "소변 의", "소변 에", "소변 은", "소변 을", "소견", "소개", "소근", "소금", "소연", "보증", "오주", "꾸준", "꾸중", "호주", "오전", "오중", "오증", "5중", "여중", "5준"};
		public static final String[] FILTERS_DIAPERS_BIG = {"대변", "대면", "ddong", "똥", "태변", "태연", "대전", "대련", "배변", "해변", "택연", "퇴근", "동", "농", "우동", "야동", "웅", "응", "똥을", "똥이", "음", "뿡", "눙이", "쫑"};
		public static final String[] FILTERS_BREASTFEEDING = {"모유", "모요", "모두", "모여", "모유의", "모유는", "무역", "무료", "무려", "보유", "모의"};
		public static final String[] FILTERS_DRY_MILK = {"분유", "분류", "푸뉴", "분노", "분식", "분량", "분명", "분명히", "분위기", "금융", "아뇨"};
		public static final String[] FILTERS_MILK = {"우유", "우주", "온유", "오류", "유", "u"};
		public static final String[] FILTERS_BABY_FOOD = {"이유식", "유식", "휴식", "주식", "뮤직", "유식", "유색", "news", "예식", "인식", "eu식", "류신", "후식"};
		public static final String[] FILTERS_SLEEP = {"수면", "잠", "주면", "숨은", "주문", "수명", "수능", "소년", "소년의", "소년 의", "수면제", "수년", "참", "처음", "참새", "3"};
	}
	
	public static final class BabyData implements BaseColumns{
		
		public static final String DB_NAME = "BabyMaster.db";
		public static final int DB_VERSION = 1;
		
		public static final String BABYS = "Babys";
		public static final String RECORDS = "Records";
		
		// Babys Columns
		public static final String NAME_KOR = "NAME_KOR";
		public static final String NAME_ENG = "NAME_ENG";
		public static final String PICTURE_PATH = "PICTURE_PATH";
		public static final String BIRTHDAY = "BIRTHDAY";
		public static final String SEX = "SEX";
		public static final String HEIGHT = "HEIGHT";
		public static final String WEIGHT = "WEIGHT";
		
		// Records Columns
		public static final String BABY_ID = "BABY_ID";
		public static final String TYPE = "TYPE";
		public static final String REGISTRATION_DATE = "REGISTRATION_DATE";
		public static final String RECORD_INT = "RECORD_INT";
		public static final String RECORD_STR = "RECORD_STR";
		
	}

}
