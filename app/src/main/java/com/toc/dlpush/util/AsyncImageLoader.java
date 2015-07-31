package com.toc.dlpush.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AsyncImageLoader {
	
	private Context mContext;
	
	public AsyncImageLoader(Context context){
		this.mContext = context;
	}
	
	public Drawable loadImageFromNetwork(String imageUrl)
	{
		 Drawable drawable = null;     
	        if(imageUrl == null )     
	            return null;     
	        String imagePath = "";     
	        String   fileName   = "";     
	                 
	        // 获取url中图片的文件名与后缀     
	        if(imageUrl!=null&&imageUrl.length()!=0){      
	            fileName  = imageUrl.substring(imageUrl.lastIndexOf("/")+1);     
	        }     
	             
	        // 图片在手机本地的存放路径,注意：fileName为空的情况     
	        imagePath = mContext.getCacheDir() + "/" + fileName;     
	    
	        Log.i("test","imagePath = " + imagePath);     
	        File file = new File(mContext.getCacheDir(),fileName);// 保存文件     
	        Log.i("test","file.toString()=" + file.toString());     
	        if(!file.exists()&&!file.isDirectory())     
	        {     
	            try {     
	                // 可以在这里通过文件名来判断，是否本地有此图片     
	                     
	                FileOutputStream   fos=new   FileOutputStream( file );     
	                InputStream is = new URL(imageUrl).openStream();     
	                int   data = is.read();      
	                while(data!=-1){      
	                        fos.write(data);      
	                        data=is.read();;      
	                }      
	                fos.close();     
	                is.close();     
//	              drawable = Drawable.createFromStream(     
//	                      new URL(imageUrl).openStream(), file.toString() ); // (InputStream) new URL(imageUrl).getContent();     
	                drawable = Drawable.createFromPath(file.toString());     
	                Log.i("test", "file.exists()不文件存在，网上下载:" + drawable.toString());     
	            } catch (IOException e) {     
	                Log.d("test", e.getMessage());     
	            }     
	        }else    
	        {     
	            drawable = Drawable.createFromPath(file.toString());     
	            Log.i("test", "file.exists()文件存在，本地获取");     
	        }     
	             
	        if (drawable == null) {     
	            Log.d("test", "null drawable");     
	        } else {     
	            Log.d("test", "not null drawable");     
	        }     
	             
	        return drawable ;     
	}

}
