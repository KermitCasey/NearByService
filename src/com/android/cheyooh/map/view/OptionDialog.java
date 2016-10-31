package com.android.cheyooh.map.view;

import java.util.ArrayList;

import com.android.cheyooh.map.R;
import com.android.cheyooh.map.adapter.OptionsDialogAdapter;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;


/** 
 * 类说明：   用法：先调用 {@link #OptionDialog(Context, String, String[])} 进行初始化，然后调用
 * {@link #setOptionsItemClickListener(OnOptionsItemClickListener)}设置监听。
 * @author  xinhui.cheng
 * @date    2013-4-7
 * @version 1.0
 */
public class OptionDialog extends Dialog {

	private Context mContext;
	private String[] mOptions;
	private String mTitle;
	private TextView mTitleTv;
	private ListView mListView;
	
	public OptionDialog(Context context , String title , String[] options) {
		super(context , R.style.custom_dialog_theme);
		mContext = context;
		mTitle = title;
		mOptions = options;
		init();
		loadData();
	}
	
	
	private void init(){
		setContentView(R.layout.options_dialog_layout);
		mTitleTv = (TextView) findViewById(R.id.options_dialog_title_tv);
		mListView = (ListView) findViewById(R.id.options_dialog_list);
	}
	
	private void loadData(){
		if(mOptions == null || mOptions.length == 0){
			return;
		}
		ArrayList<String> optionsList = new ArrayList<String>();
		for (String op : mOptions) {
			optionsList.add(op);
		}
		mTitleTv.setText(mTitle);
		mListView.setAdapter(new OptionsDialogAdapter(mContext, optionsList));
	}
	
	/**
	 * 设置监听
	 * @param listener
	 * @return 
	 */
	public OptionDialog setOptionsItemClickListener(OnItemClickListener listener){
		mListView.setOnItemClickListener(listener);
		return this;
	}
	
	
	@Override
	public boolean dispatchKeyEvent(android.view.KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
    			|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN
    			|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
    			|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
    			|| event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT){
    		return true;
    	}
		
		return super.dispatchKeyEvent(event);
		
	}

}
