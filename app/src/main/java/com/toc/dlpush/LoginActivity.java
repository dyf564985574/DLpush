package com.toc.dlpush;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.toc.dlpush.dao.Configuration;
import com.toc.dlpush.getui.PushDemoReceiver;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.notices.util.UserJsonVo;
import com.toc.dlpush.setting.util.Updatas;
import com.toc.dlpush.tips.ProposeActivity;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.EditMatch;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.PhoneJson;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.User;
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
public class LoginActivity extends Activity {
    private Context mContext;
    private RelativeLayout rl_user;
    private Button mLogin;
    private Button register;
    TextView dialog_msg;
    String index;//判断是否以点过是
    String flag="1";//这是原始状态 1 不是自动登录  2代表自动登录
    String flag01;
    ImageView auto_login;
    UserJsonVo userJsonVo=new UserJsonVo();
    String cid="1";
    User user;
    private EditText account,password;
    ProgressBars progress;
    ProgressDialog progressDialog;
    String username;//用户名
    String pwd;//密码
    public static int path=0;
    private String update="0";
    private Updatas updatase;
    private int up;
//    String cid;
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
                    UtilTool.showToast(LoginActivity.this, "下载apk失败");
                    pd.dismiss();
                    break;
                case UPDATE_ERROR:
//                    UtilTool.showToast(WelcomeActivity.this, "此版本是最新版本");
                    break;
                case NEED_UPDATE:
                    // 从服务器下载最新的apk安卓
                    if (msg.obj == null) {
                        pd.setMessage("正在下载更新");
//                        new Thread(new DownloadApkTask()).start();
                    } else {
                        pd.setProgress((Integer) msg.obj);
                        pd.setMessage("正在下载更新" + ((Integer) msg.obj) + "%");
                    }

                    pd.show();
//
                    break;
                case 10:
                    UtilTool.showToast(LoginActivity.this, Constantes.NETERROR);
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
         Intent it = getIntent();
        up = it.getIntExtra("update",1);

        Constantes.aa=0;
        //c从数据库中获取Cid
        cid= UtilTool.getSharedPre(LoginActivity.this, "users", "cid", "1");
        //获取是否是自动登录
        flag= UtilTool.getSharedPre(LoginActivity.this, "users", "auto", "1");
        //获取用户名
        username= UtilTool.getSharedPre(LoginActivity.this, "users", "phone", "");
        //获取密码
        pwd= UtilTool.getSharedPre(LoginActivity.this, "users", "code", "");
        //update;
        update= UtilTool.getSharedPre(LoginActivity.this, "users", "update", "1");

        flag01=flag;
        progress=new ProgressBars(progressDialog,LoginActivity.this,Constantes.LANDING);

        setContentView(R.layout.activity_login);
        //判断是否同意过免责声明
        index=UtilTool.getSharedPre(LoginActivity.this,"user","relief","0");

