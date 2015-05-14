package com.cldxk.farcar.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.farcar.R;

public class AboutActivity extends EBaseActivity implements OnClickListener{
	
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
		title_tx.setText("关于");
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_about;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		
		switch(view.getId()) {
			case R.id.fragment_actionbar_back:
				finish();
				break;	
			default:
				break;
		}
	}

}
