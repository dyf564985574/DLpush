package com.toc.dlpush.tips;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toc.dlpush.ManagerActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.setting.util.Return;
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.util.CommentJsons;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.ProposeDao;
import com.toc.dlpush.util.SensitivewordFilter;
import com.toc.dlpush.util.SuggestDao;
import com.toc.dlpush.util.UtilTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by  on 2015/6/8.
 */
public class TipsDetail extends Activity implements View.OnClickListener {
    Comment comment;
    Bundle bundle;
    TextView tips_detail_title;//标题
    TextView tips_detail_time;//时间
    TextView tips_detail_name;//推送单位
    TextView tips_detail_content;//内容
    RelativeLayout set_add_back;
    int index = 0;
    String nid;
    String uid;//用户的ID
    String offset = "0";//查询开始位置
    String showNum = "100";//第一次界面显示的list条数
    View rootView;
    ProgressBars progress;
    ProgressDialog progressDialog;
    List<Comment> subminsList = new ArrayList<Comment>();
    Button detail_process;//处理按钮
    int path = 0;
    ProposeDao dao;
    SuggestDao dao01;
    int flag = 0;
    LinearLayout reply_main_two;//评论框
    Button new_reply_back;//评论返回键
    Button publish_btn;//评论发布按钮
    Button cancel_btn;//取消评论按钮
    EditText et_content;//评论输入框
    String reply_note;
    TextView note, note01;
    LinearLayout line;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        FinishActivity.addActivity(this);
        setContentView(R.layout.manager_tips_detail);
        Intent it = getIntent();
        nid = it.getStringExtra("id");
        index = it.getIntExtra("index", 1);
        path = it.getIntExtra("path", 0);
        flag = it.getIntExtra("flag", 0);
        Log.i("comment", nid + "");
        inview();
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            UtilTool.showToast(this, Constantes.NETERROR);
        } else {
            //
            new GetDatas().execute();
        }
    }

    public void inview() {
        String text = "正在加载...";
        textView2 = (TextView) findViewById(R.id.textView2);
        if(path==1){
            textView2.setText("投诉详情");
        }else {
            textView2.setText("建议详情");
        }
        line = (LinearLayout) findViewById(R.id.line);
        note01 = (TextView) findViewById(R.id.note01);
        note = (TextView) findViewById(R.id.note);
        et_content = (EditText) findViewById(R.id.et_content);
        new_reply_back = (Button) findViewById(R.id.new_reply_back);
        new_reply_back.setOnClickListener(this);
        publish_btn = (Button) findViewById(R.id.publish_btn);
        publish_btn.setOnClickListener(this);

        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(this);

        reply_main_two = (LinearLayout) findViewById(R.id.reply_main_two);
        tips_detail_title = (TextView) findViewById(R.id.manager_detail_title);
        progress = new ProgressBars(progressDialog, TipsDetail.this, Constantes.LOADING);
        tips_detail_content = (TextView) findViewById(R.id.manager_detail_content);
        detail_process = (Button) findViewById(R.id.detail_process);
        detail_process.setOnClickListener(this);
        set_add_back = (RelativeLayout) findViewById(R.id.set_add_back);
        set_add_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_add_back:
                if (index == 2) {
                    Intent it01 = new Intent(this, ManagerActivity.class);
                    it01.putExtra("index01", flag);
                    startActivity(it01);
                }
                finish();
                FinishActivity.deleteActivity(this);
                break;
            case R.id.detail_process:
//                reply_main_two.setVisibility(View.VISIBLE);
//                detail_process.setVisibility(View.GONE);
                Dialog();
                break;
            case R.id.publish_btn:
                reply_note = et_content.getText().toString().trim();
                if ((("".equals(reply_note)) || (reply_note == null))) {
                    UtilTool.showToast(TipsDetail.this, Constantes.NOTNULL);
                } else if(SensitivewordFilter.GetSensitive(reply_note, TipsDetail.this)){
                    UtilTool.showToast(TipsDetail.this, Constantes.SENSITIVE);
                }else{
                ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    UtilTool.showToast(this, Constantes.NETERROR);
                } else {

                    new Submit_process().execute();
                }
            }
            case R.id.cancel_btn:
                reply_main_two.setVisibility(View.GONE);
                detail_process.setVisibility(View.VISIBLE);
                break;
            case R.id.new_reply_back:
                reply_main_two.setVisibility(View.GONE);
                detail_process.setVisibility(View.VISIBLE);
                break;
        }
    }

    //获取意见与建议的数据
    private class GetDatas extends AsyncTask<Void, Void, CommentJsons> {

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
            if ("0".equals(submins.getError())) {
                comment = submins.getComment();
                if ("comment".equals(comment.getType())) {
                    tips_detail_title.setText(comment.getCompanyname() + "的意见");
                } else {
                    tips_detail_title.setText(comment.getCompanyname() + "的建议");
                }
                //如果是已处理 处理按钮不显示
                if (comment.getStatus().equals("1")) {
                    detail_process.setVisibility(View.VISIBLE);
                }
                tips_detail_content.setText("  " + comment.getContent());
                if ((comment.getStatus().equals("0")) && (comment.getNote() != null)) {
                    note01.setVisibility(View.VISIBLE);
                    note.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    note.setText("           " + comment.getNote());
                    Log.i("note", comment.getNote().trim());
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

    private CommentJsons GetList(String json) {
        CommentJsons sub = new CommentJsons();
        sub = FastjsonUtil.json2object(json, CommentJsons.class);
        android.util.Log.i("userJsonVoList", sub + "");
        return sub;
    }

    //
    // 区域经理处理
    private class Submit_process extends AsyncTask<Void, Void, Return> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setProgressBar();
        }

        @Override
        protected Return doInBackground(Void... params) {
            return GetProcessJson(GetProcess());
        }

        @Override
        protected void onPostExecute(Return aReturn) {
            super.onPostExecute(aReturn);
            progress.delProgressBar();
            if ((aReturn.getError() != null) && (aReturn.getError().equals("0"))) {
                UtilTool.showToast(TipsDetail.this, Constantes.SUBMITSUCCESS);
                detail_process.setVisibility(View.GONE);
                if (path == 2) {
                    Revise_contact(nid);
                } else {
                    Revise_contact01(nid);
                }
                reply_main_two.setVisibility(View.GONE);
                note01.setVisibility(View.VISIBLE);
                note.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);
//
//                Log.i("note", comment.getNote().trim());

            } else {
                UtilTool.showToast(TipsDetail.this, Constantes.SUBMITFAILURE);
            }
        }
    }

    public String GetProcess() {
        String json = "";
        String loadUrl = RemoteURL.USER.PROCESS;
        //sig加密
        Log.i("HashMap", "id  " + nid + "   note" + reply_note);
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("id", nid);
        sigParams.put("commentUpdateStatus", "commentUpdateStatus333109");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", nid);
        params.put("note", reply_note);
        try {
            json = HttpUtil.getUrlWithSig(loadUrl, params, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
        return json;
    }

    public Return GetProcessJson(String json) {
        Return returns = new Return();
        returns = FastjsonUtil.json2object(json, Return.class);
        android.util.Log.i("returns", returns + "");
        return returns;
    }

    //修改数据库中的数据
    public void Revise_contact(String nid) {
        dao = new ProposeDao(TipsDetail.this);
        ContentValues values = new ContentValues();
        values.put("status", "0");
        dao.update("propose", values, "id=?", new String[]{nid});
        dao.close();
    }

    //修改数据库中的数据
    public void Revise_contact01(String nid) {
        dao01 = new SuggestDao(TipsDetail.this);
        ContentValues values = new ContentValues();
        values.put("status", "0");
        dao01.update("suggest", values, "id=?", new String[]{nid});
        dao01.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (index == 2) {
                Intent it01 = new Intent(this, ManagerActivity.class);
                it01.putExtra("index01", flag);
                startActivity(it01);
            }
            finish();
            FinishActivity.deleteActivity(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void Dialog() {
        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.edit_dialog, null);
        final EditText editText= (EditText) layout.findViewById(R.id.feedback_content_edit);
// 2. 新建对话框对象
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reply_note = editText.getText().toString().trim();
                if (("".equals(reply_note)) || (reply_note == null)) {
                    UtilTool.showToast(TipsDetail.this, "输入不能为空");
                } else if(SensitivewordFilter.GetSensitive(reply_note,TipsDetail.this)){
                    UtilTool.showToast(TipsDetail.this, Constantes.SENSITIVE);
                }else{
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo == null || !networkInfo.isAvailable()) {
                        UtilTool.showToast(TipsDetail.this, Constantes.NETERROR);
                    } else {
                        note.setText("           " +reply_note );
                        new Submit_process().execute();
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
