package com.don.demo.servicetest;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RIntentService extends IntentService {

	public RIntentService() {
//		super("yyyyyy");
		super("yyyyyyyyyyy");  
		// TODO Auto-generated constructor stub
		Log.i("", "RIntentService----RIntentService");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.i("", "RIntentService----onStart");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.i("", "RIntentService----onHandleIntent");
		System.out.println("开始睡眠");  
        try {  
            Thread.sleep(20000);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        System.out.println("睡眠结束");
	}

}
