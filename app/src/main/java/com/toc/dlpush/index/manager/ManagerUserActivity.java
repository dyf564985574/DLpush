package com.toc.dlpush.index.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.ManagerUserAdapter;
import com.toc.dlpush.adapter.SearchAdapter;
import com.toc.dlpush.caring.util.Caring;
import com.toc.dlpush.caring.util.CaringJson;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.other.UserDetailActivity;
import com.toc.dlpush.util.ClientJson;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.CustomLinerLayout;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.NewsDao;
import com.toc.dlpush.util.User;
import com.toc.dlpush.util.UserDao;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.BackHandledFragment;
import com.toc.dlpush.view.CustomListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/8.
 */
public class ManagerUserActivity extends BackHandledFragment implements View.OnClickListener {
    View rootView;
    Context context;
    String uid;
    CustomListView listView;
    ManagerUserAdapter adapter;
    List<User> userList = new ArrayList<User>();
    List<User> userList01 = new ArrayList<User>();
    UserDao dao;
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
    LinearLayout message_one;//模糊查询框
    FrameLayout message_two;//主页面
    LinearLayout message_three;//模糊查询列表
    Button message_search;//模糊查询按钮
    EditText message_TexSearch;//模糊输入框
    Button  message_BtnCancle;//取消按钮
//    RelativeLayout user_screen;
    ListView message_Three_list;
    SearchAdapter adapter01;
    LinearLayout manager_title;
    private float y;
    private int height;
    boolean hadIntercept=false;
    private ImageView water;//水印图片
    private RelativeLayout user_screen;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.manager_user,container, false);
        user_screen = (RelativeLayout) rootView.findViewById(R.id.user_screen);
        UtilTool.SetTitle(getActivity(), user_screen );
        context=getActivity();
        uid= UtilTool.getSharedPre(getActivity(), "users", "uid", "0");
        inview();
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(context, Constantes.NETERROR);
            GetUserlink();
            offset =String.valueOf(userList.size());
            adapter.notifyDataSetChangedEx(userList);
            listView.onLoadMoreComplete(true,"1");
        }else{
            if(GetUserlink()){
                adapter.notifyDataSetChangedEx(userList);
                offset =String.valueOf(userList.size());
                listView.onLoadMoreComplete(true,"1");
            }else {
                new GetCaring().execute();
            }
            }
        return rootView;
    }
    public void inview(){
        manager_title= (LinearLayout) rootView.findViewById(R.id.manager_title);
        message_Three_list= (ListView) rootView.findViewById(R.id.message_Three_list);
        water = (ImageView) rootView.findViewById(R.id.water);
        UtilTool.SetView(getActivity(),water,2);
        adapter01=new SearchAdapter(getActivity(),userList01);
        message_Three_list.setAdapter(adapter01);
        message_Three_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),UserDetailActivity.class);
                Log.i("position",+position+"");
                int idx=position;
                User user=new User();
                user=userList01.get(idx);
                Bundle bundle=new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
//        user_screen= (RelativeLayout) rootView.findViewById(R.id.user_screen);
        message_one= (LinearLayout) rootView.findViewById(R.id.message_one);
        message_two= (FrameLayout) rootView.findViewById(R.id.message_two);
        message_three= (LinearLayout) rootView.findViewById(R.id.message_three);
        message_BtnCancle= (Button) rootView.findViewById(R.id.message_BtnCancle);
        message_BtnCancle.setOnClickListener(this);
        message_TexSearch= (EditText) rootView.findViewById(R.id.message_TexSearch);
        message_search= (Button) rootView.findViewById(R.id.message_search);
        message_search.setOnClickListener(this);
        listView= (CustomListView) rootView.findViewById(R.id.manager_user_list);
        adapter = new ManagerUserAdapter(getActivity(),userList);
        listView.setAdapter(adapter);
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(),UserDetailActivity.class);
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
                // TODO Auto-generated method stub
                ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(context, Constantes.NETERROR);
                    listView.onRefreshComplete();
                }else{
                    //联网更新数据
                    new GetRefreshDatas().execute();
                }
            }
//                list.clear();
//
//                adapter.notifyDataSetChangedEx(list);
//                listView.onRefreshComplete();

        });

        //上拉刷新
        listView.setOnLoadListener(new CustomListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(context, Constantes.NETERROR);
                    listView.onLoadMoreComplete(true,"1");
                }else{
                    //联网更新数据
                    new GetLoadDatas().execute();
                }

            }
        });
        /**
         * 模糊查询输入框的监听
         */
        message_TexSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(null == message_TexSearch || "".equals(message_TexSearch)){
                    UtilTool.showToast(getActivity(), Constantes.CATEGORIES);
                }else{
                    String text=message_TexSearch.getText().toString();
//					UtilTool.showToast(getActivity(), text);
                    Boolean conn=UtilTool.Connectivity(getActivity());
                    if(conn){//有网
                        new SearchUserByRealNameTask().execute(text);
                    }else{
                        UtilTool.showToast(getActivity(), Constantes.NETERROR);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.message_search:

                hadIntercept=true;
//                y = message_search.getY();
                height = user_screen.getHeight();
                Log.i("height",y + "   " + height);
                TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -height);
                animation.setDuration(500);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        message_BtnCancle.setVisibility(View.VISIBLE);
                        user_screen.setVisibility(View.GONE);
                        message_two.setVisibility(View.GONE);
                        message_one.setVisibility(View.GONE);
                        message_three.setVisibility(View.VISIBLE);


                    }
                });
                manager_title.startAnimation(animation);
