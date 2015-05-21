package com.cldxk.app.farcar.adapter;

import java.util.ArrayList;

import com.cldxk.app.config.YSOrderStatus;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.farcar.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FromeAndGoAdapter extends BaseAdapter implements OnClickListener{
	
	private ArrayList<YSOrderModel> listItems = null;	
	//ArrayList<YSOrderModel>listItems = null;
	private Context mContext = null;
	
	//用于按钮点击事件回调接口
	private Callback mCallback = null;
	

	public FromeAndGoAdapter( Context mContext,ArrayList<YSOrderModel> listItems
			,Callback callback) {
		super();
		this.mContext = mContext;
		this.listItems = listItems;
		mCallback = callback;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	// 重设数据源,避免adapter.notifyDataSetChanged()无响应
	public void set_datasource(ArrayList<YSOrderModel> d) {
		this.listItems = d;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final ViewHolder holder;
        if(convertView == null){
	          //使用自定义的list_items作为Layout
	          convertView = LayoutInflater.from(mContext).inflate(R.layout.carmsg_list_layout, parent, false);
	          //使用减少findView的次数
			  holder = new ViewHolder();
			  holder.fromTv = ((TextView) convertView.findViewById(R.id.carmsg_from)) ;
			  holder.goTv = ((TextView) convertView.findViewById(R.id.carmsg_go));
			  holder.statusTv = ((TextView) convertView.findViewById(R.id.carmsg_status));
			  holder.phoneImg = ((ImageView) convertView.findViewById(R.id.carmsg_phone));
			  holder.priceTv = (TextView) convertView.findViewById(R.id.ys_order_money);
			  //设置标记
			  convertView.setTag(holder);
        }else{
      	  holder = (ViewHolder) convertView.getTag();
        }
		
		//填充数据
        YSOrderModel orderitem = (YSOrderModel)listItems.get(position);
        if (orderitem == null) {
            return null;
        }
        
        holder.goTv.setText(orderitem.getCityDest());
        holder.fromTv.setText(orderitem.getCityFrom()); 
        holder.priceTv.setText("预约路费:"+orderitem.getOrderPrice()+"");
        
        if(orderitem.getOrderStatues() == YSOrderStatus.YSOrder_Select){
        		holder.statusTv.setText("已抢单");
        }else if(orderitem.getOrderStatues() == YSOrderStatus.YSOrder_Pay){
        		holder.statusTv.setText("已支付完成");
        }else{
        		holder.statusTv.setText("未接单");
        }
        
        holder.phoneImg.setBackgroundResource(R.drawable.call_image);
        //设置按钮点击事件
        holder.phoneImg.setTag(position);
        holder.phoneImg.setOnClickListener(this);
        		
		return convertView;
	}
	
    /**
	 * ViewHolder类
	 */
	static class ViewHolder {
		TextView fromTv;
		TextView goTv;
		TextView statusTv;
		ImageView phoneImg;
		TextView  priceTv;
	}
	
	   /**
	  * 自定义接口，用于回调按钮点击事件到Activity
	  * @author Ivan Xu
	  * 2014-11-26
	  */
	 public interface Callback {
	     public void click(View v);
	 }

	 //按钮事件
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mCallback.click(v);
		
	}

}
