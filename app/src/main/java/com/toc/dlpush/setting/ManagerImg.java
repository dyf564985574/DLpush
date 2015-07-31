package com.toc.dlpush.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.toc.dlpush.R;
import com.toc.dlpush.http.RemoteURL;
import com.toc.dlpush.util.FinishActivity;
import com.toc.dlpush.util.NotAsyncImageLoader;

/**
 * Created by yuanfei on 2015/6/24.
 */
public class ManagerImg extends Activity implements View.OnClickListener {
    RelativeLayout manager_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FinishActivity.addActivity(this);
        setContentView(R.layout.manager_img);
        ImageView mImageView = (ImageView) findViewById(R.id.manager_img);
        manager_back= (RelativeLayout) findViewById(R.id.manager_back);
        manager_back.setOnClickListener(this);
        String imageUrl = RemoteURL.USER.IMAGER;
        Log.i("imageUrl",imageUrl);
        NotAsyncImageLoader asynImageLoader = new NotAsyncImageLoader();
        asynImageLoader.showImageAsyn(mImageView, imageUrl, R.mipmap.logo, false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.manager_back:
                finish();
                FinishActivity.deleteActivity(this);
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            FinishActivity.deleteActivity(this);

            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
}
