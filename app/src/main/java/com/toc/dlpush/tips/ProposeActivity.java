package com.toc.dlpush.tips;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.richsoft.afinal.tools.http.AjaxParams;
import com.toc.dlpush.R;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.setting.util.Return;
import com.toc.dlpush.tips.util.subminJson;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.EditMatch;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinalTools;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.SensitivewordFilter;
import com.toc.dlpush.util.SigUtil;
import com.toc.dlpush.util.UtilTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 袁飞 on 2015/5/19.
 * DLpush
 */
public class ProposeActivity extends Fragment implements View.OnClickListener {
    View RootView;
    EditText feedback_content_edit;//输入框
    Button submit_button;//提交按钮
    String uid;//用户ID
    ProgressBars progress;
    private TimeCount time;
    private TimeCount time01;


    ProgressDialog progressDialog;
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        RootView= inflater.inflate(R.layout.activity_propose,container, false);
        feedback_content_edit= (EditText) RootView.findViewById(R.id.feedback_content_edit);
        uid=UtilTool.getSharedPre(getActivity(),"users","uid","0");
        progress=new ProgressBars(progressDialog, getActivity(),Constantes.SUBMITTING);
        submit_button= (Button) RootView.findViewById(R.id.submit_button);
        submit_button.setOnClickListener(this);
        time=new TimeCount(30000,1000);
        //获取屏幕的宽和高
        int height= UtilTool.getHeight(getActivity());
        int width= UtilTool.getWidth(getActivity());
        //控件适应屏幕
        feedback_content_edit.getLayoutParams().height=(height/3);
        submit_button.getLayoutParams().width=((width)/3);

        return RootView;
    }

    /**
     * 对输入框进行监听
     */
    public  void editAddText(){
        feedback_content_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>149){
                    UtilTool.showToast(getActivity(),Constantes.NUMBERLONG);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    /**
     * 上传内容
     * @author yangweisi
     */
    private class submitDatas extends AsyncTask<Void, Void, Return> {

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
            if("0".equals(result.getError())){
                UtilTool.showToast(getActivity(),"提交成功");
                Constantes.SUGGESTPUSH=1;
                Constantes.PROPOSEPUSH=1;
                feedback_content_edit.setText("");
            }else{
                UtilTool.showToast(getActivity(),"提交失败请检查网络");
            }
            progress.delProgressBar();




        }
    }

    /*
    * 反馈传值
    */
    private String Get() {
        String json = "";


        try {
            String  loadUrl = RemoteURL.USER.SUBMIT;


            //sig签名
        HashMap<String, String> sigParams=new HashMap<String, String>();
        sigParams.put("type", "feedback");
        sigParams.put("uid", uid);
            HashMap<String, String> params=new HashMap<String, String>();
            params.put("content",feedback_content_edit.getText().toString());
            params.put("type","feedback");
            params.put("uid", uid);

            json = HttpUtil.getUrlWithSig(loadUrl,params,sigParams);// 从服务器获取数据

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if((null==json)||("".equals(json))){
            return "";
        }else{
            android.util.Log.i("json ",json);
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
        }else{
            userJsonVoList= FastjsonUtil.json2object(json, Return.class);
            android.util.Log.i("userJsonVoList",userJsonVoList+"");
            return userJsonVoList;
        }

    }
    @Override
    public void onStop() {
        Log.i("ProposeActivity","onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("ProposeActivity","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.i("ProposeActivity","onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("ProposeActivity","onResume");
        SetTime();
        super.onResume();

    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.submit_button:
                 if(EditMatch.allNull(feedback_content_edit, getActivity())){

                 }else if(SensitivewordFilter.GetSensitive(feedback_content_edit.getText().toString(),getActivity())){
                     UtilTool.showToast(getActivity(),Constantes.SENSITIVE);
//                     submit_button.setText("");
             }else{
                     //检查网络
                     ConnectivityManager connectivityManager=(ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                     NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                     if(networkInfo==null||!networkInfo.isAvailable()){
                         UtilTool.showToast(getActivity(), Constantes.NETERROR);
                     }else{
                         //开始提交
                         new  submitDatas().execute();
                         Date nowDate = Calendar.getInstance().getTime();
                         SetShare();
                         time.start();//开始计时

                     }

                 }


                 break;
         }
    }
    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            submit_button.setText("提交");
            submit_button.setClickable(true);
//            SetShare01();
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            submit_button.setClickable(false);

            submit_button.setText("提交"+"("+millisUntilFinished / 1000 + "s)");
        }
    }
    public void SetShare(){
        Date nowDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        java.util.Date date=new java.util.Date();
        String str=sdf.format(nowDate);
        UtilTool.setSharedPre(getActivity(),"data","time",str);
    }
    //将时间记录在数据库中
    public String GetShare(){
       return UtilTool.getSharedPre(getActivity(),"data","time","1");
    }
    //时间取消
    public void SetShare01(){
        UtilTool.setSharedPre(getActivity(),"data","time","1");
    }
    //判断时间是否已经超过30秒
    public void SetTime(){

        if("1".equals(GetShare())){
            return;
        }else{
            Date nowDate = Calendar.getInstance().getTime();
            long timeLong = nowDate.getTime() -GetDate(GetShare()).getTime();
            if (timeLong<30*1000){
                time01 = new TimeCount(30000-timeLong, 1000);//构造CountDownTimer对象
                time01.start();
            }else{
//                SetShare01();
            }

        }
    }
    public  Date GetDate(String index){

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟
        Date date= null;
        try {
            date = sdf.parse(index);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
