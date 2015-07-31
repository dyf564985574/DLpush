package com.toc.dlpush.caring;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.MainActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.caring.util.CaringJsonVo;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.UtilTool;

//import net.steamcrafted.loadtoast.LoadToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 袁飞 on 2015/5/26.
 * DLpush
 */
public class CaringDetail extends Activity implements View.OnClickListener {
    WebView news_detail_content;//内容
    RelativeLayout set_add_back;
    String nid;//新闻的ID
    int index;
    String roid;
    ProgressBars progress;
    ProgressDialog progressDialog;
//    LoadToast lt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        FinishActivity.addActivity(this);
        setContentView(R.layout.caring_detail);
        roid=UtilTool.getSharedPre(this,"user","manager","0");
        Intent it=getIntent();
        nid=it.getStringExtra("nid");
        index=it.getIntExtra("index",1);
        inview();
        //判断是否有网络连接
        ConnectivityManager connectivityManager=(ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(CaringDetail.this, Constantes.NETERROR);
        }else{
            //开始联网更新
            new GetCaring().execute();

        }

    }
    public  void inview(){
        String text = "正在加载...";
        progress=new ProgressBars(progressDialog,CaringDetail.this,Constantes.LOADING);
//        lt = new LoadToast(this).setText(text).setTranslationY(100);
        news_detail_content= (WebView) findViewById(R.id.news_detail_content);
//        news_detail_content.setInitialScale(39);
        set_add_back= (RelativeLayout) findViewById(R.id.set_add_back);
        set_add_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.set_add_back:
                 FinishActivity.addActivity(this);
                 if(index==2){
                     if("5".equals(roid)){
                         finish();
                     }else {
                         Intent it = new Intent(this, MainActivity.class);
                         it.putExtra("index", 1);
                         startActivity(it);
                         finish();
                     }
                 }else{
                     finish();
                 }

                 break;
         }
    }

    //异步获取通知详情
    private class GetCaring extends AsyncTask<Void,Void,CaringJsonVo> {
        @Override
        protected void onPreExecute() {
            progress.setProgressBar();
        }

        @Override
        protected CaringJsonVo doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(CaringJsonVo caringJsonVo) {
            super.onPostExecute(caringJsonVo);
            if(("0".equals(caringJsonVo.getError()))&&caringJsonVo!=null){

                //正则 替代所有的额\t
                Pattern p = Pattern.compile("\t|\\\\");

                Matcher m = p.matcher(caringJsonVo.getNews().getContent());

                String dest = m.replaceAll("");
                dest= dest.replace("localhost",RemoteURL.HTTPHOST);
                Log.i("dest",dest);

                progress.delProgressBar();
                WebSettings settings = news_detail_content.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                settings.setLoadWithOverviewMode(true);

                settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

                news_detail_content.loadDataWithBaseURL(null,dest,"text/html","UTF-8",null);
            }else{
                UtilTool.showToast(CaringDetail.this,"此条数据已被删除");
                finish();
            }

        }
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetJson(){
        String json="";
        String loadUrl= RemoteURL.USER.DetailCARING.replace("{nid}", nid);

        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("newsdetail", "newsdetail");
        sigParams.put("randomnum", "789321");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.util.Log.i("json ",json);
        return json;
    }
    public CaringJsonVo GetList(String json) {
        CaringJsonVo notifyJsonVoList = new CaringJsonVo();
        if ((null == json) || ("".equals(json))) {
            return notifyJsonVoList;
        } else{
            notifyJsonVoList = FastjsonUtil.json2object(json,
                    CaringJsonVo.class);
        Log.i("notifyJsonVoList", notifyJsonVoList + "");
        return notifyJsonVoList;
    }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            FinishActivity.deleteActivity(this);
            if(index==2){
                if("5".equals(roid)){
                    finish();
                }else {
                    Intent it = new Intent(this, MainActivity.class);
                    it.putExtra("index", 1);
                    startActivity(it);
                    finish();
                }
            }else{
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
