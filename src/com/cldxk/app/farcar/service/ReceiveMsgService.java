package com.cldxk.app.farcar.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindCallback;

import com.alibaba.fastjson.JSON;
import com.cldxk.app.EApplication;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.config.YSOrderType;
import com.cldxk.app.farcar.db.CityMsgEntity;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.app.utils.ActivityStackUtil;
import com.cldxk.farcar.MainActivity;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ReceiveMsgService extends Service implements Runnable{
	
	private static final String TAG="tjc";
	
	//创建任务队列
	private static Queue<Task> tasks =new LinkedList<Task>();
	
	//Activity链表
	private static ArrayList<Activity> activitys =new ArrayList<Activity>();
	
	//线程运行标志位
	private boolean isRun;
	
	//订单队列
	private List<YSOrderModel>listItems = new ArrayList<YSOrderModel>();
	
	private DbUtils db = null;
	
	
	//消息处理Handler对象	
	Handler handler =new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			//判断消息对象,执行相应的动作
			switch (msg.what) {
			case YSOrderType.GET_NEWORDER:
			{				
				MainActivity activity = (MainActivity) getActivityByName("MainActivity");
				if(null != activity){
					
					activity.refreshUi(msg.obj);
				}
				break;
			}

			default:
				break;
			}
			
		};
	};
	
		
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 添加一个Activity到队列内，并移除之前的Activity
	 * @param act
	 */
	public static void addActivity(Activity act)
	{
		if(!activitys.isEmpty())
		{
			for (Activity acts : activitys) {
				if(acts.getClass().equals(act.getClass().getName()))
				{
					activitys.remove(acts);
					break;
				}
			}
		}
		
		//添加新的Activity
		activitys.add(act);
	}
	
	/**
	 * 由名字返回相应地activity
	 * @param name
	 * @return
	 */
	public Activity getActivityByName(String name)
	{
		if(!ActivityStackUtil.getActivityList().isEmpty())
		{
			for(Activity act : ActivityStackUtil.getActivityList())
			{
				if(null!=act)
				{
					if(act.getClass().getName().indexOf(name)>0)
					{
						Log.i("tjc", "----->cccc");
						return act;
					}
				}
				
			}
		}
		
		return null;
	}
	
	/**
	 * 添加一个新任务到任务队列中
	 * @param t
	 */
	public static void newTask(Task t)
	{
		tasks.add(t);
	}

	/**
	 * 执行相应的任务
	 * @param task
	 * @throws WeiboException 
	 */	
	public void doTask(Task task)
	{
//		//获得系统的消息队列
//		Message message= handler.obtainMessage();
//		
//		//获取任务的Id
//		message.what =task.getTaskId();
//		
//		//具体做什么事情
//		{
//			switch (task.getTaskId()) {
//			case Task.GET_NEW_ORDER:
//				{
//					
//
//					//message.obj ="登陆成功";
//					break;
//				}
//
//			default:
//				break;
//			}
//		}
//		
//		//执行完毕,发送消息到主线程
//		//handler.sendEmptyMessage(message.what);
//		handler.sendMessage(message);
		
		//获取最新订单数据
		getNewmsgData();
		
		
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		//获取数据库数据
		db = EApplication.getMdb();
				
		//线程启动标志置位
		isRun=true;
		
		//创建并启动线程
		Thread thread =new Thread(this);
		thread.start();
		
		super.onCreate();
		
	}
		
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//执行任务
		while(isRun==true)
		{
			Task task=null;
			//任务队列非空
			if(!tasks.isEmpty())
			{
				//取出一个任务,之后将它从任务队列中移除
//				task=tasks.poll();
				//取出一个任务,不从任务队列中移除
				task=tasks.peek();
				if(null!=task)
				{
					//执行任务,发送消息给主线程
					doTask(task);
				}
			}
			
			//线程休眠1分钟再执行
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 退出系统
	 * @param context
	 */
	public static void appExit(Context context)
	{
		
		
		//Finish 所有的Activity
		for (Activity activity : activitys)
		{
			if(!activity.isFinishing())
				activity.finish();
		}
		
	// 结束 Service
		
		Intent service = new Intent("com.cldxk.app.farcar.service.ReceiveMsgService");
		context.stopService(service);
		
		
	}
	
	public void getNewmsgData(){
		
		//取出上次刷新时间
		String lasttime = EApplication.getMsharePreferenceUtil().loadStringSharedPreference("lasttime", "");
		if(null == lasttime || lasttime.length() == 0)
		{
			return;
		}
	
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date date  = null;
		try {
				date = sdf.parse(lasttime);
		} catch (ParseException e) {
		    e.printStackTrace();
		}  
		
//		//Log.i("tjc", "date--->"+new BmobDate(date).getDate());
		
		BmobQuery query = new BmobQuery("ys_order");
		query.addWhereLessThan("orderStatues", 1);
		query.addWhereGreaterThan("createdAt", new BmobDate(date));
		
		//添加复杂查询条件
        try {
			List<CityMsgEntity> listitems = db.findAll(CityMsgEntity.class);
			if(null != listitems){
			if(listitems.size()>0){				
				ArrayList<String> listitem = new ArrayList<String>();
				for(int i =0 ;i<listitems.size();i++){
					CityMsgEntity cityentity = listitems.get(i);
					listitem.add(cityentity.getCity_choice_name());
				}
				query.addWhereContainedIn("cityDest", listitem);
			}}
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		query.findObjects(getApplicationContext(), new FindCallback() {
			
			@Override
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				//没有数据直接返回
				if(arg0.length() == 0)
				{
					return;						
				}
				Log.i("tjc", arg0.toString());
				
				//清空list数据
				listItems.clear();
				
				//刷新数据适配器
				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
				for (YSOrderModel ysOrderModel : orders) {
					listItems.add(ysOrderModel);
					
				}
				
				
				//记录最后刷新时间
				//格式化日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
				String    str    =    sdf.format(curDate); 				
				EApplication.getMsharePreferenceUtil().saveSharedPreferences("lasttime", str);
				
				//发送handler调用语音播放
				
				//获得系统的消息队列
				Message message= handler.obtainMessage();
				
				//获取任务的Id
				message.what = YSOrderType.GET_NEWORDER;
				//发送消息
				message.obj = listItems;
				
				//执行完毕,发送消息到主线程
				handler.sendMessage(message);
				
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
								
			}
		});
		
				
	}
	
	
	

}
