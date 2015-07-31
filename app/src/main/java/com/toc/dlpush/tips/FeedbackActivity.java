package com.toc.dlpush.tips;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.toc.dlpush.MainActivity;
import com.toc.dlpush.ManagerActivity;
import com.toc.dlpush.R;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.UtilTool;
import com.toc.dlpush.view.TitleBarView;

/**
 * Created by yuanfei on 2015/6/23.
 */
public class FeedbackActivity extends FragmentActivity implements View.OnClickListener {
    TitleBarView mTitleBarView;
    int first=1;//判断是不是第一次进入
    private int currentTab=0;
    RelativeLayout notices_relativeLayout;
    protected static final String TAG = "FeedbackActivity";
    int index=1;
//    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
//        RootView= inflater.inflate(R.layout.submit,container, false);
//        Log.i("TabTipsActivity", "TabTipsActivity");
//        findview();
//
//        return RootView;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FinishActivity.addActivity(this);
        setContentView(R.layout.submit);
        Intent it=getIntent();
        index=it.getIntExtra("index",1);
        findview();
    }

    /**
     * 实例化控件
     */
    public void findview(){
        mTitleBarView=(TitleBarView) findViewById(R.id.title_bar);
        notices_relativeLayout= (RelativeLayout) findViewById(R.id.notices_back);
        notices_relativeLayout.setOnClickListener(this);
        mTitleBarView.getTitleLeft().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleLeft().isEnabled()) {
                    mTitleBarView.getTitleLeft().setEnabled(false);
                    mTitleBarView.getTitleRight().setEnabled(true);

                    FragmentTransaction ft=obtainFragmentTransaction(0);
                    SuggestListActivity newsFragment=new SuggestListActivity();
                    ft.replace(R.id.child_fragment, newsFragment, FeedbackActivity.TAG);
                    //ft.addToBackStack(TAG);
                    currentTab=0;
                    ft.commit();
                }
            }


        });

        mTitleBarView.getTitleRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTitleBarView.getTitleRight().isEnabled()) {
                    mTitleBarView.getTitleLeft().setEnabled(true);
                    mTitleBarView.getTitleRight().setEnabled(false);

                    FragmentTransaction ft=obtainFragmentTransaction(1);
                    ProposeListActivity callFragment=new ProposeListActivity();
                    ft.replace(R.id.child_fragment, callFragment,FeedbackActivity.TAG);
                    currentTab=1;
                    ft.commit();

                }

            }
        });
        if(index==1){
            mTitleBarView.getTitleLeft().performClick();
        }else{
            mTitleBarView.getTitleRight().performClick();
        }

    }




    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
    public FragmentTransaction obtainFragmentTransaction(int index){
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        if(first!=1) {
            if (index > currentTab) {
                ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
            } else if (index == currentTab) {

            } else {
                ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out);
            }
        }else{
            first=2;
        }
        return ft;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notices_back:
//                FinishActivity.deleteActivity(this);
                Intent it = new Intent(FeedbackActivity.this,MainActivity.class);
                it.putExtra("index",2);
                startActivity(it);
                    finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
//            FinishActivity.deleteActivity(this);
            Intent it = new Intent(FeedbackActivity.this,MainActivity.class);
            it.putExtra("index",2);
            startActivity(it);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
