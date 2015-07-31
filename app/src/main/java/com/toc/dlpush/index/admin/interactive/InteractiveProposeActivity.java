package com.toc.dlpush.index.admin.interactive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.toc.dlpush.R;
import com.toc.dlpush.adapter.TipingAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.tips.ProposeDetail;
import com.toc.dlpush.tips.util.Comment;
import com.toc.dlpush.util.AdminNewsUtil;
import com.toc.dlpush.util.AdminNoticeDetailUtil;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.InteractiveJson;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yuanfei on 2015/7/23.
 */
public class InteractiveProposeActivity extends Fragment implements View.OnClickListener {
    private View rootView;
    private Context context;
    private View interProposrHead;
    private String uid;//人员的ID
    private RelativeLayout notices_screen;//抬头布局
    private CustomListView listView;//上拉刷新 下拉刷新布局
    private String offset = "0";//查询开始位置
    private String showNum = "10";//第一次界面显示的list条数
    private DonutProgress donutProgress01;
    private CircleProgress circleProgress;
    private DonutProgress donutProgress02;
    private List<Comment> list;
    private TipingAdapter adapter;
    private int count;
    private int satisfaction;
    private int deal_rate;
    private int undeal_rate;
    private String yclorwcl;//0 是已处理  1是未处理  null 是全部
    private  Timer timer ;
    int a=0;
    public  String year,month;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                a = a+1;
                if(a <=  deal_rate){
                    donutProgress01.setProgress(donutProgress01.getProgress() + 1);
                }
                if(a <= undeal_rate){
                    donutProgress02.setProgress(donutProgress02.getProgress() + 1);
                }
                if(a <= satisfaction){
                    circleProgress.setProgress(circleProgress.getProgress() + 1);
                }
                if(a == 100){
//                    timer.cancel();
                }
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.inter_propose,container, false);
        Bundle bundle = getArguments();//从activity传过来的Bundle
        year = bundle.getString("year");
        month = bundle.getString("month");
        if(timer !=null){
            timer.cancel();
        }
        init();
        if(!UtilTool.isNetwork(getActivity())){
            UtilTool.showToast(getActivity(), Constantes.NETERROR);
            listView.AutoRefresh();
            listView.onLoadMoreComplete(true, "1");
        } else {
            listView.AutoRefresh();

            new GetInteractive().execute();
        }
