package com.isut.cabdriver.model;

import com.google.gson.annotations.SerializedName;

public class UpdateResponse {

	@SerializedName("data")
	private Data data;

	@SerializedName("message")
	private String message;

	@SerializedName("statusCode")
	private int statusCode;

	@SerializedName("status")
	private String status;
	@SerializedName("email")
	private String email;

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

	public String getEmail() {
		return email;
	}
}