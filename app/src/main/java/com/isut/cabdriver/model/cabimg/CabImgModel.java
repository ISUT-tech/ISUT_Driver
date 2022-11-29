package com.isut.cabdriver.model.cabimg;

import com.google.gson.annotations.SerializedName;

public class CabImgModel{

	@SerializedName("data")
	private String data;

	@SerializedName("message")
	private String message;

	@SerializedName("statusCode")
	private int statusCode;

	@SerializedName("status")
	private String status;

	public String getData(){
		return data;
	}

	public String getMessage(){
		return message;
	}

	public int getStatusCode(){
		return statusCode;
	}

	public String getStatus(){
		return status;
	}
}