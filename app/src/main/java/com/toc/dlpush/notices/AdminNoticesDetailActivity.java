package com.toc.dlpush.notices;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.ManagerActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.notices.util.Notify;
import com.toc.dlpush.notices.util.NotifyGroup;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.other.ReadPeopleActivity;
import com.toc.dlpush.other.UnReadPeopleActivity;
import com.toc.dlpush.other.admin.AdminReadPeopleActivity;
import com.toc.dlpush.other.admin.AdminUnReadPeopleActivity;
import com.toc.dlpush.setting.util.Userlink;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.UtilTool;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuanfei on 2015/7/24.
 */
public class AdminNoticesDetailActivity extends Activity implements View.OnClickListener,GestureDetector.OnGestureListener,View.OnTouchListener {
    private RelativeLayout notices_relativeLayout;//返回
    private  RelativeLayout notices_share;//分享
    private WebView webview;
    private NotifyJsonVo notices=new NotifyJsonVo();//数据集合
    private Notify notify=new Notify();
    private String html;//HTML5
    private String nid;//通知的id
    private  List<Userlink> userlinks;//联系人集合
    private String uid;//用户的ID
    ProgressBars progress;
    private ProgressDialog progressDialog;
    private LinearLayout notices_details;
    private GestureDetector detector;
    private LinearLayout manager_check;
    private int index;
    //显示/隐藏动画效果
    private Animation mShowAction;
    private Animation mHiddenAction;
    private RelativeLayout tips_drop,tips_other,manager_unread,manager_read;
    private TextView read_num;//已读人数
    private TextView unread_num;//未读人数
    boolean show = true;
    private String read_num01;//已读人数
    private String count_num;//未读人数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        FinishActivity.addActivity(this);
        setContentView(R.layout.notices_detail);
        uid= UtilTool.getSharedPre(AdminNoticesDetailActivity.this, "users", "uid", "0");
        //从通知列表界面获取通知的ID
        Intent it=getIntent();
//        notices=(NotifyJsonVo) it.getSerializableExtra("notices");
        nid=it.getStringExtra("nid");
        index=it.getIntExtra("index", 1);
        read_num01 = it.getStringExtra("readnum");
        count_num = it.getStringExtra("countnum");
        Log.i("Intent", nid + "nid");
        Log.i("nid",notices+"");
        uid=UtilTool.getSharedPre(this,"users","uid","0");
        progress=new ProgressBars(progressDialog,AdminNoticesDetailActivity.this, Constantes.LOADING);
        inview();
//        String text = "正在加载...";
//        lt = new LoadToast(this).setText(text).setTranslationY(100);

