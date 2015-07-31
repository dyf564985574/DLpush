package com.toc.dlpush.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.toc.dlpush.R;

/**
 * 自定义popupWindow
 *
 * @author wwj
 *
 *
 */
public class AddPopWindow extends PopupWindow {
    private View conentView;
    private RelativeLayout relative;
    public AddPopWindow(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popupwindow, null);
        relative= (RelativeLayout) conentView.findViewById(R.id.relative);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x90666666);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.app_pop);
        RelativeLayout addTaskLayout = (RelativeLayout) conentView
                .findViewById(R.id.pop_cancle);
//        Button btn_cancle= (Button) conentView.findViewById(R.id.btn_cancle);
//       LinearLayout teamMemberLayout = (LinearLayout) conentView
//                .findViewById(R.id.team_member_layout);
        addTaskLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AddPopWindow.this.dismiss();
            }
        });

//        btn_cancle.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                AddPopWindow.this.dismiss();
//            }
//        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }
}
