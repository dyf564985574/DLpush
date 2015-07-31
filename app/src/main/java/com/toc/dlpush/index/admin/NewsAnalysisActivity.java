package com.toc.dlpush.index.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.toc.dlpush.R;
import com.toc.dlpush.adapter.CaringAdapter;
import com.toc.dlpush.caring.CaringDetail;
import com.toc.dlpush.caring.util.Caring;
import com.toc.dlpush.dao.NewnumDao;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.notices.AdminNoticesDetailActivity;
import com.toc.dlpush.util.AdminNewsUtil;
import com.toc.dlpush.util.AdminNoticeDetailUtil;
import com.toc.dlpush.util.AdminNoticesUtil;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.NewsDao;
import com.toc.dlpush.util.ThreeNotices;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;
import com.toc.dlpush.wheelview.DatePopupWindow;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/22.
 */
public class NewsAnalysisActivity extends Fragment {
    View rootView;
    private String uid;
    private Context context;
    private CustomListView listView;
    private RelativeLayout caring_screen;
    private View head;
    private BarChart mChart;
    private CaringAdapter adapter;
    private List<Caring> list;
    private List<AdminNoticesUtil> numlist;
    private List<AdminNoticesUtil> newNumlist;
    private int count_num = 0;
    private String offset = "0";//查询开始位置
    private String showNum = "10";//第一次界面显示的list条数
    private DatePopupWindow window;
    public  String year,month;
    private int index;
    private RelativeLayout news_time;//时间选择器
    private TextView tv_news_time;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.new_analy_main,container, false);
        context=getActivity();
        uid= UtilTool.getSharedPre(context, "users", "uid", "0");
        Log.i("TabTipsActivity", "TabCaringActivity");
        return rootView;
    }
    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        caring_screen= (RelativeLayout) rootView.findViewById(R.id.new_screen);
        UtilTool.SetTitle(getActivity(), caring_screen);
        if(Constantes.ADMINUPDATE_TIP ==1){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR) + "";
            index = c.get(Calendar.MONTH) + 1;
            if (index < 10) {
                month = "0" + index;
            } else {
                month = index + "";
            }

            UtilTool.setSharedPre(getActivity(),"new","year",year);
            UtilTool.setSharedPre(getActivity(),"new","month",month);
        }else{
            year = UtilTool.getSharedPre(getActivity(),"new","year","1");
            month = UtilTool.getSharedPre(getActivity(),"new","month","1");
        }
        init();
        SetBar();
        ConnectivityManager connectivityManager=(ConnectivityManager) this.getActivity().getSystemService(this.getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){
            UtilTool.showToast(context, Constantes.NETERROR);
            GetUserlink();
            offset =String.valueOf(list.size());
            adapter.notifyDataSetChangedEx(list);
            listView.onLoadMoreComplete(true,"1");
            SetBar();
        }else{
            if(GetUserlink()){
                adapter.notifyDataSetChangedEx(list);
                offset =String.valueOf(list.size());
                listView.onLoadMoreComplete(true,"1");
                if( Constantes.ADMINUPDATE_TIP == 0) {
                    SetBar();
                }
                else{
                    Constantes.ADMINUPDATE_TIP = 0;
                    listView.AutoRefresh();
                    new GetAdminTips().execute();
                }
            }else {
                listView.AutoRefresh();
                new GetAdminTips().execute();
            }
        }
        Constantes.ADMINUPDATE_TIP = 0;
    }
    public void init(){
        list=new ArrayList<Caring>();
        numlist = new ArrayList<AdminNoticesUtil>();
        newNumlist = new ArrayList<AdminNoticesUtil>();
        adapter = new CaringAdapter(getActivity(),list,false);
        listView = (CustomListView) rootView.findViewById(R.id.new_list01);
        head = LayoutInflater.from(getActivity()).inflate(
                R.layout.news_list_head, null);
        listView.addHeaderView(head);
        listView.setAdapter(adapter);
        tv_news_time = (TextView) head.findViewById(R.id.tv_news_time);
        tv_news_time.setText(year+"-"+month);
        news_time = (RelativeLayout) head.findViewById(R.id.news_time);
        news_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataView();
            }
        });

        Initialzation();
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position!=1) {
                    Intent it = new Intent(context, CaringDetail.class);
                    Log.i("position", position + "");
                    it.putExtra("nid", list.get(position - 2).getId());
                    startActivityForResult(it,1);
                }
            }
        });
        //下拉刷新
        listView.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                Log.i("下拉刷新", "下拉刷新");
                ConnectivityManager connectivityManager = (ConnectivityManager)context. getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
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
                ConnectivityManager connectivityManager = (ConnectivityManager)context. getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    UtilTool.showToast(context, Constantes.NETERROR);
                    listView.onLoadMoreComplete(true, "0");
                } else {
                    //联网更新数据
                    new GetLoadDatas().execute();
                }

            }
        });
    }
    public void Initialzation(){
        mChart = (BarChart) head.findViewById(R.id.chart1);
        mChart.setDescription("");
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        //设置是否有阴影
        mChart.setDrawBarShadow(true);

        //设置是否有网格
        mChart.setDrawGridBackground(false);
        //设置是否能拖拽
        mChart.setScaleEnabled(false);
        //设置是否能触摸
        mChart.setTouchEnabled(false);
//        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setAxisMaxValue(110);
        yAxis.setSpaceTop(2000f);
//        yAxis.setAxisMinValue(25);
//        yAxis.setLabelCount(5);

//        yAxis.setDrawTopYLabelEntry(false);
        XAxis xAxis = mChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(4);
        xAxis.setDrawGridLines(false);

//        xAxis.setYOffset(10);
//        xAxis.setXOffset(10);

        mChart.getAxisLeft().setDrawGridLines(false);
    }
    public void SetBar(){
        count_num = 0;
       for (int i=0;i<numlist.size();i++){
           count_num=count_num + Integer.parseInt(numlist.get(i).getNum());
       }

//        mChart.setBorderWidth(100);
//        mChart.setDragOffsetX(20);
//        mChart.setTop(20);
        getData(numlist.size());
        mChart.animateY(600);
        mChart.getLegend().setEnabled(false);
        Legend l =mChart.getLegend();
        l.setEnabled(false);


//        Legend mLegend = mChart.getLegend();
//        mLegend.setForm(Legend.LegendForm.CIRCLE);
    }
    public void getData(int index){
        if(newNumlist!=null||newNumlist.size()!=0){
            newNumlist.clear();
        }
        //如果按天显示 则每五天按一组数据
        if(index>20){
            int day = 0;
            int num = 0;
              for (int i = 0; i < index; i++) {
                  Log.i("parseInt",Integer.parseInt(numlist.get(i).getNum())+"");
                num = Integer.parseInt(numlist.get(i).getNum())+num;
                if(i==4){
                    AdminNoticesUtil adminNoticesUtil = new AdminNoticesUtil();
                    adminNoticesUtil.setDay(5+"");
                    adminNoticesUtil.setNum(num+"");
                    newNumlist.add(adminNoticesUtil);
                    Log.i("newNumlist",num+"");
                    num = 0;
                }else if(i==9){
                    AdminNoticesUtil adminNoticesUtil1 = new AdminNoticesUtil();
                    adminNoticesUtil1.setDay(10+"");
                    adminNoticesUtil1.setNum(num+"");
                    newNumlist.add(adminNoticesUtil1);
                    Log.i("newNumlist",num+"");
                    num = 0;
                }else if(i==14){
                    AdminNoticesUtil adminNoticesUtil2 = new AdminNoticesUtil();
                    adminNoticesUtil2.setDay(15+"");
                    adminNoticesUtil2.setNum(num+"");
                    newNumlist.add(adminNoticesUtil2);
                    Log.i("newNumlist",num+"");
                    num = 0;
                }else if(i==19){
                    AdminNoticesUtil adminNoticesUtil2 = new AdminNoticesUtil();
                    adminNoticesUtil2.setDay(20+"");
                    adminNoticesUtil2.setNum(num+"");
                    newNumlist.add(adminNoticesUtil2);
                    Log.i("newNumlist",num+"");
                    num = 0;
                }else if(i==24){
                    AdminNoticesUtil adminNoticesUtil3 = new AdminNoticesUtil();
                    adminNoticesUtil3.setDay(25+"");
                    adminNoticesUtil3.setNum(num+"");
                    newNumlist.add(adminNoticesUtil3);
                    Log.i("newNumlist",num+"");
                    num = 0;
                }else if(i==(index-1)){
                    AdminNoticesUtil adminNoticesUtil4 = new AdminNoticesUtil();
                    adminNoticesUtil4.setDay((index)+"");
                    adminNoticesUtil4.setNum(num+"");
                    newNumlist.add(adminNoticesUtil4);
                    Log.i("newNumlist",num+"");
                    num = 0;
                }
            }
        }else{
           newNumlist.addAll(numlist);
        }
        Log.i("newNumlist",newNumlist+"");
        //添加y轴数据
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < newNumlist.size(); i++) {
            if(count_num==0){
                count_num=1;
            }
            yVals1.add(new BarEntry(Float.parseFloat(newNumlist.get(i).getNum())*100/count_num,i));
            Log.i("parseFloat","百分比"+Float.parseFloat(newNumlist.get(i).getNum())*100/count_num);
//            yVals1.add(new BarEntry(Integer.parseInt(newNumlist.get(i).getNum())*100/count_num,i));
        }
        //添加x轴数据
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < newNumlist.size() ; i++) {
            if(newNumlist.size()!=12){
                xVals.add(newNumlist.get(i).getDay());
            }else{
                xVals.add((i+1)+"");
            }

        }

        BarDataSet set1 = new BarDataSet(yVals1, "Y轴设置");
    //  set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        set1.setDrawValues(false);
        set1.setColor(Color.rgb(77,186,122));
        set1.setBarSpacePercent(55f);
        set1.setBarShadowColor(Color.rgb(238,238,238));
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

    //  data.setSpacing(10);
        mChart.setData(data);
        mChart.invalidate();
    }
    /**
     * 刚进入的时候 加载数据
     */
    private class GetAdminTips extends AsyncTask<Void,Void,AdminNewsUtil> {
        @Override
        protected void onPreExecute() {

        }

    @Override
    protected AdminNewsUtil doInBackground(Void... params) {
        return GetList(GetJson());
    }

    @Override
    protected void onPostExecute(AdminNewsUtil adminNewsUtil) {
        super.onPostExecute(adminNewsUtil);

        if((null==adminNewsUtil.getError())||("1".equals(adminNewsUtil.getError()))){
            UtilTool.showToast(context, Constantes.NETERROR);
        }else{
            int off=Integer.parseInt(offset);
            off+=10;
            offset =String.valueOf(off);
            numlist = adminNewsUtil.getDaynumlist();
            list = adminNewsUtil.getNewslist();
            Empty_data();
            AddNews(list,numlist);
            adapter.notifyDataSetChangedEx(list);
            SetBar();
        }
        listView.onRefreshComplete();
    }
}

    /**
     * 从网络上获取数据
     * @return  返回JSON数据
     */
    public String GetJson(){
        String json="";
        String loadUrl= RemoteURL.USER.TIPS_LIST;
        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("detailyearormonth123123", "detailyearormonth123123");
        sigParams.put("detailyearormonth567567", "detailyearormonth567567");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("year",year);
        params.put("month",month);
        params.put("offset",offset);
        params.put("length","10");
        try {
            json= HttpUtil.getUrlWithSig(loadUrl, params, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json",json);
        return json;
    }
    public AdminNewsUtil GetList(String json){
        AdminNewsUtil adminNewsUtil=new AdminNewsUtil();
        if((null==json)||("".equals(json))){
            return adminNewsUtil;
        }else{
            adminNewsUtil = FastjsonUtil.json2object(json,
                    AdminNewsUtil.class);
            Log.i("notifyJsonVoList",adminNewsUtil+"");
            return adminNewsUtil;
        }
    }
    /*
    * 下拉刷新
    * 异步加载数据
    */
    private class GetRefreshDatas extends AsyncTask<Void, Void, AdminNewsUtil> {

        @Override
        protected AdminNewsUtil doInBackground(Void... params) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            offset = "0";
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(AdminNewsUtil result) {
            super.onPostExecute(result);
//  			flags=true;
            if (result.getError() != null &&"0".equals(result.getError())) {
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
                list.clear();
                list.addAll(result.getNewslist());
                Empty_data();
                AddNews(list,numlist);
                SetBar();
                adapter.notifyDataSetChangedEx(list);
                if (list.size() < 8) {
                    listView.onLoadMoreComplete(true, "1");
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
    private class GetLoadDatas extends AsyncTask<Void, Void, AdminNewsUtil> {

        @Override
        protected AdminNewsUtil doInBackground(Void... params) {

            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(AdminNewsUtil result) {
            super.onPostExecute(result);

            if (result.getError() != null && result != null && result.getNewslist().size() > 0) {
//  				Notices noid = result.get((result.size() - 1));
                int off = Integer.parseInt(offset);
                off += 10;
                offset = String.valueOf(off);
//  				listNews.clear();

                list.addAll(result.getNewslist());
                adapter.notifyDataSetChangedEx(list);

                listView.onLoadMoreComplete(true, "0");


            } else {
                listView.onLoadMoreComplete(false, "");// 加载更多完成
            }
        }
    }
    private void showDataView()
    {
        if(month==null){
            month="13";
        }
        window = new DatePopupWindow(getActivity(), "", new DatePopupWindow.OnDateSelectListener()
        {

            @Override
            public void onDateSelect(int year01,int month01)
            {
                year=year01+"";
                if(month01<10){
                    month = "0"+month01;
                    tv_news_time.setText(year + "-" + month);
                }else if(month01<13){
                    month = month01+"";
                    tv_news_time.setText(year + "-" + month);
                }else{
                    month = null;
                    tv_news_time.setText(year );
                }
                UtilTool.setSharedPre(getActivity(),"new","year",year);
                UtilTool.setSharedPre(getActivity(),"new","month",month);
                listDown();
            }
        },Integer.parseInt(year),Integer.parseInt(month)-1);
        window.showWindow(news_time);
    }
    //清空数据库
    public void Empty_data() {
      NewsDao  dao = new NewsDao(context);
        Log.i("dao","dasdas");
        dao.delete("news", null, null);
        dao.close();
        NewnumDao  dao01 = new NewnumDao(context);
        Log.i("dao","dasdas");
        dao01.delete("newnum", null, null);
        dao01.close();
    }
    //读取联系人
    public Boolean GetUserlink() {
        NewsDao dao = new NewsDao(context);
        list = dao.queryList(Caring.class, "news", new String[]{"*"}, null, null, "creatdate desc", null, null);
        dao.close();
        NewnumDao dao01 = new NewnumDao(context);
        numlist = dao01.queryList(AdminNoticesUtil.class, "newnum", new String[]{"*"}, null, null, "", null, null);
        dao01.close();
        if (list.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表
    public void AddNews(List<Caring> list01 ,List<AdminNoticesUtil> list02){
        NewsDao dao=new NewsDao(context);
        dao.AddList(list01);
        dao.close();
        NewnumDao dao01=new NewnumDao(context);
        dao01.Add_contact(list02);
        dao01.close();
    }
    public void listDown(){
        listView.AutoRefresh();
        if(!UtilTool.isNetwork(getActivity())){
            UtilTool.showToast(context, Constantes.NETERROR);
            listView.onLoadMoreComplete(true,"1");
            listView.onRefreshComplete();
        }else{
            new GetAdminTips().execute();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SetBar();
    }
}
