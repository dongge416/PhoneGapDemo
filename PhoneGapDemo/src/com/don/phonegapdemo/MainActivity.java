package com.don.phonegapdemo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.DroidGap;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity implements CordovaInterface {

	public CordovaWebView cordovaWebView;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	
	 /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_main);

       cordovaWebView = (CordovaWebView) findViewById(R.id.cordovaWebView);

       cordovaWebView.loadUrl("file:///android_asset/www/myindex.html");

   }
   
   
   public Context getContext() {
       return this;
   }

   public void startActivityForResult(CordovaPlugin command, Intent intent,
           int requestCode) {
       // TODO Auto-generated method stub
       
   }

   public void setActivityResultCallback(CordovaPlugin plugin) {
       // TODO Auto-generated method stub
       
   }

   //Note: This must always return an activity!
   public Activity getActivity() {
       return this;
   }

   @Deprecated
   public void cancelLoadUrl() {
       // TODO Auto-generated method stub
       
   }

   public Object onMessage(String id, Object data) {
       // TODO Auto-generated method stub
       return null;
   }

   public ExecutorService getThreadPool() {
       // TODO Auto-generated method stub
       return threadPool;
   }
   
   @Override
   /**
    * The final call you receive before your activity is destroyed.
    */
   public void onDestroy() {
       super.onDestroy();
       if (cordovaWebView != null) {
           // Send destroy event to JavaScript
           cordovaWebView.handleDestroy();
       }
   }
}