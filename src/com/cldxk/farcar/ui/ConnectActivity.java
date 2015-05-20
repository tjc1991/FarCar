package com.cldxk.farcar.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.farcar.R;

public class ConnectActivity extends EBaseActivity implements OnClickListener{

	private LinearLayout call_lv = null;
	private TextView call_text = null;
	
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	private String phone = "";
	
	public static final String SENT_SMS_ACTION = "SENT_SMS_ACTION";
	private SMSSendResultReceiver mSMSReceiver = new SMSSendResultReceiver();
	private IntentFilter mSMSResultFilter = new IntentFilter();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		phone = "";
		
		String username = msharePreferenceUtil.loadStringSharedPreference("userNick", "");
		String userphone =  msharePreferenceUtil.loadStringSharedPreference("userName", "");
		String userdrivercar = msharePreferenceUtil.loadStringSharedPreference("userCar", "");
		
		//注册广播
		mSMSResultFilter.addAction(SENT_SMS_ACTION);  
        registerReceiver(mSMSReceiver, mSMSResultFilter);
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("联系客户");
		
		call_lv = this.findLinearLayoutById(R.id.wode_call);
		call_lv.setOnClickListener(this);
		call_text = this.findTextViewById(R.id.wode_call_text);
		
		if(username == null || userphone == null){
			Toast.makeText(this, "用户名或手机号或车牌号错误", Toast.LENGTH_SHORT);
			call_lv.setVisibility(View.GONE);
			return;
		}
		
		phone = getIntent().getStringExtra("phone");
		if(phone != null)
		{
			call_text.setText(phone);
		}
		
		//发送短信,通知用户		
		sendMyMsg("【雨山远程拼车】 你已成功预约出租车，"+"车牌号:"+userdrivercar+""+
		"司机:"+username+"，"+"联系电话:"+userphone+
		" 已接单,希望您及时联系司机确认，预祝本次旅途愉快~~");
		
	}
	
	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_connect;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.wode_call:
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ call_text.getText()));
			startActivity(intent);// 内部类
			break;
			
		case R.id.fragment_actionbar_back:
			finish();
			break;	
		
		default:
			break;
		}
	}
	
	public void sendMyMsg(String msg){
				 
        SmsManager sms = SmsManager.getDefault();
        
        /* 建立自定义Action常数的Intent(给PendingIntent参数之用) */  
        Intent itSend = new Intent(SENT_SMS_ACTION); 
        
        /* sentIntent参数为传送后接受的广播信息PendingIntent */  
        PendingIntent mSendPI = PendingIntent.getBroadcast(getApplicationContext(), 0, itSend, 0);  
          
        if(phone != null && !TextUtils.isEmpty(phone)){
        	
        	sms.sendTextMessage(phone, null, msg, mSendPI, null);
        }
	}
	
	class SMSSendResultReceiver extends BroadcastReceiver
    {
        @SuppressLint("ShowToast")
		@Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                			Toast.makeText(getApplicationContext(), "我们已通知客户", Toast.LENGTH_SHORT);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    default:
                    		Toast.makeText(getApplicationContext(), "通知用户失败,您可电话联系对方", Toast.LENGTH_SHORT);
                    		break;
                }
        }
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		/* 取消注册自定义Receiver */  
	    unregisterReceiver(mSMSReceiver);
	}

}
