package com.cldxk.farcar.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cldxk.app.EApplication;
import com.cldxk.app.base.EBaseActivity;
import com.cldxk.app.farcar.db.CityMsgEntity;
import com.cldxk.farcar.R;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class CitySettingActivity extends EBaseActivity implements OnClickListener{
	
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	private ListView city_listview = null;
	
	private DbUtils db = null;
	
	//保存城市列表信息
	//private HashMap<Integer, String> citylists = new  HashMap<Integer, String>();
	
	private String JiLincity[] = {			
			"长春",
			"双阳",
			"农安",
			"九台",
			"德惠",
			"榆树市",
			"吉林市",
			"永吉",
			"桦甸",
			"蛟河",
			"舒兰",
			"延吉市",
			"汪清",
			"和龙",
			"安图",
			"敦化",
			"图们",
			"龙井市",
			"四平",
			"双辽",
			"公主岭",
			"通化",
			"集安",
			"白城",
			"通榆",
			"大安",
			"洮南",
			"镇赉",
			"辽源",
			"东辽",
			"东丰",
			"松原",
			"扶余",
			"乾安",
			"临江市",
			"靖宇",
			"长白山",
			"抚松",
			"白山",
			"珲春",
			"梅河口",
			"柳河",
			"辉南",
			"龙嘉机场",
			"伊通满族自治县",
			"龙井县"
			};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//得到数据库单例,考虑可以优化
//		db = DbUtils.create(this);
//        db.configAllowTransaction(true);
//        db.configDebug(true);
		db = EApplication.getMdb();
		
		InitMyView();
		
	}
	
	public void InitMyView(){
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("拼车目的城市");
		back_btn.setImageResource(R.drawable.city_back_btn);
		
		city_listview = this.findListViewById(R.id.citysetting_lv);
		
		//加载数据
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,  
	            android.R.layout.simple_list_item_multiple_choice, JiLincity); 
		
		city_listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
		
		city_listview.setAdapter(adapter);
		
		//设置选中状态
		//从数据库中取出数据	
		try {
			List<CityMsgEntity> citys = db.findAll(CityMsgEntity.class);
			if(null != citys){
			if(citys.size()>0)
			{
				for (CityMsgEntity cityMsgEntity : citys) {
					Log.i("tjc", "key-->"+cityMsgEntity.getCity_choice_index()+""+"value-->"+cityMsgEntity.getCity_choice_name());	
					city_listview.setItemChecked(cityMsgEntity.getCity_choice_index(), true);
				} 
				
			}}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_citysetting;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()) {
		case R.id.fragment_actionbar_back:
			
			//保存数据到数据库
			SaveCityDataToSqlite();
	         			
			//结束Activity
			finish();
			break;	
		default:
			break;
	}
		
	}
	
	/**
	 * 保存数据到数据库
	 */
	public void SaveCityDataToSqlite(){
		
		ArrayList<CityMsgEntity>citymsg_entitys = new ArrayList<CityMsgEntity>();
				
		//获取数据
		long[] select_index = city_listview.getCheckItemIds();
		for (int i = 0; i < select_index.length; i++) {
			CityMsgEntity citymsg_entity = new CityMsgEntity();						
			int index = Integer.valueOf((int)select_index[i]);
			String cityname = (String)city_listview.getItemAtPosition((int) select_index[i]);
			citymsg_entity.setCity_choice_index(index);
			citymsg_entity.setCity_choice_name(cityname);
			citymsg_entitys.add(citymsg_entity);
			//citylists.put(index, cityname);
			//Log.i("tjc", "key-->"+index+""+"value-->"+cityname);	
		}
			
//		if(select_index.length <=0 )
//		{
//			return;
//		}
		
		//保存数据到数据库
		try {			
			//有待优化,移除原先数据库中内容，保存新内容到数据库
			db.deleteAll(CityMsgEntity.class);
			
			db.saveAll(citymsg_entitys);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}

}
