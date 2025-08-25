package com.example.quiz15.vo;

public class BasicRes {

	private int code;

	private String message;
	
	//建構方法:不管成功失敗都會回傳訊息super
	public BasicRes() {
		super();
	}
	

	public BasicRes(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}



	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
