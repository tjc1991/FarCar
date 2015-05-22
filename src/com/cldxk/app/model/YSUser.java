package com.cldxk.app.model;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

public class YSUser extends BmobUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//用户姓名
	private String userNick;
	
	//用户身份证
	private String userCar;
	
	//用户电话
	private String userTelephone;
	
	//用户权限
	private int userPower;
	
	//用户类型
	private int userType;
	
	//用户工资
	//private float userwage;
	

	public String getUserNike() {
		return userNick;
	}

	public void setUserNike(String userName) {
		this.userNick = userName;
	}

	public String getUserCar() {
		return userCar;
	}

	public void setUserCar(String userCar) {
		this.userCar = userCar;
	}

	public String getUserTelephone() {
		return userTelephone;
	}

	public void setUserTelephone(String userTelephone) {
		this.userTelephone = userTelephone;
	}

	public int getUserPower() {
		return userPower;
	}

	public void setUserPower(int userPower) {
		this.userPower = userPower;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

//	public float getUserwage() {
//		return userwage;
//	}
//
//	public void setUserwage(float userwage) {
//		this.userwage = userwage;
//	}
	
	
	

}
