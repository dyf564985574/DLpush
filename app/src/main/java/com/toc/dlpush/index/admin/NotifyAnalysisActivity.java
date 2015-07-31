package com.toc.dlpush.index.admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.toc.dlpush.R;
import com.toc.dlpush.dao.ChangeNumDao;
import com.toc.dlpush.dao.StopNumDao;
import com.toc.dlpush.dao.TempNumDao;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.mpchartview.ChartItem;
import com.toc.dlpush.mpchartview.LineChartItem;
import com.toc.dlpush.notices.AdminNoticesListActivity;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.util.AdminNoticesUtil;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.NoticesDao;
import com.toc.dlpush.util.ThreeNotices;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;
import com.toc.dlpush.wheelview.DatePopupWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * Created by yuanfei on 2015/7/22.
 */
public class NotifyAnalysisActivity extends Fragment {
    private View rootView;//总体布局
    private View head;
    private Context context;
    TextView time_tv;
    private String uid;//人员的ID
    private RelativeLayout notices_screen;//抬头布局
    private CustomListView listView;//上拉刷新 下拉刷新布局
    //计划停电
    private List<AdminNoticesUtil> stoplist = new ArrayList<AdminNoticesUtil>();
    private List<AdminNoticesUtil> chartList = new ArrayList<AdminNoticesUtil>();
    //变线通知
    private List<AdminNoticesUtil> changelist = new ArrayList<AdminNoticesUtil>();
    //临时停电
    private List<AdminNoticesUtil> templist = new ArrayList<AdminNoticesUtil>();
    private String[] name = new String[]{Constantes.STOP_NUM, Constantes.TEMP_NUM, Constantes.CHANGE_NUM};
    private int[] num = new int[3];
    private int num01;
    private int num02;
    private int num03;
    private List<AdminNoticesUtil> namelist = new ArrayList<AdminNoticesUtil>();
    private DatePopupWindow window;
    public String year, month;
    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.notices_analy_main, container, false);
        context = getActivity();

        if(Constantes.ADMINUPDATE_NOTICE ==1){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR) + "";
            index = c.get(Calendar.MONTH) + 1;
            if (index < 10) {
                month = "0" + index;
            } else {
                month = index + "";
            }
            UtilTool.setSharedPre(getActivity(),"notice","year",year);
            UtilTool.setSharedPre(getActivity(),"notice","month",month);
        }else{
            year = UtilTool.getSharedPre(getActivity(),"notice","year","1");
            month = UtilTool.getSharedPre(getActivity(),"notice","month","1");
        }


        uid = UtilTool.getSharedPre(context, "users", "uid", "0");
        Log.i("TabTipsActivity", "TabCaringActivity");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        notices_screen = (RelativeLayout) rootView.findViewById(R.id.notices_screen);
        UtilTool.SetTitle(getActivity(), notices_screen);
        listView = (CustomListView) rootView.findViewById(R.id.listView1);
        head = LayoutInflater.from(getActivity()).inflate(
                R.layout.listchart_head, null);

        time_tv = (TextView) head.findViewById(R.id.tv_time);
        time_tv.setText(year + "-" + month);
        listView.addHeaderView(head);
        SetAdapter();
        if (!UtilTool.isNetwork(getActivity())) {
            UtilTool.showToast(context, Constantes.NETERROR);
            GetUserlink();
            Intcount();
            SetAdapter();
        } else {
            if(GetUserlink()){
                if(Constantes.ADMINUPDATE_NOTICE == 0){
                    Intcount();
                    SetAdapter();
                }else{
                    listDown();
                    Constantes.ADMINUPDATE_NOTICE = 0;
                }

            }else {
//                new GetAdminNotices().execute();
                listDown();
            }

        }
        Constantes.ADMINUPDATE_NOTICE = 0;
        //list的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    showDataView();
                } else {
                    Log.i("tv_model", position + "");
                    Intent it = new Intent(getActivity(), AdminNoticesListActivity.class);
                    it.putExtra("type", position - 2);
                    it.putExtra("year", year);
                    it.putExtra("month", month);
                    it.putExtra("num" ,num[position-2]);
                    startActivity(it);
                }
            }
        });
        //下拉刷新
        listView.setOnRefreshListener(new CustomListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
//                Log.i("下拉刷新", "下拉刷新");
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    UtilTool.showToast(context, Constantes.NETERROR);
                    listView.onRefreshComplete();
                } else {
                    //联网更新数据
                    new GetAdminNotices().execute();
                }
            }
        });
    }

    public void SetAdapter() {
        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 30 items
        for (int i = 0; i < 3; i++) {
            if (chartList != null && chartList.size() != 0) {
                chartList.clear();
            }
            if (i == 0) {
                chartList.addAll(stoplist);
            } else if (i == 1) {
                chartList.addAll(templist);
            } else {
                chartList.addAll(changelist);
            }
            list.add(new LineChartItem(generateDataLine(chartList.size()), context.getApplicationContext(), name[i], num[i] ,chartList.size()));
        }
        ChartDataAdapter cda = new ChartDataAdapter(context.getApplicationContext(), list);
        listView.setAdapter(cda);
    }

    /**
     * adapter that supports 3 different item types
     */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return
     */
    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();
        //添加X轴数据 第一个
        for (int i = 0; i < cnt; i++) {
            e1.add(new Entry(Integer.parseInt(chartList.get(i).getNum()), i + 1));
        }
        LineDataSet d1 = new LineDataSet(e1, "");
        Log.i("LineDataSet111", "LineDataSet111");
        d1.setLineWidth(1.5f);
        d1.setCircleSize(3.5f);
