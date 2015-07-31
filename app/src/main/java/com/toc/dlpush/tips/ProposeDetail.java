package com.toc.dlpush.tips;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.MainActivity;
import com.toc.dlpush.ManagerActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.util.CommentJsons;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.ProposeDao;
import com.toc.dlpush.util.SuggestDao;
import com.toc.dlpush.util.UtilTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yangweisi on 2015/6/14.
 */
public class ProposeDetail extends Activity implements View.OnClickListener {
    Comment comment;
    Bundle bundle;
    TextView tips_detail_title;//标题
    TextView tips_detail_time;//时间
    TextView tips_detail_name;//推送单位
    TextView tips_detail_content;//内容
    RelativeLayout set_add_back;
    int index=0;
    String nid;
    String uid;//用户的ID
    String offset = "0";//查询开始位置
    String showNum = "100";//第一次界面显示的list条数
    View rootView;
    ProgressBars progress;
    ProgressDialog progressDialog;
    List<Comment> subminsList=new ArrayList<Comment>();
    TextView note,note01;
    LinearLayout line;
    private  TextView textView2;
    private int path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        FinishActivity.addActivity(this);
        setContentView(R.layout.manager_tips_detail);
        Intent it=getIntent();
        nid=it.getStringExtra("id");
        index=it.getIntExtra("index", 0);
        Constantes.flag=it.getIntExtra("flag",0);
        path = it.getIntExtra("path",0);
        Log.i("flag",Constantes.flag+"");
        if(Constantes.flag==1){
            Revise_contact(nid);
        }else if(Constantes.flag==2){
            Revise_contact01(nid);
        }
        Log.i("comment", nid + "");
        inview();
        ConnectivityManager connectivityManager=(ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(this, Constantes.NETERROR);
        }else{
            new GetDatas().execute();
        }
    }
    public  void inview(){
        String text = "正在加载...";
        textView2 = (TextView) findViewById(R.id.textView2);
        if(path==1){
            textView2.setText("投诉详情");
        }else {
            textView2.setText("建议详情");
        }
        line= (LinearLayout) findViewById(R.id.line);
        note01= (TextView) findViewById(R.id.note01);
        note= (TextView) findViewById(R.id.note);
        tips_detail_title= (TextView) findViewById(R.id.manager_detail_title);
        progress=new ProgressBars(progressDialog,ProposeDetail.this,Constantes.LOADING);
        tips_detail_content= (TextView) findViewById(R.id.manager_detail_content);

        set_add_back= (RelativeLayout) findViewById(R.id.set_add_back);
        set_add_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_add_back:
                FinishActivity.deleteActivity(this);
                if((index==2)||(index==1)){
                    Intent it01=new Intent(this, FeedbackActivity.class);
                    it01.putExtra("index",index);
                    startActivity(it01);
                }
                finish();
                break;
        }
    }
    //获取意见与建议的数据
    private class GetDatas extends AsyncTask<Void,Void,CommentJsons> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CommentJsons doInBackground(Void... params) {
            return GetList(GetJson());
        }
        @Override
        protected void onPostExecute(CommentJsons submins) {
//            super.onPostExecute(submins);
            if("0".equals(submins.getError())){
                comment=submins.getComment();
                if("comment".equals(comment.getType())){
                    tips_detail_title.setText(comment.getCompanyname()+"的意见");
                }else{
                    tips_detail_title.setText(comment.getCompanyname()+"的建议");
                }
                tips_detail_content.setText("  "+comment.getContent());
                if((comment.getStatus().equals("0"))&&(comment.getNote()!=null)){
                    note01.setVisibility(View.VISIBLE);
                    note.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    note.setText("           "+comment.getNote());
                    Log.i("note",comment.getNote().trim());
                }
            }
        }
    }
    /*
    * 获取反馈意见列表
    */
    private String GetJson() {
        String json = "";

        String loadUrl = RemoteURL.USER.SUGGEST.replace("{id}", nid);
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("id", nid);
        sigParams.put("commentDetail", "commentDetail12309");


        try {
            json = HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
        return json;
    }
    private CommentJsons GetList(String json){
        CommentJsons sub=new CommentJsons();
        sub= FastjsonUtil.json2object(json, CommentJsons.class);
        android.util.Log.i("userJsonVoList",sub+"");
        return sub;
    }
    //修改数据库中的数据
    public void Revise_contact(String nid){
        ProposeDao  dao=new ProposeDao(ProposeDetail.this);
        ContentValues values=new ContentValues();
        values.put("status","0");
        dao.update("propose",values,"id=?",new String[]{nid});
        dao.close();
    }
    //修改数据库中的数据
    public void Revise_contact01(String nid){
        SuggestDao  dao01=new SuggestDao(ProposeDetail.this);
        ContentValues values=new ContentValues();
        values.put("status","0");
        dao01.update("suggest",values,"id=?",new String[]{nid});
        dao01.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            FinishActivity.deleteActivity(this);
            if((index==2)||(index==1)){
                Intent it01=new Intent(this, FeedbackActivity.class);
                it01.putExtra("index",index);
                startActivity(it01);
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
