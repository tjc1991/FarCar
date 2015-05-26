package com.cldxk.app.farcar.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

import com.alibaba.fastjson.JSON;
import com.cldxk.app.EApplication;
import com.cldxk.app.base.BaseFragment;
import com.cldxk.app.config.YSOrderStatus;
import com.cldxk.app.farcar.adapter.FromeAndGoAdapter;
import com.cldxk.app.farcar.adapter.FromeAndGoAdapter.Callback;
import com.cldxk.app.farcar.db.CityMsgEntity;
import com.cldxk.app.listview.XListView;
import com.cldxk.app.listview.XListView.IXListViewListener;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.farcar.R;
import com.cldxk.farcar.ui.ConnectActivity;
import com.cldxk.farcar.ui.OrderMsgShowActivity;
import com.cldxk.plug.user.LoginActivity;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MessageFragment extends BaseFragment implements Callback{
	
	private View view = null;
	
//	private XListView list = null;
	private ListView list = null;
	
	private FromeAndGoAdapter orderAdapter = null;
	
	private ArrayList<YSOrderModel>listItems = null;
	
	private static final int MSG_OK = 18;
	
	//private boolean PullRefresh = true;
	
	//语音合成
	private static String TAG = "tjc"; 	
	
//	//所有订单消息
//	List<String>Vorders = new ArrayList<String>();
	
	private DbUtils db = null;
	
	private String ordertelePhone = null;
	
	//语音播放
	Handler handler = new Handler(){
		
		//处理消息
		public void handleMessage(android.os.Message msg) {
						
			switch (msg.what) {
			case MSG_OK:		
				Intent it = new Intent(getActivity(), ConnectActivity.class);
				it.putExtra("phone", ordertelePhone);
				startActivity(it);
				break;

			default:
				break;
			}
			
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// TODO Auto-generated method stub
		
		//全局数据库单例
		db = EApplication.getMdb();
		
		view = inflater.inflate(R.layout.fragmet_message_main, container, false);

		initMyview();
		
		return view;
	}
	
	public void initMyview(){
		
		list = (ListView) view.findViewById(R.id.msg_list_main);

		//设置数据适配器
		orderAdapter = new FromeAndGoAdapter(getActivity(), listItems,this);
		
		list.setAdapter(orderAdapter);
								
	}
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		// /获取并更新数据
		//GetCarMsgData();
		//getDestMsgData();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getDestMsgData();
		
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		listItems = new ArrayList<YSOrderModel>();
		//设置数据适配器
		orderAdapter = new FromeAndGoAdapter(getActivity(), listItems,this);
	}
	
	/**
	 * 获取指定目的城市的数据
	 */
	public void getDestMsgData(){
		
		//系统方式,便捷
		listItems.clear();
		
		//查询服务器获取数据
		BmobQuery<YSOrderModel> query = new BmobQuery<YSOrderModel>();
		//按照时间降序
        query.order("-createdAt");
        
        //查询显示没有接单的数据
        query.addWhereLessThan("orderStatues", 1);

        //添加查询约束条件
        try {
        	
        		if(null != db){
			List<CityMsgEntity> listitems = db.findAll(CityMsgEntity.class);
			if(listitems != null){
			if(listitems.size()>0){				
				ArrayList<String> listitem = new ArrayList<String>();
				for(int i =0 ;i<listitems.size();i++){
					CityMsgEntity cityentity = listitems.get(i);
					listitem.add(cityentity.getCity_choice_name());
				}
				query.addWhereContainedIn("cityDest", listitem);
			}}
        		}
			
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
      //执行查询，第一个参数为上下文，第二个参数为查找的回调
        query.findObjects(getActivity(), new FindListener<YSOrderModel>() {
			
			@Override
			public void onSuccess(List<YSOrderModel> arg0) {
				// TODO Auto-generated method stub
				
				//停止刷新
				//Toast.makeText(getActivity(), "加载"+arg0.length()+""+"条订单", Toast.LENGTH_SHORT).show();
				//Log.i("tjc", arg0.toString());
				
				if(null == arg0){
					return;
				}
				
				//刷新数据适配器
				for (YSOrderModel ysOrderModel : arg0) {
					listItems.add(ysOrderModel);
				}
				
//				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
//				for (YSOrderModel ysOrderModel : orders) {
//					listItems.add(ysOrderModel);
////					String orderstr = "快来抢单,从"+ysOrderModel.getCityFrom()+"出发去往"
////							+ysOrderModel.getCityDest()+""+"方向";
////					Vorders.add(orderstr);
//				}
				
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
			public void  onError(int code, String msg) {
				// TODO Auto-generated method stub
				
				//停止刷新				
				Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
				
				
			}
		});
		
		//自定义方式查询订单
//		listItems.clear();
//		
//		//查询服务器获取数据
//		BmobQuery query = new BmobQuery("ys_order");
//		//按照时间降序
//        query.order("-createdAt");
//        
//        //查询显示没有接单的数据
//        query.addWhereLessThan("orderStatues", 1);
//
//        //添加查询约束条件
//        try {
//        	
//        		if(null != db){
//			List<CityMsgEntity> listitems = db.findAll(CityMsgEntity.class);
//			if(listitems != null){
//			if(listitems.size()>0){				
//				ArrayList<String> listitem = new ArrayList<String>();
//				for(int i =0 ;i<listitems.size();i++){
//					CityMsgEntity cityentity = listitems.get(i);
//					listitem.add(cityentity.getCity_choice_name());
//				}
//				query.addWhereContainedIn("cityDest", listitem);
//			}}
//        		}
//			
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        
//      //执行查询，第一个参数为上下文，第二个参数为查找的回调
//        query.findObjects(getActivity(), new FindCallback() {
//			
//			@Override
//			public void onSuccess(JSONArray arg0) {
//				// TODO Auto-generated method stub
//				
//				//停止刷新
//				//Toast.makeText(getActivity(), "加载"+arg0.length()+""+"条订单", Toast.LENGTH_SHORT).show();
//				//Log.i("tjc", arg0.toString());
//				
//				//刷新数据适配器
//				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
//				for (YSOrderModel ysOrderModel : orders) {
//					listItems.add(ysOrderModel);
////					String orderstr = "快来抢单,从"+ysOrderModel.getCityFrom()+"出发去往"
////							+ysOrderModel.getCityDest()+""+"方向";
////					Vorders.add(orderstr);
//				}
//				
//				orderAdapter.set_datasource(listItems);
//				orderAdapter.notifyDataSetChanged();
//				
//				//记录最后刷新时间
//				//格式化日期
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
//				String    str    =    sdf.format(curDate); 				
//				msharePreferenceUtil.saveSharedPreferences("lasttime", str);
//											
//			}
//			
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//				//停止刷新				
//				Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
//				
//				
//			}
//		});
		
	}	
	
	/**
	 * 获取最新订单数据
	 */
	public void getNewmsgData(){
		
		//系统方式,方便快速
		listItems.clear();		
		BmobQuery<YSOrderModel> query = new BmobQuery<YSOrderModel>();

		//按照时间降序
        query.order("-createdAt");
		query.setLimit(10);
		query.setSkip(0);
		//query.addWhereGreaterThan("createdAt", new BmobDate(date));
		query.addWhereLessThan("orderStatues", 1);
        //添加查询约束条件
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
		
		
		query.findObjects(getActivity(), new FindListener<YSOrderModel>() {
			
			@Override
			public void onSuccess(List<YSOrderModel> arg0) {
				// TODO Auto-generated method stub
				
				if(null == arg0){
					return;
				}
				
				//停止刷新
				if(arg0.size() == 0)
				{
					Toast.makeText(getActivity(), "没有最新订单", Toast.LENGTH_SHORT).show();						
					return;
				}else{
					Toast.makeText(getActivity(), "加载"+arg0.size()+""+"条新订单", Toast.LENGTH_SHORT).show();	
				}
				//Log.i("tjc", arg0.toString());
				
				//刷新数据适配器
				for (YSOrderModel ysOrderModel : arg0) {
					listItems.add(ysOrderModel);
				}
//				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
//				for (YSOrderModel ysOrderModel : orders) {
//					listItems.add(ysOrderModel);
//				}				
				orderAdapter.set_datasource(listItems);
				orderAdapter.notifyDataSetChanged();
				
			}
			
			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub				
				//停止刷新
				Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();								
			}
		});
		
		
		
		//自定义方式查询
//		listItems.clear();
//		
//		BmobQuery query = new BmobQuery("ys_order");
//
//		//按照时间降序
//        query.order("-createdAt");
//		query.setLimit(10);
//		query.setSkip(0);
//		//query.addWhereGreaterThan("createdAt", new BmobDate(date));
//		query.addWhereLessThan("orderStatues", 1);
//        //添加查询约束条件
//        try {
//			List<CityMsgEntity> listitems = db.findAll(CityMsgEntity.class);
//			if(null != listitems){
//			if(listitems.size()>0){				
//				ArrayList<String> listitem = new ArrayList<String>();
//				for(int i =0 ;i<listitems.size();i++){
//					CityMsgEntity cityentity = listitems.get(i);
//					listitem.add(cityentity.getCity_choice_name());
//				}
//				query.addWhereContainedIn("cityDest", listitem);
//			}}
//			
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		query.findObjects(getActivity(), new FindCallback() {
//			
//			@Override
//			public void onSuccess(JSONArray arg0) {
//				// TODO Auto-generated method stub
//				
//				//停止刷新
//				if(arg0.length() == 0)
//				{
//					Toast.makeText(getActivity(), "没有最新订单", Toast.LENGTH_SHORT).show();						
//					return;
//				}else{
//					Toast.makeText(getActivity(), "加载"+arg0.length()+""+"条新订单", Toast.LENGTH_SHORT).show();	
//				}
//				Log.i("tjc", arg0.toString());
//				
//				//刷新数据适配器
//				List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
//				for (YSOrderModel ysOrderModel : orders) {
//					listItems.add(ysOrderModel);
//				}				
//				orderAdapter.set_datasource(listItems);
//				orderAdapter.notifyDataSetChanged();
//				
//			}
//			
//			@Override
//			public void onFailure(int arg0, String arg1) {
//				// TODO Auto-generated method stub				
//				//停止刷新
//				Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();								
//			}
//		});
						
	}
	
	//打电话按钮回调事件
	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		
		final int index = (Integer)v.getTag();		
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		final Dialog dialog = builder.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.qd_dialog);
		ViewGroup logout = (ViewGroup) window.findViewById(R.id.logout);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				final YSOrderModel ysoder = listItems.get(index);
				final String orderId = ysoder.getObjectId();
				Log.i("tjc", orderId+"");
				Log.i("tjcxx--->", ysoder.getOrderStatues()+"");
				
				ordertelePhone = ysoder.getTelePhone();
				//重新查询订单
				final ProgressDialog progressDialog =ProgressDialog.show(getActivity(), 
						"一键抢单", "正在抢单...");
				
				BmobQuery<YSOrderModel> query = new BmobQuery<YSOrderModel>();
				query.addWhereContains("objectId", orderId);
				
				query.getObject(getActivity(), orderId, new GetListener<YSOrderModel>()  {

				    @Override
				    public void onSuccess(YSOrderModel arg0) {
				        // TODO Auto-generated method stub
				    	
				    	if(null == arg0){
				    		return;
				    	}				    					    
				    		if(arg0.getOrderStatues() == 0){
				    			
				    			//抢单
								YSOrderModel updateorder = new YSOrderModel();
								updateorder.setOrderStatues(YSOrderStatus.YSOrder_Select);
								updateorder.setOrderPrice(ysoder.getOrderPrice());

								String orderGo = msharePreferenceUtil.loadStringSharedPreference("userName", "");
								if(null != orderGo && !TextUtils.isEmpty(orderGo)){									
									updateorder.setOrderGoPhone(orderGo);
								}else{
						    			progressDialog.dismiss();
									Toast.makeText(getActivity(), "抢单失败,请刷新后再尝试", Toast.LENGTH_SHORT).show();
									return;
								}
								updateorder.update(getActivity(), orderId, new UpdateListener() {
					
								    @Override
								    public void onSuccess() {
								        // TODO Auto-generated method stub
								    		progressDialog.dismiss();
								    		handler.sendEmptyMessage(MSG_OK);			    		
								    }
					
								    @Override
								    public void onFailure(int code, String msg) {
								        // TODO Auto-generated method stub
								    		progressDialog.dismiss();
								    		
								    }
								});
				
				
//				query.findObjects(getActivity(), new FindListener<YSOrderModel>() {
//
//				    @Override
//				    public void onSuccess(List<YSOrderModel> arg0) {
//				        // TODO Auto-generated method stub
//				    	
//				    	if(null == arg0){
//				    		return;
//				    	}
//				    	
//				    	List<YSOrderModel>orders = JSON.parseArray(arg0.toString(), YSOrderModel.class);
//				    	YSOrderModel object = orders.get(0);
//				    	Log.i("tjc--->", object.getOrderStatues()+"");
//				    		if(object.getOrderStatues() == 0){
//				    			
//				    			//抢单
//								YSOrderModel updateorder = new YSOrderModel();
//								updateorder.setOrderStatues(YSOrderStatus.YSOrder_Select);
//								updateorder.setOrderPrice(ysoder.getOrderPrice());
//
//								String orderGo = msharePreferenceUtil.loadStringSharedPreference("userName", "");
//								if(null != orderGo && !TextUtils.isEmpty(orderGo)){									
//									updateorder.setOrderGoPhone(orderGo);
//								}else{
//						    			progressDialog.dismiss();
//									Toast.makeText(getActivity(), "抢单失败,请刷新后再尝试", Toast.LENGTH_SHORT).show();
//									return;
//								}
//								updateorder.update(getActivity(), orderId, new UpdateListener() {
//					
//								    @Override
//								    public void onSuccess() {
//								        // TODO Auto-generated method stub
//								    		progressDialog.dismiss();
//								    		handler.sendEmptyMessage(MSG_OK);			    		
//								    }
//					
//								    @Override
//								    public void onFailure(int code, String msg) {
//								        // TODO Auto-generated method stub
//								    		progressDialog.dismiss();
//								    		
//								    }
//								});
				    			
				    			
				    			
				    		}else{				    			
				    			//订单已被抢
				    			progressDialog.dismiss();
				    			Toast.makeText(getActivity(), "订单被抢,请刷新后再尝试", Toast.LENGTH_SHORT).show();
				    			
				    		}
				        
				    }

				    @Override
				    public void onFailure(int code, String arg0) {
				        // TODO Auto-generated method stub
				    	progressDialog.dismiss();
				    	Toast.makeText(getActivity(), "抢单故障", Toast.LENGTH_SHORT).show();
				       
				    }

				});
				
				
				
				
//				if(ysoder.getOrderStatues()<1){	
//					String ordertelePhone = ysoder.getTelePhone();
//					Intent it = new Intent(getActivity(), ConnectActivity.class);
//					it.putExtra("phone", ordertelePhone);
//					startActivity(it);
//				}

			}
		});
		ViewGroup logoutcancel = (ViewGroup) window
				.findViewById(R.id.lougoutcancel);
		logoutcancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
	}

	/**
	 * 更新ListView
	 * @param listorderdata
	 */
	public void refreshFragment(List<YSOrderModel> listorderdata){
		
		//始终保留最新订单
		for(YSOrderModel ys : listItems){
			if(ys.getOrderStatues()>=1){
				listItems.remove(ys);
			}
		}
		
		for (YSOrderModel ysOrderModel : listorderdata) {
			//if()
			listItems.add(ysOrderModel);
		}
		orderAdapter.set_datasource(listItems);
		orderAdapter.notifyDataSetChanged();
				
	}
	

}
