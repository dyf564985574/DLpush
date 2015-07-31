package com.toc.dlpush.index.admin;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.toc.dlpush.R;
import com.toc.dlpush.adapter.ManagerUserAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.StuffJson;
import com.toc.dlpush.util.User;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.CustomListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/22.
 */
public class StuffAnalysisActivity extends Fragment {
    private View rootView;
    private Context context;
    private String uid;
    private RelativeLayout stuff_screen;
    private PieChart mChart;
    private StuffJson json;
    private LinearLayout linearlayout01 ,linearlayout02 ,linearlayout03;
    private List<User> userList;
    private TextView stuff_count,stuff_online,stuff_offline,stuff_unlogin;
//    private String[] mParties = new String[] {
//            "上线率", "在线率", "未装机率", "Party D", "Party E", "Party F", "Party G", "Party H",
//            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
//            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
//            "Party Y", "Party Z"
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.stuff_main, container, false);
        context = getActivity();
        uid = UtilTool.getSharedPre(context, "users", "uid", "0");
        Log.i("TabTipsActivity", "TabCaringActivity");
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userList = new ArrayList<User>();
        stuff_screen = (RelativeLayout) rootView.findViewById(R.id.stuff_screen);
        stuff_online = (TextView) rootView.findViewById(R.id.stuff_online);
        stuff_count = (TextView) rootView.findViewById(R.id.stuff_count);
        stuff_offline = (TextView) rootView.findViewById(R.id.stuff_offline);
        stuff_unlogin = (TextView) rootView.findViewById(R.id.stuff_unlogin);
        UtilTool.SetTitle(getActivity(), stuff_screen);
        if(!UtilTool.isNetwork(getActivity())){
            UtilTool.showToast(getActivity(), Constantes.NETERROR);
        } else {
            new GetStuff().execute();
        }
//        init();
    }

    private void init() {
        mChart = (PieChart) rootView.findViewById(R.id.stuff_chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
//        mChart.setDescriptionPosition(100,100);
        mChart.setHoleColorTransparent(true);

        mChart.setTransparentCircleColor(Color.WHITE);

        mChart.setHoleRadius(20f);//半径
        mChart.setTransparentCircleRadius(0f);// 半透明圈

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);// 初始旋转角度
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);//是否能手动旋转

        setData(3, 100);

        mChart.animateY(600, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.);
        l.setEnabled(false);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < count; i++) {
            //未登录用户
            if(i==0){
                yVals1.add(new Entry(Float.parseFloat(json.getNologin()), i));
            }else if(i==1){
                //离线用户
                yVals1.add(new Entry(Float.parseFloat(json.getIslogin())-Float.parseFloat(json.getOnline()), i));

            }else{
                yVals1.add(new Entry(Float.parseFloat(json.getOnline()), i));
            }
//            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count; i++)
            xVals.add(i+"");

        PieDataSet dataSet = new PieDataSet(yVals1, "");

        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f); // 选中态多出的长度
        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
//        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setHighlightEnabled(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);
//        data.setValueTypeface(tf);
        mChart.setData(data);
        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }
    /**
     * 刚进入的时候 加载数据
     */
    private class GetStuff extends AsyncTask<Void, Void, StuffJson> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected StuffJson doInBackground(Void... params) {
            return GetList(GetJson());
        }

        @Override
        protected void onPostExecute(StuffJson stuffJson) {
            super.onPostExecute(stuffJson);

            if ((null == stuffJson.getError()) || ("1".equals(stuffJson.getError()))) {
                UtilTool.showToast(getActivity(), Constantes.NETERROR);
            } else {
                     json = stuffJson;
                stuff_count.setText(Constantes.TOTAL_USER + json.getAllnum()+Constantes.PEOPLE);
                stuff_online.setText(Constantes.ONLINE_USER + json.getOnline()+Constantes.PEOPLE);
                stuff_offline.setText(Constantes.OFFLINE_USER +(int)(Float.parseFloat(json.getIslogin())-Float.parseFloat(json.getOnline()))+Constantes.PEOPLE);
                stuff_unlogin.setText(Constantes.NOTLOGIN_USER + json.getNologin()+Constantes.PEOPLE);
                init();
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
        String loadUrl = RemoteURL.USER.STUFF;

        //sig加密
        HashMap<String, String> sigParams = new HashMap<String, String>();
        sigParams.put("countbyisappislogin23417", "countbyisappislogin23417");
        sigParams.put("countbyisappislogin93028", "countbyisappislogin93028");
        try {
            json = HttpUtil.getUrlWithSig(loadUrl,  sigParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("json", json);
        return json;
    }

    public StuffJson GetList(String json) {
        StuffJson stuffJson = new StuffJson();
        if ((null == json) || ("".equals(json))) {
            return stuffJson;
        } else {
            stuffJson = FastjsonUtil.json2object(json,
                    StuffJson.class);
            Log.i("notifyJsonVoList", stuffJson + "");
            return stuffJson;
        }
    }
}
