package com.toc.dlpush.wheelview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.toc.dlpush.R;

import java.util.Calendar;


/**
 * 性别
 *
 * @author he xiaohui
 *
 *         2015-2-8 下午4:06:12
 */
public class DatePopupWindow extends PopupWindow
{

    private View mMenuView;

    private WheelView year_view, month_view;

    private NumericWheelAdapter yearAdapter, monthAdapter, dayAdapter;

    OnDateSelectListener selectListener;

    String title;

    Calendar c;

    int curYears = 0;
    int curMonth = 0;

    public DatePopupWindow(Activity context, String title,
                           OnDateSelectListener selectListener,int year,int month)
    {
        super(context);
//        this.curYears = year;
//        this.curMonth = month;
        c  = Calendar.getInstance();
        this. curYears = c.get(Calendar.YEAR);
        this.curMonth = c.get(Calendar.MONTH);

        this.title = title;
        this.selectListener = selectListener;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.wheel_date_picker, null);
        year_view = (WheelView) mMenuView.findViewById(R.id.year);
        month_view = (WheelView) mMenuView.findViewById(R.id.month);
        TextView title_textview = (TextView) mMenuView
                .findViewById(R.id.title_textview);
//        title_textview.setText(title);
        Log.i("initView","initView");
        initView();
        // 设置按钮监听
        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener()
        {

            public boolean onTouch(View v, MotionEvent event)
            {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (y < height)
                    {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
      public void SetDate(int curYears,int curMonth){
          this.curYears = curYears;
          this.curMonth = curMonth;
      }
    private void initView() {

        year_view.setAdapter(new NumericWheelAdapter(1950, 2050));
//        yearAdapter = new NumericWheelAdapter(curYears - 50, curYears + 10);
//        year_view.setAdapter(yearAdapter);
        year_view.setCyclic(true);

        month_view.setAdapter(new NumericWheelAdapter(1, 13, "%02d"));
        month_view.setCyclic(true);


        year_view.setCurrentItem(curYears-1950);
        month_view.setCurrentItem(curMonth);


        View close_button = mMenuView.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        View ok_button = mMenuView.findViewById(R.id.ok_button);
        ok_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int month = month_view.getCurrentItem()+1;
                int year = year_view.getCurrentItem()+1950;
                String value = year_view.getCurrentItem()+1950
                        + "-" + month;
                if (null != selectListener) {
                    selectListener.onDateSelect(year,month);
                }
                dismiss();
            }
        });

    }


    public void showWindow(View view)
    {
        showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    public interface OnDateSelectListener
    {
        public void onDateSelect(int year,int month);
    }
}
