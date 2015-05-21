package com.cldxk.app.farcar.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import cn.bmob.v3.BmobUser;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import com.cldxk.app.EApplication;
import com.cldxk.app.base.BaseFragment;
import com.cldxk.app.breakrules.RulesActivity;
import com.cldxk.app.config.CldxkConfig;
import com.cldxk.app.imagecache.FileCache;
import com.cldxk.app.imagecache.ImageLoader;
import com.cldxk.app.model.YSUser;
import com.cldxk.app.updateversion.UpdateManager;
import com.cldxk.app.utils.SharePreferenceUtil;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.R;
import com.cldxk.farcar.ui.AboutActivity;
import com.cldxk.farcar.ui.BankCarActivity;
import com.cldxk.farcar.ui.UserAccountMoney;
import com.cldxk.farcar.ui.UserOrderActivity;
import com.cldxk.plug.user.LoginActivity;
import com.cldxk.plug.user.RecoveryPasswdActivity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserCenterFragment extends BaseFragment implements OnClickListener{
	
	private View view = null;
	private ViewGroup user_logout,
	usercenter_about, user_center_myorder, user_center_claer,
	user_wodeqianbao, wemall_user_center_changepasswd, topuserinfo,user_center_bank,user_tixian;
	private HashMap<String, Object> accountinfo;
	private TextView usercenter_username, usercenter_address;
	private Handler handler = null;
	private String useruid, username, useraddress, userphone;
	private ImageView mFace;
	// result,request code
	private static final int PHOTO_REQUEST_CAMERA = 0x111;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 0x112;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 0x113;// 结果
	private Bitmap bitmap;
	// 临时图像文件
	private String PHOTO_FILE_NAME = ".temp.jpg";
	private File tempFile;
	private ImageLoader imageLoader;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_user_center, container, false);
		initMyLayout();
		return view;
	}
	
	public void initMyLayout(){
		imageLoader = new ImageLoader(getActivity());
		
		topuserinfo = (ViewGroup) view.findViewById(R.id.topuserinfo);
		topuserinfo.setOnClickListener(this);
		
		user_center_myorder = (ViewGroup) view
				.findViewById(R.id.user_center_myorder);
		user_center_myorder.setOnClickListener(this);
		wemall_user_center_changepasswd = (ViewGroup) view
				.findViewById(R.id.wemall_user_center_changepasswd);
		wemall_user_center_changepasswd.setOnClickListener(this);
		user_wodeqianbao = (ViewGroup) view
				.findViewById(R.id.wemall_user_center_wodeqianbao);
		user_wodeqianbao.setOnClickListener(this);
		usercenter_about = (ViewGroup) view.findViewById(R.id.usercenter_about);
		usercenter_about.setOnClickListener(this);
		
		user_center_bank = (ViewGroup) view.findViewById(R.id.wemall_user_center_bankcar);
		user_center_bank.setOnClickListener(this);
		
		user_tixian = (ViewGroup) view.findViewById(R.id.user_center_tixian);
		user_tixian.setOnClickListener(this);
		
		user_logout = (ViewGroup) view.findViewById(R.id.user_logout);
		user_logout.setOnClickListener(this);
		mFace = (ImageView) view.findViewById(R.id.user_center_user_icon);
		mFace.setOnClickListener(this);
		
		usercenter_address = (TextView) view
				.findViewById(R.id.user_center_address);
		usercenter_username = (TextView) view
				.findViewById(R.id.user_center_username);
		
		user_center_claer = (ViewGroup) view
				.findViewById(R.id.user_center_clear);
		user_center_claer.setOnClickListener(this);
		
			
			//本地加载
			//useruid = "456";	
			username = msharePreferenceUtil.loadStringSharedPreference("userName", "");
			userphone = msharePreferenceUtil.loadStringSharedPreference("userNick", "");
			if(null != username && !TextUtils.isEmpty(username))
			{
				useruid = username;
			}else{
				useruid = "t123";
			}	
			if(null != username && null != usercenter_address){
				
				usercenter_username.setText(username);
				usercenter_address.setText(userphone);	
			}
		
		// 异步加载头像
		imageLoader.DisplayImage(CldxkConfig.API_UPLOADS
				+ Utils.MD5(useruid) + ".jpg", mFace);
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.user_logout:
			LogoutTip();
			break;
		case R.id.usercenter_about:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			break;
		case R.id.user_center_clear:
			//违章查询
			startActivity(new Intent(getActivity(), RulesActivity.class));			
			break;
		case R.id.user_center_myorder:
			//我的订单
			startActivity(new Intent(getActivity(), UserOrderActivity.class));
			
			break;
			
		case R.id.wemall_user_center_changepasswd:
			//改变密码
			recoverMyPassword();		
			break;	
			
		case R.id.wemall_user_center_bankcar:
			//绑定银行卡
			selectBankcar();
			break;	
			
		case R.id.wemall_user_center_tixian:
			//用户资金和体现
			gotoMyDetailMoney();
			break;	
						
		case R.id.user_center_user_icon:
			ChangeUserIcon();
			break;
		case R.id.wemall_user_center_wodeqianbao:
			//版本更新
			UpdateManager manager = new UpdateManager(getActivity());
			// 检查软件更新
			manager.checkUpdate();
			break;
			
		default:
			break;
		}
		
	}
	
	
	/*
	 * 
	 * 注销账户时销毁Preferences
	 */

	public void DestroyPreferences() {
		//msharePreferenceUtil.removeAllKey();
		msharePreferenceUtil.removeKey("userFinish");
	}	
	
	/*
	 * 
	 * 账户注销提示
	 */

	public void LogoutTip() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		final Dialog dialog = builder.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.user_logout_dialog);
		ViewGroup logout = (ViewGroup) window.findViewById(R.id.logout);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				dialog.dismiss();
				//清除用户信息
				DestroyPreferences();
				//跳转到登陆界面
				Intent it = new Intent(getActivity(), LoginActivity.class);
				startActivity(it);

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

	/*
	 * 
	 * 更换头像的弹出选择框
	 */

	public void ChangeUserIcon() {
		final AlertDialog dlg = new AlertDialog.Builder(getActivity()).create();
		dlg.show();
		Window window = dlg.getWindow();
		window.setContentView(R.layout.user_choose_pic_layout);
		ViewGroup choose_from_camre = (ViewGroup) window
				.findViewById(R.id.choose_from_camre);
		choose_from_camre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				camera();

			}
		});
		ViewGroup choose_from_gallery = (ViewGroup) window
				.findViewById(R.id.choose_from_gallery);
		choose_from_gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dlg.dismiss();
				gallery();
			}
		});
	}

	// //////////////////////头像相关-//////////////////

	/*
	 * 上传图片
	 */
	public void upload() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			byte[] buffer = out.toByteArray();
			byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
			String photo = new String(encode);
			
			RequestParams params = new RequestParams();
			//参数传递方式
			List<NameValuePair> values = new ArrayList<NameValuePair>(); 
			values.add(new BasicNameValuePair("photo", photo));
			values.add(new BasicNameValuePair("uid", useruid));
			params.addBodyParameter(values);
			
			httpClient.send(HttpMethod.POST, CldxkConfig.API_UPLOAD_IMG, params, new RequestCallBack<String>(){

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "连接服务器异常", Toast.LENGTH_SHORT)
					.show();
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), "头像上传成功!",
							Toast.LENGTH_SHORT).show();
					// 新的头像上传成功后旧的缓存失效,我们需要删除之//
					FileCache.Filedelete(CldxkConfig.API_UPLOADS
							+ Utils.MD5(useruid)
							+ ".jpg");
					//Toast.makeText(getActivity(), "头像上传失败!",
					//		Toast.LENGTH_SHORT).show();
				}
				
				
			});
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * 从相册获取
	 */
	public void gallery() {
		// 激活系统图库，选择一张图片
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	/*
	 * 从相机获取
	 */
	public void camera() {
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		// 判断存储卡是否可以用，可用进行存储
		if (hasSdcard()) {
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
		}
		startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
	}

	/**
	 * 剪切图片
	 * 
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	/*
	 * 判断扩展存储是否存在
	 */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 
	 * 
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */

	@SuppressLint("ShowToast")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// ///////////////////////////////////////////////////////////////////
		switch (resultCode) {
		// 修改密码成功的返回结果
		case 0x211:
			Bundle b = data.getExtras();
			String str = b.getString("result");
			if (str.equals("1")) {
				DestroyPreferences();
				
				//返回登陆界面
				Intent it = new Intent(getActivity(), LoginActivity.class);
				startActivity(it);
			}
		default:
			break;

		}
		// ///////////////////////////////////////////////////////////////////
		switch (requestCode) {
		// 从图库获取图像的返回结果
		case PHOTO_REQUEST_GALLERY:
			if (data != null) {
				System.out.println("有图");
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}
			break;
		// 从照相机获取图像的返回结果
		case PHOTO_REQUEST_CAMERA:
			if (hasSdcard()) {
				tempFile = new File(Environment.getExternalStorageDirectory(),
						PHOTO_FILE_NAME);
				crop(Uri.fromFile(tempFile));
			} else {
				Toast.makeText(getActivity(), "未找到存储卡,无法存储照片!", 0).show();
			}
			break;
		// 裁减图像的返回结果
		case PHOTO_REQUEST_CUT:
			try {
				// 获取到图像，将图像设置到到imageview，并启动图像上传
				bitmap = data.getParcelableExtra("data");
				this.mFace.setImageBitmap(bitmap);
				System.out.println("--------------------------");
				upload();

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}	
	
	public void recoverMyPassword(){
		
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

					Intent intent = new Intent(getActivity(), RecoveryPasswdActivity.class);
					// 用Bundle携带数据
					Bundle bundle = new Bundle();
					// 传递name参数为tinyphp
					bundle.putString("phone", phone);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		registerPage.show(getActivity());
	}	
	
	/*
	 * 绑定银行卡
	 * */
	public void selectBankcar(){
		
		startActivity(new Intent(getActivity(), BankCarActivity.class));
		
	}
	
	/*
	 * 提现
	 * */
	public void gotoMyDetailMoney(){
		
		startActivity(new Intent(getActivity(), UserAccountMoney.class));
	}
	

}
