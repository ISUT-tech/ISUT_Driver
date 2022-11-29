package com.isut.cabdriver.model.newuser;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("cabs")
	private List<CabsItem> cabs;

	@SerializedName("driverId")
	private int driverId;
@SerializedName("email")
	private String email;

	@SerializedName("rewardPoints")
	private Object rewardPoints;

	@SerializedName("licenseNumber")
	private Object licenseNumber;
	public List<CabsItem> getCabs(){
		return cabs;
	}

	public int getDriverId(){
		return driverId;
	}

	public Object getRewardPoints(){
		return rewardPoints;
	}

	public Object getLicenseNumber() {
		return licenseNumber;
	}

	public String getEmail() {
		return email;
	}
}