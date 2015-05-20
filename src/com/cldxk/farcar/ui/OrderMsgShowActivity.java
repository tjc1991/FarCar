package com.cldxk.farcar.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.listener.UpdateListener;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.config.YSOrderStatus;
import com.cldxk.app.config.YSOrderType;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.farcar.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

public class OrderMsgShowActivity extends EBaseActivity implements OnClickListener{
	
	private ImageView real_prepare_img = null;
	private Button close_btn = null;
	private TextView cr_num_tv = null;
	private TextView from_tv = null;
	private TextView go_tv = null;
	private TextView time_tv = null;
	private Button qd_btn = null;
	private TextView djs_tv = null;
	
	private YSOrderModel ysorder  = null;
	
	private static final int MSG_SUCCESS = 16;
	private static final int MSG_ERROR = 17;
	
	
	// 语音合成对象
	private SpeechSynthesizer mTts;
	
	private static String TAG = "tjc"; 	

	// 默认云端发音人
	public static String voicerCloud="xiaoyan";
			
	private String mesg = null;
	
	private Toast mToast;
	
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	
	//30s抢单时间
	private int retain_time = 15;
	
	private static final int EVERY_TIME = 10;
	
	private static final int FINISH_ACTIVITY = 11;
	
	private String orderGo = null;
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			
			switch(msg.what)
			{
				case EVERY_TIME:
					
					//刷新UI
					djs_tv.setText(((Integer)msg.obj).intValue()+"");
					break;
					
				case FINISH_ACTIVITY:					
					//结束Activity
					finish();
					break;
					
				case MSG_SUCCESS:					
					//结束Activity
					Toast.makeText(getApplicationContext(), "抢单成功", Toast.LENGTH_SHORT).show();
					String ordertelePhone = ysorder.getTelePhone();
//					Bundle bundle = new Bundle();
//					bundle.putString("phone", ordertelePhone);
					Intent it = new Intent(OrderMsgShowActivity.this, ConnectActivity.class);
					it.putExtra("phone", ordertelePhone);
					startActivity(it);
					finish();
					break;
					
				case MSG_ERROR:					
					//结束Activity
					Toast.makeText(getApplicationContext(), "抢单失败", Toast.LENGTH_SHORT).show();
					finish();
					break;
					
					default:
						break;
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		orderGo = "";
		orderGo = msharePreferenceUtil.loadStringSharedPreference("userName", "");
		
		//初始化抢单时间
		retain_time = 15;
		
		initMyLatout();
		
		//初始化语音设置
		initMyVoice();
		
		//获取数据
		ysorder = (YSOrderModel) this.getIntent().getSerializableExtra("mysorder");
		
		if(null != ysorder)
		{
			//Log.i("tjc", "--->"+ysorder.getCityFrom());
			
			from_tv.setText(ysorder.getCityFrom());
			go_tv.setText(ysorder.getCityDest());

			//设置订单预定时间
			if(ysorder.getOrderType() == YSOrderType.Others){
				
				real_prepare_img.setImageResource(R.drawable.order_fragment_type_preorder);
				time_tv.setText("预约时间: "+ysorder.getOrderYuYueMsg());
				mesg = "预约,  "+"快来抢单,"+ysorder.getOrderYuYueMsg()+"从"+ysorder.getCityFrom()+"出发去往"
						+ysorder.getCityDest()+""+"方向";
			}else{
				real_prepare_img.setImageResource(R.drawable.order_fragment_type_instant);
				mesg = "实时,  "+"快来抢单,从"+ysorder.getCityFrom()+"出发去往"
						+ysorder.getCityDest()+""+"方向";
			}
									
			PlayMyOrder(mesg);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					while(retain_time >0 ){
						
						retain_time--;
						try {
							//休眠1秒
							Thread.sleep(1000);
							
							Message msg = handler.obtainMessage();
							
							if(retain_time == 0){
								msg.what = FINISH_ACTIVITY;								
							}else{
								msg.what = EVERY_TIME;
								msg.obj = retain_time;
							}
							handler.sendMessage(msg);
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}).start();

	    }			
		
		
	}
	
