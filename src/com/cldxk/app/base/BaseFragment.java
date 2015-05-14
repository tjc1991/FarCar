package com.cldxk.app.base;

import com.cldxk.app.EApplication;
import com.cldxk.app.utils.SharePreferenceUtil;
import com.lidroid.xutils.HttpUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment{
	
	protected HttpUtils httpClient;
	protected SharePreferenceUtil msharePreferenceUtil = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		//获得httpClient对象
		httpClient = EApplication.getHttpClient();
		
		///获得sharePreference对象
		msharePreferenceUtil = EApplication.getMsharePreferenceUtil();
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

}
