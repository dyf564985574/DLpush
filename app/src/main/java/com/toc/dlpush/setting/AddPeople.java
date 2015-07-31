package com.toc.dlpush.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.setting.util.Contact;
import com.toc.dlpush.setting.util.One_Contact;
import com.toc.dlpush.setting.util.Return;
import com.toc.dlpush.setting.util.Userlink;
import com.toc.dlpush.setting.util.UserlinkDao;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.EditMatch;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.ProgressBars;
import com.toc.dlpush.util.UtilTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 袁飞 on 2015/5/23.
 * DLpush
 * 添加联系人
 * 1、当数据库中有一位联系人时输入框的末尾的图标显示的事删除状态
 * 2、当数据库中没有联系人时  输入框输入到十一位手机号时  末尾的图片自动变为确定图标
 * 3、当要修改联系人时   监听Edittext 当Edittext中的数据变化时  末尾的图标变为确定图标
 * 4、删除、修改、添加联系人的时候先上传到后台 后台保存数据后  手机端再保存到数据库
 *
 *
 */
public class AddPeople extends Activity implements View.OnClickListener {
    //三个末尾图标
    ImageView set_add01;
    ImageView set_add02;
    ImageView set_add03;
    //三个输入框
    TextView set_add_ed01;
    TextView set_add_ed02;
    TextView set_add_ed03;
    //判断三个图标的状态
    Boolean add_delete01=false;
    Boolean add_delete02=false;
    Boolean add_delete03=false;
    //判断开始时是否有数据
    Boolean data01=false;
    Boolean data02=false;
    Boolean data03=false;
    //电话号码
    String phone;
    UserlinkDao dao;
    //联系人集合
    List<Userlink>userlinks=new ArrayList<Userlink>();
    Boolean Hasdata;
    int find;//1代表第一个按钮  2代表第二个按钮 3代表第三个按钮
    String phoneId;//联系人的ID
    RelativeLayout set_add_relativeLayout;
    int index;//0代表从设置界面跳转过来的   1 代表从通知详情跳转过来的
    RelativeLayout add_notices;
    ProgressBars progress;
    ProgressDialog progressDialog;
    String uid;//用户的ID
//    String []



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        FinishActivity.addActivity(this);
        setContentView(R.layout.set_add_contacts);
        Empty_data();
        Intent it=getIntent();
        index=it.getIntExtra("index",0);
        uid=UtilTool.getSharedPre(this,"users","uid","0");
        init();
//        editAddText();
//
        Hasdata= GetUserlink();
        if(!Hasdata){
            ConnectivityManager connectivityManager=(ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if(networkInfo==null||!networkInfo.isAvailable()){
                UtilTool.showToast(AddPeople.this, Constantes.NETERROR);
            }else{
                //开始获取
                new GetData().execute();
            }
        }



    }

