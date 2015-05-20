package com.cldxk.farcar.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.farcar.adapter.FromeAndGoAdapter;
import com.cldxk.app.farcar.adapter.FromeAndGoAdapter.Callback;
import com.cldxk.app.listview.XListView;
import com.cldxk.app.listview.XListView.IXListViewListener;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.farcar.R;

public class UserOrderActivity extends EBaseActivity implements IXListViewListener, OnClickListener,OnItemClickListener,Callback{
	
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
	
	//当前页号
	//当前页号
	private int cur_page = 0;
	private int page_size = 0;
	//最后一次操作索引号,用于分页
	private int last_length = 0;
	
	//下拉条件索引,按日期查询
	private String last_date = "";	
	private String cur_date = "";
	
	//下拉条件索引,按日期查询
	private String pullup_last_date = "";	
	private String pullup_cur_date = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//当前页号初始化为0
		cur_page = 0;
		page_size = 10;
		last_length =0;
		last_date = "";
		cur_date = "";
		pullup_last_date = "";
		pullup_cur_date = "";
		
		myphone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
		Log.i("tjc", "-->myphone="+myphone);
		initMyview();
	}
	
	public void initMyview(){
		
		list = (XListView) this.findViewById(R.id.msg_list);
		list.setPullRefreshEnable(PullRefresh);
		list.setPullLoadEnable(true);
		list.setXListViewListener(this);
		list.setOnItemClickListener(this);
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("我的订单");
		
		listItems = new ArrayList<YSOrderModel>();
		//设置数据适配器
		orderAdapter = new FromeAndGoAdapter(this, listItems,this);
				
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

	/*
	 * 上拉加载历史数据
	 * */	
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

		getHistorymsgData(cur_page);
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
		//按照时间降序
        query.order("-createdAt");
        
		//添加查询参数
		if(myphone != null){		
			//key /value
			query.addWhereContains("orderGoPhone", myphone);
		}
		
		//分页查询
		query.setLimit(page_size);
		query.setSkip(0);
		
      //执行查询，第一个参数为上下文，第二个参数为查找的回调
        query.findObjects(this, new FindCallback() {
			
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
				
				//停止刷新
				stoponLoad();
				
				if(arg0.length()<= 0){
					Toast.makeText(getApplicationContext(), "没有最新数据了", Toast.LENGTH_SHORT).show();
					return;
				}
				if(arg0.length() == page_size){
					//当前索引自加1
					cur_page++;
					Log.i("tjc", "-->"+cur_page+"");
				}
				Toast.makeText(getApplicationContext(), "加载"+arg0.length()+""+"条订单", Toast.LENGTH_SHORT).show();
				Log.i("tjc", arg0.toString());
								
				com.alibaba.fastjson.JSONArray jsonarray = JSON.parseArray(arg0.toString());
				
				com.alibaba.fastjson.JSONObject jsonobj = jsonarray.getJSONObject(0);
				//保存下拉最新数据时间
				last_date = jsonobj.getString("createdAt");
				
				com.alibaba.fastjson.JSONObject pullup_jsonobj = jsonarray.getJSONObject(arg0.length()-1);
				//保存上拉最新数据时间
				pullup_last_date = pullup_jsonobj.getString("createdAt");
				
				//刷新数据适配器
				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);				
								
				Log.i("tjc", "date="+last_date);
				Log.i("tjc", "pullup_last_date="+pullup_last_date);
				
				for (YSOrderModel ysOrderModel : orders) {
					
					listItems.add(ysOrderModel);
					
				}
								
				orderAdapter.set_datasource(listItems);
				orderAdapter.notifyDataSetChanged();
				
				//当前索引+1
				//cur_page++;
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
	
	/**
	 *  查询最新数据,不分页,数据量比较小
	 * @param page
	 */
	public void getNewmsgData(){
		
		BmobQuery query = new BmobQuery("ys_order");
		
		if(myphone != null){		
			query.addWhereContains("orderGoPhone", myphone);
		}
		
		if(!last_date.equals("") && !TextUtils.isEmpty(last_date)){
			
			//Log.i("tjc", "--->last="+last_date);
			
			//构造查询条件
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date date  = null;
			try {
			    date = sdf.parse(last_date);
			} catch (ParseException e) {
			    e.printStackTrace();
			}  
			//这是查询时间之后的数据
			query.addWhereGreaterThan("createdAt",new BmobDate(date));
			
		}

		query.findObjects(this, new FindCallback() {
			
			public void onSuccess(JSONArray arg0) {
				// TODO Auto-generated method stub
				
				//停止刷新
				stoponLoad();
				if(arg0.length() <= 0)
				{
					Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();						
					return;
				}else
				{
					int cur_length = arg0.length();	
					
					cur_date = last_date;
					
					com.alibaba.fastjson.JSONArray jsonarray = JSON.parseArray(arg0.toString());
					
					com.alibaba.fastjson.JSONObject jsonobj = jsonarray.getJSONObject(cur_length-1);
					
					//Log.i("tjc", arg0.toString());
					
					//保存最新数据时间
					last_date = jsonobj.getString("createdAt");
					//Log.i("tjc", "---->haha="+last_date);
					
					if(cur_date.equals(last_date))
					{											
						Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();						
						return;
					}
					cur_length=Math.abs(cur_length-1);
					Toast.makeText(getApplicationContext(), "加载"+cur_length+""+"条新订单", Toast.LENGTH_SHORT).show();	
															
					for(int i =1;i<jsonarray.size();i++)
					{						
						com.alibaba.fastjson.JSONObject jsobj = jsonarray.getJSONObject(i);
						YSOrderModel model = new YSOrderModel();
						model.setTelePhone(jsobj.getString("telePhone"));
						model.setOrderYuYueMsg(jsobj.getString("orderYuYueMsg"));
						model.setCityFrom(jsobj.getString("cityFrom"));
						model.setObjectId(jsobj.getString("objectId"));
						model.setOrderType(jsobj.getIntValue("OrderType"));
						model.setOrderPrice(jsobj.getIntValue("orderPrice"));
						model.setCityDest(jsobj.getString("cityDest"));
						model.setOrderGoPhone(jsobj.getString("orderGoPhone"));
						model.setOrderStatues(jsobj.getIntValue("orderStatues"));
						listItems.add(0,model);						
					}
					orderAdapter.set_datasource(listItems);
					orderAdapter.notifyDataSetChanged();
				}				
				
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

	/*
	 * 上拉加载历史数据,分页
	 * */	
	public void getHistorymsgData(final int page){
	
		BmobQuery query = new BmobQuery("ys_order");
		
		query.order("-createdAt");
		if(myphone != null){		
		query.addWhereContains("orderGoPhone", myphone);
		}
		//分页查询
		query.setLimit(page_size);
		//Log.i("tjc", "-->"+page+""+"-->"+cur_page+""+"-->"+page_size+"");
		//query.setSkip(page*page_size);
		
		if(!pullup_last_date.equals("") && !TextUtils.isEmpty(pullup_last_date)){
			
			//Log.i("tjc", "--->pullup_last_date="+pullup_last_date);
			
			//构造查询条件
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Date date  = null;
			try {
			    date = sdf.parse(pullup_last_date);
			} catch (ParseException e) {
			    e.printStackTrace();
			}  
			//这是查询时间之后的数据
			query.addWhereLessThan("createdAt",new BmobDate(date));
			
		}

		query.findObjects(this, new FindCallback() {
		
		@Override
		public void onSuccess(JSONArray arg0) {
			// TODO Auto-generated method stub
			
			//停止刷新
			stoponLoad();
			if(arg0.length() <= 0)
			{
				Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();						
				return;
			}else{
				
				int cur_length = arg0.length();	
				
				//Log.i("tjc", "cur_length="+cur_length+"");
				pullup_cur_date = pullup_last_date;
				
				com.alibaba.fastjson.JSONArray jsonarray = JSON.parseArray(arg0.toString());
				
				com.alibaba.fastjson.JSONObject jsonobj = jsonarray.getJSONObject(cur_length-1);
				
				//Log.i("tjc", arg0.toString());
				
				//保存最新数据时间
				pullup_last_date = jsonobj.getString("createdAt");
				//Log.i("tjc", "---->haha="+pullup_last_date);
				
				if(pullup_cur_date.equals(pullup_last_date))
				{											
					Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();						
					return;
				}
				
				if(cur_length == page_size){
					//当前索引自加1
					cur_page++;
					//Log.i("tjc", "-->"+cur_page+"");
				}
				
				//cur_length=Math.abs(cur_length-1);
				Toast.makeText(getApplicationContext(), "加载"+cur_length+""+"条新订单", Toast.LENGTH_SHORT).show();	
														
				for(int i =0;i<jsonarray.size();i++)
				{						
					com.alibaba.fastjson.JSONObject jsobj = jsonarray.getJSONObject(i);
					YSOrderModel model = new YSOrderModel();
					model.setTelePhone(jsobj.getString("telePhone"));
					model.setOrderYuYueMsg(jsobj.getString("orderYuYueMsg"));
					model.setCityFrom(jsobj.getString("cityFrom"));
					model.setObjectId(jsobj.getString("objectId"));
					model.setOrderType(jsobj.getIntValue("OrderType"));
					model.setOrderPrice(jsobj.getIntValue("orderPrice"));
					model.setCityDest(jsobj.getString("cityDest"));
					model.setOrderGoPhone(jsobj.getString("orderGoPhone"));
					model.setOrderStatues(jsobj.getIntValue("orderStatues"));
					listItems.add(model);						
				}
				orderAdapter.set_datasource(listItems);
				orderAdapter.notifyDataSetChanged();																
			}
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
	
	
//	public void getNewmsgData(final int page){
//				
//		BmobQuery query = new BmobQuery("ys_order");
//		
//        query.order("-createdAt");
//		if(myphone != null){		
//			query.addWhereContains("orderGoPhone", myphone);
//		}
//		//分页查询
//		query.setLimit(page_size);
//		Log.i("tjc", "-->"+page+""+"-->"+cur_page+""+"-->"+page_size+"");
//		query.setSkip(page*page_size);
//		query.findObjects(this, new FindCallback() {
//			
//			@Override
//			public void onSuccess(JSONArray arg0) {
//				// TODO Auto-generated method stub
//				
//				//停止刷新
//				stoponLoad();
//				if(arg0.length() <= 0)
//				{
//					Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();						
//					return;
//				}else{
//					
//					int cur_length = arg0.length();	
//					Log.i("tjc", "cur_length="+cur_length+"");
//					Log.i("tjc", "last_length="+last_length+"");
//					
//					if(last_length != cur_length){
//												
//						int new_length = cur_length - last_length;
//						Toast.makeText(getApplicationContext(), "加载"+new_length+""+"条新订单", Toast.LENGTH_SHORT).show();	
//
//						//刷新数据适配器
//						List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
//						
//						if(cur_length == 10){
//							//当前索引自加1
//							cur_page++;
//							Log.i("tjc", "-->"+cur_page+"");
//						}
//						for(int i = last_length;i<cur_length;i++){
//							Log.i("tjc", "add");
//							YSOrderModel ysOrderModel = orders.get(Math.abs(cur_length-last_length-1));
//							listItems.add(0,ysOrderModel);
//						}
//						if(last_length == 10){
//							last_length =0;
//						}
//						last_length = cur_length;
//						orderAdapter.set_datasource(listItems);
//						orderAdapter.notifyDataSetChanged();
//					}else{
//						Toast.makeText(getApplicationContext(), "没有最新订单", Toast.LENGTH_SHORT).show();	
//						
//					}
//				}
//				//Log.i("tjc", arg0.toString());
//				
////				//刷新数据适配器
////				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
////				for (YSOrderModel ysOrderModel : orders) {
////					listItems.add(0,ysOrderModel);
////				}
////				
////				orderAdapter.set_datasource(listItems);
////				orderAdapter.notifyDataSetChanged();
//				
//				
//			}
//			
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//				//停止刷新
//				stoponLoad();
//				Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
//				
//				
//			}
//		});
//		
//				
//	}

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
		
	}

	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		final int index = (Integer)v.getTag();	
		YSOrderModel ysoder = listItems.get(index);
		if(null != ysoder){			
			String ordertelePhone = ysoder.getTelePhone();
			if(null != ordertelePhone &&!TextUtils.isEmpty(ordertelePhone)){
				
				Intent it = new Intent(UserOrderActivity.this, ConnectActivity.class);
				it.putExtra("phone", ordertelePhone);
				startActivity(it);
			}
		}
		
	}	
	
//	{"telePhone":"18704319065",
//	"updatedAt":"2015-05-19 12:16:17",
//	"orderYuYueMsg":"",
//	"cityFrom":"大安",
//	"createdAt":"2015-05-19 12:16:13",
//	"objectId":"f78515a710",
//	"OrderType":0,
//	"orderPrice":0,
//	"cityDest":"长春",
//	"orderGoPhone":"15004311065",
//	"orderStatues":1}
	
}
