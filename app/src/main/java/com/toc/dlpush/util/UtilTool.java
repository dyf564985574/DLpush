package com.toc.dlpush.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.toc.dlpush.R;
import com.toc.dlpush.setting.util.UserlinkDao;

/**
 * Created by 袁飞 on 2015/5/18.
 * DLpush
 * 工具类集合
 */
public class UtilTool {
    /**
     *
     * @param context
     * @return 屏幕的宽度
     */
    public static int getWidth(Activity context){
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        Log.i("heightPixels", "宽度  " + width );
        return width;
    }

    /**
     *
     * @param context
     * @return 屏幕的高度
     */
    public static int getHeight(Activity context){
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int height = metric.heightPixels;   // 屏幕高度（像素）.
        Log.i("heightPixels",  "高度： " + height);
        return height;
    }

    /**
     * 初始化四个界面 标题栏定义为整个页面的1/13
     * @param context  每个页面的名称
     * @param screen 主容器
     */
    public static void SetTitle(Activity context,View screen){
        int width= getWidth(context);
        int height=getHeight(context);
        screen.getLayoutParams().height=(height/13);
    }
    //定义每个View的高度
    public static void SetView(Activity context,View screen,int index){
        int width= getWidth(context);
        int height=getHeight(context);
        screen.getLayoutParams().height=(height/index);
        screen.getLayoutParams().width=(height/index);
    }
    /**
     * 提示框
     * @param context 上下文
     * @param info 要显示的内容
     */
    public static void showToast(Context context,String info){
        Toast toast=Toast.makeText(context, info, Toast.LENGTH_LONG);
        toast.show();
    }
    /**
     * SharedPreferences    存
     * @param context 上下文
     * @param name 名字
     * @param keys 关键字
     * @param values 值
     */
    public static void setSharedPre(Context context,String name,String keys,String values){
        SharedPreferences preferences=context.getSharedPreferences(name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(keys, values);
        editor.commit();
    }

    /**
     * SharedPreferences    取
     * @param context 上下文
     * @param name 名字
     * @param keys 关键字
     * @param values 默认值
     */
    public static String getSharedPre(Context context,String name,String keys,String values){
        SharedPreferences preferences=context.getSharedPreferences(name, context.MODE_PRIVATE);
        String pass=preferences.getString(keys, values);
        return pass;
    }
   public  static boolean netError(){
       return true;
   }
    //清空指定数据库
    public static boolean ClearData(Context context){
        NoticesDao dao=new NoticesDao(context);
        dao.delete("notices",null,null);
        dao.close();
        NewsDao dao01=new NewsDao(context);
        dao01.delete("news",null,null);
        dao01.close();
        SuggestDao dao02=new SuggestDao(context);
        dao02.delete("suggest",null,null);
        dao02.close();
        ProposeDao dao03=new ProposeDao(context);
        dao03.delete("propose",null,null);
        dao03.close();
        UserlinkDao dao04=new UserlinkDao(context);
        dao04.delete("user_link",null,null);
        dao04.close();
        return true;
    }
    public  static String Notnull(String index){
        if(index==null){
            return "";
        }else{
            return index;
        }
    }
    /**
     * 动画效果
     * @param ll 传入的LinearLayout
     * @param show 标志位true:显示的动画效果;false:隐藏的动画效果
     */
    public static void translateAnimations(LinearLayout ll,boolean show){

        if(show){//显示的动画效果
            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(300);
            ll.startAnimation(mShowAction);
        }else{//隐藏的动画效果
            TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -0.5f);
            mHiddenAction.setDuration(300);
            ll.startAnimation(mHiddenAction);
        }
    } /**
     * 动画效果
     * @return  true:有网；false:无网络
     */
    public static Boolean Connectivity(Context ctx){

        Boolean flag=false;
        ConnectivityManager connectivityManager=(ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()){

            flag=false;
        }else{
            flag=true;
        }
        return flag;
    }
    public static void HorizontalAnimations(Button button,boolean show ){
        if(show){//显示的动画效果
            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(300);
            button.startAnimation(mShowAction);
        }else{//隐藏的动画效果
            TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, -0.5f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    0.0f);
            mHiddenAction.setDuration(300);
            button.startAnimation(mHiddenAction);
        }
    }
    /**
     * 打开或隐藏键盘
     * @param editText 传入的EditTEext
     * @param flag 标志位 true：显示键盘； false：隐藏键盘
     */
    public static void closeInputMethod(EditText editText,boolean flag) {
        if (flag) {//显示键盘
            editText.requestFocus();
            InputMethodManager imm=(InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }else{//隐藏键盘
            InputMethodManager imm=(InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static boolean isNetwork(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo==null||!networkInfo.isAvailable()) {
             return false;
        }else{
            return true;
        }
    }
}
