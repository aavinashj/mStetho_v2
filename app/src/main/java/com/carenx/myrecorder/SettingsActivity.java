package com.carenx.myrecorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**  
* This is an Activity Class that is used to change the user's Preferences.
* @author Avinash joshi
* 
* @version 1.0 Build May 30, 2015. Copyright 2015 CareNx Innovations Pvt. Ltd., Mumbai.
* 
*/
public class SettingsActivity extends Activity{

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		getActionBar().setTitle("Settings");
		
		final SharedPreferences prefs = MyRecorder.getApplication().getSharedPreferences();
		
		CheckBox chk = (CheckBox) findViewById(R.id.chkUncompressed);
		chk.setChecked(prefs.getBoolean("unCompressed", false));
		
		chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					prefs.edit().putBoolean("unCompressed", true).commit();
				}else {
					prefs.edit().putBoolean("unCompressed", false).commit();
				}
			}
		});
	}

	
}
