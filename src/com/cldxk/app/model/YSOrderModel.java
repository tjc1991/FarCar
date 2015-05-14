package com.cldxk.app.model;

import java.io.Serializable;

import android.R.bool;
import cn.bmob.v3.BmobObject;

public class YSOrderModel extends BmobObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//出发地
	private String cityFrom;
	
	//目的地
	private String cityDest;
	
	//时间
	private String fromTime;
	
	//联系电话
	private String telePhone;
	
	//接单者联系电话
	private String orderGoPhone;
	
	//订单状态
	private int orderStatues;
	
	//订单类型
	private int OrderType;
	
	//订单金额
	private long orderPrice;
	
	//fastjson无法解析父类的方法
	
	private String orderYuYueMsg;
	
	//设置数据库表名
	public YSOrderModel(){
		this.setTableName("ys_order");
	}

	public String getCityFrom() {
		return cityFrom;
	}

	public void setCityFrom(String cityFrom) {
		this.cityFrom = cityFrom;
	}

	public String getCityDest() {
		return cityDest;
	}

	public void setCityDest(String cityDest) {
		this.cityDest = cityDest;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getTelePhone() {
		return telePhone;
	}

	public void setTelePhone(String telePhone) {
		this.telePhone = telePhone;
	}

	public int getOrderStatues() {
		return orderStatues;
	}

	public void setOrderStatues(int orderStatues) {
		this.orderStatues = orderStatues;
	}

	public int getOrderType() {
		return OrderType;
	}

	public void setOrderType(int orderType) {
		OrderType = orderType;
	}

	public long getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(long orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getOrderYuYueMsg() {
		return orderYuYueMsg;
	}

	public void setOrderYuYueMsg(String orderYuYueMsg) {
		this.orderYuYueMsg = orderYuYueMsg;
	}

	public String getOrderGoPhone() {
		return orderGoPhone;
	}

	public void setOrderGoPhone(String orderGoPhone) {
		this.orderGoPhone = orderGoPhone;
	}


	
	

}
