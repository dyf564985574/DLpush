package com.toc.dlpush.index;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.CaringAdapter;
import com.toc.dlpush.caring.CaringDetail;
import com.toc.dlpush.caring.util.Caring;
import com.toc.dlpush.caring.util.CaringJson;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.NewsDao;
import com.toc.dlpush.util.NoticesDao;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by 袁飞 on 2015/5/18.
 * DLpush
 */
public class TabCaringActivity extends Fragment {
    View rootView;
    RelativeLayout caring_screen;
    CustomListView listView;
    CaringAdapter adapter;
    CaringJson caringJson;
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
    String uid;//用户的ID
    List<Caring> list=new ArrayList<Caring>();
    NewsDao dao;
    Context context;
    LinearLayout backbround;//背景图
    private ImageView water;//水印图片
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.activity_caring,container, false);
        context=getActivity();
        uid= UtilTool.getSharedPre(context, "users", "uid", "0");
        Log.i("TabTipsActivity", "TabCaringActivity");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        caring_screen= (RelativeLayout) rootView.findViewById(R.id.caring_screen);
        UtilTool.SetTitle(getActivity(), caring_screen);
        findview();
        indata();
    }
    public void findview(){
        water = (ImageView) rootView.findViewById(R.id.water);
        listView= (CustomListView) rootView.findViewById(R.id.caring_list01);
        backbround = (LinearLayout) rootView.findViewById(R.id.background01);
        UtilTool.SetView(getActivity(),water,2);
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
                if("-1".equals(list.get(0).getId())){
                    UtilTool.showToast(context,"此条为测试数据");
                    return;
                }else {
                    Intent intent = new Intent(context, CaringDetail.class);
                    intent.putExtra("nid", list.get(arg2 - 1).getId());
                    startActivityForResult(intent,1);
                }
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

    }
    public void indata(){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(context, Constantes.NETERROR);
            GetUserlink();
            offset =String.valueOf(list.size());
            adapter.notifyDataSetChangedEx(list);
            listView.onLoadMoreComplete(true,"1");
        }else{
            if((Constantes.CARINGPUSH==0)&&GetUserlink()){
                adapter.notifyDataSetChangedEx(list);
                offset =String.valueOf(list.size());
                listView.onLoadMoreComplete(true,"1");
            }else {
                Constantes.CARINGPUSH=0;
                new GetCaring().execute();
            }
            //联网更新数据

        }


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
                if(caringJson1.getNewslist().size()==0){
                    listView.setBackgroundColor(0x00000000);
                }else{
                    listView.setBackgroundColor(Color.WHITE);
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
                Empty_data();
                AddNews(list);
                System.out.println("下拉刷新");

                adapter.notifyDataSetChangedEx(list);
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
        Log.i("TabCaringActivity","onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("TabCaringActivity","onDestroy");
        super.onDestroy();
    }
    @Override
    public void onPause() {
        Log.i("TabCaringActivity","onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("TabCaringActivity","onResume");
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.i("TabCaringActivity","onStart");
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        Log.i("TabCaringActivity","onDestroyView");
        super.onDestroyView();
    }
    //清空数据库
    public void Empty_data() {
        dao = new NewsDao(context);
        Log.i("dao","dasdas");
        dao.delete("news", null, null);
        dao.close();
    }
    //读取联系人
    public Boolean GetUserlink() {
        NewsDao dao = new NewsDao(context);
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
//                ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getSystemService(this.getActivity().CONNECTIVITY_SERVICE);
//                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//                if (networkInfo == null || !networkInfo.isAvailable()) {
//                    UtilTool.showToast(context, "没有网络");
//
//                } else {
//                    //
//                    offset = "0";
//                    new GetCaring().execute();
//                }
                break;
        }


    }
}
