package com.toc.dlpush.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;


import android.util.Log;

import com.toc.dlpush.util.SigUtil;

public class HttpUtil {
    
	 private static final String TAG = "HttpUtil";
	 private static final int TIME_OUT = 10*1000;   //超时时间
     private static final String CHARSET = "utf-8"; //设置编码

	/**
	 * 带sig值的get请求方法
	 * @param url
	 * @param sigParams
	 * @return
	 * @throws java.io.IOException
	 */
	public static String getUrlWithSig(String url,Map<String,String> sigParams) throws IOException{
		String sig = SigUtil.generateSig(sigParams);
		
		if(url.contains("?")){//已经存在参数了
			url += "&sig=" + sig;
		}else{//未存在请求参数
			url += "?sig=" + sig;
		}
		
		Log.i(TAG, "GET拼接的url         "+url);
		return getUrl(url);
	}
	
	/**
	 * 带sig值的post请求方法
	 * @param url
	 * @param params
	 * @param sigParams
	 * @return
	 * @throws java.io.IOException
	 */
	public static String getUrlWithSig(String url,Map<String, String> params,Map<String,String> sigParams) throws IOException{
		String sig = SigUtil.generateSig(sigParams);
		if(url.contains("?")){//已经存在参数了
			url += "&sig=" + sig;
		}else{//未存在请求参数
			url += "?sig=" + sig;
		}
		
		Log.i(TAG, "POST拼接的url           "+url);
		
		return postUrl(url, params);
	}
	
	
	
