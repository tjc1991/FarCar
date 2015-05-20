package com.cldxk.plug.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.config.CldxkConfig;
import com.cldxk.app.model.YSUser;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.MainActivity;
import com.cldxk.farcar.R;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecoveryPasswdActivity extends EBaseActivity {
	private EditText wemall_recoverypasswd_new, wemall_recoverypasswd_new_re;
	private ProgressBar wemall_recoverypasswd_loadingBar;
	private TextView wemall_recoverypasswd_button;
	private Handler handler = null;
	private int RecoveryPasswdtstate = -1;
	private String phone = "";

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.wemall_user_recoverypasswd);

		Bundle bundle = this.getIntent().getExtras();
		phone = bundle.getString("phone");

		wemall_recoverypasswd_new = (EditText) findViewById(R.id.wemall_recoverypasswd_new);
		wemall_recoverypasswd_new_re = (EditText) findViewById(R.id.wemall_recoverypasswd_new_re);
		//wemall_recoverypasswd_loadingBar = (ProgressBar) findViewById(R.id.wemall_recoverypasswd_loadingBar);
		wemall_recoverypasswd_button = (TextView) findViewById(R.id.wemall_recoverypasswd_button);
		wemall_recoverypasswd_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 隐藏键盘
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				RecoveryPasswdCheck();
			}
		});
	}

	public void RecoveryPasswdCheck() {
		if (wemall_recoverypasswd_new.getText().toString().length() < 6) {
			Toast.makeText(this, "新密码至少要六位额....", Toast.LENGTH_SHORT).show();
		} else if (!(wemall_recoverypasswd_new.getText().toString()
				.equals(wemall_recoverypasswd_new_re.getText().toString()))) {
			Toast.makeText(this, "两次输入的新密码好像不一样额....", Toast.LENGTH_SHORT)
					.show();
		} else if (wemall_recoverypasswd_new.getText().toString()
				.equals(wemall_recoverypasswd_new_re.getText().toString())) {
			//wemall_recoverypasswd_loadingBar.setVisibility(View.VISIBLE);
			reapasswd();
		}
	}

	@SuppressLint("HandlerLeak")
	public void reapasswd() {

		//先更新服务器一
		String userid = msharePreferenceUtil.loadStringSharedPreference("userobjId", "");
		if(userid == null){
			return;
		}
		
		final ProgressDialog dialog = ProgressDialog.show(this, "用户认证状态", "正在查询中...");
		dialog.setCancelable(false);
		
		YSUser newUser = new YSUser();
		newUser.setPassword(Utils.MD5(wemall_recoverypasswd_new.getText().toString()));
		newUser.update(this,userid,new UpdateListener() {
		    @Override
		    public void onSuccess() {
		        // TODO Auto-generated method stub
		    		//在更新服务器二
		    	dialog.dismiss();
		    		updateYsSystem();
		    	
		    }
		    @Override
		    public void onFailure(int code, String msg) {
		        // TODO Auto-generated method stub
		     	dialog.dismiss();
		        Toast.makeText(getApplicationContext(),"更新用户信息失败", Toast.LENGTH_SHORT).show();
		    }
		});		
				
	}

	public void updateYsSystem(){
		
		final ProgressDialog dialog = ProgressDialog.show(this, "用户认证状态", "正在查询中...");
		dialog.setCancelable(false);
		
		//发送Http请求
		
		RequestParams params = new RequestParams();
		//参数传递方式
		List<NameValuePair> values = new ArrayList<NameValuePair>(); 
		values.add(new BasicNameValuePair("phone",phone));
		values.add(new BasicNameValuePair("new", wemall_recoverypasswd_new.getText().toString()));
		
		params.addBodyParameter(values);
		
		httpClient.send(HttpMethod.POST, CldxkConfig.API_REPEAT_PWD, params, new RequestCallBack<String>(){

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "连接服务器异常", Toast.LENGTH_SHORT)
				.show();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				Log.i("tjc", "--->msg="+arg0.result);
								
				JSONObject resultjson = JSON.parseObject(arg0.result);
				int msgid = resultjson.getIntValue("code");
				Log.i("tjc", "--->code="+msgid+"");
				if(msgid == 200){
					
					Toast.makeText(getApplicationContext(), "更新密码成功", Toast.LENGTH_SHORT)
					.show();
					
					//保存新密码
					msharePreferenceUtil.saveSharedPreferences("userpwd", wemall_recoverypasswd_new.getText().toString());
					
					//清楚登录状态
					msharePreferenceUtil.saveSharedPreferences("userFinish", "");
					
					//跳转Activity
					startActivity(new Intent(RecoveryPasswdActivity.this, LoginActivity.class));
															
					finish();
				}

			}
							
		});
		
		
	}
	
	
	
	
	public void result() {

		if (RecoveryPasswdtstate == 0) {

			Toast.makeText(this, "当前手机号未注册,请注册后登录", Toast.LENGTH_SHORT).show();
		}
		if (RecoveryPasswdtstate == -1) {

			Toast.makeText(this, "链接服务器异常,请稍候重试", Toast.LENGTH_SHORT).show();
		}
		if (RecoveryPasswdtstate == 1) {
			Intent intent = new Intent(RecoveryPasswdActivity.this, MainActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("result", "1");
			bundle.putString("phone",phone);
			intent.putExtras(bundle);
			setResult(0x212, intent);
			Toast.makeText(this, "恢复密码成功,请使用新密码登录", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.user_recoverypasswd;
	}

}
