package com.toc.dlpush.http;

/**
 * Created by 袁飞 on 2015/5/18.
 * 接口工具类
 */
public class RemoteURL {
    //网络接口
    public static final String HOSTLOCAL="http://114.215.120.67:80/dlpush/webservice";
    public static final String HOSTIMAGE="http://114.215.120.67:80/dlpush";
    public static final String HTTPHOST="114.215.120.67:80";
//    public static final String HTTPHOST="192.168.1.197:8083";
//    public static final String HOSTLOCAL="http://192.168.1.197:8083/dlpush/webservice";
//    public static final String HOSTIMAGE="http://192.168.1.197:8083/dlpush";
    public interface USER{
        //登陆
        public static final String LOGIN=HOSTLOCAL+"/login/login/{phone}/{passwd}/{imei}/{phonetype}";
        //修改密码
        public static final String REVISE=HOSTLOCAL+"/user/changpasswd/{oldpasswd}/{newpasswd}/{id}?";
        //通知列表
        public static final String Notices=HOSTLOCAL+"/notify/notifylist/{uid}/{offset}/{length}?";
        //通知详情
        public static final String DetialNotices=HOSTLOCAL+"/notify/notifydetail/{nid}/{uid}?";
        //添加联系人
        public static final String Add_Contact=HOSTLOCAL+"/userlink/saveuserlink?";
        //查询联系人
        public static final String Search_Contact=HOSTLOCAL+"/userlink/findallbyuid/{uid}?";
        //修改联系人
        public static final String Revise_Contact=HOSTLOCAL+"/userlink/updateuserlink?";
        //获取联系人列表
        public static final String Contact=HOSTLOCAL+"/userlink/findallbyuid/{uid}?";
        //删除联系人
        public static final String Delete_Contact=HOSTLOCAL+"/userlink/deleteuserlink/{id}/{phone}?";
        //新闻列表
        public static final String CARING=HOSTLOCAL+"/news/newslist/{offset}/{length}?";
        //新闻详情
        public static final String DetailCARING=HOSTLOCAL+"/news/newsdetail/{nid}?";
        //短信分享
        public static final String SMSSHARE=HOSTLOCAL+"/notify/notifyshare/{phones}/{uid}/{nid}?";
        //提交意见与建议
        public static final String SUBMIT=HOSTLOCAL+"/comment/savecommentorfeedback";
        //意见与建议列表
        public static final String SUBMITLIST=HOSTLOCAL+"/comment/commentorfeedbacklist";
        //注销登陆
        public static final String LOGOUT=HOSTLOCAL+"/login/logout/{uid}";
        //更新软件
        public static final String UPDATA=HOSTLOCAL+"/apk/apkupdatecheck/{phonetype}?";
        //意见详情
        public static final String SUGGEST=HOSTLOCAL+"/comment/commentdetail/{id}?";
        //用户查看意见与建议列表
        public static final String TIPSLIST=HOSTLOCAL+"/comment/commentlistbyuid/{offset}/{length}/{type}/{uid}?";
        //意见建议处理接口
        public static final String PROCESS=HOSTLOCAL+"/comment/commentupdatestatus";
        //区域经理图片
        public static final String IMAGER=HOSTIMAGE+"/img/qyjlfbt.jpg";
        //获取客户的个人信息
        public static final String CLIENT=HOSTLOCAL+"/user/getmanagerlist";
        //获取区域经理的电话
        public static final String MANAGERPHONE=HOSTLOCAL+"/user/getmanagerphone/{id}?";
        //模糊查询的接口
        public static final String BLURRY = HOSTLOCAL+"/user/getmanagerlistfuzzy";
        //客户经理通知列表
        public static final String MANAGERNOTIC=HOSTLOCAL+"/notify/managernotifylist/{uid}/{offset}/{length}?";
        //已读人、未读人
        public static final String READ=HOSTLOCAL+"/notify/managernotifyisornoread/{uid}/{nid}/{isread}/{offset}/{length}?";
        //客户经理通知详情
        public static final String MANAGERNOTICEDETAIL=HOSTLOCAL+"/notify/managernotifydetail/{nid}/{uid}?";
        //三种通知图表数据
        public static final String THREENOTICES = HOSTLOCAL+"/notify/countyearormonth";
        //超级管理员通知详情
        public static final String ADMIN_NOTICE_DETAIL = HOSTLOCAL+"/notify/detailyearormonth";
        //贴士 加 列表
        public static final String TIPS_LIST = HOSTLOCAL+"/news/detailyearormonth";
        //超级管理员已读/未读人员列表
        public static final String ADMIN_READ = HOSTLOCAL+"/notify/notifyisornoread";
        //超级管理员互动页面
        public static final String INTERACTIVE = HOSTLOCAL+"/comment/countyearormonth";
        //超级管理员查看人员
        public static final String STUFF = HOSTLOCAL+"/user/countbyisappislogin/";
    }
}
