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

import com.toc.dlpush.R;
import com.toc.dlpush.adapter.ManagerUserAdapter;
import com.toc.dlpush.http.HttpUtil;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;
import com.toc.dlpush.util.StuffJson;
import com.toc.dlpush.util.User;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.PieChart02View;
import com.toc.mylibrary.org.xclcharts.chart.PieChart;
import com.toc.mylibrary.org.xclcharts.chart.PieData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/28.
 */
public class StuffActivity extends Fragment {
    private View rootView;
    private View head;
    private Context context;
    private String uid;
    private RelativeLayout stuff_screen;
    private StuffJson json;
    private ManagerUserAdapter adapter;
    private LinearLayout linearlayout01 ,linearlayout02 ,linearlayout03;
    private List<User> userList;
    private PieChart02View pieChart02View;
    private LinkedList<PieData> chartData =  new LinkedList<PieData>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.stuffer_main, container, false);
        context = getActivity();
        uid = UtilTool.getSharedPre(context, "users", "uid", "0");
        Log.i("TabTipsActivity", "TabCaringActivity");
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pieChart02View = (PieChart02View) rootView.findViewById(R.id.piechart);
        userList = new ArrayList<User>();
        stuff_screen = (RelativeLayout) rootView.findViewById(R.id.stuff_screen);

        UtilTool.SetTitle(getActivity(), stuff_screen);
        if(!UtilTool.isNetwork(getActivity())){
            UtilTool.showToast(getActivity(), Constantes.NETERROR);
        } else {
            new GetStuff().execute();
        }
        init();
    }
    public void init(){
        //设置图表数据源
        PieData pieData = new PieData("","20%",20, Color.rgb(77, 83, 97)) ;
//		pieData.setCustLabelStyle(XEnum.SliceLabelStyle.INSIDE,Color.WHITE);
        chartData.add(pieData);

//        chartData.add(new PieData("","白糖(5%)",5,Color.rgb(75, 132, 1)));

        //将此比例块突出显示
        PieData pd = new PieData("","35%",35,Color.rgb(180, 205, 230));
        pd.setItemLabelRotateAngle(45.f);
        chartData.add(pd);

        PieData pdOther = new PieData("","15%",15,Color.rgb(148, 159, 181));
//		pdOther.setCustLabelStyle(XEnum.SliceLabelStyle.INSIDE,Color.BLACK);
        chartData.add(pdOther);

        PieData pdTea = new PieData("","30%",30,Color.rgb(253, 180, 90),true);
//		pdTea.setCustLabelStyle(XEnum.SliceLabelStyle.OUTSIDE,Color.rgb(253, 180, 90));
        chartData.add(pdTea);

        pieChart02View.setChart(chartData);
        pieChart02View.start();
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
            json = HttpUtil.getUrlWithSig(loadUrl, sigParams);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
