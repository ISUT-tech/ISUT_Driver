package com.isut.cabdriver.model.cabs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CabsModel{

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("message")
	private String message;

	@SerializedName("statusCode")
	private int statusCode;

	@SerializedName("status")
	private String status;

	public List<DataItem> getData(){
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