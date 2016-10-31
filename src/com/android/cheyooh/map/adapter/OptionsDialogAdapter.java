package com.android.cheyooh.map.adapter;

import java.util.List;

import com.android.cheyooh.map.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/** 
 * 类说明：   
 * @author  xinhui.cheng
 * @date    2013-4-7
 * @version 1.0
 */
public class OptionsDialogAdapter extends SimpleBaseAdapter<String> {

	public OptionsDialogAdapter(Context context, List<String> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = getInflater().inflate(R.layout.options_dialog_item_layout, null);
			holder.optionTv = (TextView) convertView.findViewById(R.id.options_dialog_item_option_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.optionTv.setText(mList.get(position));
		return convertView;
	}
	
	private static class ViewHolder{
		TextView optionTv;
	}

}
