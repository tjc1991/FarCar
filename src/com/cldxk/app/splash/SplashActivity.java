package com.cldxk.app.splash;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.config.CldxkConfig;
import com.cldxk.farcar.MainActivity;
import com.cldxk.farcar.R;
import com.cldxk.plug.user.LoginActivity;
import com.cldxk.plug.user.RecoveryPasswdActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * 
 * 闪屏
 * 
 * @author liudewei
 *
 */
@SuppressLint("HandlerLeak")
public class SplashActivity extends EBaseActivity {
	private Handler mMainHandler = new Handler() {

		public void handleMessage(Message msg) {
			
			String userfinish = msharePreferenceUtil.loadStringSharedPreference("userFinish", "");
			String userphone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
			
			Log.i("tjc", "-->="+userphone);
			
			if(null != userfinish && !TextUtils.isEmpty(userfinish) && userfinish.equals("finish"))
			{
				//加载司机车型与车牌号
				String carxh = msharePreferenceUtil.loadStringSharedPreference("carxh", "");
				String carph = msharePreferenceUtil.loadStringSharedPreference("userCar", "");
				
				if(carxh.length()==0 || carph.length()==0){
					
					//请求数据
					GetUserMsgCar(userphone , msharePreferenceUtil.loadStringSharedPreference("userpwd",""));
																			
				}else{
					Intent it = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(it);
				}
				
				
//				Intent it = new Intent(SplashActivity.this, MainActivity.class);
//				startActivity(it);
			}else if(null != userphone && !TextUtils.isEmpty(userphone))
			{
				Intent it = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(it);
			}	
			else{
				Intent it = new Intent(SplashActivity.this, WelcomeActivity.class);
				startActivity(it);	
			}
			finish();
			
		
			
//			String userphone = msharePreferenceUtil.loadStringSharedPreference("userName", "");
//			if(null != userphone && !TextUtils.isEmpty(userphone))
//			{
//				Intent it = new Intent(SplashActivity.this, MainActivity.class);
//				startActivity(it);
//			}else{
//				Intent it = new Intent(SplashActivity.this, WelcomeActivity.class);
//				startActivity(it);	
//			}
//			finish();
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.wemall_splash);
		
//		@SuppressWarnings("unused")
//		SQLProcess chushihuaProcess=new SQLProcess(this);//初始化数据库,防止FC
//		// ////////////////////初始化第三方SDK组件///////////////////////////////////
		//短信验证SDK
		mMainHandler.sendEmptyMessageDelayed(0, 2000);
		
		//msharePreferenceUtil.saveSharedPreferences("userCar", "吉ATX365");
		//msharePreferenceUtil.saveSharedPreferences("carxh", "圣达菲");
		
		
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.common_start_splash;
	}
	
	public void GetUserMsgCar(String ph , String pwd){
		
		//发送Http请求
		
		RequestParams params = new RequestParams();
		//参数传递方式
		List<NameValuePair> values = new ArrayList<NameValuePair>(); 
		values.add(new BasicNameValuePair("account",ph));
		values.add(new BasicNameValuePair("passwd", pwd));
		
		params.addBodyParameter(values);
		
		httpClient.send(HttpMethod.POST, CldxkConfig.API_CHECK_MSG, params, new RequestCallBack<String>(){

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "连接服务器异常", Toast.LENGTH_SHORT)
				.show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				
				Log.i("tjc", "--->msg="+arg0.result);
								
				JSONObject resultjson = JSON.parseObject(arg0.result);
				int msgid = resultjson.getIntValue("code");
				Log.i("tjc", "--->code="+msgid+"");
				if(msgid == 200){
					
					JSONArray jsarray = resultjson.getJSONArray("data");
					
					JSONObject xhobj = jsarray.getJSONObject(0);
					
					Log.i("tjc", "--->sda");
					
					String xhstr = xhobj.getString("bankcar");
					
					JSONObject carobj = jsarray.getJSONObject(0);
					String carstr = carobj.getString("bxdlicene");
					
					
					Log.i("tjc", "--->xhstr="+xhstr);
					Log.i("tjc", "--->carstr="+carstr);
					
					//保存新密码
					msharePreferenceUtil.saveSharedPreferences("userCar",xhstr);
					
					//清楚登录状态
					msharePreferenceUtil.saveSharedPreferences("carxh", carstr);
					
					//跳转Activity
					Intent it = new Intent(SplashActivity.this, MainActivity.class);
					startActivity(it);										
					finish();
				}

			}
							
		});
	}
	
	

}