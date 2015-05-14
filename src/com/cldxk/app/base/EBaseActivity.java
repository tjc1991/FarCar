package com.cldxk.app.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cldxk.app.EApplication;
import com.cldxk.app.utils.ActivityStackUtil;
import com.cldxk.app.utils.SharePreferenceUtil;
import com.lidroid.xutils.HttpUtils;

public  abstract class EBaseActivity extends Activity{
	
	protected HttpUtils httpClient;
	protected SharePreferenceUtil msharePreferenceUtil = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);  //无title  
		setContentView(setLayout());
		ActivityStackUtil.add(this);
		
		//获得httpClient对象
		httpClient = EApplication.getHttpClient();
		
		///获得sharePreference对象
		msharePreferenceUtil = EApplication.getMsharePreferenceUtil();
		
		((EApplication)getApplication()).putActivity(getClass().getCanonicalName(), this);
		
	}
	
	
	protected void showToastMsg(int  msg)
	{
		showToastMsg(getString(msg));
	}
	
	protected void showToastMsg(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityStackUtil.remove(this);
	}
	
	
	///////////////////////////////////////////////
	
	
  public abstract	int setLayout();
	
	
	protected Dialog onCreateDialog(int id) {
		
		return ActivityHelper.createLoadingDialog(this);
	}

//////////////////////////View ////////////////////////////////

	protected Button findButtonById(int id)
	{
	return (Button) this.findViewById(id);
	}

	protected ListView findListViewById(int id)
	{
	return (ListView) this.findViewById(id);
	}

	protected ImageView findImageViewById(int id)
	{
	return (ImageView) this.findViewById(id);
	}

	protected SurfaceView findSurfaceViewById(int id)
	{
	return (SurfaceView) this.findViewById(id);
	}


	protected ProgressBar findProgressBarById(int id)
	{
	return (ProgressBar) this.findViewById(id);
	}

	protected TextView findTextViewById(int id)
	{
	return (TextView) this.findViewById(id);
	}

	protected EditText findEditTextById(int id)
	{
	return (EditText) this.findViewById(id);
	}


	protected LinearLayout findLinearLayoutById(int id)
	{
	return (LinearLayout) this.findViewById(id);
	}

	
	protected RelativeLayout findRelativeLayoutById(int id)
	{
	return (RelativeLayout) this.findViewById(id);
	}
	
	
	
}

