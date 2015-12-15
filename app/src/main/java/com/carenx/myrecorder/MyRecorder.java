package com.carenx.myrecorder;

import android.app.Application;
import android.content.SharedPreferences;

/**  
* 
* This is an Application Class that can be accessed over the entire project.
* @author Avinash joshi
* 
* @version 1.0 Build May 30, 2015. Copyright 2015 CareNx Innovations Pvt. Ltd., Mumbai.
* 
*/
public class MyRecorder extends Application{
	private static SharedPreferences mPrefs;
	
	private static MyRecorder myApplication;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		myApplication = new MyRecorder();
		mPrefs=getSharedPreferences("Settings", MODE_PRIVATE);
	}
	
	public SharedPreferences getSharedPreferences() 
	{
		if (mPrefs != null) 
		{
			return mPrefs;
		} 
		else 
		{
			return getSharedPreferences("Settings", MODE_PRIVATE);
		}
	}

	public static MyRecorder getApplication()
	{
		if (myApplication == null) 
		{
			myApplication = new MyRecorder();
		}
		return myApplication;
	}

}
