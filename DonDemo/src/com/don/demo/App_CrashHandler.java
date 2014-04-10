package com.don.demo;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.don.demo.mail.MailSenderInfo;
import com.don.demo.mail.SimpleMailSender;




public class App_CrashHandler implements UncaughtExceptionHandler{

	
	//保存奔溃日志开关
	private static boolean flag_save=true;
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
	private static App_CrashHandler INSTANCE = new App_CrashHandler();// CrashHandler实例
	private Context mContext;// 程序的Context对象
	private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
	private SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分

	/** 保证只有一个PKE_CrashHandler实例 */
	private App_CrashHandler() {

	}

	/** 获取PKE_CrashHandler实例 ,单例模式 */
	public static App_CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
	}

	/**
	 * 当UncaughtException发生时会转入该重写的方法来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
		handleException(ex);
			// 如果自定义的没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * 异常信息 true 如果处理了该异常信息;否则返回false.
	 */
	public boolean handleException(Throwable ex) {
		
		if (!flag_save) {
			return false;
		}
		
		if (ex == null)
			return false;
		new Thread() {
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出", 0).show();
				
				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param context
	 */
	public void collectDeviceInfo(Context context) {
		try {
			PackageManager pm = context.getPackageManager();// 获得包管理器
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				info.put("versionName", versionName);
				info.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		Field[] fields = Build.class.getDeclaredFields();// 反射机制
		for (Field field : fields) {
			try {

				field.setAccessible(true);
				info.put(field.getName(), field.get("").toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : info.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\r\n");
		}
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		// 循环着把所有的异常信息写入writer中
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();// 记得关闭
		String result = writer.toString();
		sb.append(result);
		// 保存文件
		long timetamp = System.currentTimeMillis();
		String time = format.format(new Date());
		String fileName = "crash-" + time + "-" + timetamp + ".log";
		final String info = sb.toString(); 
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//			     //这个类主要是设置邮件
				  MailSenderInfo mailInfo = new MailSenderInfo(); 
				  mailInfo.setMailServerHost("smtp.163.com"); 
				  mailInfo.setMailServerPort("25"); 
				  mailInfo.setValidate(true); 
				  mailInfo.setUserName("send@163.com"); 
				  mailInfo.setPassword("*********");//您的邮箱密码 
				  mailInfo.setFromAddress("send@163.com"); 
				  mailInfo.setToAddress("recv@qq.com"); 
				  mailInfo.setSubject("程序出现奔溃:"); 
				  mailInfo.setContent(info); 
			        //这个类主要来发送邮件
				  SimpleMailSender sms = new SimpleMailSender();
//			         sms.sendTextMail(mailInfo);//发送文体格式
				  		sms.sendTextMail(mailInfo);
				Log.i("", "=============");
				
			}
		}).start();
		
//	     //这个类主要是设置邮件
//		  MailSenderInfo mailInfo = new MailSenderInfo(); 
//		  mailInfo.setMailServerHost("smtp.163.com"); 
//		  mailInfo.setMailServerPort("25"); 
//		  mailInfo.setValidate(true); 
//		  mailInfo.setUserName("dongge416@163.com"); 
//		  mailInfo.setPassword("82702083a");//您的邮箱密码 
//		  mailInfo.setFromAddress("dongge416@163.com"); 
//		  mailInfo.setToAddress("591547708@qq.com"); 
//		  mailInfo.setSubject("程序出现奔溃:"+fileName); 
//		  mailInfo.setContent(sb.toString()); 
//	        //这个类主要来发送邮件
//		  SimpleMailSender sms = new SimpleMailSender();
////	         sms.sendTextMail(mailInfo);//发送文体格式
//		  		sms.sendTextMail(mailInfo);
		Log.e("", "=============");
		
//		FileUtil.creatDir(Environment.getExternalStorageDirectory()+"/hgsoft/OperateManager/");
//		WriteLog.writeLog(Environment.getExternalStorageDirectory()+"/hgsoft/OperateManager/", "程序出现奔溃:"+fileName+"\n奔溃详细信息:\n"+sb.toString());
		
		
		return null;
	}

}