//        d1.setHighLightColor(getResources().getColor(R.color.transparent));
        d1.setColor(Color.WHITE);
        d1.setCircleColor(Color.WHITE);
//        d1.setCircleColorHole(getResources().getColor(R.color.transparent));
        d1.setHighLightColor(Color.WHITE);
        d1.setCircleColorHole(0xDDF6C02D);
        d1.setDrawCircleHole(true);
        d1.setValueTextColor(Color.WHITE);
//        d1.setDrawFilled(false);
        d1.setDrawValues(false);
        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);

        LineData cd = new LineData(getMonths(stoplist.size()), sets);
        return cd;
    }

    //x轴坐标
    private ArrayList<String> getMonths(int count) {

        ArrayList<String> m = new ArrayList<String>();
        for (int i = 0; i < count + 1; i++) {
            if (i == 0) {
                m.add("");
            } else {
                m.add((i) + "");
            }
        }
        return m;
    }

    /**
     * 刚进入的时候 加载数据
     */
    private class GetAdminNotices extends AsyncTask<Void, Void, ThreeNotices> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ThreeNotices doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(ThreeNotices noticesGroupInfo) {
            super.onPostExecute(noticesGroupInfo);

            if ((null == noticesGroupInfo.getError()) || ("1".equals(noticesGroupInfo.getError()))) {
                UtilTool.showToast(context, Constantes.NETERROR);
                listView.onRefreshComplete(); // 加载更多完成
            } else {
                Empty_data();
                stoplist = noticesGroupInfo.getStoplist();
                changelist = noticesGroupInfo.getChangelist();
                templist = noticesGroupInfo.getTemplist();
                AddNotices(stoplist,templist,changelist);
                Intcount();
                SetAdapter();

                listView.onRefreshComplete(); // 加载更多完成
            }
        }
    }

    /**
     * 从网络上获取数据
     *
     * @return 返回JSON数据
     */
    public String GetJson() {
        String json = "";
        String loadUrl = RemoteURL.USER.THREENOTICES;

        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("countyearormonth009182", "countyearormonth139341");
        sigParams.put("countyearormonth892010", "countyearormonth675952");
        HashMap<String, String> params = new HashMap<String, String>();
        if("13".equals(month)){
            params.put("month", null);
        }else{
            params.put("month", month);
        }
        params.put("year", year);

        try {
            json = HttpUtil.getUrlWithSig(loadUrl, params, sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
        return json;
    }

    public ThreeNotices GetList(String json) {
        ThreeNotices notifyJsonVoList = new ThreeNotices();
        if ((null == json) || ("".equals(json))) {
            return notifyJsonVoList;
        } else {
            notifyJsonVoList = FastjsonUtil.json2object(json,
                    ThreeNotices.class);
            Log.i("notifyJsonVoList", notifyJsonVoList + "");
            return notifyJsonVoList;
        }
    }

    private void showDataView() {
        if (month == null) {
            month = "13";
        }
        window = new DatePopupWindow(getActivity(), "", new DatePopupWindow.OnDateSelectListener() {

            @Override
            public void onDateSelect(int year01, int month01) {
                year = year01 + "";
                if (month01 < 10) {
                    month = "0" + month01;
                    time_tv.setText(year + "-" + month);
                } else if (month01 < 13) {
                    month = month01 + "";
                    time_tv.setText(year + "-" + month);
                } else {
                    month = null;
                    time_tv.setText(year);
                }
                UtilTool.setSharedPre(getActivity(),"notice","year",year);
                UtilTool.setSharedPre(getActivity(),"notice","month",month);
//                if (!UtilTool.isNetwork(getActivity())) {
//                    UtilTool.showToast(context, Constantes.NETERROR);
//                    listView.onLoadMoreComplete(true, "1");
//                } else {
//                    new GetAdminNotices().execute();
//                }
                listDown();
            }
        }, Integer.parseInt(year), Integer.parseInt(month) - 1);
        window.showWindow(time_tv);
    }

    //清空数据库
    public void Empty_data() {
        Log.i("getActivity", context + "");
        StopNumDao dao = new StopNumDao(context);
        int a = dao.delete("stopnotices", null, null);
        dao.close();
        TempNumDao dao01 = new TempNumDao(context);
        int a1 = dao01.delete("tempnotices", null, null);
        dao01.close();
        ChangeNumDao dao02 = new ChangeNumDao(context);
        int a2 = dao02.delete("changenotices", null, null);
        dao02.close();
    }

    //读取联系人
    public Boolean GetUserlink() {
        StopNumDao dao = new StopNumDao(context);
        stoplist = dao.queryList(AdminNoticesUtil.class, "stopnotices", new String[]{"*"}, null, null, "", null, null);
        dao.close();
        TempNumDao dao01 = new TempNumDao(context);
        templist = dao01.queryList(AdminNoticesUtil.class, "tempnotices", new String[]{"*"}, null, null, "", null, null);
        dao01.close();
        ChangeNumDao dao02 = new ChangeNumDao(context);
        changelist = dao02.queryList(AdminNoticesUtil.class, "changenotices", new String[]{"*"}, null, null, "", null, null);
        dao02.close();
        Log.i("chartList",stoplist.size()+" "+changelist.size()+"  "+templist.size());
        Log.i("list03",changelist+"");
        if (stoplist.size() == 0&&changelist.size()==0&&templist.size()==0) {
            return false;
        } else {
            return true;
        }
    }
    //添加通知列表
    public void AddNotices(List<AdminNoticesUtil> list01,List<AdminNoticesUtil> list02,List<AdminNoticesUtil> list03){

        StopNumDao dao = new StopNumDao(context);
        dao.Add_contact(list01);
        dao.close();
        TempNumDao dao01 = new TempNumDao(context);
        dao01.Add_contact(list02);
        dao01.close();
        ChangeNumDao dao02 = new ChangeNumDao(context);
        dao02.Add_contact(list03);
        dao02.close();
    }

    public void Intcount(){

        num[0] = 0;
        num[1] = 0;
        num[2] = 0;
        for (int i = 0; i < stoplist.size(); i++) {
            num[0] = Integer.parseInt(stoplist.get(i).getNum()) + num[0];
            num[1] = Integer.parseInt(templist.get(i).getNum()) + num[1];
            num[2] = Integer.parseInt(changelist.get(i).getNum()) + num[2];
        }
        Log.i("Intcount",num[0]+" "+num[1]+" "+num[2]);
    }
    public void listDown(){
        listView.AutoRefresh();
        if(!UtilTool.isNetwork(getActivity())){
            UtilTool.showToast(context, Constantes.NETERROR);
            listView.onLoadMoreComplete(true,"1");
            listView.onRefreshComplete();
        }else{
            new GetAdminNotices().execute();
        }

    }

}