        //判断是否有网络连接
        ConnectivityManager connectivityManager=(ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(AdminNoticesDetailActivity.this, Constantes.NETERROR);
        }else{
            //开始联网更新
            new GetNotices().execute();
        }
    }
    public void inview(){
        //显示的动画效果
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        //消失的动画效果
        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction.setDuration(500);
        detector = new GestureDetector(this);
        read_num = (TextView) findViewById(R.id.read_num);
        unread_num = (TextView) findViewById(R.id.unread_num);
        read_num.setText(read_num01);
        int unread_num01 = 0;
        unread_num01 = Integer.parseInt(count_num)-Integer.parseInt(read_num01);
        unread_num.setText(unread_num01+"");
        //已读未读的弹出窗
        manager_check= (LinearLayout) findViewById(R.id.manager_check);
        notices_relativeLayout= (RelativeLayout) findViewById(R.id.notices_back);
        notices_details= (LinearLayout) findViewById(R.id.notices_details);
        notices_relativeLayout.setOnClickListener(this);
        notices_share= (RelativeLayout) findViewById(R.id.notices_share);
        tips_drop= (RelativeLayout) findViewById(R.id.manager_drop);
        notices_share.setOnClickListener(this);
        webview= (WebView) findViewById(R.id.webview);
        tips_other= (RelativeLayout) findViewById(R.id.manager_other);
        tips_other.setOnClickListener(this);
        manager_unread= (RelativeLayout) findViewById(R.id.manager_unread);
        manager_unread.setOnClickListener(this);
        manager_read= (RelativeLayout) findViewById(R.id.manager_read);
        manager_read.setOnClickListener(this);
        webview.setOnTouchListener(this);
        webview.setLongClickable(true);
    }

    /**
     * 初始化控件
     */
    public void init(){
        read_num.setText(notify.getReadnum());
        int unread_num01 = 0;
        unread_num01 = Integer.parseInt(notify.getCountnum())-Integer.parseInt(notify.getReadnum());
        unread_num.setText(unread_num01+"");
        html= notify.getContent();
        Log.i("notify",html);
        //正则 替代所有的额\t
        Pattern p = Pattern.compile("\t|\\.");
        Matcher m = p.matcher(html);
        String dest = m.replaceAll("");
//        dest.replaceAll("\\.","");
        //启用支持javascript
        WebSettings settings = webview.getSettings();
//        settings.setUseWideViewPort(true);//设置webview推荐使用的窗口
        settings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式
        settings.setJavaScriptEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.loadDataWithBaseURL(null,dest,"text/html","UTF-8",null);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回按钮
            case R.id.notices_back:
                FinishActivity.deleteActivity(this);
                if(index==2){

                    Intent it=new Intent(this, ManagerActivity.class);
                    startActivity(it);
                    finish();
                }else{
                    finish();
                }
                break;
            //已读未读
            case R.id.notices_share:
                if(show){
                    manager_check.setVisibility(View.VISIBLE);
                    tips_drop.startAnimation(mShowAction);
                }else{
                    manager_check.setVisibility(View.GONE);
                    tips_drop.startAnimation(mHiddenAction);
                    show = false;
                }

                break;
            case R.id.manager_other:
                manager_check.setVisibility(View.GONE);
                tips_drop.startAnimation(mHiddenAction);
                break;
            case R.id.manager_read:
                Intent it = new Intent(AdminNoticesDetailActivity.this, AdminReadPeopleActivity.class);
                it.putExtra("nid",nid);
                startActivity(it);
                manager_check.setVisibility(View.GONE);
                break;
            case R.id.manager_unread:
                Intent it01 = new Intent(AdminNoticesDetailActivity.this, AdminUnReadPeopleActivity.class);
                it01.putExtra("nid",nid);
                startActivity(it01);
                manager_check.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//        Log.i("onScroll","MotionEvent1"+e1.getX()+ "     MotionEvent2"+e2.getX()+"   distanceX"+distanceX);
//        if(e1.getX()-e2.getX() > 120){
//            FinishActivity.deleteActivity(this);
//            if(index==2){
//                Intent it=new Intent(this, MainActivity.class);
//                startActivity(it);
//                finish();
//
//            }else{
//                finish();
//            }
//
//            }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        Log.i("onScroll","MotionEvent1  "+e1.getX()+ "     MotionEvent2   "+e2.getX()+"   velocityX"+velocityX+"    yyyyy   "+Math.abs(e2.getY()-e1.getY()));
//        if((e2.getX()-e1.getX() > 200)&&(2*Math.abs(e2.getY()-e1.getY())<Math.abs(e2.getX()-e1.getX()))){
//            FinishActivity.deleteActivity(this);
//            if(index==2){
//                Intent it=new Intent(this, MainActivity.class);
//                startActivity(it);
//                finish();
//
//            }else{
//                finish();
//            }
//
//        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("touch","touch");
        return detector.onTouchEvent(event);
    }


    //异步获取通知详情
    private class GetNotices extends AsyncTask<Void,Void,NotifyGroup> {
        @Override
        protected void onPreExecute() {
            progress.setProgressBar();
//          lt.show();
        }

        @Override
        protected NotifyGroup doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(NotifyGroup noticesGroupInfo) {
            super.onPostExecute(noticesGroupInfo);
            progress.delProgressBar();
            if((noticesGroupInfo.getTip()!=null)&&("0".equals(noticesGroupInfo.getError()))){
                notify=noticesGroupInfo.getNotify();
//                lt.success();
                init();

            }else{
                UtilTool.showToast(AdminNoticesDetailActivity.this,"此条数据已被删除");
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
        String loadUrl= RemoteURL.USER.DetialNotices.replace("{nid}", nid).replace("{uid}", uid);

        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("nid", nid);
        sigParams.put("uid", uid);
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.util.Log.i("json ",json);
        return json;
    }
    public NotifyGroup GetList(String json){
        NotifyGroup notifyJsonVoList=new NotifyGroup();
//        Log.i("notifyJsonVoList",(notifyJsonVoList==null)+"");
        if(("".equals(json)||(null==json))){
            return notifyJsonVoList;
        }else{
            notifyJsonVoList = FastjsonUtil.json2object(json,
                    NotifyGroup.class);
            Log.i("notifyJsonVoList",notifyJsonVoList+"");
            return notifyJsonVoList;
        }

    }



//    //获取联系人数据
//    private class GetData extends AsyncTask<Void,Void,Contact> {
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected Contact doInBackground(Void... params) {
//            return GetList01(GetJson01());
//        }
//
//        @Override
//        protected void onPostExecute(Contact res) {
//            super.onPostExecute(res);
//            if ("0".equals(res.getError())){
//                Log.i("userlinks"," "+userlinks+"   "+userlinks.size());
//                if((null!=userlinks)&&userlinks.size()!=0){
//                    //转发完成
//                    for(int i=0;i<userlinks.size();i++){
//                        Add_contact(userlinks.get(i));
//                    }
//
////                      UtilTool.showToast(NoticesDetialActivity.this,"转发完成");
//                }else{
//                    dialog();
//                }
//
//            }
//        }
//    }
//
//    /**
//     * 联系人
//     * 从网络上获取数据
//     * @return  返回JSON数据
//     */
//    public String GetJson01(){
//        String json="";
//        String loadUrl= RemoteURL.USER.Contact.replace("{uid}", "3");
//        //sig加密
//        HashMap<String, String> sigParams = new HashMap<String, String>();
//        sigParams.put("uid", "3");
//        sigParams.put("findAllByUid", "findAllByUid");
//        try {
//            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        android.util.Log.i("json",json);
//        return json;
//    }
//
//
//    /**
//     * 掉短信转发接口
//     * @return  返回JSON数据
//     */
//    public String Getsms(List<Userlink> userlinkList){
//        String phone;
//        if(userlinkList.size()==1){
//            phone=userlinkList.get(0).getLinkphone();
//        }else if (userlinkList.size()==2){
//            phone=userlinkList.get(0).getLinkphone()+","+userlinkList.get(1).getLinkphone();
//        }else{
//            phone=userlinkList.get(0).getLinkphone()+","+userlinkList.get(1).getLinkphone()+","+userlinkList.get(2).getLinkphone();
//        }
//        String json="";
//        String loadUrl= RemoteURL.USER.SMSSHARE.replace("{phones}", phone).replace("{uid}",uid).replace("{nid}",nid);
//        //sig加密
//        HashMap<String, String> sigParams = new HashMap<String, String>();
//        sigParams.put("phones", phone);
//        sigParams.put("uid", uid);
//        try {
//            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        android.util.Log.i("json",json);
//        return json;
//    }
//
//
//    /**
//     * 联系人
//     * @param json
//     * @return
//     */
//    public Contact GetList01(String json){
//        Contact res=new Contact();
//        res = FastjsonUtil.json2object(json,
//                Contact.class);
//        userlinks=res.getLinklist();
//        if((userlinks.size()!=0)&&"0".equals(res.getError())){
//            GetShare(Getsms(userlinks));
//        }
//        Log.i("Return",res+"");
//        return res;
//    }
//    //解析分享返回的JSON
//    //短信
//    public ShareData GetShare(String json){
//        ShareData shareData=new ShareData();
//        shareData=FastjsonUtil.json2object(json,ShareData.class);
//
//        return shareData;
//    }


//    //分享通知
//    private class GetShareSms extends AsyncTask<Void,Void,ShareData> {
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected ShareData doInBackground(Void... params) {
//            return GetShare(Getsms(userlinks));
//        }
//
//        @Override
//        protected void onPostExecute(ShareData res) {
//            super.onPostExecute(res);
//            if ("0".equals(res.getError())){
//                UtilTool.showToast(AdminNoticesDetailActivity.this,"转发完成");
//            }else{
//                UtilTool.showToast(AdminNoticesDetailActivity.this,"转发失败"+res.getTip());
//            }
//            if("3".equals(res.getSharenum())){
//                notices_share.setClickable(false);
//            }
//
//        }
//    }



//    //弹出窗
//    public void dialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNoticesDetailActivity.this);
//        builder.setMessage("您没有添加联系人，请问是否添加");
//        builder.setTitle("提示");
//        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                Intent it=new Intent(AdminNoticesDetailActivity.this, AddActivity.class);
//                it.putExtra("index",1);
//                startActivity(it);
//            }
//        });
//        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.create().show();
//    }
//    //读取联系人
//    public void GetUserlink(){
//        UserlinkDao dao=new UserlinkDao(this);
//        userlinks=dao.queryList(Userlink.class,"user_link",new String[]{"*"},null,null,"id",null,null);
//        dao.close();
//    }
//    //向数据库添加联系人
//    public  void Add_contact(Userlink userlink){
//        UserlinkDao dao=new UserlinkDao(this);
//        ContentValues values=new ContentValues();
//        values.put("id",userlink.getId());
//        values.put("uid",userlink.getUid());
//        values.put("linkphone",userlink.getLinkphone());
//        dao=new UserlinkDao(this);
//        dao.insert("user_link",values);
//        dao.close();
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            FinishActivity.deleteActivity(this);
            if(!show){
                manager_check.setVisibility(View.GONE);
                tips_drop.startAnimation(mHiddenAction);
            }else {
                if (index == 2) {
                    Intent it = new Intent(this, ManagerActivity.class);
                    startActivity(it);
                    finish();
                } else {
                    finish();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}