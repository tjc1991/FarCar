package com.cldxk.plug.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RecoveryPage;
import cn.smssdk.gui.RegisterPage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.config.CldxkConfig;
import com.cldxk.app.customview.CustomProgressDialog;
import com.cldxk.app.model.YSUser;
import com.cldxk.app.model.YSWage;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.MainActivity;
import com.cldxk.farcar.R;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class LoginActivity extends EBaseActivity implements OnClickListener{
	
	private TextView login, regist, wemall_forget_password;
	private EditText account, passwd;
	CustomProgressDialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initMylayout();
				
	}
	
	public void initMylayout(){
		
	regist = (TextView) findViewById(R.id.wemall_regist_button);
	login = (TextView) findViewById(R.id.wemall_login_button);
	account = (EditText) findViewById(R.id.wemall_login_account);
	passwd = (EditText) findViewById(R.id.wemall_login_passwd);

	wemall_forget_password = (TextView) findViewById(R.id.wemall_forget_password);
	wemall_forget_password.setOnClickListener(this);
	regist.setOnClickListener(this);
	login.setOnClickListener(this);
		
	
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_user_login;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.wemall_regist_button:
			// startActivity(new Intent(getActivity(), Regist.class));
			// 打开手机注册
			RegisterPage registerPage = new RegisterPage();
			registerPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果,启动帐号信息完善界面
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String phone = (String) phoneMap.get("phone");

						// //////////////////////////////////////////////////////////////////

						Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
						// 用Bundle携带数据
						Bundle bundle = new Bundle();
						// 传递name参数为tinyphp
						bundle.putString("phone", phone);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
			registerPage.show(LoginActivity.this);
			break;
		case R.id.wemall_forget_password:
			// startActivity(new Intent(getActivity(), Regist.class));
			// 打开手机验证
			RecoveryPage recoveryPage = new RecoveryPage();
			recoveryPage.setRegisterCallback(new EventHandler() {
				public void afterEvent(int event, int result, Object data) {
					// 解析注册结果,启动帐号信息完善界面
					if (result == SMSSDK.RESULT_COMPLETE) {
						@SuppressWarnings("unchecked")
						HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
						String phone = (String) phoneMap.get("phone");

						// //////////////////////////////////////////////////////////////////
						Intent intent = new Intent(LoginActivity.this,
								RecoveryPasswdActivity.class);
						// 用Bundle携带数据
						Bundle bundle = new Bundle();
						// 传递name参数为tinyphp
						bundle.putString("phone", phone);
						intent.putExtras(bundle);
						startActivityForResult(intent, 0x212);

					}
				}
			});
			recoveryPage.show(LoginActivity.this);
			break;
		case R.id.wemall_login_button:
			// 隐藏键盘
			this.HideKeyboard();
			if (((account.getText().toString().trim()).length() == 0)
					|| ((passwd.getText().toString().trim()).length() == 0)) {

				Toast.makeText(this, "帐号或密码为空", Toast.LENGTH_SHORT)
						.show();
			}

			else {
				regist.setClickable(false);
				login.setClickable(false);
								
				//检测用户状态
				if(checkOk()==true){					
					//获取用户信息
					this.getaccountinfo();
				}else{
					
					//查询用户审核状态
					checkUserStatus();
				}
				
			}
			break;
						
		default:
			break;
		}
	}

	public void getaccountinfo() {
			
		final YSUser user = new YSUser();
		user.setUsername(account.getText().toString());
//		user.setPassword(Utils.MD5(passwd.getText().toString()));
		user.setPassword(Utils.MD5(passwd.getText().toString()));
		user.login(getApplicationContext(), new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
				
				//保存用户信息
				msharePreferenceUtil.saveSharedPreferences("userName", account.getText().toString());
				
				//保存新密码
				msharePreferenceUtil.saveSharedPreferences("userpwd",passwd.getText().toString());
				
				//  保存用户状态
				msharePreferenceUtil.saveSharedPreferences("userFinish", "finish");
								
				regist.setClickable(true);
				login.setClickable(true);
				
				//获取用户信息
				getNewuserNick();				
								
				//切换到主界面
				Intent it = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(it);
				
				finish();
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
				regist.setClickable(true);
				login.setClickable(true);
			}
		});

	}
	
		public void HideKeyboard() {
			try {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(this.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (Exception e) {

			}

		}
			
	/**
	 * 显示美团进度对话框
	 * @param v
	 */
	public void showmeidialog(){
		dialog =new CustomProgressDialog(LoginActivity.this, "正在加载中",R.anim.frame);
		dialog.show();
	}
	
	public void closemeidialog(){
		if(dialog != null)
		dialog.dismiss();
	}	
	
	
	public void getNewuserNick(){
		
		String usernick = msharePreferenceUtil.loadStringSharedPreference("userNick", "");
		
		if(null != usernick && !TextUtils.isEmpty(usernick)){			
			//切换到主界面
			Intent it = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(it);
			
			finish();
		}
		else{
			//Log.i("tjc", "---->");
			//网络连接正常,则加载数据
			if(Utils.isNetworkConnected(getApplicationContext()))
			{
				
				//加载用户信息
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						final String tusername = msharePreferenceUtil.loadStringSharedPreference("userName", "");
						BmobQuery<YSUser> query = new BmobQuery<YSUser>();
						query.addWhereEqualTo("username", tusername);
						query.findObjects(getApplicationContext(), new FindListener<YSUser>() {
						    @Override
						    public void onSuccess(List<YSUser> object) {
						        // TODO Auto-generated method stub
						    //	Log.i("tjc", "---->aa"+tusername);
						    //	Log.i("tjc", object.size()+"");
						       if(object.size()>0){
						    	   //保存用户昵称
						    	   	msharePreferenceUtil.saveSharedPreferences("userNick", object.get(0).getUserNike());
						    	   	
						    	   	//保存用户Id
						    	   	msharePreferenceUtil.saveSharedPreferences("userobjId", object.get(0).getObjectId()+"");
						    	   	
						    	   	//保存用户登录成功状态
						    	   	msharePreferenceUtil.saveSharedPreferences("userFinish", "finish");
						    	   	
						    	   	
						    	   	//Log.i("tjc", "-->id="+object.get(0).getObjectId());
									//切换到主界面
									Intent it = new Intent(LoginActivity.this, MainActivity.class);
									startActivity(it);
									
									finish();
						    	   	
						       }
						    }
						    @Override
						    public void onError(int code, String msg) {
						        // TODO Auto-generated method stub
						    		//Log.i("tjc", "---->error");
						    		
						    		Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
						    }
						});
						
					}
				}).start();
			}else{
				
				//切换到主界面
				Intent it = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(it);
				
				finish();
			}
		}
		
	}
	
	private Boolean checkOk(){
		
		String msg = msharePreferenceUtil.loadStringSharedPreference("statusok", "");
		if(null == msg){
			return false;
		}else if(msg.equals("已开通")){
			return true;
		}else{
			return false;
		}
	}
	
	private void checkUserStatus(){
		 
		final ProgressDialog dialog = ProgressDialog.show(this, "用户认证状态", "正在查询中...");
		dialog.setCancelable(false);
		
		//发送Http请求
		
		RequestParams params = new RequestParams();
		//参数传递方式
		List<NameValuePair> values = new ArrayList<NameValuePair>(); 
		values.add(new BasicNameValuePair("account",account.getText().toString() ));
		values.add(new BasicNameValuePair("passwd", passwd.getText().toString()));
		
		params.addBodyParameter(values);
		
		httpClient.send(HttpMethod.POST, CldxkConfig.API_LOGIN, params, new RequestCallBack<String>(){

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Toast.makeText(getApplicationContext(), "连接服务器异常", Toast.LENGTH_SHORT)
				.show();
				
				regist.setClickable(true);
				login.setClickable(true);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				regist.setClickable(true);
				login.setClickable(true);
				
				Log.i("tjc", "--->msg="+arg0.result);
				
				
				JSONObject resultjson = JSON.parseObject(arg0.result);
				int msgid = resultjson.getIntValue("code");
				Log.i("tjc", "--->code="+msgid+"");
				if(msgid == 200){
					JSONArray jsonarray = JSON.parseArray(resultjson.getString("data"));
					if(null != jsonarray){
						
						JSONObject jsonstatus = jsonarray.getJSONObject(0);
						String msgst = jsonstatus.getString("userstatus");
//						Log.i("tjc", jsonstatus.getString("userstatus"));
						
						if(msgst != null){
						if(msgst.equals("已开通")){
							
							msharePreferenceUtil.saveSharedPreferences("statusok", "已开通");
							Toast.makeText(getApplicationContext(), "已通过,可以直接登陆",
									Toast.LENGTH_SHORT).show();
							
						}else{
							Toast.makeText(getApplicationContext(), "请耐心等待审核...",
									Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "请耐心等待审核...",
								Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "请耐心等待审核...",
							Toast.LENGTH_SHORT).show();
				}
				}else{
					Toast.makeText(getApplicationContext(), "用户信息错误...",
							Toast.LENGTH_SHORT).show();
					
				}
				
//				JSONArray jsonarray = JSON.parseArray(arg0.result);
//				if(null != jsonarray){
//					
//					JSONObject jsonobj = jsonarray.getJSONObject(0);
//					int msgid = jsonobj.getIntValue("code");
//					Log.i("tjc", "--->code="+msgid+"");
//					
//					JSONArray dataarray = jsonarray.getJSONArray(1);
//					JSONObject jsonstatus = dataarray.getJSONObject(0);
//					String msgst = jsonstatus.getString("userstatus");
//					Log.i("tjc", "--->msgst="+msgst+"");
//					
//					if(msgst != null){
//						if(msgst.equals("已开通")){
//							
//							msharePreferenceUtil.saveSharedPreferences("statusok", "已开通");
//							Toast.makeText(getApplicationContext(), "已通过,可以直接登陆",
//									Toast.LENGTH_SHORT).show();
//							
//						}else{
//							Toast.makeText(getApplicationContext(), "请耐心等待审核...",
//									Toast.LENGTH_SHORT).show();
//						}
//					}
//				}else{
//					Toast.makeText(getApplicationContext(), "请耐心等待审核...",
//							Toast.LENGTH_SHORT).show();
//				}
//				
////				Toast.makeText(getApplicationContext(), "完成认证",
////						Toast.LENGTH_SHORT).show();
//				
//				//finish();
			}
							
		});
		
	}

}


