package com.cldxk.app.farcar.service;

import java.util.Map;

public class Task {

	//任务Id
	private int taskId;
	
	//任务参数
	private Map<String,Object> taskParams;
	
	//获取最新订单数据
	public static final int GET_NEW_ORDER=1;
	
	public Task(int taskId, Map<String, Object> taskParams) {
		super();
		this.taskId = taskId;
		this.taskParams = taskParams;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Map<String, Object> getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(Map<String, Object> taskParams) {
		this.taskParams = taskParams;
	}
	
	
}
