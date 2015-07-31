package com.toc.dlpush.util;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author GURR 2014-1-16
 */
public class EditMatch {
	private static String color = "'green'";
	public static void setColor(String color) {
		EditMatch.color = color;
	}

	public static boolean isNUll(TextView e) {
		if (TextUtils.isEmpty(e.getText().toString().trim())) {
			e.requestFocus();
			// s.setError("应由字母数字下划线组成");

			// , getResources().getDrawable(id)

			e.setError(Html
					.fromHtml("<font color=" + color + "'>尚未填写内容</font>"));

			return true;
		}
		return false;
	}

	public static boolean isNUllWithoutWarning(TextView e) {
		if (TextUtils.isEmpty(e.getText().toString().trim())) {

			return true;
		}
		return false;
	}

	public static boolean notEmail(EditText e,Context context) {
		if (!e.getText()
				.toString()
				.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
			showEditSeterror(e, "邮箱格式错误",context);

			return true;
		}
		return false;
	}

	public static boolean notMoblie(EditText e,Context context) {
		if (!e.getText().toString().matches("^(13|15|18|14|17|19)[0-9]{9}$")) {
			showEditSeterror(e, "手机格式错误",context);

			return true;
		}
		return false;
	}

	/**
	 * 验证字符长度范围
	 * 
	 * @param s
	 * @return
	 */
	public static boolean notNumCont(EditText s, int min, int max,Context context) {
		// !s.getText().toString().matches("\\w{" + min + "," + max + "}$"
		int w = s.getText().toString().length();
		if (w < min || w > max) {

			String t = ("应由" + min + "-" + max + "字符组成");
			// s.setError("应由" + min + "-" + max + "字符组成");
			// , getResources().getDrawable(id)

			showEditSeterror(s, t,context);
			return true;
		}
		return false;
	}

	/**
	 * 验证字符长度不小于
	 * 
	 * @param s
	 * @return
	 */
	public static boolean notMinLength(EditText s, int min,Context context) {
		// !s.getText().toString().matches("\\w{" + min + "," + max + "}$"
		int w = s.getText().toString().length();
		if (w < min) {

			String t = ("应由至少" + min + "字符组成");
			// s.setError("应由" + min + "-" + max + "字符组成");
			// , getResources().getDrawable(id)

			showEditSeterror(s, t,context);
			return true;
		}
		return false;
	}

	public static boolean notMatchGuhua(EditText s,Context context) {
		if (!s.getText()
				.toString()
				.matches(
						"((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)")) {

			String t = ("请输入真实固话号码");
			// s.setError("应由" + min + "-" + max + "字符组成");
			// , getResources().getDrawable(id)
			showEditSeterror(s, t,context);

			return true;
		}
		return false;
	}

	public static boolean notMatchPhone(EditText s,Context context) {

		String q = s.getText().toString().trim();

		if (!q.matches("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)")
				&& !q.matches("^(13|15|18)\\d{9}$")) {

			String t = ("请输入真实电话号码");
			// s.setError("应由" + min + "-" + max + "字符组成");
			// , getResources().getDrawable(id)
			showEditSeterror(s, t,context);

			return true;
		}
		return false;
	}

	/**
	 * 验证权威数字字母下划线
	 * 
	 * @param s
	 * @return
	 */
	public static boolean notMatchword(EditText s,Context context) {
		if (!s.getText().toString().matches("^[a-zA-Z0-9]+$")) {

			// s.setError("应由字母数字下划线组成");

			// , getResources().getDrawable(id)

			showEditSeterror(s, "应由字母数字下划线或点组成",context);
			return true;
		}
		return false;
	}

	/**
	 * 两个edittext输入相同
	 * 
	 * @param s
	 * @param e
	 * @return
	 */
	public static boolean notEqual(EditText s, EditText e,Context context) {
		if (!s.getText().toString().equals(e.getText().toString())) {

			// s.setError("应由字母数字下划线组成");

			// , getResources().getDrawable(id)

			showEditSeterror(e, "两次输入内容不同",context);
			return true;
		}
		return false;
	}

	/**
	 * 全中文
	 * 
	 * @param e
	 * @return
	 */
	public static boolean notChinese(EditText e,Context context) {
		if (!e.getText().toString().matches("^[\\u4e00-\\u9fa5]+$")) {
			showEditSeterror(e, "只能输入中文",context);
			return true;
		}
		return false;
	}

	public static boolean allNull(EditText e,Context context) {
//		for (EditText editText : e) {
			if (TextUtils.isEmpty(e.getText().toString())) {
                e.requestFocus();
				// s.setError("应由字母数字下划线组成");

				// , getResources().getDrawable(id)
//                editText.setError(Html.fromHtml("尚未填写内容"));
//				editText.setError(Html.fromHtml("<font color=" + color
//						+ "'>尚未填写内容</font>"),null);
                showEditSeterror(e,"尚未填写内容",context);


				return true;
//			}
		}

		return false;
	}

	public static boolean notQQ(EditText e,Context context) {

		if (!e.getText().toString().matches("^[1-9]\\d{4,9}$")) {
			showEditSeterror(e, "请输入正确的qq号",context);
			return true;
		}
		return false;
	}

	private static void showEditSeterror(EditText editText, String t,Context context) {
		editText.requestFocus();
//		editText.setError(Html.fromHtml("<font color=" + color + ">" + t
//				+ "</font>"),null);
        UtilTool.showToast(context,t);

	}
}
