package com.don.demo;

import com.don.demo.servicetest.RIntentService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void test(View view){
		
		startService(new Intent(this,RIntentService.class));  
	}

}
