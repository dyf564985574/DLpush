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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.TipingAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.setting.util.UserlinkDao;
import com.toc.dlpush.tips.TipsDetail;
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.tips.util.CommentJson;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.SuggestDao;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.BackHandledFragment;
import com.toc.dlpush.view.CustomListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by 袁飞 on 2015/6/3.
 * DLpush
 */
public class ManagerSuggestActivity extends BackHandledFragment {
    TipingAdapter adapter;
    CustomListView suggest_list;
    List<Comment> subminsList=new ArrayList<Comment>();
    String uid;//用户的ID
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
    View rootView;
    RelativeLayout tip_screen;
    TextView tv_model;
    SuggestDao dao;
    Context context;
    private ImageView water;//水印图片
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.feedback_main,container, false);
        context=getActivity();
        tip_screen= (RelativeLayout) rootView.findViewById(R.id.tip_screen);
        UtilTool.SetTitle(getActivity(), tip_screen );
        uid= UtilTool.getSharedPre(getActivity(), "users", "uid", "0");
        init();
        ConnectivityManager manager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(context, Constantes.NETERROR);
            GetUserlink();
            adapter.notifyDataSetChangedEx(subminsList);
            offset = String.valueOf(subminsList.size());
            suggest_list.onLoadMoreComplete(true,"1");
        }else {
            if (Constantes.SUGGESTPUSH == 0&&GetUserlink()) {
                adapter.notifyDataSetChangedEx(subminsList);
                offset = String.valueOf(subminsList.size());
                suggest_list.onLoadMoreComplete(true,"1");
            } else {
                Constantes.SUGGESTPUSH=0;
                new GetDatas().execute();
            }
        }
        return rootView;
    }
    //

    public void init(){
        suggest_list= (CustomListView)rootView.findViewById(R.id.suggest_list);
        adapter=new TipingAdapter(context,subminsList);
        water = (ImageView) rootView.findViewById(R.id.water);
        UtilTool.SetView(getActivity(),water,2);
        suggest_list.setAdapter(adapter);
        //点击事件
        suggest_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(context,TipsDetail.class);
                intent.putExtra("id",subminsList.get(position-1).getId());
                intent.putExtra("path",1);
                Log.i("id",subminsList.get(position-1).getId());
                startActivityForResult(intent, 1);
            }
        });
        suggest_list.setOnLoadListener(new CustomListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ConnectivityManager manager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(context, Constantes.NETERROR);
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
                ConnectivityManager manager= (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if(networkInfo==null||!networkInfo.isAvailable()){
                    UtilTool.showToast(context, Constantes.NETERROR);
                    suggest_list.onRefreshComplete();
                }else{
                    //联网更新数据
                    new GetRefreshDatas().execute();

                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
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
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(CommentJson submins) {
//            super.onPostExecute(submins);
            if("0".equals(submins.getError())){
                subminsList=submins.getCommentList();
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                Empty_data();
                AddSuggest(subminsList);
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
            if("0".equals(commentJson.getError())&&commentJson.getCommentList().size()>0){
                subminsList.clear();
                subminsList=commentJson.getCommentList();
                int off=Integer.parseInt(offset);
                Empty_data();
                AddSuggest(subminsList);
                off+=10;
                offset =String.valueOf(off);
                adapter.notifyDataSetChangedEx(subminsList);
                Log.i("offset","adapter"+subminsList.size());

                if(commentJson.getCommentList().size() < 8){
                    suggest_list.onLoadMoreComplete(true,"1");
                }
                suggest_list.onRefreshComplete(); // 加载更多完成
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
            if("0".equals(commentJson.getError())){
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
                }else{
                    suggest_list.onLoadMoreComplete();
                }
            }else{
                suggest_list.onRefreshComplete();
            }
        }
    }

    /*
    * 获取反馈意见列表
    */
    private String GetJson() {
        String json = "";
        try {
            String  loadUrl = RemoteURL.USER.SUBMITLIST;
            //sig签名
            HashMap<String, String> sigParams=new HashMap<String, String>();
            sigParams.put("offset", offset);
            sigParams.put("length", showNum);
            HashMap<String, String> params=new HashMap<String, String>();
            params.put("offset",offset);
            params.put("length",showNum);
            params.put("type", "comment");
            params.put("managerid",uid);

            json = HttpUtil.getUrlWithSig(loadUrl, params, sigParams);// 从服务器获取数据

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
        if (subminsList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表
    public void AddSuggest(List<Comment> list01){
        dao=new SuggestDao(context);
        dao.AddList(list01);
        dao.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            GetUserlink();
            adapter.notifyDataSetChangedEx(subminsList);
        }
    }
}
