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
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.tips.util.CommentJson;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.ProposeDao;
import com.toc.dlpush.util.SuggestDao;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/6/23.
 */
public class ProposeListActivity extends Fragment {
    ProposeListAdapter adapter;
    CustomListView propose_list;
    List<Comment> subminsList=new ArrayList<Comment>();
    String uid;//用户的ID
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
//    RelativeLayout tip_screen;
    View rootView;
    TextView tv_model;
    ProposeDao dao;
    Context context;
    private ImageView water;//水印图片
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.propose_list,container, false);
        context=getActivity();
        Constantes.aa=Constantes.aa+1;
        uid= UtilTool.getSharedPre(getActivity(), "users", "uid", "0");
        init();
        ConnectivityManager manager= (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(getActivity(), Constantes.NETERROR);
            GetUserlink();
            adapter.notifyDataSetChangedEx(subminsList);
            offset =String.valueOf(subminsList.size());
            propose_list.onLoadMoreComplete(true,"1");
        }else{
            if(Constantes.PROPOSEPUSH==0&&(GetUserlink()||Constantes.aa!=1)){
                adapter.notifyDataSetChangedEx(subminsList);
                offset =String.valueOf(subminsList.size());
                propose_list.onLoadMoreComplete(true,"1");
            }else {
                Constantes.PROPOSEPUSH=0;
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
        propose_list= (CustomListView)rootView.findViewById(R.id.propose_list);
        adapter=new ProposeListAdapter(context,subminsList);
        propose_list.setAdapter(adapter);
        //点击事件
        propose_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("AdapterView","AdapterView");
                Intent intent=new Intent(getActivity(),ProposeDetail.class);
                intent.putExtra("id",subminsList.get(position-1).getId());
                intent.putExtra("path",1);
                Log.i("id", subminsList.get(position - 1).getId());
                startActivity(intent);
            }
        });
        //上拉刷新
        propose_list.setOnLoadListener(new CustomListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ConnectivityManager manager= (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(getActivity(), Constantes.NETERROR);
                    propose_list.onLoadMoreComplete();
                }else{
                    //联网更新数据
                    new GetOnLoadDatas().execute();

                }
            }
        });
        //下拉刷新
        propose_list.setOnRefreshListener(new CustomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager manager= (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(getActivity(), Constantes.NETERROR);
                    propose_list.onRefreshComplete();
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
            if("0".equals(submins.getError())){
                subminsList=submins.getCommentList();
                Empty_data();
                AddSuggest(subminsList);
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                if(subminsList.size()==0){
                    propose_list.setBackgroundColor(getResources().getColor(R.color.transparent));
                }else{
                    propose_list.setBackgroundColor(getResources().getColor(R.color.white));
                }
                adapter.notifyDataSetChangedEx(subminsList);
                if(subminsList.size()<8){
                    propose_list.onLoadMoreComplete(true,"1");
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
                Empty_data();
                AddSuggest(subminsList);
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                adapter.notifyDataSetChangedEx(subminsList);
                if(subminsList.size()<8){
                    propose_list.onLoadMoreComplete(true,"1");
                }
                propose_list.onRefreshComplete();
            }else{
                propose_list.onRefreshComplete();
            }
        }
    }
//上拉刷新
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
                    propose_list.onLoadMoreComplete(true,"1");
                }else {
                    propose_list.onLoadMoreComplete(true,"0");
                }
            }else{
                propose_list.onLoadMoreComplete(true,"1");
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
                    .replace("{type}","feedback").replace("{uid}",uid);
            //sig签名
            HashMap<String, String> sigParams=new HashMap<String, String>();
            sigParams.put("uid",uid);
            sigParams.put("commentListByUid", "commentListByUid222131");
            json = HttpUtil.getUrlWithSig(loadUrl,sigParams);// 从服务器获取数据

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
        dao = new ProposeDao(context);
        int a=dao.delete("propose", null, null);
        Log.i("aaaaa",a+"");
        dao.close();
    }

    //读取联系人
    public Boolean GetUserlink() {
        ProposeDao dao = new ProposeDao(context);
        subminsList = dao.queryList(Comment.class, "propose", new String[]{"*"}, null, null, "creatdate desc", null, null);
        if(subminsList.size()==0){
            propose_list.setBackgroundColor(getResources().getColor(R.color.transparent));
        }else{
            propose_list.setBackgroundColor(getResources().getColor(R.color.white));
        }
        dao.close();
        if (subminsList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表
    public void AddSuggest(List<Comment> list01){
        dao=new ProposeDao(context);
        dao.AddList(list01);
        dao.close();
    }
}