	/**
	 * get请求
	 * @param url
	 * @return
	 * @throws java.io.IOException
	 */
    public static String getUrl(String url) throws IOException {
            HttpGet request = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
               
            	return EntityUtils.toString(response.getEntity());
            } else {
                    return "";
            }
    }

   /**
    * post请求
    * @param url
    * @param params
    * @return
    * @throws org.apache.http.client.ClientProtocolException
    * @throws java.io.IOException
    */
    public static String postUrl(String url,  Map<String, String> params)
                    throws ClientProtocolException, IOException {
        Log.i("String" ,params+"");
            return postUrl(url, params, "UTF-8");
    }

   /**
    * 修改编码格式的post
    * @param url
    * @param params
    * @param encoding
    * @return
    * @throws org.apache.http.client.ClientProtocolException
    * @throws java.io.IOException
    */
    public static String postUrl(String url, Map<String, String> params,
                    String encoding) throws ClientProtocolException, IOException {
    	
    	
    	
    	
			 List<NameValuePair> param = new ArrayList<NameValuePair>(); 
             Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
           
             while (iterator.hasNext()) {
            	
                    Entry<String, String> entry = iterator.next();
                    param.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
          
            HttpPost request = new HttpPost(url);
            //String newStr = new String(param.toString().getBytes(), "UTF-8");
//            StringEntity se = new StringEntity(newStr);
            
            request.setEntity(new UrlEncodedFormEntity(param,"UTF-8"));
         
            HttpClient client = new DefaultHttpClient();
          
            client.getParams().setParameter(
                            CoreConnectionPNames.CONNECTION_TIMEOUT, 10000); 
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
            HttpResponse response = client.execute(request);
             Log.i(TAG,  response.getStatusLine().getStatusCode()+"");
            
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	
            	Log.i(TAG,  "POST请求的时候有返回值");
               return EntityUtils.toString(response.getEntity());
                    
            } else {
            	
            	Log.i(TAG,  "POST请求的时候的返回空");
                    return null;
            }
    }
    
    /**
     * 通过构造基于 HTTP 协议的传输内容实现图片自动上传到服务器功能
     * 参考：http://blog.csdn.net/newjueqi/article/details/4777779
     *       http://bertlee.iteye.com/blog/1134576
     * @param RequestURL 路径
     * @param uploadFile 文件
     * @param newName 文件名字
     * @return 返回值
     * @throws java.io.IOException
     */
    public static String postFile(String RequestURL, File uploadFile,String newName) throws IOException{
     	
    	String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";// 数据分隔符
        String json="";
        try
        {
          URL url =new URL(RequestURL);
          HttpURLConnection con=(HttpURLConnection)url.openConnection();
          /* 允许Input、Output，不使用Cache */
          con.setDoInput(true);//允许输入 
          con.setDoOutput(true);//允许输出  
          con.setUseCaches(false);//不使用Cache  一般上传都不允许缓存，这样容易报错
          /* 设置传送的method=POST */
          con.setRequestMethod("POST");
          /* 
           * setRequestProperty 
           * setRequestProperty主要是设置HttpURLConnection请求头里面的属性
           * 比如Cookie、User-Agent（浏览器类型）等等，具体可以看HTTP头相关的材料
           * 至于要设置什么这个要看服务器端的约定
          */
          con.setRequestProperty("Connection", "Keep-Alive");
          con.setRequestProperty("Charset", "UTF-8");
          con.setRequestProperty("Content-Type",
                             "multipart/form-data;boundary="+boundary);
          /* 设置DataOutputStream 
           * ds.writeBytes  将字符串按字节顺序写出到基础输出流中
          */
          DataOutputStream ds =new DataOutputStream(con.getOutputStream());
          ds.writeBytes(twoHyphens + boundary + end);//分隔符，把请求和内容分开
          //Content-Disposition: form-data; name="upload_file"; filename="apple.jpg",根据服务器上的字段换成相应的字段（images）
          ds.writeBytes("Content-Disposition: form-data; " +
                  "name=\"images\";filename=\"" +
                  newName +"\"" + end);
          ds.writeBytes(end);   

          /* 取得文件的FileInputStream */
          FileInputStream fStream = new FileInputStream(uploadFile);
          /* 设置每次写入1024bytes */
          int bufferSize = 1024;
          byte[] buffer = new byte[bufferSize];
          
          int length = -1;
          /* 从文件读取数据至缓冲区 */
          while((length = fStream.read(buffer)) != -1)
          {
            /* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
            Log.i(TAG, 18+"");
          }
          ds.writeBytes(end);
          ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

          /* close streams */
          fStream.close();
          ds.flush();
          /* 取得Response内容 */
          InputStream is = con.getInputStream();
          
//          Map<String, List<String>> headerFields = con.getHeaderFields();
//          Set<String> keySet = headerFields.keySet();
//          for (String key : keySet) {
//			Log.i(TAG,""+headerFields.get(key));
//		  }
          
          int ch;
          StringBuffer b =new StringBuffer();
          while( ( ch = is.read() ) != -1 )
          {
            b.append( (char)ch );
          }
          /* 将Response显示于Dialog */
          json=b.toString().trim();
          Log.i(TAG,"图片路径"+json );
        
          Log.i(TAG,"图片路径----转码"+json );
          /* 关闭DataOutputStream */
          ds.close();
        }catch(Exception e){
        	Log.i(TAG, "上传失败");
        }
		return json;
     	        
    }
    public static String postFileFriend(String RequestURL, File uploadFile,String newName) throws IOException{
     	
    	String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";// 数据分隔符
        String json="";
        try
        /**
         */
        {
          URL url =new URL(RequestURL);
          HttpURLConnection con=(HttpURLConnection)url.openConnection();
          /* 允许Input、Output，不使用Cache */
          con.setDoInput(true);//允许输入
          con.setDoOutput(true);//允许输出
          con.setUseCaches(false);//不使用Cache  一般上传都不允许缓存，这样容易报错
          /* 设置传送的method=POST */
          con.setRequestMethod("POST");
          /* 
           * setRequestProperty 
           * setRequestProperty主要是设置HttpURLConnection请求头里面的属性
           * 比如Cookie、User-Agent（浏览器类型）等等，具体可以看HTTP头相关的材料
           * 至于要设置什么这个要看服务器端的约定
          */
          con.setRequestProperty("Connection", "Keep-Alive");
          con.setRequestProperty("Charset", "UTF-8");
          con.setRequestProperty("Content-Type",
                             "multipart/form-data;boundary="+boundary);
          /* 设置DataOutputStream 
           * ds.writeBytes  将字符串按字节顺序写出到基础输出流中
          */
          DataOutputStream ds =new DataOutputStream(con.getOutputStream());
          ds.writeBytes(twoHyphens + boundary + end);//分隔符，把请求和内容分开
          //Content-Disposition: form-data; name="upload_file"; filename="apple.jpg",根据服务器上的字段换成相应的字段（images）
          ds.writeBytes("Content-Disposition: form-data; " +
                  "name=\"cutfriendimg\";filename=\"" +
                  newName +"\"" + end);
          ds.writeBytes(end);   

          /* 取得文件的FileInputStream */
          FileInputStream fStream = new FileInputStream(uploadFile);
          /* 设置每次写入1024bytes */
          int bufferSize = 1024;
          byte[] buffer = new byte[bufferSize];
          
          int length = -1;
          /* 从文件读取数据至缓冲区 */
          while((length = fStream.read(buffer)) != -1)
          {
            /* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
            Log.i(TAG, 18+"");
          }
          ds.writeBytes(end);
          ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

          /* close streams */
          fStream.close();
          ds.flush();
          /* 取得Response内容 */
          InputStream is = con.getInputStream();
          
//          Map<String, List<String>> headerFields = con.getHeaderFields();
//          Set<String> keySet = headerFields.keySet();
//          for (String key : keySet) {
//			Log.i(TAG,""+headerFields.get(key));
//		  }
          
          int ch;
          StringBuffer b =new StringBuffer();
          while( ( ch = is.read() ) != -1 )
          {
            b.append( (char)ch );
          }
          /* 将Response显示于Dialog */
          json=b.toString().trim();
          Log.i(TAG,"图片路径"+json );
        
          Log.i(TAG,"图片路径----转码"+json );
          /* 关闭DataOutputStream */
          ds.close();
        }catch(Exception e){
        	Log.i(TAG, "上传失败");
        }
		return json;
     	        
    }
    }
    	