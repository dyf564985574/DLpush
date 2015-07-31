package com.toc.dlpush.index.manager;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.CaringAdapter;
import com.toc.dlpush.caring.CaringDetail;
import com.toc.dlpush.caring.util.Caring;
import com.toc.dlpush.caring.util.CaringJson;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.NewsDao;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.BackHandledFragment;
import com.toc.dlpush.view.CustomListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by 袁飞 on 2015/6/3.
 * DLpush
 */
public class ManagerCaringActivity extends BackHandledFragment {
    View rootView;
    RelativeLayout caring_screen;
    CustomListView listView;
    CaringAdapter adapter;
    CaringJson caringJson;
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
    String uid;//用户的ID
    List<Caring> list=new ArrayList<Caring>();
    String roid;
    NewsDao dao;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.manager_caring,container, false);
        context=getActivity();
        uid= UtilTool.getSharedPre(getActivity(), "users", "uid", "0");
        roid=UtilTool.getSharedPre(getActivity(),"user","manager","0");
        Log.i("TabTipsActivity", "TabCaringActivity");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        caring_screen= (RelativeLayout) rootView.findViewById(R.id.caring_screen);
        UtilTool.SetTitle(getActivity(), caring_screen );
        findview();
        indata();
    }
    public void findview(){
        listView= (CustomListView) rootView.findViewById(R.id.manager_caring_list01);
        adapter=new CaringAdapter(context,list);
        listView.setAdapter(adapter);
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
//                Bundle bundle=new Bundle();
//                bundle.putSerializable("caring",list.get(arg2-1));
                Intent intent=new Intent(context,CaringDetail.class);
                intent.putExtra("nid",list.get(arg2-1).getId());
                startActivityForResult(intent,1);
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
                    UtilTool.showToast(getActivity(), Constantes.NETERROR);
                    listView.onRefreshComplete();
                }else{
                    //联网更新数据
                    new GetRefreshDatas().execute();
                }
            }

        });

        //上拉刷新
        listView.setOnLoadListener(new CustomListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(getActivity(), Constantes.NETERROR);
                    listView.onLoadMoreComplete(true,"1");
                }else{
                    //联网更新数据
                    new GetLoadDatas().execute();
                }

            }
        });

    }
    //进入刷新页面
    public void indata(){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(context, Constantes.NETERROR);
            GetUserlink();
            adapter.notifyDataSetChangedEx(list);
            offset =String.valueOf(list.size());
            listView.onLoadMoreComplete(true,"1");
        }else{
            if(Constantes.CARINGPUSH==0&&GetUserlink()){
                adapter.notifyDataSetChangedEx(list);
                offset =String.valueOf(list.size());
                listView.onLoadMoreComplete(true,"1");
            }else {
                Constantes.CARINGPUSH=0;
                new GetCaring().execute();
            }

        }


    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 刚进入的时候 加载数据
     */
    private class GetCaring extends AsyncTask<Void,Void,CaringJson> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected CaringJson doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(CaringJson caringJson1) {
            super.onPostExecute(caringJson1);
            if((null!=caringJson1.getError())&& ("0".equals(caringJson1.getError()))){
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                list=caringJson1.getNewslist();
                Empty_data();
                AddNews(list);
                if(list.size()==0){
                    listView.setBackground(getResources().getDrawable(R.mipmap.background_02));
                }else{
                    listView.setBackgroundColor(getResources().getColor(R.color.white));
                }
                adapter.notifyDataSetChangedEx(list);
                if(list.size()<8){
                    listView.onLoadMoreComplete(true,"1");
                }
            }

        }
    }

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetJson(){
        String json="";
        String loadUrl= RemoteURL.USER.CARING.replace("{offset}",offset)
                .replace("{length}","10");
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("newsList", "newsList");
        sigParams.put("randomnum", "000111");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json ",json);
        return json;
    }
    //json数据解析
    public CaringJson GetList(String json){

        CaringJson caringJson1=new CaringJson();
        if(("".equals(json))||(null==json)){
            return caringJson1;
        }else {
            caringJson1 = FastjsonUtil.json2object(json,
                    CaringJson.class);
            Log.i("caringJson1", caringJson1 + "");
            return caringJson1;
        }

    }
    /*
      * 下拉刷新
      * 异步加载数据
      */
    private class GetRefreshDatas extends AsyncTask<Void, Void, CaringJson> {

        @Override
        protected CaringJson doInBackground(Void... params) {
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
        protected void onPostExecute(CaringJson result) {
            super.onPostExecute(result);
            if (result.getError() != null && result.getNewslist().size() > 0) {
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                list.clear();
                list.addAll(result.getNewslist());

                System.out.println("下拉刷新");

                adapter.notifyDataSetChangedEx(list);
                Empty_data();
                AddNews(list);
                if(list.size()<8){
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
    private class GetLoadDatas extends AsyncTask<Void, Void, CaringJson> {

        @Override
        protected CaringJson doInBackground(Void... params) {

            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(CaringJson result) {
            super.onPostExecute(result);

            if (result.getError() != null && result.getNewslist().size() > 0) {
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
//  				listNews.clear();
                list.addAll(result.getNewslist());
                Empty_data();
                AddNews(list);
                System.out.println("上拉刷新");
                adapter.notifyDataSetChangedEx(list);
                listView.onLoadMoreComplete(true,"0");


                System.out.println("上拉=====");
            } else {
                System.out.println("上拉+++++");
                listView.onLoadMoreComplete(true,"1");

//                listView.onLoadMoreComplete(false,"0");// 加载更多完成
            }
        }
    }
    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //清空数据库
    public void Empty_data() {
        dao = new NewsDao(context);
        dao.delete("news", null, null);
        dao.close();
    }
    //读取数据库
    public Boolean GetUserlink() {
        dao = new NewsDao(context);
        list = dao.queryList(Caring.class, "news", new String[]{"*"}, null, null, "creatdate desc", null, null);
        dao.close();
        if (list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表
    public void AddNews(List<Caring> list01){
        dao=new NewsDao(context);
        dao.AddList(list01);
        dao.close();
    }
  //返回刷新
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1){
            ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
            if(networkInfo==null||!networkInfo.isAvailable()){
                UtilTool.showToast(context, Constantes.NETERROR);
                GetUserlink();
                adapter.notifyDataSetChangedEx(list);
                offset =String.valueOf(list.size());
                listView.onLoadMoreComplete(true,"1");
            }else{
                    new GetCaring().execute();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
