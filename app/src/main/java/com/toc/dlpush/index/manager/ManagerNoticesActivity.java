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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.toc.dlpush.MainActivity;
import com.toc.dlpush.ManagerActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.adapter.GuideAdapter;
import com.toc.dlpush.adapter.ManagerNoticesAdapter;
import com.toc.dlpush.adapter.NoticesAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.notices.ManagerNoticesDetailActivity;
import com.toc.dlpush.notices.NoticesDetialActivity;
import com.toc.dlpush.notices.util.NoticesGroupInfo;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.GuideUtil;
import com.toc.dlpush.util.NoticesDao;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.BackHandledFragment;
import com.toc.dlpush.view.CustomListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/15.
 */
public class ManagerNoticesActivity extends BackHandledFragment {
    View rootView;
    int width ;//屏幕的宽度
    int height;//屏幕的高度
    public static boolean isOpen = false;//判断页面是否开启
    RelativeLayout notices_screen;
    CustomListView listView;//兹定于listview；
    public static List<NotifyJsonVo> list=new ArrayList<NotifyJsonVo>();//数据集合
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
    String uid;//用户的ID
    Button btn;
    NoticesDao dao;
    Context context;
    //    LoadToast lt;//自定义加载栏
    List<NotifyJsonVo>notifyJsonVos = new ArrayList<NotifyJsonVo>();
    public  ManagerNoticesAdapter adapter;//list的适配器
    public LinearLayout line_guide;//引导页
    List<GuideUtil> guideUtilList=new ArrayList<GuideUtil>();
    private ImageView water;//水印图片
    private ImageView img_help;//帮助按钮
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.manager_notices,container, false);
        context=getActivity();
        isOpen=true;
        uid= UtilTool.getSharedPre(getActivity(), "users", "uid", "0");
        findview();

        ConnectivityManager connectivityManager=(ConnectivityManager) this.getActivity().getSystemService(this.getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(context, Constantes.NETERROR);
            GetUserlink();
            offset =String.valueOf(list.size());
            adapter.notifyDataSetChangedEx(list);
            listView.onLoadMoreComplete(true,"1");
        }else{
            if((Constantes.NOTICESPUSH==0)&&GetUserlink()){
                adapter.notifyDataSetChangedEx(list);
                offset =String.valueOf(list.size());
                listView.onLoadMoreComplete(true,"1");
            }else {
                Constantes.NOTICESPUSH=0;
                new GetNotices().execute();
            }
        }

        return rootView;
    }



    /**
     * 实例化控件
     * 控件多屏幕适配
     */
    public  void findview(){
        water = (ImageView) rootView.findViewById(R.id.water);
        //加载动画  实例化
        String text = "正在加载...";
        //引导图实例化
        img_help= (ImageView) rootView.findViewById(R.id.img_help);
        img_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManagerActivity.line_guide.setVisibility(View.VISIBLE);
            }
        });
        notices_screen= (RelativeLayout) rootView.findViewById(R.id.notices_screen);
        listView= (CustomListView) rootView.findViewById(R.id.manager_notices_list01);
        adapter=new ManagerNoticesAdapter(context, list);
        UtilTool.SetView(getActivity(),water,2);
        listView.setAdapter(adapter);
        UtilTool.SetTitle(getActivity(), notices_screen);
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                if("-1".equals(list.get(0).getId())){
                    UtilTool.showToast(context,"此条为测试数据");
                    return;
                }else {
                    list.get(arg2 - 1).setIsread("is");
                    Intent intent = new Intent(context, ManagerNoticesDetailActivity.class);
                    intent.putExtra("nid", list.get(arg2 - 1).getId());
                    intent.putExtra("readnum", list.get(arg2 - 1).getReadnum());
                    intent.putExtra("countnum", list.get(arg2 - 1).getCountnum());
                    startActivityForResult(intent, 1);
                }
            }
        });
        //下拉刷新
        listView.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                Log.i("下拉刷新", "下拉刷新");
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
                Log.i("上拉刷新", "上拉刷新");
                ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(context, Constantes.NETERROR);
                    listView.onLoadMoreComplete(true,"0");
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
    private class GetNotices extends AsyncTask<Void,Void,NoticesGroupInfo> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected NoticesGroupInfo doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(NoticesGroupInfo noticesGroupInfo) {
            super.onPostExecute(noticesGroupInfo);

            if((null==noticesGroupInfo.getError())||("1".equals(noticesGroupInfo.getError()))){
                UtilTool.showToast(context,"网络异常");
            }else{
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                list=noticesGroupInfo.getNotifylist();
                Empty_data();
                AddNotices(list);
                if(list.size()==0){
                    listView.setBackgroundColor(getResources().getColor(R.color.transparent));
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
        String loadUrl= RemoteURL.USER.MANAGERNOTIC.replace("{uid}",uid).replace("{offset}",offset)
                .replace("{length}","10");
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("uid", uid);
        sigParams.put("offset", offset);
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json",json);
        return json;
    }
    public NoticesGroupInfo GetList(String json){
        NoticesGroupInfo notifyJsonVoList=new NoticesGroupInfo();
        if((null==json)||("".equals(json))){
            return notifyJsonVoList;
        }else{
            notifyJsonVoList = FastjsonUtil.json2object(json,
                    NoticesGroupInfo.class);
            Log.i("notifyJsonVoList",notifyJsonVoList+"");
            return notifyJsonVoList;
        }
    }
    /*
      * 下拉刷新
      * 异步加载数据
      */
    private class GetRefreshDatas extends AsyncTask<Void, Void, NoticesGroupInfo> {

        @Override
        protected NoticesGroupInfo doInBackground(Void... params) {
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
        protected void onPostExecute(NoticesGroupInfo result) {
            super.onPostExecute(result);
//  			flags=true;
            if (result.getError() != null && result.getNotifylist().size() > 0) {
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                list.clear();
                list.addAll(result.getNotifylist());
                Empty_data();
                AddNotices(list);
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
    private class GetLoadDatas extends AsyncTask<Void, Void, NoticesGroupInfo> {

        @Override
        protected NoticesGroupInfo doInBackground(Void... params) {

            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(NoticesGroupInfo result) {
            super.onPostExecute(result);

            if (result.getError()!=null&&result != null && result.getNotifylist().size() > 0) {
//  				Notices noid = result.get((result.size() - 1));
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
//  				listNews.clear();

                list.addAll(result.getNotifylist());
                Empty_data();
                AddNotices(list);
                System.out.println("上拉刷新");
                adapter.notifyDataSetChangedEx(list);

                listView.onLoadMoreComplete(true,"0");


                System.out.println("上拉=====");
            } else {
                System.out.println("上拉+++++");
                listView.onLoadMoreComplete(false,"1");// 加载更多完成
            }
        }
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getSystemService(this.getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    UtilTool.showToast(context, "没有网络");

                } else {

                    offset = "0";
                    new GetNotices().execute();
                }
                break;
        }

    }
    //清空数据库
    public void Empty_data() {
        Log.i("getActivity",context+"");
        NoticesDao dao = new NoticesDao(context);
        int a=dao.delete("notices", null, null);
        Log.i("aaaaa",a+"");
        dao.close();
    }
    //读取联系人
    public Boolean GetUserlink() {
        NoticesDao dao = new NoticesDao(context);
        list = dao.queryList(NotifyJsonVo.class, "notices", new String[]{"*"}, null, null, "sendtime desc", null, null);
        dao.close();
        if (list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表
    public void AddNotices(List<NotifyJsonVo> list01){
        dao=new NoticesDao(context);
        dao.Add_contact(list01);
        dao.close();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