        //是否出现免责声明
        if("0".equals(index)){
            MyDialog();
        }else {
            //不是第一次登陆 且安装了新版本

        }
        Log.i("MyDialog",update);
        if(("1".equals(update))) {
            UtilTool.setSharedPre(LoginActivity.this, "users", "update","2");
            this.deleteDatabase(Configuration.DB_NAME);
        }
        mContext=this;
        findView();
        init();
        //判断是不是自动登录
        if("2".equals(flag)){
            account.setText(username);
            password.setText(pwd);
            ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if(networkInfo==null||!networkInfo.isAvailable()){
                UtilTool.showToast(LoginActivity.this, Constantes.NETERROR);
            }else{
                //联网更新数据
                new GetLogin().execute();
            }
        }
//        if(up==2) {
//            SetUpdate();
//        }

    }
    private void findView(){
        rl_user=(RelativeLayout) findViewById(R.id.rl_user);
        mLogin=(Button) findViewById(R.id.login);

        account=(EditText) findViewById(R.id.account);
        account.setText(username);
        password=(EditText) findViewById(R.id.password);

    }
    //自动更新
    public void SetUpdate() {
        ConnectivityManager connectivityManager01 = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo01 = connectivityManager01.getActiveNetworkInfo();
        if (networkInfo01 == null || !networkInfo01.isAvailable()) {
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
            timer.schedule(task, 3000);

        } else {
            new Updata().execute();
        }
    }
    private void init(){
        Animation anim= AnimationUtils.loadAnimation(mContext, R.anim.login_anim);
        anim.setFillAfter(true);
        rl_user.startAnimation(anim);
        mLogin.setOnClickListener(loginOnClickListener);
    }
    /*
    * 登录
    */
    private View.OnClickListener loginOnClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if("1".equals(cid)){
                cid=PushDemoReceiver.cid;
            }
            if(EditMatch.allNull(account, LoginActivity.this)||EditMatch.allNull(password,LoginActivity.this)||EditMatch.notMoblie(account,LoginActivity.this)||EditMatch.notNumCont(password,6,24,LoginActivity.this)||(EditMatch.notMatchword(password,LoginActivity.this))){
                return;
            }
            username=account.getText().toString().trim();
            pwd=password.getText().toString().trim();
            ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if(networkInfo==null||!networkInfo.isAvailable()){
                UtilTool.showToast(LoginActivity.this, Constantes.NETERROR);
            }else{
                //联网更新数据
                new GetLogin().execute();
            }

        }
    };
    /**
     * 登陆提交
     * @yuanfei
     */
    private class GetLogin extends AsyncTask<Void, Void, UserJsonVo> {

        protected void onPreExecute() {

//            lt.show();
            progress.setProgressBar();
        }

        @Override
        protected UserJsonVo doInBackground(Void... paramArrayOfParams) {
            // TODO Auto-generated method stub
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return GetJson(Get());
        }
        @Override
        protected void onPostExecute(UserJsonVo result) {
            // TODO Auto-generated method stub
            if((null==result)||("".equals(result))){
                progress.delProgressBar();
//                lt.error();
                Toast.makeText(LoginActivity.this, Constantes.NOPHONE, Toast.LENGTH_SHORT).show();
                UtilTool.setSharedPre(LoginActivity.this, "users", "auto", "1");
            }else {
//                user=userJsonVo.getUser();
//                userJsonVo=result;
                if ((null != userJsonVo) && ("0".equals(userJsonVo.getError()))) {
                    progress.delProgressBar();
                    user = userJsonVo.getUser();
                    //如果登陆账号清除数据库 username 前一个账号  account 现账号
                    if(!(username.equals(account.getText().toString()))){
                        UtilTool.ClearData(LoginActivity.this);
                    }
                    Log.i("user", user + "");
                    SetShare();
                    progress.delProgressBar();
                    if("5".equals(user.getRoleid())){
                        path=1;
                        Intent it = new Intent(LoginActivity.this, ManagerActivity.class);
                        startActivity(it);
                    }else if("4".equals(user.getRoleid())){
                        path=2;
                        Intent it = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(it);
                    }else{
                        Intent it = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(it);
                    }
                    UtilTool.setSharedPre(LoginActivity.this, "users", "auto", "2");
                    finish();
                } else {
                    progress.delProgressBar();
                    Toast.makeText(LoginActivity.this, userJsonVo.getTip(), Toast.LENGTH_SHORT).show();
                    UtilTool.setSharedPre(LoginActivity.this, "users", "auto", "2");
                }
            }



        }
    }

    /*
    * 登录
    */
    private String Get() {
        String json = "";
        // 唯一设备ID
        if("1".equals(cid)){
            cid=PushDemoReceiver.cid;
        }
        try {
            Log.i("loadUrl",""+account.getText().toString()+" "+password.getText().toString()+"  "+ cid);
            String  loadUrl = RemoteURL.USER.LOGIN.replace("{phone}",username)
                    .replace("{passwd}",pwd).replace("{imei}",cid)
                    .replace("{phonetype}","0");
            //sig加密
            HashMap<String, String> sigParams = new HashMap<String, String>();
            sigParams.put("phone", username);
            sigParams.put("passwd", pwd);

            json = HttpUtil.getUrlWithSig(loadUrl, sigParams);// 从服务器获取数据

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("json ",json);
        return json;
    }
    /**
     * json数据的转换
     * @param json  json数据
     * @return
     */
    public UserJsonVo  GetJson(String json){
        UserJsonVo userJsonVoList=new UserJsonVo();
        if((null==json)||("".equals(json))){
//                lt.error();
            Toast.makeText(LoginActivity.this, Constantes.NOPHONE, Toast.LENGTH_SHORT).show();
            UtilTool.setSharedPre(LoginActivity.this, "users", "auto", "1");
        }else {
            userJsonVoList= FastjsonUtil.json2object(json, UserJsonVo.class);
            userJsonVo=userJsonVoList;
            Log.i("userJsonVoList",userJsonVoList+"");
            if(userJsonVoList.getError().equals("0")&& (userJsonVoList.getUser()!=null)&&("4".equals(userJsonVoList.getUser().getRoleid()))){
            GetPhoneList(GetPhoneJson());
            }
        }
        return userJsonVoList;
    }

    /**
     * 将个人信息记录到SharedPreferences中  方便在后面的界面中取用
     */
    public void SetShare(){
        //是否第一次登陆
        //记录在首选项
        //是不是区域经理
        UtilTool.setSharedPre(LoginActivity.this,"user","manager",user.getRoleid());
        UtilTool.setSharedPre(LoginActivity.this, "users", "phone",user.getPhone());
        UtilTool.setSharedPre(LoginActivity.this, "users", "code",password.getText().toString().trim());
        UtilTool.setSharedPre(LoginActivity.this, "users", "code", password.getText().toString());
        UtilTool.setSharedPre(LoginActivity.this, "users", "companyname", user.getCompanyname());
        UtilTool.setSharedPre(LoginActivity.this, "users", "linkman", user.getLinkman());
        UtilTool.setSharedPre(LoginActivity.this, "users", "uid", user.getId());
        UtilTool.setSharedPre(LoginActivity.this, "users", "isapp", user.getIsapp());
        UtilTool.setSharedPre(LoginActivity.this, "users", "cid", cid);
    }
    /**
     * 自定义dialog
     * 免责声明
     */
    public void MyDialog(){

        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.mydialog, null);
        final Dialog dialog = new AlertDialog.Builder(LoginActivity.this).create();
        dialog_msg= (TextView) layout.findViewById(R.id.dialog_msg);
        dialog_msg.setMovementMethod(ScrollingMovementMethod.getInstance());
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);
        //确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.dialog_ok);
        btnOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                UtilTool.setSharedPre(LoginActivity.this,"user","relief","1");
                dialog.dismiss();
            }
        });
        // 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                finish();
            }
        });
    }
    /**
     * 个推初始化
     */
    public void Gettui(){
        String packageName = getApplicationContext().getPackageName();
        ApplicationInfo appInfo;
        try {
            appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        PushManager.getInstance().initialize(this.getApplicationContext());
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetPhoneJson(){
        String json="";
        String loadUrl= RemoteURL.USER.MANAGERPHONE.replace("{id}",userJsonVo.getUser().getId());
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("id", userJsonVo.getUser().getId());
        sigParams.put("grtmanagerphone", "grtmanagerphone09190");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json",json);
        return json;
    }
    public void GetPhoneList(String json){
        PhoneJson phoneJson=new PhoneJson();
        if((null==json)||("".equals(json))){

        }else{
            phoneJson = FastjsonUtil.json2object(json,
                    PhoneJson.class);
            Log.i("notifyJsonVoList",phoneJson+"");
            if((null==phoneJson.getError())||("1".equals(phoneJson.getError()))){
                UtilTool.showToast(LoginActivity.this,"网络异常");
            }else{

                    UtilTool.setSharedPre(LoginActivity.this,"user","managerphone",phoneJson.getManagerphone());
            }
        }

    }
    //更新软件
    public class Updata extends AsyncTask<Void, Void, Updatas> {
        @Override
        protected void onPostExecute(Updatas updatas) {
            super.onPostExecute(updatas);
            if ((null == updatas.getError()) || ("1".equals(updatas.getError()))) {
                UtilTool.showToast(LoginActivity.this, Constantes.NETERROR);
            } else {
                //判断版本号是否相同
                //getVersion()
                Log.i("getVersion",getVersion()+"  "+getVersion().substring(0, 3)+"  "+updatas.getVersion().substring(0, 3)+"  "+getVersion().substring(getVersion().length()-1,getVersion().length())+"   "+updatas.getVersion().substring(updatas.getVersion().length()-1,updatas.getVersion().length())+"  "+getVersion().length());
                if (getVersion() != null && updatas.getVersion() != null) {
                    if (getVersion().substring(0, 3).equals(updatas.getVersion().substring(0, 3))) {
                        if(getVersion().substring(getVersion().length()-1,getVersion().length()).equals(updatas.getVersion().substring(updatas.getVersion().length()-1,updatas.getVersion().length()))){
//                            UtilTool.showToast(WelcomeActivity.this,"已是最新版本");
//                            Intent intent;
//                            intent = new Intent(mContext, LoginActivity.class);
//                            startActivity(intent);
//                            finish();
                            Log.i("gengxin","gengxin111111");
                        }else{
                            Log.i("gengxin","gengxin");
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
   * 获取当前程序的版本号
   */
    public String getVersion() {
        String versionname;
        //获取包管理者
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            versionname = info.versionName;
            return "1.0.0";
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "version load error";
        }
    }
    public  void DialogUpdata() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//        builder.setTitle(Constantes.PROMPT);
        builder.setMessage("软件有更新，请更新使用");
        builder.setPositiveButton(Constantes.UPDATA, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                System.exit(0);

                //通知主线程更新
                Intent intent = new Intent();
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
//                Intent intent;
//                intent = new Intent(mContext, LoginActivity.class);
//                startActivity(intent);
//                finish();
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
