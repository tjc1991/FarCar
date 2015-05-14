package com.cldxk.plug.user;

import java.util.HashMap;
import java.util.List;

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

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.customview.CustomProgressDialog;
import com.cldxk.app.model.YSUser;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.MainActivity;
import com.cldxk.farcar.R;

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
				this.getaccountinfo();
			}
			break;
		default:
			break;
		}
	}

	public void getaccountinfo() {
		
		final YSUser user = new YSUser();
		user.setUsername(account.getText().toString());
		user.setPassword(Utils.MD5(passwd.getText().toString()));
		user.login(getApplicationContext(), new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
				
				//保存用户信息
				msharePreferenceUtil.saveSharedPreferences("userName", account.getText().toString());
				
				regist.setClickable(true);
				login.setClickable(true);
				//获取用户信息
				getNewuserNick();
				
				
//				//切换到主界面
//				Intent it = new Intent(LoginActivity.this, MainActivity.class);
//				startActivity(it);
//				
//				finish();
				
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

}


