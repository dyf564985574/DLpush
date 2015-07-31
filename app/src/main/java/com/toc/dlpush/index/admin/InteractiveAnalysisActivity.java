package com.toc.dlpush.index.admin;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.toc.dlpush.R;
import com.toc.dlpush.index.admin.interactive.InteractiveProposeActivity;
import com.toc.dlpush.index.admin.interactive.InteractiveSuggestActivity;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.TitleBarView;
import com.toc.dlpush.wheelview.DatePopupWindow;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yuanfei on 2015/7/22.
 */
public class InteractiveAnalysisActivity extends Fragment {
    private View rootView;
    private Context context;
    private String uid;
    private RelativeLayout tip_screen;
    private PieChart mChart;
    //显示/隐藏动画效果
    private Animation mShowAction;
    private  Animation mHiddenAction;
    private TitleBarView mTitleBarView;
    private FrameLayout child_fragment;
    private static final String TAG = "InteractiveAnalysisActivity";
    private RelativeLayout inter_time;
    private DatePopupWindow window;
    public  String year,month;
    private int index;
    private TextView tv_inter_time;
    private int flag = 0;
    private boolean come = false;
    private   Calendar c;
    FrameLayout inter_child_fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.interactive_main, container, false);
        context = getActivity();
        uid = UtilTool.getSharedPre(context, "users", "uid", "0");
        SetYear();
        Log.i("TabTipsActivity", "TabCaringActivity");
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tip_screen = (RelativeLayout) rootView.findViewById(R.id.tip_screen);
        inter_child_fragment = (FrameLayout) rootView.findViewById(R.id.inter_child_fragment);
        UtilTool.SetTitle(getActivity(), tip_screen);
        init();
    }

    private void init() {
        //显示的动画效果
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        //消失的动画效果
        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHiddenAction.setDuration(500);
        mTitleBarView=(TitleBarView) rootView.findViewById(R.id.title_bar);
        child_fragment= (FrameLayout) rootView.findViewById(R.id.child_fragment);
        inter_time = (RelativeLayout) rootView.findViewById(R.id.inter_time);
        inter_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataView();
            }
        });
        tv_inter_time = (TextView) rootView.findViewById(R.id.tv_inter_time);
        mTitleBarView.getTitleLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleLeft().isEnabled()||come) {
                    if(!come){
//                        SetYear();
                        tv_inter_time.setText(year + "-" + month);
                    }
                    come = false;
                    flag = 0;
//                    SetYear();

                    Log.i("mTitleBarView","mTitleBarView");
                    mTitleBarView.getTitleLeft().setEnabled(false);
                    mTitleBarView.getTitleRight().setEnabled(true);
                    Bundle bundle = new Bundle();
                    bundle.putString("month",month);
                    bundle.putString("year",year);
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    InteractiveSuggestActivity newsFragment=new InteractiveSuggestActivity();
                    newsFragment.setArguments(bundle);
                    ft.replace(R.id.inter_child_fragment, newsFragment,InteractiveAnalysisActivity.TAG);
                    //ft.addToBackStack(TAG);
                    ft.commit();
                }
            }
        });

        mTitleBarView.getTitleRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleRight().isEnabled()||come) {
                    if(!come){
//                        SetYear();
                        tv_inter_time.setText(year + "-" + month);
                    }
                    come = false;
                    flag = 1;
                    Log.i("getTitleRight","getTitleRight");
                    mTitleBarView.getTitleLeft().setEnabled(true);
                    mTitleBarView.getTitleRight().setEnabled(false);
                    Bundle bundle = new Bundle();
                    bundle.putString("month",month);
                    bundle.putString("year",year);
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    InteractiveProposeActivity callFragment=new InteractiveProposeActivity();
                    callFragment.setArguments(bundle);
                    ft.replace(R.id.inter_child_fragment, callFragment,InteractiveAnalysisActivity.TAG);
                    ft.commit();
                }
            }
        });
        if(Constantes.flag==1){
            mTitleBarView.getTitleRight().performClick();
            Constantes.flag=2;
        }else{
            mTitleBarView.getTitleLeft().performClick();
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
                    tv_inter_time.setText(year + "-" + month);
                }else if(month01<13){
                    month = month01+"";
                    tv_inter_time.setText(year + "-" + month);
                }else{
                    month = null;
                    tv_inter_time.setText(year );
                }
                come = true;
              if(flag ==1){

                  mTitleBarView.getTitleRight().performClick();
              }else{
                  mTitleBarView.getTitleLeft().performClick();
              }
            }
        },Integer.parseInt(year),Integer.parseInt(month)-1);
        window.showWindow(inter_time);
    }
    private void SetYear(){


        c  = Calendar.getInstance();
        year = c.get(Calendar.YEAR)+"";
        index = c.get(Calendar.MONTH)+1;
        if(index<10){
            month = "0"+index;
        }else {
            month = index+ "";
        }

    }
}
