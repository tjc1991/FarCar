package com.cldxk.farcar;

import java.util.ArrayList;
import java.util.List;

import com.cldxk.app.base.OrderMsgInrerface;
import com.cldxk.app.farcar.adapter.PagerAdapter;
import com.cldxk.app.farcar.fragment.HomeFragment;
import com.cldxk.app.farcar.fragment.MessageFragment;
import com.cldxk.app.farcar.fragment.UserCenterFragment;
import com.cldxk.app.farcar.service.ReceiveMsgService;
import com.cldxk.app.farcar.service.Task;
import com.cldxk.app.model.YSOrderModel;
import com.cldxk.app.utils.ActivityStackUtil;
import com.cldxk.app.utils.Utils;
import com.cldxk.farcar.ui.CitySettingActivity;
import com.cldxk.farcar.ui.OrderMsgShowActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.cldxk.app.farcar.broadcast.NetReceiver;
import com.cldxk.app.farcar.broadcast.NetReceiver.NetState;

public class MainActivity extends FragmentActivity implements
OnPageChangeListener, OnCheckedChangeListener, OrderMsgInrerface, NetReceiver.NetEventHandle, OnClickListener{
	
	private PagerAdapter pageadapter = null;
	private TextView titleText;
	private RadioButton navigationBtn[] = new RadioButton[2];
	// 页卡内容
	private ViewPager viewPager;
	// Tab页面列表
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	// 当前页面
	//private HomeFragment homefragment = null;
	private MessageFragment messagefragment = null;
	private UserCenterFragment userfragment = null;
	
	private int index;	
	public static PagerAdapter mpAdapter;
	
	//网络状态标示
	private RelativeLayout net_rv = null;
	
	private Button city_choice_btn = null;
	
	private Button refresh_btn = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		ActivityStackUtil.add(this);
		
		//添加广播
		NetReceiver.ehList.add(this);
						
		//homefragment = new HomeFragment();
		messagefragment = new MessageFragment();
		userfragment = new UserCenterFragment();
		
		titleText = (TextView) findViewById(R.id.main_title_text);
		net_rv = (RelativeLayout) this.findViewById(R.id.netstat_rv);
		city_choice_btn = (Button) this.findViewById(R.id.city_choice_btn);
		refresh_btn = (Button) this.findViewById(R.id.refresh_btn);
		
		InitViewPager();
		
		navigationBtn[0] = (RadioButton) findViewById(R.id.type_tab_cart);
		navigationBtn[1] = (RadioButton) findViewById(R.id.type_tab_user);
		navigationBtn[0].setChecked(true);// 初始化第一个按钮为选中
		titleText.setText("实时消息");
		for (int i = 0; i < navigationBtn.length; i++) {
			navigationBtn[i].setOnCheckedChangeListener(this);
		}
		
		//创建service更新数据
		Intent it =new Intent(this, ReceiveMsgService.class);
		startService(it);
		
		ReceiveMsgService.newTask(new Task(Task.GET_NEW_ORDER, null));
		 
		//城市选择事件
		city_choice_btn.setOnClickListener(this);
        refresh_btn.setOnClickListener(this);
        
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		viewPager.setOffscreenPageLimit(2);
		fragmentList.add(messagefragment);
		fragmentList.add(userfragment);
		mpAdapter = new PagerAdapter(getSupportFragmentManager(), fragmentList);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(mpAdapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(this);
	}

	public PagerAdapter getAdapter() {
		return pageadapter;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		navigationBtn[arg0].setChecked(true);
	}

	/**
	 * 横竖屏切换
	 */

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		viewPager.setCurrentItem(index);
		super.onConfigurationChanged(newConfig);
	}

	/*
	 * 
	 * 页面切换监听器
	 */

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		HideKeyboard();
		int currentFragment = 0;
		if (!isChecked)
			return;
		switch (buttonView.getId()) {
		case R.id.type_tab_cart:
			currentFragment = 0;
			titleText.setText("实时消息");
			refresh_btn.setVisibility(View.VISIBLE);
			break;
		case R.id.type_tab_user:
			currentFragment = 1;
			titleText.setText("个人中心");
			refresh_btn.setVisibility(View.GONE);
			break;
		}
		viewPager.setCurrentItem(currentFragment);
	}

	// 切换到商品列表页面
	public void gotoshop() {
		viewPager.setCurrentItem(0);
		navigationBtn[0].isChecked();

	}

	// 切换到购物车页面
	public void gotocart() {
		viewPager.setCurrentItem(1);
		navigationBtn[1].isChecked();

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
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//判断网络连接状态
		if(Utils.isNetworkConnected(this) == true){
			net_rv.setVisibility(View.GONE);
		}else{
			net_rv.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	
		// 结束 Service		
		Intent service = new Intent("com.cldxk.app.farcar.service.ReceiveMsgService");
		getApplicationContext().stopService(service);
		
		//移除广播
		NetReceiver.ehList.remove(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refreshUi(Object... params) {
		// TODO Auto-generated method stub	
		
		ArrayList<YSOrderModel> neworders = (ArrayList<YSOrderModel>)params[0];
		YSOrderModel mmodel = neworders.get(0);
		//更新订单列表
		if(null != messagefragment){
			messagefragment.refreshFragment(neworders);
		}
		
		//语音播报最新的一个订单
		Intent it = new Intent(MainActivity.this,OrderMsgShowActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("mysorder", mmodel);
		it.putExtras(bundle);
		startActivity(it);
		
	}

	/**
	 * 处理网络连接状态
	 */
	@Override
	public void netState(NetState netCode) {
		// TODO Auto-generated method stub   		
        switch (netCode){
        case NET_NO:
        		net_rv.setVisibility(View.VISIBLE);
            break;
        case NET_2G:
        case	 NET_3G:
        case	 NET_4G:
        case	 NET_WIFI:
        case	 NET_UNKNOWN:
        			net_rv.setVisibility(View.GONE);
            break;

        default:
        		break;
        }
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
			case R.id.city_choice_btn:
				startActivity(new Intent(MainActivity.this,CitySettingActivity.class));
				break;
				
			case R.id.refresh_btn:
				//更新订单列表
				if(null != messagefragment){
					messagefragment.getNewmsgData();;
				}
				break;
								
				default:
					break;
			
		}
	}

}
