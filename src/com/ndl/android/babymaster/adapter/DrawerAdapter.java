package com.ndl.android.babymaster.adapter;

import com.ndl.android.babymaster.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends BaseAdapter {

	private Context mContext;
	
	private String[] aryName;
	private int[] aryImageResId = {R.drawable.ic_drawer_home, R.drawable.ic_drawer_recorder, R.drawable.ic_drawer_log, 
			R.drawable.ic_drawer_stats, R.drawable.ic_drawer_profile, R.drawable.ic_drawer_guide}; // , R.drawable.ic_drawer_setting};
	
	public DrawerAdapter(Context context) {
		mContext = context;
		
		aryName = context.getResources().getStringArray(R.array.menu);
	}

	@Override
	public int getCount() {
		return aryName.length;
	}

	@Override
	public Object getItem(int position) {
		return aryName[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iconView;
		TextView nameView;
		
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_list_item, parent, false);
			
			iconView = (ImageView) convertView.findViewById(R.id.img_menu_icon);
			nameView = (TextView) convertView.findViewById(R.id.txt_menu_name);
			
			// convertView 와 연관된 태그를 설정한다.
			convertView.setTag(new ViewHolder(iconView, nameView));
		}else{
			// 저장한 태그에서 가져온 ViewHolder 오브젝트에서 View 를 꺼내어 쓴다.
			// 다시 전개해서 생성할 필요가 없다.
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			iconView = viewHolder.iconView;
			nameView = viewHolder.nameView;
		}
		
		iconView.setImageResource(aryImageResId[position]);
		
		nameView.setText(aryName[position]);
		
		return convertView;
	}
	
	private static class ViewHolder{
		public final ImageView iconView;
		public final TextView nameView;
		
		public ViewHolder(ImageView icon, TextView name){
			this.iconView = icon;
			this.nameView = name;
		}
	}
}


