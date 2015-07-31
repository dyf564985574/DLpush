package com.toc.dlpush.tips;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.ProposeAdapter;
import com.toc.dlpush.adapter.ProposeListAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.tips.util.CommentJson;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.NoticesDao;
import com.toc.dlpush.util.SuggestDao;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/6/23.
 */
public class SuggestListActivity extends Fragment {
    ProposeListAdapter adapter;
    CustomListView suggest_list;
    List<Comment> subminsList=new ArrayList<Comment>();
    String uid;//用户的ID
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
//    RelativeLayout tip_screen;
    View rootView;
    TextView tv_model;
    SuggestDao dao;
    Context context;
    private ImageView water;//水印图片
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.suggest_list,container, false);
//        tip_screen= (RelativeLayout) rootView.findViewById(R.id.tip_screen);
//        UtilTool.SetTitle(getActivity(), tip_screen);
        context=getActivity();
        uid= UtilTool.getSharedPre(getActivity(), "users", "uid", "0");
        Constantes.index=Constantes.index+1;
        init();

        ConnectivityManager manager= (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(getActivity(), Constantes.NETERROR);
            GetUserlink();
            adapter.notifyDataSetChangedEx(subminsList);
            offset =String.valueOf(subminsList.size());
            suggest_list.onLoadMoreComplete(true,"1");
        }else{
            if(Constantes.SUGGESTPUSH==0&&(GetUserlink()||Constantes.index!=1)){
                adapter.notifyDataSetChangedEx(subminsList);
                offset =String.valueOf(subminsList.size());
                suggest_list.onLoadMoreComplete(true,"1");
            }else {
                Constantes.SUGGESTPUSH=0;
                new GetDatas().execute();
            }
            //联网更新数据


        }
        return rootView;
    }
    //

    public void init(){
        water = (ImageView) rootView.findViewById(R.id.water);
        UtilTool.SetView(getActivity(),water,2);
        suggest_list= (CustomListView)rootView.findViewById(R.id.suggest_list);
        adapter=new ProposeListAdapter(getActivity(),subminsList);
        suggest_list.setAdapter(adapter);
        //点击事件
        suggest_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(context,ProposeDetail.class);
                intent.putExtra("id",subminsList.get(position-1).getId());
                intent.putExtra("path",1);
                Log.i("id", subminsList.get(position - 1).getId());
                startActivity(intent);
            }
        });
        suggest_list.setOnLoadListener(new CustomListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ConnectivityManager manager= (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(getActivity(), Constantes.NETERROR);
                    suggest_list.onLoadMoreComplete();
                }else{
                    //联网更新数据
                    new GetOnLoadDatas().execute();

                }
            }
        });
        suggest_list.setOnRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager manager= (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(getActivity(), Constantes.NETERROR);
                    suggest_list.onRefreshComplete();
                }else{
                    //联网更新数据
                    new GetRefreshDatas().execute();

                }
            }
        });
    }
    //获取意见与建议的数据
    private class GetDatas extends AsyncTask<Void,Void,CommentJson> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CommentJson doInBackground(Void... params) {
            return GetList(GetJson());
        }
        @Override
        protected void onPostExecute(CommentJson submins) {
//            super.onPostExecute(submins);
            //判断网络请求是否成功 0 是成功  1是失败
            if("0".equals(submins.getError())){
                subminsList=submins.getCommentList();
                Empty_data();
                AddSuggest(subminsList);
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                if(subminsList.size()==0){
                    suggest_list.setBackgroundColor(getResources().getColor(R.color.transparent));
                }else{
                    suggest_list.setBackgroundColor(getResources().getColor(R.color.white));
                }
                adapter.notifyDataSetChangedEx(subminsList);
                if(subminsList.size()<8){
                    suggest_list.onLoadMoreComplete(true,"1");
                }
            }
        }
    }

    /**
     * 下拉刷新获取数据
     * @return
     */
    private class GetRefreshDatas extends AsyncTask<Void,Void,CommentJson>{
        @Override
        protected void onPreExecute() {
            offset="0";
            super.onPreExecute();
        }

        @Override
        protected CommentJson doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(CommentJson commentJson) {
            super.onPostExecute(commentJson);
            if("0".equals(commentJson.getError())){
                subminsList.clear();
                subminsList=commentJson.getCommentList();
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                Empty_data();
                AddSuggest(subminsList);
                adapter.notifyDataSetChangedEx(subminsList);
                if(subminsList.size()<8){
                    suggest_list.onLoadMoreComplete(true,"1");
                }
                suggest_list.onRefreshComplete();
            }else{
                suggest_list.onRefreshComplete();
            }
        }
    }

    private class GetOnLoadDatas extends AsyncTask<Void,Void,CommentJson>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CommentJson doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(CommentJson commentJson) {
            super.onPostExecute(commentJson);
            if("0".equals(commentJson.getError())&&commentJson.getCommentList().size()>0){
//                subminsList.clear();
                subminsList.addAll(commentJson.getCommentList());
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                Empty_data();
                AddSuggest(subminsList);
                adapter.notifyDataSetChangedEx(subminsList);
                if(subminsList.size()<8){
                    suggest_list.onLoadMoreComplete(true,"1");
                }else {
                    suggest_list.onLoadMoreComplete(true,"0");
                }
            }else{
                suggest_list.onLoadMoreComplete(true,"1");
            }
        }
    }

    /*
    * 获取反馈意见列表
    */
    private String GetJson() {
        String json = "";
        try {
            String  loadUrl = RemoteURL.USER.TIPSLIST.replace("{offset}",offset).replace("{length}","10")
                    .replace("{type}","comment").replace("{uid}",uid);
            //sig签名
            HashMap<String, String> sigParams=new HashMap<String, String>();
            sigParams.put("uid",uid);
            sigParams.put("commentListByUid", "commentListByUid222131");
            json = HttpUtil.getUrlWithSig(loadUrl, sigParams);// 从服务器获取数据

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("json ", json);
        return json;
    }
    private CommentJson GetList(String json){
        CommentJson sub=new CommentJson();
        sub= FastjsonUtil.json2object(json, CommentJson.class);
        Log.i("userJsonVoList",sub+"");
        return sub;
    }
    //清空数据库
    public void Empty_data() {
        dao = new SuggestDao(context);
        int a=dao.delete("suggest", null, null);
        Log.i("aaaaa",a+"");
        dao.close();
    }

    //读取联系人
    public Boolean GetUserlink() {
        SuggestDao dao = new SuggestDao(context);
        subminsList = dao.queryList(Comment.class, "suggest", new String[]{"*"}, null, null, "creatdate desc", null, null);
        dao.close();
        if(subminsList.size()==0){
            suggest_list.setBackgroundColor(getResources().getColor(R.color.transparent));
        }else{
            suggest_list.setBackgroundColor(getResources().getColor(R.color.white));
        }
        if (subminsList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表

    /**
     *
     ***********************************************************************************************
     * @param list01
     */
    public void AddSuggest(List<Comment> list01){
        dao=new SuggestDao(context);
        dao.AddList(list01);
        dao.close();
    }
}
