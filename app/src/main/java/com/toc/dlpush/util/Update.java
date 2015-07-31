package com.toc.dlpush.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;


import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.toc.dlpush.setting.util.Updatas;

public class Update {

	private static final String TAG = "Update";
    private  static int fileSize=0;
    private static int downLoadFileSize = 0;
    //下载APK
	public static final File DownLoadApk(Updatas upgrade,Handler handler){

		String path=upgrade.getPath();
//        String path="";
		Log.i(TAG, path);
		URL url;
        fileSize=0;
        downLoadFileSize = 0;
		try {
			url = new URL(upgrade.getPath());
			Log.i(TAG, path);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
            fileSize =conn.getContentLength();
			InputStream is=conn.getInputStream();//获取输入流
			String filename=path.substring(path.lastIndexOf("/")+1);
			File file=new File(Environment.getExternalStorageDirectory(),filename);	//存到SD卡上			
			FileOutputStream fos=new FileOutputStream(file);
			byte[] buffer =new byte[1024];
			int len=0;
			while((len=is.read(buffer))!=-1){
				fos.write(buffer,0,len);
                downLoadFileSize += len;
                Message msg=new Message();
                Log.i(TAG,downLoadFileSize+"    "+fileSize);
                msg.what=1;
                msg.obj= downLoadFileSize * 100 / fileSize;
                handler.sendMessage(msg);
            }
			fos.flush();
			fos.close();
			is.close();
			return file;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
