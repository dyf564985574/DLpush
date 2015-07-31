package com.toc.dlpush.index;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.toc.dlpush.R;
import com.toc.dlpush.setting.ManagerImg;
import com.toc.dlpush.tips.FeedbackActivity;
import com.toc.dlpush.tips.ProposeActivity;
import com.toc.dlpush.tips.ProposeDetail;
import com.toc.dlpush.tips.ProposeListActivity;
import com.toc.dlpush.tips.SuggestListActivity;
import com.toc.dlpush.tips.SuggestionActivity;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.AddPopWindow;
import com.toc.dlpush.view.TitleBarView;


/**
 * Created by 袁飞 on 2015/5/18.
 * DLpush
 */
public class TabTipsActivity extends Fragment implements View.OnClickListener {
    View RootView;
    RelativeLayout tips_screen,feedback01,regional01,tips_other,regional02;
    TitleBarView mTitleBarView;
    FrameLayout child_fragment;
    ImageView tips_button;
    LinearLayout tips_check;
    RelativeLayout tips_drop;
    private static final String TAG = "TabTipsActivity";
    String managerPhone;
    //显示/隐藏动画效果
    Animation mShowAction;
    Animation mHiddenAction;
    Context context;
    boolean show = true;
    PopupWindow pop;
    LinearLayout pop_btn;
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        context=getActivity();
        managerPhone=UtilTool.getSharedPre(getActivity(), "user", "managerphone", "1");
        if(RootView==null){
            RootView= inflater.inflate(R.layout.activity_tips,container, false);
            Log.i("TabTipsActivity", "TabTipsActivity");
            findview();
        }

