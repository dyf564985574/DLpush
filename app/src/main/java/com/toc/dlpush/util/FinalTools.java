package com.toc.dlpush.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.richsoft.afinal.tools.FinalHttp;
import com.richsoft.afinal.tools.http.AjaxCallBack;
import com.richsoft.afinal.tools.http.AjaxParams;


public class FinalTools {
	
	private static final String TAG = "FinalTools";

	/**
	 * 判断有无网络
	 * 
	 * @param instance
	 * @return
	 */
	public static boolean getNetWorkStatus(final Activity instance) {
		boolean netStatus = false;
		try {
			ConnectivityManager cwjManager = (ConnectivityManager) instance
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cwjManager != null && cwjManager.getActiveNetworkInfo() != null) {
				netStatus = cwjManager.getActiveNetworkInfo().isAvailable();
			}
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
		return netStatus;
	}

	public static void getData(Boolean isGet, String url, AjaxParams aj,
			final Activity activity, boolean hasDiaolog, respones res) {
		if (!getNetWorkStatus(activity)) {
			Toast.makeText(activity, "没有可用网络", Toast.LENGTH_SHORT).show();
			return;
		}
		if (isGet) {
			if (aj == null) {
				getFinal(url, activity, hasDiaolog, res);
			} else {
				getFinal(url, aj, activity, hasDiaolog, res);
			}
		} else {
			if (aj == null) {
				postFinal(url, activity, hasDiaolog, res);
			} else {
				postFinal(url, aj, activity, hasDiaolog, res);
			}
		}
	}

	/**
	 * 单独get处理
	 * 
	 * @param url
	 * @param activity
	 * @param hasDiaolog
	 * @param res
	 */
	public static void getFinal(String url, final Activity activity,
			boolean hasDiaolog, respones res) {
		FinalHttp fhttp = new FinalHttp();
		fhttp.configCharset("utf-8");
		fhttp.configTimeout(20000);//超时时间
		fhttp.configRequestExecutionRetryCount(2);//配置网络异常自动重复连接请求次数

		fhttp.get(url, initCallBack(activity, hasDiaolog, res));
	}

	public static void getFinal(String url, AjaxParams aParams,
			final Activity activity,

			boolean hasDiaolog, respones res) {
		FinalHttp fhttp = new FinalHttp();
		fhttp.configCharset("utf-8");
		fhttp.configTimeout(20000);//超时时间
		fhttp.configRequestExecutionRetryCount(2);//配置网络异常自动重复连接请求次数

		fhttp.get(url, aParams, initCallBack(activity, hasDiaolog, res));
	}

	public static void postFinal(String url, AjaxParams aParams,
			final Activity activity,

			boolean hasDiaolog, respones res) {

		FinalHttp fhttp = new FinalHttp();
		fhttp.configCharset("utf-8");
		fhttp.configTimeout(20000);//超时时间
		fhttp.configRequestExecutionRetryCount(2);//配置网络异常自动重复连接请求次数

		fhttp.post(url, aParams, initCallBack(activity, hasDiaolog, res));
		
		Log.i(TAG, "有参数的POST提交"+url);
	}

	public static void postFinal(String url, final Activity activity,

	boolean hasDiaolog, respones res) {

		FinalHttp fhttp = new FinalHttp();
		fhttp.configCharset("utf-8");
		fhttp.configTimeout(20000);//超时时间
		fhttp.configRequestExecutionRetryCount(2);//配置网络异常自动重复连接请求次数

		fhttp.post(url, initCallBack(activity, hasDiaolog, res));
	}

	public static AjaxCallBack<String> initCallBack(final Activity ac,
			boolean has, final respones res) {
		if (has) {
			final Mypro m = new Mypro.Builder(ac).setMes("连接中,请稍候...").create();
			return new AjaxCallBack<String>() {
				@Override
				public void onFailure(Throwable t, String strMsg) {
					// TODO Auto-generated method stub
					super.onFailure(t, strMsg);
					m.dismiss();
					Log.i(TAG, "---" + "failure:");
					FinalHttp.close();
//					Toast.makeText(ac, "连接失败", Toast.LENGTH_SHORT).show();
					res.toDo("");
				}

				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					super.onStart();
					m.show();
				}

				@Override
				public void onSuccess(String t) {
					// TODO Auto-generated method stub
					super.onSuccess(t);
					m.dismiss();
					Log.i(TAG, "---" + "success:");
					if (TextUtils.isEmpty(t)){
						Toast.makeText(ac, "暂无数据", Toast.LENGTH_SHORT).show();
					}
					if (t.contains("\"error_key\":\"session_invalid\"")) {
						Toast.makeText(ac, "登陆时效过期", Toast.LENGTH_SHORT).show();
						// Finishi.getInstance().exit();
						ac.finish();
						return;
					}
					res.toDo(t);
				}

			};
		}
		return new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String t) {
				// TODO Auto-generated method stub
				super.onSuccess(t);
				if (TextUtils.isEmpty(t))
					Toast.makeText(ac, "暂无数据", Toast.LENGTH_SHORT).show();
				if (t.contains("\"error_key\":\"session_invalid\"")) {
					Toast.makeText(ac, "登陆时效过期", Toast.LENGTH_SHORT).show();
					ac.finish();
					return;
				}
				res.toDo(t);
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, strMsg);
				Log.e("failed", "---" + strMsg);
				FinalHttp.close();
//				Toast.makeText(ac, "连接失败", Toast.LENGTH_SHORT).show();
				res.toDo("");
			}

		};
	}

	public static String DateFormat(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String s = sdf.format(date);
		return s;
	}

	public interface respones {
		public void toDo(String result);
	}
}
