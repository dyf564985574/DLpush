package com.toc.dlpush;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.index.TabCaringActivity;
import com.toc.dlpush.index.TabNoticesActivity;
import com.toc.dlpush.index.TabTipsActivity;
import com.toc.dlpush.index.admin.InteractiveAnalysisActivity;
import com.toc.dlpush.index.admin.NewsAnalysisActivity;
import com.toc.dlpush.index.admin.NotifyAnalysisActivity;
import com.toc.dlpush.index.admin.StuffActivity;
import com.toc.dlpush.index.admin.StuffAnalysisActivity;
import com.toc.dlpush.setting.AboutsActivity;
import com.toc.dlpush.setting.AddActivity;
import com.toc.dlpush.setting.ReviseActivity;
import com.toc.dlpush.setting.util.Return;
import com.toc.dlpush.setting.util.Updatas;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.Update;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.BackHandledFragment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by yuanfei on 2015/7/22.
 */
public class AdminActivity extends FragmentActivity implements View.OnClickListener {

    private SlidingMenu mMenu;
    protected static final String TAG = "AdminActivity";
    private Context mContext;
    private Button mConstact,mDeynaimic,mSetting , mStuff;
    private Button  mNews;
    private View mPopView;
    private View currentButton;

    private TextView app_cancle;
    private TextView app_exit;
    private TextView app_change;

    private int mLevel=1;
    private PopupWindow mPopupWindow;
    private LinearLayout adminBarGroup;
    private int currentTab=0;
    RelativeLayout relat_add;//添加联系人的相对布局
    RelativeLayout relat_revise;//修改密码的相对布局
    RelativeLayout relat_update;//检查更新的相对布局
    RelativeLayout relat_function;//功能接收的相对布局
    RelativeLayout relat_clear;//清除缓存的相对布局
    RelativeLayout relat_logoff;//退出当前账号的相对布局
    String uid="";
    private ProgressDialog pd;
    public static boolean flag01=false;
    public static boolean flag02=false;
    public static boolean flag03=false;
    Updatas updatase;
    Fragment mContent ;
    int first=1;//判断是不是第一次进入
    public String firstLogin;
    private long mExitTime;

    int index=0;
    //升级
    public static final int NEED_UPDATE=1;
    public static final int UPDATE_ERROR=0;
    public static final int DOWNLOAD_SUCCESS=2;
    public static final int DOWNLOAD_ERROR=3;

    private BackHandledFragment mBackHandedFragment;
    private boolean hadIntercept;
    Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case DOWNLOAD_SUCCESS:
                    pd.dismiss();
                    //安装下载好的apk
                    File file=(File) msg.obj;
                    //调用系统的apk安装器完成安装
                    System.out.println("file  "+file);
                    if(file!=null&&!file.equals("")){
                        install(file);}
                    break;
                case DOWNLOAD_ERROR:
                    UtilTool.showToast(AdminActivity.this, "下载apk失败");
                    pd.dismiss();
                    break;
                case UPDATE_ERROR:
                    UtilTool.showToast(AdminActivity.this, "此版本是最新版本");
                    break;
                case NEED_UPDATE:
                    // 从服务器下载最新的apk安卓
                    if(msg.obj==null){
                        pd.setMessage("正在下载更新");
                        new Thread(new DownloadApkTask()).start();
                    }else {
                        pd.setProgress((Integer) msg.obj);
                        pd.setMessage("正在下载更新"+((Integer) msg.obj)+"%");
                    }

                    pd.show();
//
                    break;
            }
        }
    };

    /*根据下载好的apk进行安装
     */
    private void install(File file) {
        // TODO Auto-generated method stub
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);

