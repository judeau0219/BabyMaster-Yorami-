package com.ndl.android.babymaster.viewgroup;

import java.util.ArrayList;

import com.ndl.android.babymaster.R;
import com.ndl.android.babymaster.adapter.ResultAdapter;
import com.ndl.android.babymaster.datamodel.RecordModel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class RecorderResult extends LinearLayout {

	public RecorderResult(Context context) {
		super(context);
		initialize(context, null);
	}

	public RecorderResult(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public RecorderResult(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}
	
	private Context mContext;
	
	private ArrayList<RecordModel> mResults;
	private ResultAdapter mResultAdapter;
	private ListView mResultList;
	
	private int mMaxListLength = 4;
	
	private void initialize(Context context, AttributeSet attrs){
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.recorder_result, this, true);
		
		mResults = new ArrayList<RecordModel>();
		
		mResultAdapter = new ResultAdapter(context, mResults);
		mResultList = (ListView) findViewById(R.id.list_result);
		mResultList.setAdapter(mResultAdapter);
		
		mResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
				mResultAdapter.setSelectedIndex(position);
				mResultAdapter.notifyDataSetChanged();
			}
		});
	}
	
	public void setResult(ArrayList<RecordModel> results){
		mResults.clear();
		
		int len = Math.min(results.size(), mMaxListLength);
		
		for(int i=0; i<len; i++){
			mResults.add(results.get(i));
		}
		
		mResultAdapter.setSelectedIndex(0);
		mResultAdapter.notifyDataSetChanged();
	}
	
	public RecordModel getSelectedModel(){
		return mResultAdapter.getSelectedModel();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev){
		return true;
	}

}
