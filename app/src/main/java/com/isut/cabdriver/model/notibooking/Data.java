package com.isut.cabdriver.model.notibooking;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("booking")
	private Booking booking;

	@SerializedName("discount")
	private int discount;

	@SerializedName("tip")
	private int tip;

	public Booking getBooking(){
		return booking;
	}

	public int getDiscount(){
		return discount;
	}

	public int getTip(){
		return tip;
	}
}