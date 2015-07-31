package com.toc.dlpush.other;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toc.dlpush.R;
import com.toc.dlpush.util.User;

/**
 * Created by yuanfei on 2015/7/16.
 */
public class UserDetailActivity extends Activity {
    User user;
    TextView company_name,contacts,phone,power_type,power_category,address;
    ImageView user_phone,user_message;
    RelativeLayout user_back;//返回按钮
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);
        Intent intent=getIntent();
        user=(User) intent.getSerializableExtra("user");
        init();
    }
    public void init(){
        company_name = (TextView) findViewById(R.id.company_name);
        company_name.setText(user.getCompanyname());
        contacts = (TextView) findViewById(R.id.contacts);
        contacts.setText(user.getLinkman());
        phone = (TextView) findViewById(R.id.phone);
        phone.setText(user.getPhone());
        power_type = (TextView) findViewById(R.id.power_type);
        power_type.setText(user.getCompanytype());
        power_category = (TextView) findViewById(R.id.power_category);
        power_category.setText(user.getCompanyclassify());
        address = (TextView) findViewById(R.id.address);
        address.setText(user.getCompanyaddress());
        user_back = (RelativeLayout) findViewById(R.id.user_back);
        user_phone = (ImageView) findViewById(R.id.user_phone);
        user_message = (ImageView) findViewById(R.id.user_message);
        //发短信
        user_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri smsToUri = Uri.parse("smsto:" + user.getPhone());
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                startActivity(intent);
            }
        });
        user_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getPhone()));
                startActivity(intent);
            }
        });
        user_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
