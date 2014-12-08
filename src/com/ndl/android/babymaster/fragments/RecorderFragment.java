package com.ndl.android.babymaster.fragments;

import java.util.ArrayList;
import java.util.Arrays;

import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;
import net.daum.mf.speech.api.SpeechRecognizerManager;

import com.judeau.util.LogUtil;
import com.judeau.util.StringUtil;
import com.judeau.util.UnitUtil;
import com.ndl.android.babymaster.DrawerActivity;
import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.database.Constants.RecordConst;
import com.ndl.android.babymaster.database.Constants.SpeechRecognizerConst;
import com.ndl.android.babymaster.datamodel.RecordModel;
import com.ndl.android.babymaster.interfaces.OnRecorderViewListener;
import com.ndl.android.babymaster.view.RecorderView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class RecorderFragment extends Fragment implements SpeechRecognizeListener, OnTouchListener, OnRecorderViewListener {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_recorder, container, false);
		return view;
	}
	
	private DrawerActivity mActivity;
	
	private SpeechRecognizerClient mClient;
	
	private RecorderView mSelectRecorder;
	
	private ArrayList<RecorderView> mRecorders;
	
	private int[][] mPosition;
	
	private int mRecorderH;
	
	private int mStartY;
	private int mSpaceY;
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (DrawerActivity) getActivity();
		
		SpeechRecognizerManager.getInstance().initializeLibrary(getActivity());
		
		RecorderView recorderDiaper = (RecorderView) getView().findViewById(R.id.recorder_diaper);
		RecorderView recorderMilk = (RecorderView) getView().findViewById(R.id.recorder_food);
		RecorderView recorderSleep = (RecorderView) getView().findViewById(R.id.recorder_sleep);
		
		int topH = (int) getResources().getDimensionPixelSize(R.dimen.main_top_height);
		int statusH = (int) getResources().getDimensionPixelSize(R.dimen.status_bar_height);
		int fragmentH = UnitUtil.getScreenHeight(getActivity()) - topH - statusH;
		
		mRecorderH = (int) getResources().getDimensionPixelSize(R.dimen.recorder_radius);
		mSpaceY = (int) getResources().getDimensionPixelSize(R.dimen.recorder_space);
		mStartY = (int)((fragmentH - (mRecorderH * 3) - (mSpaceY * 2))/2);
		
		// 커졌을때 scale 1.4 이기 때문에 0.4만큼 커진크기의 반인 0.2크기만큼을 최소로 지정하여 커졌을때 상하단이 잘리지 않도록 같은 여백을 둔다.
		if(mStartY < mRecorderH*0.2){
			mStartY = (int) (mRecorderH*0.2);
			mSpaceY = (int) ((fragmentH - (mRecorderH * 3) - (mStartY * 2))/2);
		}
		
		mRecorders = new ArrayList<RecorderView>();
		mRecorders.add(recorderDiaper);
		mRecorders.add(recorderMilk);
		mRecorders.add(recorderSleep);
		
		int size = mRecorders.size();
		mPosition = new int[size][size];
		
		for(int i=0; i<size; i++){
			mRecorders.get(i).setOnTouchListener(this);
			mRecorders.get(i).setOnRecorderViewListener(this);
			for(int j=0; j<size; j++){
				mPosition[i][j] = (int)(mStartY + (mRecorderH + mSpaceY) * j);
				if(i != 1 && j == 1){
					if(i < j){
						mPosition[i][j] += mSpaceY;
					}else{
						mPosition[i][j] -= mSpaceY;
					}
				}
			}
		}
	}
	
	private Boolean isBeginningOfSpeech = false;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int id = v.getId();
		int action = event.getAction();
		
		if(action == MotionEvent.ACTION_DOWN){
			recorderReady(id);
		}
		else if(action == MotionEvent.ACTION_UP){
			stopRecording();
		}
		
		return true;
	}
	
	private void recorderReady(int viewId){
		for(int i=0; i<mRecorders.size(); i++){
			int duration = 200;
			Interpolator interpolator = new DecelerateInterpolator();
			int ty = mPosition[viewId-R.id.recorder_diaper][i];
			
			boolean zoomIn = true;
			float ts = 0.7f;
			float ta = 0.3f;
			
			if(mRecorders.get(i).getId() == viewId){ // 선택된 RecorderView의 mFrameRecorder 를 축소시킨다 
				zoomIn = false;
				ts = 1.4f;
				ta = 1.0f;
			}
		
			mRecorders.get(i).setScaleAnimation(zoomIn);
			mRecorders.get(i).animate().setDuration(duration).setInterpolator(interpolator).scaleX(ts).scaleY(ts).y(ty).alpha(ta);
		}
		
		startRecording(viewId);
	}
	
	private void recorderRestore(){
		for(int i=0; i<mRecorders.size(); i++){
			int ty = mStartY + (mRecorderH + mSpaceY) * i;
			
			mRecorders.get(i).setScaleAnimation(true);
			mRecorders.get(i).animate().setDuration(200).setInterpolator(new DecelerateInterpolator()).scaleX(1).scaleY(1).y(ty).alpha(1);
		}
		
		mSelectRecorder.initArc();
	}
	
	private int mType = 0;
	
	private void startRecording(int viewId){
		SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder()
				.setApiKey(SpeechRecognizerConst.apiKey)
				.setServiceType(SpeechRecognizerClient.SERVICE_TYPE_DICTATION);
		
		mClient = builder.build();
		mClient.setSpeechRecognizeListener(this);
		mClient.startRecording(true);
		
		if(viewId == R.id.recorder_diaper){
			mType = 0;
		}
		else if(viewId == R.id.recorder_food){
			mType = 1;
		}
		else if(viewId == R.id.recorder_sleep){
			mType = 2;
		}
		
		mSelectRecorder = (RecorderView) getView().findViewById(viewId);
		mSelectRecorder.drawArc();
	}
	
	private void stopRecording(){
		recorderRestore();
		
		if(mClient != null){
			if(isBeginningOfSpeech){
				mClient.stopRecording();
			}else{
				mClient.cancelRecording();
			}
		}
		
		isBeginningOfSpeech = false;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		
		if(mClient != null) mClient.stopRecording();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		SpeechRecognizerManager.getInstance().finalizeLibrary();
	}
	
	@Override
	public void onTimeOver() {
		stopRecording();
	}

	// startRecording 수행 후 초기화가 완료되었을 때 호출됩니다.
	@Override
	public void onReady() {
		LogUtil.trace("onReady");
	}
	
	// 말하기 시작할 때
	@Override
	public void onBeginningOfSpeech() {
		isBeginningOfSpeech = true;
		LogUtil.trace("onBeginningOfSpeech");
	}
	
	// 말하기 끝났을 때
	@Override
	public void onEndOfSpeech() {
		isBeginningOfSpeech = false;
		LogUtil.trace("onEndOfSpeech");
	}
	
	@Override
	public void onResults(Bundle results) {
		ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
//		ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);
		
		ArrayList<RecordModel> ary = new ArrayList<RecordModel>();
		
		for(int i=0; i<texts.size(); i++){
			String result = texts.get(i);
			RecordModel model = getRecordModel(result);
			
			if(model != null){
				ary.add(model);
				
				if(model.type == RecordConst.BREASTFEEDING_LEFT){
					RecordModel model2 = new RecordModel();
					model2.type = RecordConst.BREASTFEEDING_RIGHT;
					model2.recordInt = model.recordInt;
					ary.add(model2);
				}
			}
		}
		
		setMakeListLightly(ary);
//		ary = new ArrayList<RecordModel>(new HashSet<RecordModel>(ary));
		
		if(ary.size() > 0){
			mActivity.showResult(ary);
		}else{
			mActivity.recordError(getResources().getString(R.string.toast_recorder_error));
		}
	}
	
	// 중복되는 데이터 삭제
	private void setMakeListLightly(ArrayList<RecordModel> ary){
		for(int i=0; i<ary.size(); i++){
			RecordModel model = ary.get(i);
			for(int j=0; j<i; j++){
				if(model.type.equals(ary.get(j).type)){
					if(model.recordStr != null){
						if(model.recordStr.equals(ary.get(j).recordStr)){
							ary.remove(j);
							i--;
						}
					}else if(model.recordInt == ary.get(j).recordInt){
						ary.remove(j);
						i--;
					}
					
				}
			}
		}
	}
	
	private RecordModel getRecordModel(String result){
		RecordModel model;
		int i, j, index;
		String type, record;
		
		if(mType == 0){ // 기저귀
			
			String[][] filters = { RecordConst.FILTERS_DIAPERS_SMALL, RecordConst.FILTERS_DIAPERS_BIG };
			
			for(i=0; i<filters.length; i++){
				for(j=0; j<filters[i].length; j++){
					index = result.indexOf(filters[i][j]);
					if(index == 0){
						model = new RecordModel();
						model.type = (i == 0) ? RecordConst.DIAPER_SMALL : RecordConst.DIAPER_BIG;
						if(!result.substring(filters[i][j].length()).trim().equals("")){
							model.recordStr = result.substring(filters[i][j].length()).trim();
						}
						
						return model;
					}
				}
			}
		}
		else if(mType == 1){ // 모유, 분유, 우유, 이유식
			String[][] filters = { RecordConst.FILTERS_BREASTFEEDING, RecordConst.FILTERS_DRY_MILK, RecordConst.FILTERS_MILK, RecordConst.FILTERS_BABY_FOOD };
			
			for(i=0; i<filters.length; i++){ // 모유
				for(j=0; j<filters[i].length; j++){
					index = result.indexOf(filters[i][j]);
					
					if(index == 0){
						model = new RecordModel();
						record = result.substring(filters[i][j].length()).trim();
						
						if(i == 0){ // 모유
							model.type = RecordConst.BREASTFEEDING_LEFT;
							int min = getElapsedRecord(record);
							if(min > 0){ // (min != -1){
								model.recordInt = min;
							}else{
								return null;
							}
						}else if(i == 1 || i == 2){ // 분유, 우유
							model.type = (i == 1) ? RecordConst.DRY_MILK : RecordConst.MILK;
							int value = parseInt(record);
							if(value > 0){
								model.recordInt = value;
							}else{
								value = (int) StringUtil.parseKoreanToInt(record);
								
								if(value > 0){
									model.recordInt = value;
								}else{
									return null;
								}
							}
						}else if(i == 3){ // 이유식
							model.type = RecordConst.BABY_FOOD;
							
							if(!record.equals("")){
								model.recordStr = result.substring(filters[i][j].length()).trim();
							}
						}
						
						return model;
					}
				}
			}
		}else if(mType == 2){ // 수면시간
			// 분류명을 같이 말한 경우
			String[] filters = RecordConst.FILTERS_SLEEP;
			for(i=0; i<filters.length; i++){
				index = result.indexOf(filters[i]);
				
				if(index == 0){
					model = new RecordModel();
					model.type = RecordConst.SLEEP;
					
					record = result.substring(filters[i].length()).trim();
					
					int min = getElapsedRecord(record);
					if(min > 0){ // (min != -1){
						model.recordInt = min;
					}else{
						return null;
					}
					
					return model;
				}
			}
			
			// 시간만 말한경우
			int min = getElapsedRecord(result);
			if(min > 0){ // (min != -1){
				model = new RecordModel();
				model.type = RecordConst.SLEEP;
				model.recordInt = min;
			}else{
				return null;
			}
			return model;
		}
		
		return null;
	}
	
	private int getElapsedRecord(String str){
		String record = StringUtil.trimAll(str);
		
		int minute = 0;
		int value;
		
		int indexH = record.indexOf("시간");
		
		if(indexH != -1){
			String hourStr = record.substring(0, indexH);
			value = parseInt(hourStr);
			
			if(value > 0){
				minute = value * 60;
			}else{
				value = parseKoreanToHour(hourStr);
				
				if(value > 0){
					minute = value * 60;
				}else{
					return -1;
				}
			}
		}
		
		int indexM = record.indexOf("분");
		
		if(indexM != -1){
			int startIndex = (indexH == -1) ? 0 : indexH+2;
			
			String minStr = record.substring(startIndex, indexM);
			value = parseInt(minStr);
			
			if(value > 0){ // int 로 캐스팅 가능한 경우
				minute += value;
			}else{ // 불가능한 경우 한글 숫자 여부 체크
				value = (int) StringUtil.parseKoreanToInt(minStr);
				
				if(value > 0){
					minute += value;
				}else{
					return -1;
				}
			}
		}else{
			int half = record.indexOf("반");
			
			if(half != -1){
				minute += 30;
			}
		}
		
		return minute;
	}
	
	private int parseInt(String str){
		try{
			return Integer.parseInt(str);
		}catch(Exception e){
			return -1;
		}
	}
	
	private int parseKoreanToHour(String hour){
		String[] aryKor = {"한", "두", "세", "네", "다섯", "여섯", "일곱", "여덟", "아홉", "열", "열세", "열두"};
		int index = Arrays.asList(aryKor).indexOf(hour);
		
		if(index > -1){
			return index+1;
		}
		
		return -1;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onError(int errorCode, String errorMsg) {
		if(errorCode == SpeechRecognizerClient.ERROR_NO_RESULT){
			mActivity.recordError("인식된 결과 값이 없습니다.\n다시 시도해 주세요.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_AUDIO_FAIL){
			mActivity.recordError("음성입력이 불가능 하거나 마이크 접근이 허용되지 않습니다.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_AUTH_FAIL){
			LogUtil.trace("onError : apikey 인증이 실패한 경우.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_AUTH_TROUBLE){
			LogUtil.trace("onError : apikey 인증 과정 중 내부 문제가 발생한 경우.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_NETWORK_FAIL || errorCode == SpeechRecognizerClient.ERROR_NETWORK_TIMEOUT){
			mActivity.recordError("네트워크 연결 상태를 확인 후 다시 시도해 주세요.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_SERVER_FAIL){
			mActivity.recordError("서버에서 오류가 발생했습니다.\n다시 시도해 주세요.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_SERVER_TIMEOUT){
			mActivity.recordError("서버 응답 시간이 초과되었습니다.\n다시 시도해 주세요.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_CLIENT){
			LogUtil.trace("onError : 클라이언트 내부 로직에서 오류가 발생한 경우.");
		}
		else if(errorCode == SpeechRecognizerClient.ERROR_RECOGNITION_TIMEOUT){
			mActivity.recordError("녹음 소요시간이 초과되었습니다.");
		}
	}
	
	// 음성이 입력되는 도중에 입력되는 음성의 크기를 dB(데시벨)로 판별한 후 재가공을 거친 상대값을 알려줍니다. 0과 1 사이의 float 값입니다.
	@Override
	public void onAudioLevel(float v) {
		//LogUtil.trace("onAudioLevel");
	}

	// 음성인식 종료되기 전까지 현재까지 인식된 문자열을 알려줍니다.
	@Override
	public void onPartialResult(String text) {
		//LogUtil.trace("onPartialResult >> " + text);
	}

}
