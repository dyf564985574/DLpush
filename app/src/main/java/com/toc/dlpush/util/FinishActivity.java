package com.toc.dlpush.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanfei on 2015/7/1.
 */
public class FinishActivity {
    public static List<Activity> acticitys=new ArrayList<Activity>();

    public static void addActivity(Activity activity){
        acticitys.add(activity);
    }
    public static void deleteActivity(Activity activity){
        acticitys.remove(activity);
    }
    public static void finishActivity(){
        for (Activity activity : acticitys) {

            activity.finish();
        }
    }
}