//        listDown();
        return rootView;
    }
    public void init(){
        list = new ArrayList<Comment>();
        adapter = new TipingAdapter(getActivity(),list);
        listView = (CustomListView) rootView.findViewById(R.id.listView1);
        interProposrHead = LayoutInflater.from(getActivity()).inflate(
                R.layout.inter_propose_head, null);
        listView.addHeaderView(interProposrHead);
        listView.setAdapter(adapter);
        circleProgress = (CircleProgress) interProposrHead.findViewById(R.id.circle_progress);

        circleProgress.setOnClickListener(this);
        donutProgress01 = (DonutProgress) interProposrHead.findViewById(R.id.ring_progress01);
        donutProgress01.setStartingDegree(-90);
        donutProgress01.setOnClickListener(this);
        donutProgress02 = (DonutProgress) interProposrHead.findViewById(R.id.ring_progress02);
        donutProgress02.setStartingDegree(-90);
        donutProgress02.setOnClickListener(this);
//        timer.schedule(new MyTask(),10,10);
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("AdapterView","AdapterView    "+position);
                Intent intent=new Intent(getActivity(),ProposeDetail.class);
                intent.putExtra("id",list.get(position-2).getId());
                intent.putExtra("path",1);
                Log.i("id", list.get(position - 2).getId());
                startActivity(intent);
            }
        });
        //下拉刷新
        listView.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                Log.i("下拉刷新", "下拉刷新");
                if (!UtilTool.isNetwork(getActivity())) {
                    UtilTool.showToast(context, Constantes.NETERROR);
                    listView.onRefreshComplete();
                } else {
                    //联网更新数据
                    new GetRefreshDatas().execute();
                }
            }

        });
        //上拉刷新
        listView.setOnLoadListener(new CustomListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                Log.i("上拉刷新", "上拉刷新");
                if (!UtilTool.isNetwork(getActivity())) {
                    UtilTool.showToast(context, Constantes.NETERROR);
                    listView.onLoadMoreComplete(true, "0");
                } else {
                    //联网更新数据
                    new GetLoadDatas().execute();
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.circle_progress:
                yclorwcl = null;
//                Update();
                listDown();
                break;
            case R.id.ring_progress01:
                yclorwcl = "0";
//                Update();
                listDown();
                break;
            case R.id.ring_progress02:
                yclorwcl = "1";
                listDown();
//                Update();
                break;
        }
    }

    /**
     * 刚进入的时候 加载数据
     */
    private class GetInteractive extends AsyncTask<Void, Void, InteractiveJson> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected InteractiveJson doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(InteractiveJson interactiveJson) {
            super.onPostExecute(interactiveJson);

            if ((null == interactiveJson.getError()) || ("1".equals(interactiveJson.getError()))) {
                UtilTool.showToast(getActivity(), Constantes.NETERROR);
            } else {
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                list = interactiveJson.getJsonvolist();
//                satisfaction = (int) ((1 - Float.parseFloat(interactiveJson.getTsl()))*100);
                count = Integer.parseInt(interactiveJson.getWcl()) + Integer.parseInt(interactiveJson.getYcl());
                if(count ==0){
                    count = 1;
                }
                deal_rate =(int)((Float.parseFloat(interactiveJson.getYcl())/count)*100);
                if(deal_rate == 0){
                    undeal_rate =(int)((Float.parseFloat(interactiveJson.getWcl())/count)*100);
                }else {
                    undeal_rate = 100 - deal_rate;
                }
                donutProgress01.setNum(interactiveJson.getYcl());
                donutProgress02.setNum(interactiveJson.getWcl());
                donutProgress01.setProgress(0);
                donutProgress02.setProgress(0);
                satisfaction = Math.round(Float.parseFloat(interactiveJson.getYcl())/count*100);
                Log.i("satisfaction ","满意度："+satisfaction+"  已处理："+interactiveJson.getYcl()+"  未处理"+undeal_rate+"   "+count);
                circleProgress.setProgress(0);
                a=0;
                timer = new Timer();
                timer.schedule(new MyTask(),10,10);
                adapter.notifyDataSetChangedEx(list);
            }
            listView.onRefreshComplete();
        }
    }

    /**
     * 从网络上获取数据
     *
     * @return 返回JSON数据
     */
    public String GetJson() {
        String json = "";
        String loadUrl = RemoteURL.USER.INTERACTIVE;

        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("countyearormonth7826","countyearormonth7826");
        sigParams.put("countyearormonth8678", "countyearormonth8678");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("year", year);
        params.put("month", month);
        params.put("type", "feedback");
        params.put("offset",offset);
        params.put("length", "10");
        params.put("yclorwcl",yclorwcl);
        try {
            json = HttpUtil.getUrlWithSig(loadUrl, params, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
        return json;
    }

    public InteractiveJson GetList(String json) {
        InteractiveJson interactiveJson = new InteractiveJson();
        if ((null == json) || ("".equals(json))) {
            return interactiveJson;
        } else {
            interactiveJson = FastjsonUtil.json2object(json,
                    InteractiveJson.class);
            Log.i("notifyJsonVoList", interactiveJson + "");
            return interactiveJson;
        }
    }
    class MyTask extends TimerTask{

        @Override
        public void run() {

//            Log.i("message",++a+"");
            Message message=new Message();
            message.what=1;
            handler.sendMessage(message);
        }
    }
    /*
  * 下拉刷新
  * 异步加载数据
  */
    private class GetRefreshDatas extends AsyncTask<Void, Void, InteractiveJson> {

        @Override
        protected InteractiveJson doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            offset = "0";
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(InteractiveJson result) {
            super.onPostExecute(result);
//  			flags=true;
            if (result.getError() != null &&"0".equals(result.getError())) {
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
                list.clear();
                list.addAll(result.getJsonvolist());
                adapter.notifyDataSetChangedEx(list);
                if (list.size() < 8) {
                    listView.onLoadMoreComplete(true, "1");
                }
                count = Integer.parseInt(result.getWcl()) + Integer.parseInt(result.getYcl());
                if(deal_rate == 0){
                    undeal_rate =(int)((Float.parseFloat(result.getWcl())/count)*100);
                }else {
                    undeal_rate = 100 - deal_rate;
                }
                deal_rate =(int)((Float.parseFloat(result.getYcl())/count)*100);
                undeal_rate = 100 - deal_rate;
                donutProgress01.setNum(result.getYcl());
                donutProgress01.setProgress(0);
                donutProgress02.setNum(result.getWcl());
                donutProgress02.setProgress(0);
                circleProgress.setProgress(0);
                satisfaction = Math.round(Float.parseFloat(result.getYcl())/count*100);
                Log.i("satisfaction ","满意度："+satisfaction+"     "+Float.parseFloat(result.getYcl())/count*100+"  已处理："+deal_rate+"  未处理"+undeal_rate+"   "+count);
                a=0;
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
    private class GetLoadDatas extends AsyncTask<Void, Void, InteractiveJson> {

        @Override
        protected InteractiveJson doInBackground(Void... params) {

            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(InteractiveJson result) {
            super.onPostExecute(result);

            if (result.getError() != null && result != null && result.getJsonvolist().size() > 0) {
//  				Notices noid = result.get((result.size() - 1));
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
//  				listNews.clear();

                list.addAll(result.getJsonvolist());
                adapter.notifyDataSetChangedEx(list);

                listView.onLoadMoreComplete(true, "0");


            } else {
                listView.onLoadMoreComplete(false, "1");// 加载更多完成
            }
        }
    }
    //更新数据
    public void Update(){
        if (!UtilTool.isNetwork(getActivity())) {
            UtilTool.showToast(context, Constantes.NETERROR);
            listView.onRefreshComplete();
        } else {
            //联网更新数据
            new GetRefreshDatas().execute();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        timer.cancel();
    }
    public void listDown(){
        offset = "0";
        listView.AutoRefresh();
        Update();
    }
}
