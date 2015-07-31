package com.toc.dlpush.setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.MainActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.UtilTool;


public class AboutsActivity extends Activity implements View.OnClickListener {

	private WebView wv;
	private String version;
	private TextView tv_version,tv_model;
    private RelativeLayout about_add_back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        FinishActivity.addActivity(this);
		setContentView(R.layout.about_abouts);

		
		version= UtilTool.getSharedPre(this, "version", "version", "");
		
		Init();
		
	}

	private void Init() {
		// TODO Auto-generated method stub
        about_add_back= (RelativeLayout) this.findViewById(R.id.about_add_back);
        about_add_back.setOnClickListener(this);
		wv=(WebView) this.findViewById(R.id.wv);
		tv_version=(TextView) this.findViewById(R.id.tv_version);
//        tv_model.setText("功能介绍");

//		tv_version.setText("TOC for Android V"+version);
		
		String data=readHTML("about_us.html");
		wv.loadDataWithBaseURL("file:\\\\android_asset", data, "text/html", "UTF-8", null);
	}
	
	public String readHTML(String htmlName){
		StringBuilder sb=new StringBuilder();
		
		BufferedReader br=null;
		
		try {
			InputStream is=getAssets().open(htmlName);
			br=new BufferedReader(new InputStreamReader(is));
			for(String str=null;(str = br.readLine()) != null;){
				sb.append(str);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
            case R.id.about_add_back:
                finish();
                FinishActivity.deleteActivity(this);
                break;
        }
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            FinishActivity.deleteActivity(this);

            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
}
