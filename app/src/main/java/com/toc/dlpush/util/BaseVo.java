package com.toc.dlpush.util;

public class BaseVo {
	private String error;
	private String tip;
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	@Override
	public String toString() {
		return "BaseVo [error=" + error + ", tip=" + tip + "]";
	}
}
