package com.toc.dlpush.getui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.toc.dlpush.LoginActivity;
import com.toc.dlpush.caring.CaringDetail;
import com.toc.dlpush.notices.NoticesDetialActivity;
import com.toc.dlpush.notices.util.Notify;
import com.toc.dlpush.notices.util.NotifyGroup;
import com.toc.dlpush.notices.util.NotifyJsonVo;
import com.toc.dlpush.tips.ProposeDetail;
import com.toc.dlpush.tips.TipsDetail;
import com.toc.dlpush.util.Constantes;
import com.toc.dlpush.util.FastjsonUtil;

public class PushDemoReceiver extends BroadcastReceiver {
    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    public static String cid;


	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
        NotifyJsonVo notifyJsonVo=new NotifyJsonVo();
		Log.d("GetuiSdkDemo123", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");

			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");


            // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);

			System.out.println("第三方回执接口调用" + (result ? "成功" : "失败")+"  "+taskid+"  "+messageid);
            Log.d("GetuiSdkDemo", "Got Payload:" + payload);
			if (payload != null) {
				String data = new String(payload);
                Log.d("GetuiSdkDemo", "Got Payload:" + data);
                if(data.contains("notify")){
                    String notice=data.substring(0,6);
                    Log.d("GetuiSdkDemo", "notice" + notice);
                        Constantes.NOTICESPUSH = 1;
                        String nid=data.substring(6,data.length());
                        Intent it=new Intent(context, NoticesDetialActivity.class);
                        it.putExtra("nid",nid);
                        if(LoginActivity.path == 1){
                            it.putExtra("index",3);
                        }else {
                            it.putExtra("index", 2);
                        }
                        //广播跳转页面必须加这一句  因为广播是没有界面的
                        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(it);
                    }else if(data.contains("comment")){
                        String nid=data.substring(7,data.length());
                        Log.i("data",nid);
                       if(LoginActivity.path==1){
                           Intent it=new Intent(context, TipsDetail.class);
                           it.putExtra("id",nid);
                           it.putExtra("index",2);
                           it.putExtra("flag",1);
                           it.putExtra("path",1);
                           //广播跳转页面必须加这一句  因为广播是没有界面的
                           it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           context.startActivity(it);

                       }else if(LoginActivity.path==2){
                           Intent it=new Intent(context, ProposeDetail.class);
                           it.putExtra("id",nid);
                           it.putExtra("index",2);
                           it.putExtra("flag",2);
                           it.putExtra("path",1);
                           //广播跳转页面必须加这一句  因为广播是没有界面的
                           it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                           context.startActivity(it);
                       }
                    }else if(data.contains("feedback")){
                        String nid=data.substring(8,data.length());
                        Log.i("data",nid);
                        if(LoginActivity.path==1){
                            Intent it=new Intent(context, TipsDetail.class);
                            it.putExtra("id",nid);
                            it.putExtra("index",1);
                            it.putExtra("flag",2);
                            it.putExtra("path",2);
                            Log.i("data",1+"");
                            //广播跳转页面必须加这一句  因为广播是没有界面的
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(it);
                        }else if(LoginActivity.path==2){
                            Intent it=new Intent(context, ProposeDetail.class);
                            it.putExtra("id",nid);
                            it.putExtra("index",2);
                            it.putExtra("flag",1);
                            Log.i("data",2+"");
                            it.putExtra("path",2);
                            //广播跳转页面必须加这一句  因为广播是没有界面的
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(it);
                        }

                }
                else{
                    Intent it=new Intent(context, CaringDetail.class);
                    it.putExtra("nid",data);
                    it.putExtra("index",2);
                    //广播跳转页面必须加这一句  因为广播是没有界面的
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(it);
                }
//                System.exit(0);
        }

            break;
		case PushConsts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			cid = bundle.getString("clientid");
            Log.i("GetuiSdkDemo123",cid);

			break;
		case PushConsts.THIRDPART_FEEDBACK:
            Log.i("GetuiSdkDemo","haole ");
			break;
		default:
			break;
		}
	}
    public NotifyGroup GetList(String json){
        NotifyGroup notifyJsonVoList=new NotifyGroup();
        notifyJsonVoList = FastjsonUtil.json2object(json,
                NotifyGroup.class);
        Notify jsonVo=new Notify();
        jsonVo=notifyJsonVoList.getNotify();
        Log.i("notifyJsonVoList",notifyJsonVoList+"");
        return notifyJsonVoList;

    }
}
