package com.toc.dlpush.util;

import com.toc.dlpush.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 袁飞 on 2015/6/15.
 */
   public abstract class Constantes {

        public  static final String NETERROR="网络连接失败，请检查网络";
        public static final String LOADING="亲，正在加载，请耐心等待哦...";
        public static final String LANDING="亲，正在登陆，请耐心等待哦...";
        public static final String SUBMITTING="亲，正在提交，请耐心等待哦...";
        public static final String SUBMITSUCCESS="处理成功";
        public static final String SUBMITFAILURE="处理失败,请重新提交";
        public static final String NOPHONE="手机号不存在";
        public static final String PROMPT="提示";
        public static final String EXITACCOUNT="是否退出当前账号？" ;
        public static final String DEFINE="确定";
        public  static final String UPDATA="更新";
        public static final String CANCLE="取消";
        public static final String AGREE="同意";
        public static int aa = 0;
        public  static int index=0;
        public  static int NOTICESPUSH = 0;//推送来时变为1 表示通知列表需要联网刷新
        public static int CARINGPUSH = 0;//推送来时变为1 表示贴士列表需要联网刷新
        public static int SUGGESTPUSH = 0;//推送来时变为1 表示意见列表需要联网刷新
        public static int PROPOSEPUSH = 0;//推送来时变为1 表示建议列表需要联网刷新
        public static int flag=0;//判断是跳进意见列表还是建议列表
        public static String NUMBERLONG="字数超过150";//判断是跳进意见列表还是建议列表
        public static String GETURL="http://fir.im/8sud";
        public static String NONET = "没有设定区域经理";
        public static String NOMANAGER = "没有设定区域经理";
        public static String NOTNULL = "输入不能为空";
        public static String SENSITIVE = "您提交的内容包含敏感词汇，请重新输入";
        public static String CATEGORIES = "请输入关键词";
        public static String STOP = "计划通知";
        public static String TEMP = "临时通知";
        public static String CHANGE = "变线通知";
        public static String STOP_NUM = "计划停电统计";
        public static String TEMP_NUM = "临时停电统计";
        public static String CHANGE_NUM = "线路变更统计";
        public static int ADMINUPDATE_NOTICE = 0;
        public static int ADMINUPDATE_TIP = 0;
        public static int ADMINUPDATE_INTER = 0;
        public static int ADMINUPDATE_STUFF = 0;
        public static String TOTAL_USER = "总用户: ";
        public static String ONLINE_USER = "在线用户: ";
        public static String OFFLINE_USER = "离线用户: ";
        public static String NOTLOGIN_USER = "未登陆用户: ";
        public static String PEOPLE = "人";




}
