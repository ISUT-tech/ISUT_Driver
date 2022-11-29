package com.isut.cabdriver.model.logins;

import com.google.gson.annotations.SerializedName;

public class LogInModels{

	@SerializedName("data")
	private Data data;

	@SerializedName("message")
	private String message;

	@SerializedName("statusCode")
	private int statusCode;

	@SerializedName("status")
	private String status;

	public Data getData(){
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