	public void initMyLatout(){
		
		real_prepare_img = this.findImageViewById(R.id.real_prepare_img);
		close_btn = this.findButtonById(R.id.close_img_btn);
		close_btn.setOnClickListener(this);
		
		cr_num_tv = this.findTextViewById(R.id.cr_num_tv);
		from_tv = this.findTextViewById(R.id.msg_from_tv);
		go_tv = this.findTextViewById(R.id.msg_go_tv);
		time_tv = this.findTextViewById(R.id.msg_time_tv);
		time_tv.setText("");
		
		djs_tv = this.findTextViewById(R.id.qd_time_new);
		djs_tv.setText(retain_time+"");
		
		qd_btn = this.findButtonById(R.id.qd_btn);
		qd_btn.setOnClickListener(this);
		
		
				
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.order_msg_dlg;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.close_img_btn:
			this.finish();
			break;
			
		case R.id.qd_btn:
			
			//释放语音资源
			if(mTts != null)
			{				
				mTts.stopSpeaking();
				mTts.destroy();
			}
			
			getANewOrder();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 抢接订单
	 */
	public void getANewOrder(){
		
		//结束线程
		//retain_time = 0;
				
		//更新订单状态
		if(null != ysorder)
		{
			final ProgressDialog progressDialog =ProgressDialog.show(this, 
					"一键抢单", "正在抢单...");
			progressDialog.setCancelable(false);
			
			if(ysorder.getOrderStatues() == YSOrderStatus.YSOrder_Normal)
			{			
				String orderId = ysorder.getObjectId();
				
				YSOrderModel updateorder = new YSOrderModel();
				updateorder.setOrderStatues(YSOrderStatus.YSOrder_Select);
				updateorder.setOrderGoPhone(orderGo);
				updateorder.update(this, orderId, new UpdateListener() {
	
				    @Override
				    public void onSuccess() {
				        // TODO Auto-generated method stub
				    		progressDialog.dismiss();
				    						    		
				    		handler.sendEmptyMessage(MSG_SUCCESS);			    		
				    }
	
				    @Override
				    public void onFailure(int code, String msg) {
				        // TODO Auto-generated method stub
				    		progressDialog.dismiss();
				    		handler.sendEmptyMessage(MSG_ERROR);	
				    }
				});
				//Log.i("tjc", orderId);			
			}
			else{
				progressDialog.dismiss();
				Toast.makeText(getApplicationContext(), "订单已被抢...", Toast.LENGTH_SHORT).show();
				//结束退出
				finish();
			}
		}
				
		//结束Activity
		
	}
	
	public void initMyVoice(){
		
		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
		mEngineType = SpeechConstant.TYPE_CLOUD;
		
		// 设置参数
		setParam();
		
	}
	
	public void PlayMyOrder(String msg)
	{
		if(null != mTts){			
			int code = mTts.startSpeaking(msg, mTtsListener);
			if (code != ErrorCode.SUCCESS) {
//			showTip("语音合成失败,错误码: " + code);	
				//showTip("语音合成失败");
				finish();
			}
		}
	}
	
	/**
	 * 初始化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
//        		showTip("初始化失败,错误码："+code);
				//showTip("初始化失败");
        	}		
		}
	};
	
	private void showTip(final String str){
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}


	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			
		}

		@Override
		public void onSpeakPaused() {
			
		}

		@Override
		public void onSpeakResumed() {
			
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {

		}

		@Override
		public void onCompleted(SpeechError error) {
			if(error == null)
			{
				//showTip("播放完成");
			}
			else if(error != null)
			{
				
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub
			
		}
	};

	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	private void setParam(){
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		//设置合成
		//设置使用云端引擎
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			//设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);
		
		//设置语速
		mTts.setParameter(SpeechConstant.SPEED,"50");

		//设置音调
		mTts.setParameter(SpeechConstant.PITCH,"50");

		//设置音量
		mTts.setParameter(SpeechConstant.VOLUME,"50");
		
		//设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE,"3");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mTts != null)
		{			
			mTts.stopSpeaking();
			// 退出时释放连接
			mTts.destroy();
		}
		
	}
	

}
