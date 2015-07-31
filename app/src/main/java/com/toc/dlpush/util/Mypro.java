package com.toc.dlpush.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.toc.dlpush.R;


public class Mypro extends Dialog {

	public Mypro(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public Mypro(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static class Builder {

		private Context context;

		private CharSequence mes = "加载中请稍后";

		public CharSequence getMes() {
			return mes;
		}

		public Builder setMes(CharSequence mes) {
			this.mes = mes;
			return this;
		}

		public Builder(Context context) {
			this.context = context;
		}

		public Mypro create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final Mypro dialog = new Mypro(context,
					R.style.Theme_AppCompat_Dialog);
			View layout = inflater.inflate(R.layout.progress_dialog, null);
			// ((TextView)layout.findViewById(id.diag_title)).setText(title);
			((TextView) layout.findViewById(R.id.diag_mes)).setText(mes);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			dialog.setCancelable(false);
			return dialog;
		}
	}

}
