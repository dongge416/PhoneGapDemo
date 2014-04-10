package com.don.demo;

import android.app.Application;

public class RApp extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		App_CrashHandler app_CrashHandler = App_CrashHandler.getInstance();
		app_CrashHandler.init(getApplicationContext());
	}
	
}
