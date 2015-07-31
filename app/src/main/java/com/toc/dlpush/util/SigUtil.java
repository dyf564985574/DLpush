package com.toc.dlpush.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SigUtil {
	/**
	 * @Title: 计算签名
	 * @Description: 
	 * 	附录1：关于请求签名
			　　为了通信安全和防止恶意攻击与调用，在调用接口之前需要计算出签名并追加到请求参数中，参数名为“sig”。签名是由请求参数计算出来的
			签名算法如下所示：
			将请求参数格式化为“key=value”格式，即“k1=v1”、“k2=v2”、“k3=v3”；
			将上述格式化好的参数键值对，以字典序升序排列后，拼接在一起，即“k1=v1k2=v2k3=v3”；
			上述字符串的MD5值 将字母a变成字母c  将字母b变成字母f   
			将上边得到的值进行MD5加密 即签名sig
			
			例如，当请求参数为（a=x&b=c&d=xx&sig=a340241a6ede3db74fe8a0b5a714b266）时，
			
			计算sig值：计算a=x&b=c&d=xx 的sig值  然后对sig进行匹配对比
			
			计算的sig值是:a340241a6ede3db74fe8a0b5a714b266
	 * 
	 * @param paramMap
	 * @return
	 */
	public static  String generateSig(Map<String,String> paramMap) {
				Set<String> keySet = paramMap.keySet();
				List<String> list = new ArrayList<String>();
				for (String key : keySet) {
					String param = key + "=" +  paramMap.get(key);
					list.add(param) ;
				}
				Collections.sort(list);//排序
				StringBuffer sigTempParams = new StringBuffer() ;
				
				for (String keyValue : list) {
					sigTempParams.append(keyValue) ;
				}

				StringBuffer sig = new StringBuffer();
				try {
				    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
				    try {
				        for (byte b : md.digest(sigTempParams.toString().getBytes("UTF-8"))) {
				        	sig.append(Integer.toHexString((b & 0xf0) >>> 4));
				        	sig.append(Integer.toHexString(b & 0x0f));
				        }
				    } catch (UnsupportedEncodingException e) {
				        for (byte b : md.digest(sigTempParams.toString().getBytes())) {
				        	sig.append(Integer.toHexString((b & 0xf0) >>> 4));
				        	sig.append(Integer.toHexString(b & 0x0f));
				        }
				    }
				} catch (java.security.NoSuchAlgorithmException ex) {
				    ex.printStackTrace();
				}
				StringBuffer theNewSig = new StringBuffer();
				
				String strReplace = sig.toString().replace("a", "c").replace("b", "f");
				try {
				    java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
				    try {
				        for (byte b : md.digest(strReplace.getBytes("UTF-8"))) {
				        	theNewSig.append(Integer.toHexString((b & 0xf0) >>> 4));
				        	theNewSig.append(Integer.toHexString(b & 0x0f));
				        }
				    } catch (UnsupportedEncodingException e) {
				        for (byte b : md.digest(strReplace.getBytes())) {
				        	theNewSig.append(Integer.toHexString((b & 0xf0) >>> 4));
				        	theNewSig.append(Integer.toHexString(b & 0x0f));
				        }
				    }
				} catch (java.security.NoSuchAlgorithmException ex) {
				    ex.printStackTrace();
				}
		    	return theNewSig.toString() ;
	}
	
	public static void main(String[] args) {
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("a", "x");
		paramMap.put("b", "c");
		paramMap.put("d", "xx");
		System.out.println(SigUtil.generateSig(paramMap));
	}
	
	
	/**
	 * 验证sig
	 * @param param_sig	传过来的sig
	 * @param gener_sig	自己生成的sig
	 * @return
	 */
	public static  boolean verifySig(String param_sig , String gener_sig) {
			if(param_sig==null || gener_sig == null){
				return false ;
			}
			if(param_sig.equals(gener_sig)) {
				return true ;
			}
		return false ;
	}
}
