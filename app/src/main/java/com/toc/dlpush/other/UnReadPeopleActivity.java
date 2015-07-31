package com.toc.dlpush.other;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.ManagerUserAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.util.ClientJson;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.User;
import com.toc.dlpush.util.UserJson;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/16.
 */
public class UnReadPeopleActivity extends Activity{
    String uid;
    CustomListView listView;
    ManagerUserAdapter adapter;
    List<User> userList = new ArrayList<User>();
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
    String nid="0";//通知的id
    RelativeLayout unread_back;
    private ImageView water;//水印图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unread_main);
        Intent it=getIntent();
        nid=it.getStringExtra("nid");
        uid= UtilTool.getSharedPre(UnReadPeopleActivity.this, "users", "uid", "0");
        inview();
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(UnReadPeopleActivity.this, Constantes.NETERROR);
        }else{
                new GetUnRead().execute();
            }
    }

    /**
     * 实例化控件
     */
    public void inview(){
        water = (ImageView) findViewById(R.id.water);
        UtilTool.SetView(UnReadPeopleActivity.this,water,2);
        unread_back = (RelativeLayout) findViewById(R.id.unread_back);
        unread_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView= (CustomListView) findViewById(R.id.unread_list);
        adapter = new ManagerUserAdapter(UnReadPeopleActivity.this,userList);
        listView.setAdapter(adapter);
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(UnReadPeopleActivity.this,UserDetailActivity.class);
                int idx=position-1;
                User user=new User();
                user=userList.get(idx);
                Bundle bundle=new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //下拉刷新
       listView.setOnRefreshListener(new CustomListView.OnRefreshListener() {
           @Override
           public void onRefresh() {
               ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
               NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
               if(networkInfo==null||!networkInfo.isAvailable()){
                   UtilTool.showToast(UnReadPeopleActivity.this, Constantes.NETERROR);
                   listView.onRefreshComplete();

               }else{
                   //联网更新数据
                   new GetRefreshDatas().execute();
               }
           }
       });
        listView.setOnLoadListener(new CustomListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(UnReadPeopleActivity.this, Constantes.NETERROR);
                    listView.onLoadMoreComplete(true,"1");
                }else{
                    //联网更新数据
                    new GetLoadDatas().execute();
                }
            }
        });
    }
    /**
     * 刚进入的时候 加载数据
     */
    private class GetUnRead extends AsyncTask<Void,Void,UserJson> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected UserJson doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(UserJson clientJson) {
            super.onPostExecute(clientJson);
            if((null!=clientJson.getError())&& ("0".equals(clientJson.getError()))){
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                userList=clientJson.getUserlist();
                Log.i("userList123", userList + "");
                if(userList.size()==0){
                    listView.setBackgroundColor(getResources().getColor(R.color.transparent));
                }else{
                    listView.setBackgroundColor(getResources().getColor(R.color.white));
                }
                adapter.notifyDataSetChangedEx(userList);
                if(userList.size()<8){
                    listView.onLoadMoreComplete(true,"1");
                }
            }else{

            }

        }
    }

    /*
         * 下拉刷新
         * 异步加载数据
         */
    private class GetRefreshDatas extends AsyncTask<Void, Void, UserJson> {

        @Override
        protected UserJson doInBackground(Void... params) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            offset ="0";
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(UserJson result) {
            super.onPostExecute(result);
            if (result.getError() != null && result.getUserlist().size() > 0) {
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                userList.clear();
                userList.addAll(result.getUserlist());
                System.out.println("下拉刷新");

                adapter.notifyDataSetChangedEx(userList);
                if(userList.size()<8){
                    listView.onLoadMoreComplete(true,"1");
                }
                listView.onRefreshComplete(); // 加载更多完成
            } else {
                listView.onRefreshComplete(); // 加载更多完成
            }
        }
    }
    /*
  	 * 上拉加载更多
  	 * 异步加载数据
  	 */
    private class GetLoadDatas extends AsyncTask<Void, Void, UserJson> {

        @Override
        protected UserJson doInBackground(Void... params) {

            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(UserJson result) {
            super.onPostExecute(result);

            if (result.getError() != null && result.getUserlist().size() > 0) {
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
//  				listNews.clear();

                userList.addAll(result.getUserlist());
                System.out.println("上拉刷新");
                adapter.notifyDataSetChangedEx(userList);
                listView.onLoadMoreComplete(true, "0");

                System.out.println("上拉=====");
            } else {
                System.out.println("上拉+++++");
                listView.onLoadMoreComplete(true, "1");

            }
        }
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetJson(){
        String json="";
        String loadUrl= RemoteURL.USER.READ.replace("{uid}",uid).replace("{nid}",nid).replace("{isread}","1")
                .replace("{offset}",offset).replace("{length}","10");
//                .replace("{id}",uid).replace("{offset}",offset)
//                .replace("{length}","10");
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("uid", uid);
        sigParams.put("nid", nid);
        try {
            json= HttpUtil.getUrlWithSig(loadUrl,  sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json ", json);
        return json;
    }
    //json数据解析
    public UserJson GetList(String json){

        UserJson clientJson=new UserJson();
        if(("".equals(json))||(null==json)){
            return clientJson;
        }else {
            clientJson = FastjsonUtil.json2object(json,
                    UserJson.class);
            Log.i("caringJson1", clientJson + "");
            return clientJson;
        }

    }
}
