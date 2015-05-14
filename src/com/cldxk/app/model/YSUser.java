package com.cldxk.app.model;

import cn.bmob.v3.BmobUser;

public class YSUser extends BmobUser {
	
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
	
	
	

}