//                UtilTool.translateAnimations(manager_title,false);
                UtilTool.closeInputMethod(message_TexSearch,true);
                String text=message_TexSearch.getText().toString();
//					UtilTool.showToast(getActivity(), text);
                Boolean conn=UtilTool.Connectivity(getActivity());
                if(conn){//有网
                    new SearchUserByRealNameTask().execute("");
                }else{
                    UtilTool.showToast(getActivity(), Constantes.NETERROR);
                }
                break;
            case R.id.message_BtnCancle:
                UtilTool.closeInputMethod(message_TexSearch,false);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    hadIntercept=false;
                    SetAnimation();
                }
                break;
    }
    }

    @Override
    public boolean onBackPressed() {
        if(hadIntercept){
            SetAnimation();
            hadIntercept=false;
            return  true;
        }else {
            return false;
        }
    }

    /**
     * 刚进入的时候 加载数据
     */
    private class GetCaring extends AsyncTask<Void,Void,ClientJson> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ClientJson doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(ClientJson clientJson) {
            super.onPostExecute(clientJson);
            if((null!=clientJson.getError())&& ("0".equals(clientJson.getError()))){
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                userList=clientJson.getManageruserlist();
                Empty_data();
                AddNews(userList);
                Log.i("userList",userList+"");
                if(userList.size()==0){
                    listView.setBackgroundColor(0x00000000);
                }else{
                    listView.setBackgroundColor(Color.WHITE);
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
    private class GetRefreshDatas extends AsyncTask<Void, Void, ClientJson> {

        @Override
        protected ClientJson doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            offset ="0";
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(ClientJson result) {
            super.onPostExecute(result);
            if (result.getError() != null && result.getManageruserlist().size() > 0) {
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                userList.clear();
                userList.addAll(result.getManageruserlist());
                Empty_data();
                AddNews(userList);

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
    private class GetLoadDatas extends AsyncTask<Void, Void, ClientJson> {

        @Override
        protected ClientJson doInBackground(Void... params) {

            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(ClientJson result) {
            super.onPostExecute(result);

            if (result.getError() != null && result.getManageruserlist().size() > 0) {
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
//  				listNews.clear();

                userList.addAll(result.getManageruserlist());
                Empty_data();
                AddNews(userList);
                adapter.notifyDataSetChangedEx(userList);
                listView.onLoadMoreComplete(true, "0");


            } else {

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
        String loadUrl= RemoteURL.USER.CLIENT;
//                .replace("{id}",uid).replace("{offset}",offset)
//                .replace("{length}","10");
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("id", uid);
        sigParams.put("getmanagerlist", "getmanagerlist226577");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", uid);
        params.put("offset", offset);
        params.put("length", "10");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, params,sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json ", json);
        return json;
    }
    //json数据解析
    public ClientJson GetList(String json){

        ClientJson clientJson=new ClientJson();
        if(("".equals(json))||(null==json)){
            return clientJson;
        }else {
            clientJson = FastjsonUtil.json2object(json,
                    ClientJson.class);
            Log.i("caringJson1", clientJson + "");
            return clientJson;
        }

    }
    //清空数据库
    public void Empty_data() {
        dao = new UserDao(context);
        dao.delete("user", null, null);
        dao.close();
    }
    //读取数据库
    public Boolean GetUserlink() {
        dao = new UserDao(context);
        userList = dao.queryList(User.class, "user", new String[]{"*"}, null, null, null, null, null);
        dao.close();
        if (userList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表
    public void AddNews(List<User> list01){
        dao=new UserDao(context);
        dao.AddList(list01);
        dao.close();
    }

    /**
     * 模糊查询数据
     */
    private class SearchUserByRealNameTask extends AsyncTask<String,Void,ClientJson> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ClientJson doInBackground(String... text) {
            String title = text[0];
            String loadUrl = RemoteURL.USER.BLURRY;
            Log.i("sigParams",title);

            HashMap<String, String> sigParams = new HashMap<String, String>();
            sigParams.put("id", uid);
            sigParams.put("getmanagerlistfuzzy", "getmanagerlistfuzzy10708");
            HashMap<String, String> params=new HashMap<String, String>();
            params.put("id",uid);
            params.put("companyname",title);
            String json = null;
            try {
                json = HttpUtil.getUrlWithSig(loadUrl, params,sigParams);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  GetList(json);
        }

        @Override
        protected void onPostExecute(ClientJson clientJson) {
            super.onPostExecute(clientJson);
            Log.i("userList01","userList01");
            if((null!=clientJson.getError())&& ("0".equals(clientJson.getError()))){
                userList01=clientJson.getManageruserlist();
                Log.i("userList01",userList01+"");
                adapter01.notifyDataSetChangedEx(userList01);
            }else{
//                UtilTool.showToast(getActivity(),"暂无数据");
            }

        }
    }
  public void SetAnimation(){
//      y = message_search.getY();
      height = user_screen.getHeight();
      Log.i("height",y+"   "+height);
      user_screen.setVisibility(View.VISIBLE);
      message_two.setVisibility(View.VISIBLE);
      message_one.setVisibility(View.VISIBLE);
      message_three.setVisibility(View.GONE);
      TranslateAnimation animation01 = new TranslateAnimation(0, 0, -height, 0);
      animation01.setDuration(500);
      animation01.setFillAfter(true);
      animation01.setAnimationListener(new Animation.AnimationListener() {
          @Override
          public void onAnimationStart(Animation animation) {

          }

          @Override
          public void onAnimationRepeat(Animation animation) {

          }

          @Override
          public void onAnimationEnd(Animation animation) {

          }
      });
      manager_title.startAnimation(animation01);
  }

}