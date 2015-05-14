package com.cldxk.farcar.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindCallback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.farcar.adapter.FromeAndGoAdapter;
import com.cldxk.app.listview.XListView;
import com.cldxk.app.listview.XListView.IXListViewListener;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.farcar.R;

public class UserOrderActivity extends EBaseActivity implements IXListViewListener, OnClickListener,OnItemClickListener{
	
	private XListView list = null;
	
	private FromeAndGoAdapter orderAdapter = null;
	
	private ArrayList<YSOrderModel>listItems = null;
	
	private boolean PullRefresh = true;
	
	//语音合成
	private static String TAG = "tjc"; 	
	
	private String myphone;
	
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		myphone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
		initMyview();
	}
	
	public void initMyview(){
		
		list = (XListView) this.findViewById(R.id.msg_list);
		list.setPullRefreshEnable(PullRefresh);
		list.setPullLoadEnable(false);
		list.setXListViewListener(this);
		list.setOnItemClickListener(this);
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("我的订单");
		
		listItems = new ArrayList<YSOrderModel>();
		//设置数据适配器
		orderAdapter = new FromeAndGoAdapter(this, listItems,null);
				
		list.setAdapter(orderAdapter);
		
		// /获取并更新数据
		GetCarMsgData();
								
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.fragmet_message;
	}

	//XLListView接口方法

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
		getNewmsgData();
		
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}
	
	private void stoponLoad() {
		
		list.stopRefresh();
		list.stopLoadMore();	
	}
	
	public void GetCarMsgData(){
		
		final ProgressDialog dialog =ProgressDialog.show(this, 
				"我的订单查询", "正在查询...");
		dialog.setCancelable(false);
		
		//查询服务器获取数据
		BmobQuery query = new BmobQuery("ys_order");
		
		//添加查询参数
		if(myphone != null){		
			//key /value
			query.addWhereContains("orderGoPhone", myphone);
		}
		//按照时间降序
        query.order("-createdAt");
      //执行查询，第一个参数为上下文，第二个参数为查找的回调
        query.findObjects(this, new FindCallback() {
			
			@Override
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
				
				//停止刷新
				stoponLoad();
				Toast.makeText(getApplicationContext(), "加载"+arg0.length()+""+"条订单", Toast.LENGTH_SHORT).show();
				Log.i("tjc", arg0.toString());
				
				//刷新数据适配器
				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
				for (YSOrderModel ysOrderModel : orders) {
					
					listItems.add(ysOrderModel);
					
				}
								
				orderAdapter.set_datasource(listItems);
				orderAdapter.notifyDataSetChanged();
				
				//记录最后刷新时间
				//格式化日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
				String    str    =    sdf.format(curDate); 				
				msharePreferenceUtil.saveSharedPreferences("lasttime", str);
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
				//停止刷新
				stoponLoad();
				Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
	}
	
	public void getNewmsgData(){
		
		//取出上次刷新时间
		String lasttime = msharePreferenceUtil.loadStringSharedPreference("lasttime", "");
		if(null == lasttime || lasttime.length() == 0)
		{
			return;
		}
		
		//Log.i("tjc", "--->"+lasttime);
		
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date date  = null;
		try {
				date = sdf.parse(lasttime);
		} catch (ParseException e) {
		    e.printStackTrace();
		}  
		
		//Log.i("tjc", "date--->"+new BmobDate(date).getDate());
		
		BmobQuery query = new BmobQuery("ys_order");
		query.addWhereGreaterThan("createdAt", new BmobDate(date));
		if(myphone != null){		
			query.addWhereContains("orderGoPhone", myphone);
		}
		query.findObjects(this, new FindCallback() {
			
			@Override
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				//停止刷新
				stoponLoad();
				if(arg0.length() == 0)
				{
					Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();						
				}else{
					Toast.makeText(getApplicationContext(), "加载"+arg0.length()+""+"条新订单", Toast.LENGTH_SHORT).show();	
				}
				Log.i("tjc", arg0.toString());
				
				//刷新数据适配器
				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
				for (YSOrderModel ysOrderModel : orders) {
					listItems.add(ysOrderModel);
				}
				
				orderAdapter.set_datasource(listItems);
				orderAdapter.notifyDataSetChanged();
				
				//记录最后刷新时间
				//格式化日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
				String    str    =    sdf.format(curDate); 				
				msharePreferenceUtil.saveSharedPreferences("lasttime", str);
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
				//停止刷新
				stoponLoad();
				Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
				
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.fragment_actionbar_back:
			finish();
			break;	
			
			default :
				break;
		}		
				
	}

	//Listview 方法
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
		// TODO Auto-generated method stub
		
		//Log.i(TAG, "--->"+pos+"");
		
		//传送订单详情到Activity
//		Intent it = new Intent(UserOrderActivity.this,OrderDetailActivity.class);
//		Bundle mBundle = new Bundle();   
//	    mBundle.putSerializable("ysorder",listItems.get(pos-1)); 					    
//	    it.putExtras(mBundle);   
//		startActivity(it);
//		
//		//结束Activity
//		finish();		
	}	
	
}
