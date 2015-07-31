package com.toc.dlpush;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.igexin.sdk.PushManager;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.setting.util.Updatas;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.Update;
import com.toc.dlpush.util.UtilTool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 袁飞 on 2015/6/3.
 * DLpush
 */
public class WelcomeActivity extends Activity {
    protected static final String TAG = "WelcomeActivity";
    private Context mContext;
    private ImageView mImageView;
    private Updatas updatase;
    private ProgressDialog pd;
    //升级
    public static final int NEED_UPDATE = 1;
    public static final int UPDATE_ERROR = 0;
    public static final int DOWNLOAD_SUCCESS = 2;
    public static final int DOWNLOAD_ERROR = 3;
    public static final int NEED_UPDATE1 = 4;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    pd.dismiss();
                    //安装下载好的apk
                    File file = (File) msg.obj;
                    //调用系统的apk安装器完成安装
                    System.out.println("file  " + file);
                    if (file != null && !file.equals("")) {
                        install(file);
                    }
                    break;
                case DOWNLOAD_ERROR:
                    UtilTool.showToast(WelcomeActivity.this, "下载apk失败");
                    pd.dismiss();
                    break;
                case UPDATE_ERROR:
//                    UtilTool.showToast(WelcomeActivity.this, "此版本是最新版本");
                    break;
                case NEED_UPDATE:
                    // 从服务器下载最新的apk安卓
                    if (msg.obj == null) {
                        pd.setMessage("正在下载更新");
                        new Thread(new DownloadApkTask()).start();
                    } else {
                        pd.setProgress((Integer) msg.obj);
                        pd.setMessage("正在下载更新" + ((Integer) msg.obj) + "%");
                    }

                    pd.show();
//
                    break;
                case 10:
                    UtilTool.showToast(WelcomeActivity.this, Constantes.NETERROR);
                    break;
                case NEED_UPDATE1 :

            }
        }
    };

    /*根据下载好的apk进行安装
     */
    private void install(File file) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
//		intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");//mime数据类型
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_welcome);
        mContext = this;
        findview();
        Gettui();
        SetUpdate();
//        init();
    }
    public void SetUpdate(){
        ConnectivityManager connectivityManager01 = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo01 = connectivityManager01.getActiveNetworkInfo();
        if (networkInfo01 == null || !networkInfo01.isAvailable()) {
//            UtilTool.showToast(WelcomeActivity.this, Constantes.NETERROR);
            Message msg = new Message();
            msg.what = 10;
            handler.sendMessage(msg);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    SetUpdate();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task,3000);

        } else {
            new Updata().execute();
        }
    }
    public void findview() {
        pd=new ProgressDialog(this);
        mImageView = (ImageView) findViewById(R.id.welcome_img);
    }

    private void init() {
        mImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra("update",2);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    /**
     * 个推初始化
     */
    public void Gettui() {
        String packageName = getApplicationContext().getPackageName();
        ApplicationInfo appInfo;
        try {
            appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PushManager.getInstance().initialize(this.getApplicationContext());
    }

    //更新软件
    public class Updata extends AsyncTask<Void, Void, Updatas> {
        @Override
        protected void onPostExecute(Updatas updatas) {
            super.onPostExecute(updatas);
            if ((null == updatas.getError()) || ("1".equals(updatas.getError()))) {
                UtilTool.showToast(WelcomeActivity.this, Constantes.NETERROR);
            } else {
                //判断版本号是否相同
                //getVersion()
                Log.i("getVersion",getVersion()+"  "+getVersion().substring(0, 3)+"  "+updatas.getVersion().substring(0, 3)+"  "+getVersion().substring(getVersion().length()-1,getVersion().length())+"   "+updatas.getVersion().substring(updatas.getVersion().length()-1,updatas.getVersion().length())+"  "+getVersion().length());
                if (getVersion() != null && updatas.getVersion() != null) {
                    if (getVersion().substring(0, 3).equals(updatas.getVersion().substring(0, 3))) {
                        if(getVersion().substring(getVersion().length()-1,getVersion().length()).equals(updatas.getVersion().substring(updatas.getVersion().length()-1,updatas.getVersion().length()))){
//                            UtilTool.showToast(WelcomeActivity.this,"已是最新版本");
                            Intent intent;
                            intent = new Intent(mContext, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            updatase = updatas;
                            DialogUpdata();
                        }

                    } else {
                        Intent intent= new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(updatas.getPath());
                        intent.setData(content_url);
                        startActivity(intent);
                    }
                }
            }
        }

        @Override
        protected Updatas doInBackground(Void... params) {
            return GetUpdataList(GetUpdataJson());
        }
    }

    public String GetUpdataJson() {
        String json = "";
        String loadUrl = RemoteURL.USER.UPDATA.replace("{phonetype}", "0");
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("apkUpdateCheck1123", "12302390");
        sigParams.put("apkUpdateCheck2233", "99081232");
        try {
            json = HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
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

    /*
    * 下载安装apk
    */
    private class DownloadApkTask implements Runnable {
        public void run() {
            try {
                File file = Update.DownLoadApk(updatase, handler);
                System.out.println("file   222" + file);
                //如果下载成功
                Message msg = new Message();
                msg.obj = file;
                msg.what = DOWNLOAD_SUCCESS;
                handler.sendMessage(msg);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Message msg = new Message();
                msg.what = DOWNLOAD_ERROR;
                handler.sendMessage(msg);
            }
        }
    }

    /*
    * 获取当前程序的版本号
    */
    public String getVersion() {
        String versionname;
        //获取包管理者
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            versionname = info.versionName;
            return versionname;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "version load error";
        }
    }
    public  void DialogUpdata(){
        AlertDialog.Builder builder=new AlertDialog.Builder(WelcomeActivity.this);
//        builder.setTitle(Constantes.PROMPT);
        builder.setMessage("软件有更新，请更新使用");
        builder.setPositiveButton(Constantes.UPDATA, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                System.exit(0);

                //通知主线程更新
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(updatase.getPath());
                intent.setData(content_url);
                startActivity(intent);

//                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        builder.setNegativeButton(Constantes.CANCLE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
