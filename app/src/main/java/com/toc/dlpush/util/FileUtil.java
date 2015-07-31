package com.toc.dlpush.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	private static final String TAG = "FileUtil";

	public static File getCacheFile(String imageUri){
		File cacheFile = null;
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){//判断手机上是否插入了SD卡，并且应用程序具有读写权限 
				File sdCardDir = Environment.getExternalStorageDirectory();//获取外部存储器
				String fileName = getFileName(imageUri);
				File dir = new File(sdCardDir.getCanonicalPath()+ com.toc.dlpush.util.AsynImageLoader.CACHE_DIR);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				cacheFile = new File(dir, fileName);
				Log.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir + ",file:" + fileName);
			}  
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "getCacheFileError:" + e.getMessage());
		}
		
		return cacheFile;
	}
	
	public static String getFileName(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}
}