        return RootView;
    }
    /**
     * 实例化控件
     */
    public void findview(){
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
        mTitleBarView=(TitleBarView) RootView.findViewById(R.id.title_bar);
        pop_btn= (LinearLayout) RootView.findViewById(R.id.pop_btn);
        child_fragment= (FrameLayout) RootView.findViewById(R.id.child_fragment);
        tips_button= (ImageView) RootView.findViewById(R.id.tips_button);
        tips_button.setOnClickListener(this);
        tips_check= (LinearLayout) RootView.findViewById(R.id.tips_check);
        tips_drop= (RelativeLayout) RootView.findViewById(R.id.tips_drop);
        feedback01= (RelativeLayout) RootView.findViewById(R.id.feedback01);
        feedback01.setOnClickListener(this);
        regional01= (RelativeLayout) RootView.findViewById(R.id.regional01);
        regional01.setOnClickListener(this);
        regional02= (RelativeLayout) RootView.findViewById(R.id.regional02);
        regional02.setOnClickListener(this);
        tips_other= (RelativeLayout) RootView.findViewById(R.id.tips_other);
        tips_other.setOnClickListener(this);
        mTitleBarView.getTitleLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleLeft().isEnabled()) {
                    Log.i("mTitleBarView","mTitleBarView");
                    mTitleBarView.getTitleLeft().setEnabled(false);
                    mTitleBarView.getTitleRight().setEnabled(true);
                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    SuggestionActivity newsFragment=new SuggestionActivity();
                    ft.replace(R.id.child_fragment, newsFragment,TabTipsActivity.TAG);
                    //ft.addToBackStack(TAG);
                    ft.commit();
                }
            }
        });

        mTitleBarView.getTitleRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleRight().isEnabled()) {
                    Log.i("getTitleRight","getTitleRight");
                    mTitleBarView.getTitleLeft().setEnabled(true);
                    mTitleBarView.getTitleRight().setEnabled(false);

                    FragmentTransaction ft=getFragmentManager().beginTransaction();
                    ProposeActivity callFragment=new ProposeActivity();

                    ft.replace(R.id.child_fragment, callFragment,TabTipsActivity.TAG);
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



    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tips_screen= (RelativeLayout) RootView.findViewById(R.id.tip_screen);
        UtilTool.SetTitle(getActivity(), tips_screen);
    }

    @Override
    public void onStart() {
        Log.i("onStart","onStart");
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("onStop","onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("onDestroy","onDestroy");
        super.onDestroy();
    }
    @Override
    public void onPause() {
        Log.i("onPause","onPause");
        super.onPause();
    }

    @Override
    public void onResume() {

//        mTitleBarView.getTitleLeft().performClick();
        Log.i("onResume","onResume");
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tips_button:
                if(show){
                    tips_check.setVisibility(View.VISIBLE);
                    tips_drop.startAnimation(mShowAction);
                    show = false;
                }else{
                    tips_check.setVisibility(View.GONE);
                    tips_drop.startAnimation(mHiddenAction);
                    show = true;
                }

                break;
            case R.id.tips_other:
                tips_check.setVisibility(View.GONE);
                tips_drop.startAnimation(mHiddenAction);
                break;
            case R.id.feedback01:
                Intent it=new Intent(context, FeedbackActivity.class);
                tips_check.setVisibility(View.GONE);
                startActivityForResult(it,1);
                break;
            case R.id.regional02://联系我们
                pop_btn.setVisibility(View.VISIBLE);
                tips_check.setVisibility(View.GONE);
                tips_drop.startAnimation(mHiddenAction);
//                AddPopWindow addPopWindow = new AddPopWindow(getActivity());
//                addPopWindow.showPopupWindow(regional01);
                showPopwindow();
                break;
            case R.id.regional01://区域经理

                Intent it01=new Intent(context, ManagerImg.class);
                tips_check.setVisibility(View.GONE);
                startActivity(it01);
                break;
        }
/**
 *
 *
 */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                Log.i("requestCode", requestCode + "");
                ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getSystemService(this.getActivity().CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isAvailable()) {
                    UtilTool.showToast(context, Constantes.NETERROR);
                } else {
//                    if (mTitleBarView.getTitleLeft().isEnabled()) {
                        Log.i("mTitleBarView","mTitleBarView");
                        mTitleBarView.getTitleLeft().setEnabled(false);
                        mTitleBarView.getTitleRight().setEnabled(true);
                        FragmentTransaction ft=getFragmentManager().beginTransaction();
                        SuggestListActivity newsFragment=new SuggestListActivity();
                        ft.replace(R.id.child_fragment, newsFragment,TabTipsActivity.TAG);
                        //ft.addToBackStack(TAG);
                        ft.commit();
                }
                break;
        }
    }
    /**
     * 显示popupWindow
     */
    private void showPopwindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwindow, null);
        RelativeLayout relative= (RelativeLayout) view.findViewById(R.id.relative);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);


        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0666666);
        window.setBackgroundDrawable(dw);


        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.app_pop);

        // 在底部显示
        window.showAtLocation(getActivity().findViewById(R.id.regional01),
                Gravity.BOTTOM, 0, 0);
        // 设置SelectPicPopupWindow弹出窗体可点击
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        // 刷新状态
        window.update();
        // 这里检验popWindow里的button是否可以点击
//        Button btn_cancle= (Button) view.findViewById(R.id.btn_cancle);

        RelativeLayout first = (RelativeLayout) view.findViewById(R.id.pop_cancle);
        RelativeLayout phone= (RelativeLayout) view.findViewById(R.id.phone);
        RelativeLayout message= (RelativeLayout) view.findViewById(R.id.message);
//        btn_cancle.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                window.dismiss();
//            }
//        });
        first.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pop_btn.setVisibility(View.GONE);
               window.dismiss();
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(managerPhone.equals("1")){

                    UtilTool.showToast(getActivity(),Constantes.NONET);
                }else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + managerPhone));
                    startActivity(intent);
                }
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(managerPhone.equals("1")){
                    UtilTool.showToast(getActivity(),Constantes.NOMANAGER);
                }else {
                    Uri smsToUri = Uri.parse("smsto:" + managerPhone);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                    startActivity(intent);
                }
            }
        });

        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                pop_btn.setVisibility(View.GONE);
//                System.out.println("popWindow消失");
            }
        });

    }
}
