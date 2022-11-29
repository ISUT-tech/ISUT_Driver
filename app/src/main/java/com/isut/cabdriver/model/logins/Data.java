package com.isut.cabdriver.model.logins;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("user_details")
	private UserDetails userDetails;

	@SerializedName("user")
	private User user;

	@SerializedName("token")
	private String token;

	@SerializedName("isCabRegistered")
	private boolean isCabRegistered;

	public UserDetails getUserDetails(){
		return userDetails;
	}

	public User getUser(){
		return user;
	}

	public String getToken(){
		return token;
	}

	public boolean isIsCabRegistered(){
		return isCabRegistered;
	}
}