    /**
     * 初始化控件
     */
    public void init(){
        if(index==1){
            add_notices.setVisibility(View.VISIBLE);
        }
        add_notices.setOnClickListener(this);
        set_add_relativeLayout= (RelativeLayout) findViewById(R.id.set_add_relativeLayout);
        set_add_relativeLayout.setOnClickListener(this);
        set_add01= (ImageView) findViewById(R.id.set_add01);
        set_add01.setOnClickListener(this);
        set_add02= (ImageView) findViewById(R.id.set_add02);
        set_add02.setOnClickListener(this);
        set_add03= (ImageView) findViewById(R.id.set_add03);
        set_add03.setOnClickListener(this);
        set_add_ed01= (TextView) findViewById(R.id.set_add_ed01);
        set_add_ed02= (TextView) findViewById(R.id.set_add_ed02);
        set_add_ed03= (TextView) findViewById(R.id.set_add_ed03);
        progress=new ProgressBars(progressDialog, this, Constantes.SUBMITTING);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_add01:
                Log.i("add_delete01",add_delete01+"   "+data01);
                find=1;
                phone = set_add_ed01.getText().toString();
                if(add_delete01&&data01){


                        dialog("是否修改数据", 1);


                    //是否修改数据
                }else if(add_delete01&&!data01){

                        dialog("是否添加数据", 2);


                    //是否添加数据
                }else if(!add_delete01&&data01){
//
                    dialog("是否删除数据", 3);

//                       }
                    //是否删除数据
                }else{
                    //清空输入框数据
                    set_add_ed01.setText("");
                }
                break;
            case R.id.set_add02:
                Log.i("add_delete02",add_delete02+"");
                find=2;
                phone = set_add_ed02.getText().toString();
                //是否修改数据
                if(add_delete02&&data02){

                        dialog("是否修改数据", 1);



                    //是否添加数据
                }else if(add_delete02&&!data02){


                        dialog("是否添加数据", 2);
                        phone = set_add_ed02.getText().toString();



                }else if(!add_delete02&&data02){
//                       if(EditMatch.notMoblie(set_add_ed02)){
//                           return;
//                       }else {
                    phone = set_add_ed02.getText().toString();
                    phoneId = userlinks.get(1).getId();
                    dialog("是否删除数据", 3);
//                       }
                    //是否删除数据
                }else{
                    //清空输入框数据
                    set_add_ed02.setText("");
                }
                break;
            case R.id.set_add03:
                Log.i("add_delete01",add_delete03+"   "+data03);
                find=3;
                phone = set_add_ed03.getText().toString();
                if(add_delete03&&data03){

                        phone = set_add_ed03.getText().toString();
                        phoneId = userlinks.get(2).getId();
                        dialog("是否修改数据", 1);


                    //是否修改数据
                }else if(add_delete03&&!data03){

                        phone = set_add_ed03.getText().toString();
                        dialog("是否添加数据", 2);


                    //是否添加数据
                }else if(!add_delete03&&data03){
//                       if(EditMatch.notMoblie(set_add_ed03)){
//                           return;
//                       }else {
                    phone = set_add_ed03.getText().toString();
                    dialog("是否删除数据", 3);
                    phoneId = userlinks.get(2).getId();
//                       }
                    //是否删除数据
                }else{
                    //清空输入框数据
                    set_add_ed03.setText("");
                }
                break;
            case R.id.set_add_relativeLayout:
                finish();

                FinishActivity.deleteActivity(this);
                break;
        }
    }
    //弹出窗
    public void dialog(String content,int index){
        final int aa=index;
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
//        builder.setMessage(content);
//        builder.setTitle("提示");
//        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
        if(aa==2){
            new GetAdd().execute();
        }else if(aa==1){
            new GetRevise().execute();
        }else if(aa==3){
            new DeleteData().execute();
            //删除联系人
        }
//            }
//        });
//
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//              @Override
//            public void onClick(DialogInterface dialog, int which) {
//                 dialog.dismiss();
//               }
//           });
//         builder.create().show();
    }




    //添加联系人数据
    private class GetAdd extends AsyncTask<Void,Void,One_Contact> {
        @Override
        protected void onPreExecute() {
            progress.setProgressBar();
        }

        @Override
        protected One_Contact doInBackground(Void... params) {
            return GetAddList(GetAddJson());
        }

        @Override
        protected void onPostExecute(One_Contact res) {
            super.onPostExecute(res);
            progress.delProgressBar();
            if ((null!=res.getError())&&("0".equals(res.getError()))){
                SetBackground(find);
                Add_contact(res.getUserlink());
                GetUserlink();
                UtilTool.showToast(AddPeople.this,"添加成功");


            }else{
                if(res.getTip()!=null){
                    UtilTool.showToast(AddPeople.this,res.getTip());
                }else {
                    UtilTool.showToast(AddPeople.this, "添加失败");
                }
            }
        }
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetAddJson(){
        String json="";
        Log.i("json",phone);
        String loadUrl= RemoteURL.USER.Add_Contact.replace("{uid}",uid).replace("{phone}",phone);
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("uid", uid);
        sigParams.put("saveUserLink", "saveUserLink");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if((null==json)||("".equals(json))){
            return "";
        }else{
            android.util.Log.i("json",json);
            return json;
        }

    }
    public One_Contact GetAddList(String json){
        One_Contact res=new One_Contact();
        if((null==json)||("".equals(json))){
            return res;
        }else {
            res = FastjsonUtil.json2object(json,
                    One_Contact.class);
            Log.i("One_Contact", res + "");
            return res;
        }
    }



    //修改联系人数据
    private class GetRevise extends AsyncTask<Void,Void,Return> {
        @Override
        protected void onPreExecute() {
            progress.setProgressBar();
        }

        @Override
        protected Return doInBackground(Void... params) {
            return GetReviseList(GetReviseJson());
        }

        @Override
        protected void onPostExecute(Return res) {
            super.onPostExecute(res);
            progress.delProgressBar();
            if ((null!=res.getError())&&("0".equals(res.getError()))){
                SetBackground(find);
                UtilTool.showToast(AddPeople.this,"修改成功");
                Revise_contact();
                GetUserlink();
            }else{
                if(res.getTip()!=null){
                    UtilTool.showToast(AddPeople.this,res.getTip());
                }else {
                    UtilTool.showToast(AddPeople.this, "修改失败");
                }
            }

        }
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetReviseJson(){
        String json="";
        Log.i("json",phoneId);
        String loadUrl= RemoteURL.USER.Revise_Contact.replace("{id}",phoneId).replace("{phone}",phone);
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("id", phoneId);
        sigParams.put("updateuserlink", "updateuserlink");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if((null==json)||("".equals(json))){
            return "";
        }else{
            android.util.Log.i("json",json);
            return json;
        }

    }
    public Return GetReviseList(String json){
        Return res=new Return();
        if((null==json)||("".equals(json))){
            return res;
        }else {
            res = FastjsonUtil.json2object(json,
                    Return.class);
            Log.i("Return", res + "");
            return res;
        }
    }



    //获取联系人数据
    private class GetData extends AsyncTask<Void,Void,Contact> {
        @Override
        protected void onPreExecute() {
            progress.setProgressBar();
        }

        @Override
        protected Contact doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(Contact res) {
            super.onPostExecute(res);
            progress.delProgressBar();
            if (null!=res.getError()&&("0".equals(res.getError()))){

                for (int i=0;i<userlinks.size();i++){
                    if((null!=userlinks.get(i))&&(i==0)){
                        Empty_data();
                        Log.i("data01",add_delete01+"");
                        set_add_ed01.setText(userlinks.get(i).getLinkphone());
                        SetBackground(1);
                        data01=true;
                        add_delete01=false;
                    }else if((null!=userlinks.get(i))&&(i==1)){
                        set_add_ed02.setText(userlinks.get(i).getLinkphone());
                        SetBackground(2);
                        data02=true;
                        add_delete02=false;
                    }else if((null!=userlinks.get(i))&&(i==2)){
                        set_add_ed03.setText(userlinks.get(i).getLinkphone());
                        SetBackground(3);
                        data03=true;
                        add_delete03=false;
                    }

                    Add_contact(userlinks.get(i));

                }


            }else{
                UtilTool.showToast(AddPeople.this,"获取联系人失败");
            }
        }
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetJson(){
        String json="";
        String loadUrl= RemoteURL.USER.Contact.replace("{uid}", uid);
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("uid", uid);
        sigParams.put("findAllByUid", "findAllByUid");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if((null==json)||("".equals(json))){
            return "";
        }else{
            android.util.Log.i("json",json);
            return json;
        }

    }
    public Contact GetList(String json){
        Contact res=new Contact();
        if((null==json)||("".equals(json))){
            return res;
        }else {
            res = FastjsonUtil.json2object(json,
                    Contact.class);
            userlinks = res.getLinklist();
            Log.i("Return", res + "");
            return res;
        }
    }




    //删除联系人数据
    private class DeleteData extends AsyncTask<Void,Void,Return> {
        @Override
        protected void onPreExecute() {
            progress.setProgressBar();
        }

        @Override
        protected Return doInBackground(Void... params) {
            return DeleteList(DeleteJson());
        }

        @Override
        protected void onPostExecute(Return res) {
            super.onPostExecute(res);
            progress.delProgressBar();
            if ((null!=res.getError())&&("0".equals(res.getError()))){
                DeleteEdittext(find);
                UtilTool.showToast(AddPeople.this,"删除成功");
                Delete_contact(phoneId);
                GetUserlink();
            }else{
                UtilTool.showToast(AddPeople.this,"删除失败");
            }
        }
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String DeleteJson(){
        String json="";
        Log.i("json",phoneId);
        String loadUrl= RemoteURL.USER.Delete_Contact.replace("{id}", phoneId).replace("{phone}",phone);
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("id", phoneId);
        sigParams.put("deleteUserLink", "deleteUserLink");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if((null==json)||("".equals(json))){
            return "";
        }else{
            android.util.Log.i("json",json);
            return json;
        }

    }
    public Return DeleteList(String json){
        Return res=new Return();
        if((null==json)||("".equals(json))){
            return res;
        }else {
            res = FastjsonUtil.json2object(json,
                    Return.class);
            Log.i("Return", res + "");
            return res;
        }
    }



    //删除数据库中的联系人
    public void Delete_contact(String id){
        dao=new UserlinkDao(this);
        dao.delete("user_link","id=?",new String[]{id});
        dao.close();
    }
    //清空数据库
    public void Empty_data(){
        dao=new UserlinkDao(this);
        dao.delete("user_link","id=?",new String[]{"*"});
        dao.close();
    }
    //修改数据库中的联系人
    public void Revise_contact(){
        dao=new UserlinkDao(this);
        ContentValues values=new ContentValues();
        values.put("linkphone",phone);
        dao.update("user_link",values,"id=?",new String[]{phoneId});
        dao.close();

    }
    //向数据库添加联系人
    public  void Add_contact(Userlink userlink){
        ContentValues values=new ContentValues();
        values.put("id",userlink.getId());
        values.put("uid",userlink.getUid());
        values.put("linkphone",userlink.getLinkphone());
        dao=new UserlinkDao(this);
        dao.insert("user_link",values);
        dao.close();
    }


    /**
     * 设置三个小图标的图
     * @param find  代表是第几个图标
     */
    public void SetBackground(int find){
        if(find==1){
            Drawable drawable= getResources().getDrawable(R.mipmap.delete_contacts);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            set_add01.setImageDrawable(drawable);
            data01=true;
            add_delete01=false;
        }else if(find==2){
            set_add02.setVisibility(View.VISIBLE);
            Drawable drawable= getResources().getDrawable(R.mipmap.delete_contacts);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            set_add02.setImageDrawable(drawable);
            data02=true;
            add_delete02=false;
        }else if(find==3){
            Drawable drawable= getResources().getDrawable(R.mipmap.delete_contacts);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            set_add03.setImageDrawable(drawable);
            data03=true;
            add_delete03=false;
        }
    }
    //读取联系人
    public Boolean GetUserlink(){
        UserlinkDao dao=new UserlinkDao(AddPeople.this);
        userlinks=dao.queryList(Userlink.class,"user_link",new String[]{"*"},null,null,"id",null,null);
        dao.close();
        if(userlinks.size()==0){
            return false;
        }else{
            Setview();
            return true;

        }


    }
    /**
     * 设置三个小图标的图
     * @param find  代表是第几个图标
     */
    public void DeleteEdittext(int find){
        if(find==1){
            data01=false;
            set_add_ed01.setText("");
        }else if(find==2){
            data02=false;
            set_add_ed02.setText("");
        }else if(find==3){
            data03=false;
            set_add_ed03.setText("");
        }
    }
    public void Isphone(String number){

    }
    public void Setview(){
        Boolean a1=false;
        Boolean a2=false;
        Boolean a3=false;
        set_add_ed01.setText("");
        set_add_ed02.setText("");
        set_add_ed03.setText("");
        for (int i=0;i<userlinks.size();i++){
            if((null!=userlinks.get(i))&&(i==0)){
//                Empty_data();
                a1=true;
                Log.i("data01",add_delete01+"");
                set_add_ed01.setText(userlinks.get(i).getLinkphone());
                SetBackground(1);
                data01=true;
                add_delete01=false;
            }else if((null!=userlinks.get(i))&&(i==1)){
                set_add_ed02.setText(userlinks.get(i).getLinkphone());
                SetBackground(2);
                a2=true;
                data02=true;
                add_delete02=false;
            }else if((null!=userlinks.get(i))&&(i==2)){
                set_add_ed03.setText(userlinks.get(i).getLinkphone());
                SetBackground(3);
                a3=true;
                data03=true;
                add_delete03=false;
            }
            if(!a1){
                data01=false;
                add_delete01=false;
                data02=false;
                add_delete02=false;
                data03=false;
                add_delete03=false;
            }
            if(!a2){
                data02=false;
                add_delete02=false;
                data03=false;
                add_delete03=false;
            }
            if(!a3){
                data03=false;
                add_delete03=false;
            }
//            Add_contact(userlinks.get(i));

        }
    }
    public Boolean Different(String phones){
        for (int i=0;i<userlinks.size();i++){
            if(userlinks.get(i).getLinkphone().equals(phones)){
                UtilTool.showToast(AddPeople.this,"输入手机号已保存，请重新输入！！！");
                return true;
            }
        }
        return false;

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
