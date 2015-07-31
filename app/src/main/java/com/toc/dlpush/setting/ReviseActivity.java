package com.toc.dlpush.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.toc.dlpush.LoginActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.setting.util.Return;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.EditMatch;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.UtilTool;

import java.util.HashMap;

/**
 * 1、修改密码
 * Created by 袁飞 on 2015/5/25.
 * DLpush
 */
public class ReviseActivity extends Activity implements View.OnClickListener{
    RelativeLayout revise_back;//返回按钮
    EditText revise_ed01;//原密码
    EditText revise_ed02;//新密码
    EditText revise_ed03;//新密码
    RelativeLayout revise_submit;//提交
    String Oldcode;
    private static String color = "'green'";
    ProgressBars progress;
    ProgressDialog progressDialog;
    String uid;//用户的ID
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        FinishActivity.addActivity(this);
        setContentView(R.layout.revise_code);
        uid=UtilTool.getSharedPre(this,"users","uid","0");
        Oldcode= UtilTool.getSharedPre(ReviseActivity.this,"users","code","1");
        init();
    }
    public void init(){
        progress=new ProgressBars(progressDialog, this,Constantes.SUBMITTING);
        revise_back= (RelativeLayout) findViewById(R.id.revise_back);
        revise_back.setOnClickListener(this);
        revise_ed01= (EditText) findViewById(R.id.revise_ed01);
        revise_ed02= (EditText) findViewById(R.id.revise_ed02);
        revise_ed03= (EditText) findViewById(R.id.revise_ed03);
        revise_submit= (RelativeLayout) findViewById(R.id.revise_submit);
        revise_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.revise_back:
               FinishActivity.deleteActivity(this);
               finish();

               break;
           case R.id.revise_submit:
               if(!(Oldcode.equals(revise_ed01.getText().toString()))){
                   if((EditMatch.allNull(revise_ed02,this))){
                       Setnull();
                   }else {
                       Log.i("Oldcode", Oldcode);
                       UtilTool.showToast(ReviseActivity.this, "原密码输入错误！！");
                       Setnull();
                   }
//                   revise_ed01.setText("");
           }else{
               if(!(EditMatch.allNull(revise_ed02,this))&&!(EditMatch.allNull(revise_ed03,this))&&!(EditMatch.notMatchword(revise_ed02,this))&&!(EditMatch.notMatchword(revise_ed03,this))&&!(EditMatch.notNumCont(revise_ed02,6,24,this))&&!(EditMatch.notNumCont(revise_ed03,6,24,this))){
                    if(!(revise_ed02.getText().toString().equals(revise_ed03.getText().toString()))){
                        UtilTool.showToast(this,"两次输入的新密码不一致");
                        revise_ed02.setText("");
                        revise_ed03.setText("");
//                        showEditSeterror(revise_ed03,"与第一次输入的新密码不一致");
                    }else if((revise_ed01.getText().toString().equals(revise_ed02.getText().toString()))){
                        UtilTool.showToast(this,"原始密码与新密码一致");
                        revise_ed02.setText("");
                        revise_ed03.setText("");
//                        showEditSeterror(revise_ed02,"输入的新密码与旧密码一致");
                    }else{
                        //检查网络
                        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                        if(networkInfo==null||!networkInfo.isAvailable()){
                            UtilTool.showToast(ReviseActivity.this, Constantes.NETERROR);
                        }else{
                            //修改密码
                            new Revise().execute();
                        }

                    }
               }else{
                   break;
               }
           }

               break;
       }
    }


    /**
     * 修改密码
     * @author yangweisi
     */
    private class Revise extends AsyncTask<Void, Void, Return> {

        protected void onPreExecute() {

            progress.setProgressBar();
        }

        @Override
        protected Return doInBackground(Void... paramArrayOfParams) {
            // TODO Auto-generated method stub
            return GetJson(Get());
        }
        @Override
        protected void onPostExecute(Return result) {
            // TODO Auto-generated method stub
            progress.delProgressBar();
            if((null!=result.getError())&& ("1".equals(result.getError()))){
                UtilTool.setSharedPre(ReviseActivity.this,"user","code",revise_ed02.getText().toString());
                UtilTool.setSharedPre(ReviseActivity.this,"users","auto","1");
                Toast.makeText(ReviseActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                Dialog();

            }else{
                Toast.makeText(ReviseActivity.this,result.getTip(),Toast.LENGTH_SHORT).show();
            }



        }
    }

    /*
    * 修改密码
    */
    private String Get() {
        String json = "";


        try {
            String  loadUrl = RemoteURL.USER.REVISE.replace("{oldpasswd}",Oldcode)
                    .replace("{newpasswd}",revise_ed02.getText().toString().trim()).replace("{id}", uid);


            //sig加密
            HashMap<String, String> sigParams = new HashMap<String, String>();
            sigParams.put("oldpasswd", Oldcode);
            sigParams.put("newpasswd", revise_ed02.getText().toString().trim());

            json = HttpUtil.getUrlWithSig(loadUrl, sigParams);// 从服务器获取数据

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if((null==json)||("".equals(json))){
            return "";
        }else {
            android.util.Log.i("json ", json);
            return json;
        }
    }

    /**
     * json数据的转换
     * @param json  json数据
     * @return
     */
    public Return GetJson(String json){
        Return userJsonVoList=new Return();
        if((null==json)||("".equals(json))){
            return userJsonVoList;
        }else {
            userJsonVoList = FastjsonUtil.json2object(json, Return.class);
            android.util.Log.i("userJsonVoList", userJsonVoList + "");
            return userJsonVoList;
        }
    }
    public void Setnull(){
        revise_ed01.setText("");
        revise_ed02.setText("");
        revise_ed03.setText("");
    }
    public  void Dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ReviseActivity.this);
        builder.setTitle("提示");
        builder.setMessage("修改密码，请重新登录");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it=new Intent(ReviseActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
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
