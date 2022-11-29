package com.isut.cabdriver.model.cabd;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class DataItem{

	@SerializedName("cabs")
	private List<CabsItem> cabs;

	@SerializedName("user")
	private User user;

	public List<CabsItem> getCabs(){
		return cabs;
	}

	public User getUser(){
		return user;
	}
}