//		intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//mime数据类型
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.admin_main);
        Constantes.ADMINUPDATE_NOTICE = 1;
        Constantes.ADMINUPDATE_TIP = 1;
        Constantes.ADMINUPDATE_INTER = 1;
        Constantes.ADMINUPDATE_STUFF = 1;
        FinishActivity.addActivity(this);
        Intent it=getIntent();
        index=it.getIntExtra("index",0);
        first=1;
        mMenu = (SlidingMenu) findViewById(R.id.id_menu);
        firstLogin=UtilTool.getSharedPre(AdminActivity.this, "users", "firstLogin", "0");
        uid= UtilTool.getSharedPre(AdminActivity.this, "users", "uid", "0");
        findView();
        init();
    }

    private void findView(){

        adminBarGroup=(LinearLayout) findViewById(R.id.admin_bar_group);
        mNews=(Button) findViewById(R.id.admin_news);
        mConstact=(Button) findViewById(R.id.admin_constact);
        mSetting=(Button) findViewById(R.id.admin_setting);
        mStuff = (Button) findViewById(R.id.admin_stuff);
        relat_add= (RelativeLayout) findViewById(R.id.relat_add);
        relat_add.setVisibility(View.GONE);
        relat_add.setOnClickListener(this);
        relat_revise= (RelativeLayout) findViewById(R.id.relat_revise);
        relat_revise.setOnClickListener(this);
        relat_update= (RelativeLayout) findViewById(R.id.relat_update);
        relat_update.setOnClickListener(this);
        relat_function= (RelativeLayout) findViewById(R.id.relat_function);
        relat_function.setOnClickListener(this);
        relat_clear= (RelativeLayout) findViewById(R.id.relat_clear);
        relat_clear.setOnClickListener(this);
        relat_logoff= (RelativeLayout) findViewById(R.id.relat_logoff);
        relat_logoff.setOnClickListener(this);
        pd=new ProgressDialog(this);
        pd.setMax(1);
        Constantes.NOTICESPUSH=1;
        Constantes.CARINGPUSH=1;
        Constantes.SUGGESTPUSH=1;
        Constantes.PROPOSEPUSH=1;
    }
    private void init(){
        mNews.setOnClickListener(newsOnClickListener);
        mConstact.setOnClickListener(constactOnClickListener);
        mSetting.setOnClickListener(settingOnClickListener);
        mStuff.setOnClickListener(stuffOnClickListener);
        if(index==0){
            mNews.performClick();

        }else if(index==1){
            mConstact.performClick();
        }else{
            mSetting.performClick();
        }

    }
    public void toggleMenu(View view){
        mMenu.toggle();
    }
    private View.OnClickListener newsOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction ft=obtainFragmentTransaction(0);
            NotifyAnalysisActivity newsFatherFragment=new NotifyAnalysisActivity();
            ft.replace(R.id.fl_content, newsFatherFragment,AdminActivity.TAG);
            ft.commit();
            setButton(v);
            mContent=newsFatherFragment;
            currentTab=0;
        }
    };

    private View.OnClickListener constactOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            FragmentTransaction ft=obtainFragmentTransaction(1);
            NewsAnalysisActivity constactFatherFragment=new NewsAnalysisActivity();

            ft.replace(R.id.fl_content, constactFatherFragment,AdminActivity.TAG);
            ft.commit();
            setButton(v);
            mContent=constactFatherFragment;
            currentTab=1;

        }
    };

    private View.OnClickListener settingOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            FragmentManager fm=getSupportFragmentManager();
//            FragmentTransaction ft=fm.beginTransaction();
            FragmentTransaction ft=obtainFragmentTransaction(2);
            InteractiveAnalysisActivity dynamicFragment=new InteractiveAnalysisActivity();
//            flag03=true;
            ft.replace(R.id.fl_content, dynamicFragment, AdminActivity.TAG);
            ft.commit();
//            switchContent(dynamicFragment);
            setButton(v);
            mContent=dynamicFragment;
            currentTab=2;

        }
    };

    private View.OnClickListener stuffOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            FragmentManager fm=getSupportFragmentManager();
//            FragmentTransaction ft=fm.beginTransaction();
            FragmentTransaction ft=obtainFragmentTransaction(3);
            StuffAnalysisActivity stuffAnalysisActivity=new StuffAnalysisActivity();
//            flag03=true;
            ft.replace(R.id.fl_content, stuffAnalysisActivity, AdminActivity.TAG);
            ft.commit();
