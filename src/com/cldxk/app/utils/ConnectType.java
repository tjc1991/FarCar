package com.cldxk.app.utils;

public class ConnectType {
	
	public static final String RESULT_OK = "200";
	public static final int Type_city_from = 1;
	public static final int Type_city_go = 2;

	public static int city_type =0;

	public static int getCity_type() {
		return city_type;
	}

	public static void setCity_type(int city_type) {
		ConnectType.city_type = city_type;
	}
	
}
