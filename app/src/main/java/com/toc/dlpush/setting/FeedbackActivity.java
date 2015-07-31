package com.toc.dlpush.setting;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.TipingAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.setting.util.Return;
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.tips.util.CommentJson;
import com.toc.dlpush.tips.util.submins;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 袁飞 on 2015/6/1.
 * DLpush
 * 区域管理员查看意见与建议列表
 */
public class FeedbackActivity extends Activity {
    TipingAdapter adapter;
    CustomListView suggest_list;
    List<Comment> subminsList=new ArrayList<Comment>();
    String uid;//用户的ID
    String offset = "0";//查询开始位置
    String showNum = "10";//第一次界面显示的list条数
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_main);
        uid=UtilTool.getSharedPre(this,"users","uid","0");
        init();
        ConnectivityManager manager= (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(this, Constantes.NETERROR);
            suggest_list.onRefreshComplete();
        }else{
            //联网更新数据
            new GetRefreshDatas().execute();

        }

    }
    public void init(){
        suggest_list= (CustomListView) findViewById(R.id.suggest_list);
        adapter=new TipingAdapter(FeedbackActivity.this,subminsList);
        suggest_list.setAdapter(adapter);
    }
    //获取意见与建议的数据
    private class GetRefreshDatas extends AsyncTask<Void,Void,CommentJson>{

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
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                adapter.notifyDataSetChangedEx(subminsList);
                if(subminsList.size()<8){
                    suggest_list.onLoadMoreComplete(true,"1");
                }
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
            sigParams.put("offset", "0");
            sigParams.put("length", "10");
            HashMap<String, String> params=new HashMap<String, String>();
            params.put("offset","0");
            params.put("length","10");
            params.put("type", "comment");

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
       android.util.Log.i("userJsonVoList",sub+"");
       return sub;
   }
}
