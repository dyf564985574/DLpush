package com.toc.dlpush.notices;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.toc.dlpush.R;
import com.toc.dlpush.adapter.ManagerNoticesAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.util.AdminNoticeDetailUtil;
import com.toc.dlpush.util.AdminNoticesUtil;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;
import com.toc.dlpush.wheelview.DatePopupWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/24.
 */
public class AdminNoticesListActivity extends Activity {
    private CustomListView listview;
    private RelativeLayout admin_notice_detail_screen;
    private View head;
    private ManagerNoticesAdapter adapter;//list的适配器
    private List<NotifyJsonVo> list;//数据集合
    private LineChart chart;//折线图事例
    private TextView date_time;
    private List<AdminNoticesUtil> numlist;
    private String offset = "0";//查询开始位置
    private String showNum = "10";//第一次界面显示的list条数
    private String uid;
    private TextView tv_model;
    private int type;
    private String notifytype;//通知类型
    private RelativeLayout notices_list_back;//返回键
    public  String year,month;
    private DatePopupWindow window;
    private int index;
    private RelativeLayout notices_detail_time;
    private TextView notice_list_type , notice_list_num;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_notice_detail);
        Intent it = getIntent();
        type = it.getIntExtra("type",-1);
        year = it.getStringExtra("year");
        month = it.getStringExtra("month");
        num = it.getIntExtra("num" ,1);
        init();
        if (!UtilTool.isNetwork(this)) {
            UtilTool.showToast(this, Constantes.NETERROR);
            listview.onLoadMoreComplete(true, "1");
        } else {
            listview.AutoRefresh();
            new GetAdminNoticesDetail().execute();
        }
    }
    public void init() {
        list = new ArrayList<NotifyJsonVo>();
        numlist = new ArrayList<AdminNoticesUtil>();
        tv_model = (TextView) findViewById(R.id.tv_model);


        notices_list_back = (RelativeLayout) findViewById(R.id.notices_list_back);
        notices_list_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        admin_notice_detail_screen = (RelativeLayout) findViewById(R.id.admin_notice_detail_screen);
        UtilTool.SetTitle(AdminNoticesListActivity.this, admin_notice_detail_screen);
        listview = (CustomListView) findViewById(R.id.notices_detail_list01);
        head = LayoutInflater.from(this).inflate(
                R.layout.notice_detail_head, null);
        listview.addHeaderView(head);
        chart = (LineChart) head.findViewById(R.id.chart1);
        //通知类型
        notice_list_type = (TextView) head.findViewById(R.id.notice_list_type);
        //通知数
        notice_list_num = (TextView) head.findViewById(R.id.notice_list_num);
        notice_list_num.setText(num+"");
        if(type==0){
            notifytype = "stop";
            tv_model.setText(Constantes.STOP_NUM);
            notice_list_type.setText(Constantes.STOP_NUM);
        }else if(type==1){
            notifytype = "change";
            tv_model.setText(Constantes.CHANGE_NUM);
            notice_list_type.setText(Constantes.CHANGE_NUM);
        }else {
            notifytype = "temp";
            tv_model.setText(Constantes.TEMP_NUM);
            notice_list_type.setText(Constantes.TEMP_NUM);
        }
        notices_detail_time = (RelativeLayout) head.findViewById(R.id.notices_detail_time);

        notices_detail_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataView();
            }
        });
        date_time = (TextView) head.findViewById(R.id.notice_detail_head_date);
        if(month==null||"13".equals(month)){
            date_time.setText(year);
        }else {
            date_time.setText(year + "-" + month);
        }
        adapter = new ManagerNoticesAdapter(AdminNoticesListActivity.this, list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position!=1) {
                    Intent it = new Intent(AdminNoticesListActivity.this, AdminNoticesDetailActivity.class);
                    Log.i("position", position + "");
                    it.putExtra("nid", list.get(position - 2).getId());
                    it.putExtra("readnum", list.get(position - 2).getReadnum());
                    it.putExtra("countnum", list.get(position - 2).getCountnum());
                    startActivity(it);
                }
            }
        });
        //下拉刷新
        listview.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                Log.i("下拉刷新", "下拉刷新");
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    UtilTool.showToast(AdminNoticesListActivity.this, Constantes.NETERROR);
                    listview.onRefreshComplete();
                } else {
                    //联网更新数据
                    new GetRefreshDatas().execute();
                }
            }

        });
        //上拉刷新
        listview.setOnLoadListener(new CustomListView.OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                Log.i("上拉刷新", "上拉刷新");
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    UtilTool.showToast(AdminNoticesListActivity.this, Constantes.NETERROR);
                    listview.onLoadMoreComplete(true, "0");
                } else {
                    //联网更新数据
                    new GetLoadDatas().execute();
                }

            }
        });
    }

    /**
     * 刚进入的时候 加载数据
     */
    private class GetAdminNoticesDetail extends AsyncTask<Void, Void, AdminNoticeDetailUtil> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected AdminNoticeDetailUtil doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(AdminNoticeDetailUtil noticesGroupInfo) {
            super.onPostExecute(noticesGroupInfo);

            if ((null == noticesGroupInfo.getError()) || ("1".equals(noticesGroupInfo.getError()))) {
                UtilTool.showToast(AdminNoticesListActivity.this, Constantes.NETERROR);
            } else {
                int off=Integer.parseInt(offset);
                off+=10;
                offset =String.valueOf(off);
                list = noticesGroupInfo.getNotifylist();
                numlist = noticesGroupInfo.getDaynumlist();
                getCount(numlist);
                setupChart(getData(numlist.size()));
                adapter.notifyDataSetChangedEx(list);
            }
            listview.onRefreshComplete();
        }
    }

    /**
     * 从网络上获取数据
     *
     * @return 返回JSON数据
     */
    public String GetJson() {
        String json = "";
        String loadUrl = RemoteURL.USER.ADMIN_NOTICE_DETAIL;

        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("detailyearormonth009182", "detailyearormonth139341");
        sigParams.put("detailyearormonth892010", "detailyearormonth675952");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("year", year);
        params.put("month", month);
        params.put("notifytype", notifytype);
        params.put("offset",offset);
        params.put("length", "10");
        try {
            json = HttpUtil.getUrlWithSig(loadUrl, params, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
        return json;
    }

    public AdminNoticeDetailUtil GetList(String json) {
        AdminNoticeDetailUtil notifyJsonVoList = new AdminNoticeDetailUtil();
        if ((null == json) || ("".equals(json))) {
            return notifyJsonVoList;
        } else {
            notifyJsonVoList = FastjsonUtil.json2object(json,
                    AdminNoticeDetailUtil.class);
            Log.i("notifyJsonVoList", notifyJsonVoList + "");
            return notifyJsonVoList;
        }
    }

    private void setupChart(LineData data) {

        // no description text
        chart.setDescription("");
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(false);// 设置是否可以触摸
        chart.setDragEnabled(false);// 是否可以拖拽
        chart.setScaleEnabled(false);// 是否可以缩放
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setSpaceBetweenLabels(6);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setTextColor(Color.WHITE);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setLabelCount(5);
        rightAxis.setDrawGridLines(false);
        Legend l = chart.getLegend();
        l.setEnabled(false);
        // set data
        chart.setData(data);

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        //未0时禁止动画
        chart.animateX(0);
    }

    private LineData getData(int count) {
        XAxis xAxis = chart.getXAxis();
        xAxis.SetCount(count);
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count+1; i++) {
            if(i==0){
                xVals.add("");
            }else {
                xVals.add((i) + "");
            }
//            xVals.add(i + 1 + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            yVals.add(new Entry(Integer.parseInt(numlist.get(i).getNum()), i+1));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);


        set1.setLineWidth(1.5f);
        set1.setCircleSize(3.5f);
        set1.setHighLightColor(Color.WHITE);
        set1.setCircleColorHole(0xDDF6C02D);
        set1.setDrawCircleHole(true);

//        set1.setDrawCubic();//直线还是曲线
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setDrawValues(false);//是否唉点上显示数字

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        return data;
    }

    /*
     * 下拉刷新
     * 异步加载数据
     */
    private class GetRefreshDatas extends AsyncTask<Void, Void, AdminNoticeDetailUtil> {

        @Override
        protected AdminNoticeDetailUtil doInBackground(Void... params) {
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
        protected void onPostExecute(AdminNoticeDetailUtil result) {
            super.onPostExecute(result);
//  			flags=true;
            if (result.getError() != null && result.getNotifylist().size() > 0) {
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
                list.clear();
                list.addAll(result.getNotifylist());
                System.out.println("下拉刷新");
                adapter.notifyDataSetChangedEx(list);
                if (list.size() < 8) {
                    listview.onLoadMoreComplete(true, "1");
                }
                listview.onRefreshComplete(); // 加载更多完成
            } else {
                listview.onRefreshComplete(); // 加载更多完成
            }
        }
    }

    /*
  * 上拉加载更多
  * 异步加载数据
  */
    private class GetLoadDatas extends AsyncTask<Void, Void, AdminNoticeDetailUtil> {

        @Override
        protected AdminNoticeDetailUtil doInBackground(Void... params) {

            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(AdminNoticeDetailUtil result) {
            super.onPostExecute(result);

            if (result.getError() != null && result != null && result.getNotifylist().size() > 0) {
//  				Notices noid = result.get((result.size() - 1));
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
//  				listNews.clear();

                list.addAll(result.getNotifylist());
                adapter.notifyDataSetChangedEx(list);

                listview.onLoadMoreComplete(true, "0");


            } else {
                listview.onLoadMoreComplete(false, "1");// 加载更多完成
            }
        }
    }
    private void showDataView()
    {
        if(month==null){
            month="13";
        }
        window = new DatePopupWindow(AdminNoticesListActivity.this, "", new DatePopupWindow.OnDateSelectListener()
        {

            @Override
            public void onDateSelect(int year01,int month01)
            {
                year=year01+"";
                if(month01<10){
                    month = "0"+month01;
                    date_time.setText(year + "-" + month);
                }else if(month01<13){
                    month = month01+"";
                    date_time.setText(year + "-" + month);
                }else{
                    month = null;
                    date_time.setText(year );
                }

                listDown();
            }
        },Integer.parseInt(year),Integer.parseInt(month)-1);
        window.showWindow(date_time);
    }
    public void listDown(){
        listview.AutoRefresh();
        if(!UtilTool.isNetwork(this)){
            UtilTool.showToast(this, Constantes.NETERROR);
            listview.onLoadMoreComplete(true,"1");
            listview.onRefreshComplete();
        }else{
            new GetAdminNoticesDetail().execute();
        }

    }
    public void getCount(List<AdminNoticesUtil> list1){
        num = 0;
        for (int i = 0; i < list1.size(); i++) {
            num = Integer.parseInt(list1.get(i).getNum()) + num;
        }
        notice_list_num.setText(num+"");

    }
}