//            switchContent(dynamicFragment);
            setButton(v);
            mContent=stuffAnalysisActivity;
            currentTab=3;

        }
    };

    private void setButton(View v){
        if(currentButton!=null&&currentButton.getId()!=v.getId()){
            currentButton.setEnabled(true);
        }
        v.setEnabled(false);
        currentButton=v;
    }
    public FragmentTransaction obtainFragmentTransaction(int index){
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        if(first!=1) {
            if (index > currentTab) {
                ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
            } else if (index == currentTab) {

            } else {
                ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        }else{
            first=2;
        }
        return ft;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.relat_add :
                Intent it=new Intent(AdminActivity.this, AddActivity.class);
                startActivity(it);
                break;
            case R.id.relat_revise:
                Intent it01=new Intent(AdminActivity.this, ReviseActivity.class);
                startActivity(it01);
                break;
            case R.id.relat_logoff:
                ConnectivityManager connectivityManager=(ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(AdminActivity.this, Constantes.NETERROR);
                }else{
                    Dialog();
                }
                break;
            case  R.id.relat_update:
                ConnectivityManager connectivityManager01=(ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo01=connectivityManager01.getActiveNetworkInfo();
                if(networkInfo01==null||!networkInfo01.isAvailable()){
                    UtilTool.showToast(AdminActivity.this, Constantes.NETERROR);
                }else{
                    new Updata().execute();
                }
                break;
            case R.id.relat_clear:
                DialogClear();
                break;
            case R.id.relat_function:
                Intent it02=new Intent(AdminActivity.this, AboutsActivity.class);
                startActivity(it02);
                break;

        }
    } //注销登陆


    public  class Logout extends AsyncTask<Void,Void,Return> {
        @Override
        protected void onPostExecute(Return aReturn) {
            super.onPostExecute(aReturn);
            if((null==aReturn.getError())||("1".equals(aReturn.getError()))){
                UtilTool.setSharedPre(AdminActivity.this,"users","auto","1");
                Intent it02=new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(it02);
                finish();
            }else{
                UtilTool.setSharedPre(AdminActivity.this,"users","auto","1");
//                DataCleanManager.cleanApplicationData(AdminActivity.this);
                UtilTool.ClearData(AdminActivity.this);
                Intent it02=new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(it02);
                finish();
            }
        }
        @Override
        protected Return doInBackground(Void... params) {
            return GetList(GetJson());
        }
    }
    public  String GetJson(){
        String json="";
        String loadUrl= RemoteURL.USER.LOGOUT.replace("{uid}", uid);
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("uid", uid);
        sigParams.put("logout", "logout1123");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
        return json;
    }
    public Return GetList(String json) {
        Return return01 = new Return();
        if ((null == json) || ("".equals(json))) {
            return return01;
        } else {
            return01 = FastjsonUtil.json2object(json,
                    Return.class);
            Log.i("return01", return01 + "");
            return return01;
        }
    }
    //更新软件
    public  class Updata extends AsyncTask<Void,Void, Updatas>{
        @Override
        protected void onPostExecute(Updatas updatas) {
            super.onPostExecute(updatas);
            if((null==updatas.getError())||("1".equals(updatas.getError()))){
                UtilTool.showToast(AdminActivity.this,Constantes.NETERROR);
            }else{
                //判断版本号是否相同
                //getVersion()
                if(getVersion().equals(updatas.getVersion())){
                    UtilTool.showToast(AdminActivity.this,"已是最新版本");
                }else{
                    //通知主线程更新
                    updatase=updatas;
                    DialogUpdata();
                }
//                UtilTool.showToast(AdminActivity.this,"已是最新版本");
            }
        }
        @Override
        protected Updatas doInBackground(Void... params) {
            return GetUpdataList(GetUpdataJson());
        }
    }

    public  String GetUpdataJson(){
        String json="";
        String loadUrl= RemoteURL.USER.UPDATA.replace("{phonetype}", "0");
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("apkUpdateCheck1123", "12302390");
        sigParams.put("apkUpdateCheck2233", "99081232");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json",json);
        return json;
    }
    public Updatas GetUpdataList(String json) {
        Updatas updatas = new Updatas();
        if ((null == json) || ("".equals(json))) {
            return updatas;
        } else {
            updatas = FastjsonUtil.json2object(json,
                    Updatas.class);
            Log.i("return01", updatas + "");
            return updatas;
        }
    }
    public  void Dialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminActivity.this);
//        builder.setTitle(Constantes.PROMPT);
        builder.setMessage(Constantes.EXITACCOUNT);
        builder.setPositiveButton(Constantes.DEFINE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Logout().execute();
            }
        });
        builder.setNegativeButton(Constantes.CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    public  void DialogClear(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminActivity.this);
//        builder.setTitle(Constantes.PROMPT);
        builder.setMessage("确定清除缓存？");
        builder.setPositiveButton(Constantes.DEFINE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                DataCleanManager.cleanApplicationData(AdminActivity.this);
                UtilTool.ClearData(AdminActivity.this);
                UtilTool.showToast(AdminActivity.this, "清除缓存成功");
            }
        });
        builder.setNegativeButton(Constantes.CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//
//            DialogBack();
//            return false;
//        }else {
//            return super.onKeyDown(keyCode, event);
//        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public  void DialogBack(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminActivity.this);
//        builder.setTitle(Constantes.PROMPT);
        builder.setMessage("您确定要退出自贸区通电？");
        builder.setPositiveButton(Constantes.DEFINE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                System.exit(0);
                FinishActivity.finishActivity();
                finish();
//                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        builder.setNegativeButton(Constantes.CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    /*
	 * 获取当前程序的版本号
	 */
    public String getVersion() {
        String versionname;
        //获取包管理者
        PackageManager pm=getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(getPackageName(), 0);
            versionname=info.versionName;
            return versionname;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "version load error";
        }
    }

    /*
     * 下载安装apk
     */
    private class DownloadApkTask implements Runnable{
        public void run() {
            try {
                File file= Update.DownLoadApk(updatase, handler);
                System.out.println("file   222"+file);
                //如果下载成功
                Message msg=new Message();
                msg.obj=file;
                msg.what=DOWNLOAD_SUCCESS;
                handler.sendMessage(msg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Message msg=new Message();
                msg.what=DOWNLOAD_ERROR;
                handler.sendMessage(msg);
            }
        }
    }
    @Override
    public void onStop() {
        Log.i("AdminActivity","onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("AdminActivity","onDestroy");
        super.onDestroy();
    }
    @Override
    public void onPause() {
        Log.i("AdminActivity","onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("AdminActivity","onResume");
        super.onResume();
    }
    public  void DialogUpdata(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminActivity.this);
//        builder.setTitle(Constantes.PROMPT);
        builder.setMessage("软件有更新，请更新使用");
        builder.setPositiveButton(Constantes.UPDATA, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                System.exit(0);
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(updatase.getPath());
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(Constantes.CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
