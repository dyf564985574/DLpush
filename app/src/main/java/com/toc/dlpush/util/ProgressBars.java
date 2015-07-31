package com.toc.dlpush.util;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBars {

	ProgressDialog progressDialog;
	Context context;
    String index;
	
	public ProgressBars(ProgressDialog progressDialog,Context context,String index) {
		
		this.progressDialog=progressDialog;
		this.context=context;
        this.index=index;
	}

	public void setProgressBar(){
		// 创建ProgressDialog对象
		progressDialog = new ProgressDialog(context);
		// 设置进度条风格，风格为圆形，旋转的
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 标题
//		progressDialog.setTitle("提示");
		// 设置ProgressDialog 提示信息
		progressDialog.setMessage(index);

		// 设置ProgressDialog 标题图标
		// progressDialog.setIcon(R.drawable.wait);
		// 设置ProgressDialog 的进度条是否不明确
		progressDialog.setIndeterminate(false);
		progressDialog.show();
	}
	
	public void delProgressBar(){
		if (null != progressDialog && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
