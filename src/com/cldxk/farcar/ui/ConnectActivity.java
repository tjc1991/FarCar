package com.cldxk.farcar.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.farcar.R;

public class ConnectActivity extends EBaseActivity implements OnClickListener{

	private LinearLayout call_lv = null;
	private TextView call_text = null;
	
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		back_btn.setOnClickListener(this);
		title_tx.setText("联系客户");
		
		call_lv = this.findLinearLayoutById(R.id.wode_call);
		call_lv.setOnClickListener(this);
		call_text = this.findTextViewById(R.id.wode_call_text);
		
		String phone = getIntent().getStringExtra("phone");
		if(phone != null)
		{
			call_text.setText(phone);
		}
	}
	
	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_connect;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.wode_call:
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ call_text.getText()));
			startActivity(intent);// 内部类
			break;
			
		case R.id.fragment_actionbar_back:
			finish();
			break;	
		
		default:
			break;
		}
	}

}
