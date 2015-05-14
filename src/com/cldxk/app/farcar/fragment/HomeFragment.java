package com.cldxk.app.farcar.fragment;

import com.cldxk.app.base.BaseFragment;
import com.cldxk.app.breakrules.RulesActivity;
import com.cldxk.farcar.R;
import com.cldxk.farcar.ui.AboutActivity;
import com.cldxk.farcar.ui.TtsDemoActivity;
import com.cldxk.farcar.ui.UseHelpActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HomeFragment extends BaseFragment implements OnClickListener{
	
	private View view = null;
	private Button real_btn = null;
	private Button call_btn = null;
	private Button wz_btn = null;
	private Button ab_btn = null;	
	private Button use_btn = null;


	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragmet_home, container, false);
		
		real_btn = (Button) view.findViewById(R.id.real_btn);
		call_btn = (Button) view.findViewById(R.id.call_btn);
		wz_btn = (Button) view.findViewById(R.id.wz_btn);
		ab_btn = (Button) view.findViewById(R.id.about_btn);
		use_btn = (Button) view.findViewById(R.id.use_btn);
		real_btn.setOnClickListener(this);
		call_btn.setOnClickListener(this);
		wz_btn.setOnClickListener(this);
		ab_btn.setOnClickListener(this);
		use_btn.setOnClickListener(this);

		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.real_btn:
		
			break;
		case R.id.call_btn:

			break;
		case R.id.wz_btn:
			startActivity(new Intent(getActivity(), RulesActivity.class));			
			break;
		case R.id.about_btn:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			break;
		case R.id.use_btn:
			startActivity(new Intent(getActivity(), UseHelpActivity.class));			
			break;
		default:
			break;
		}
		
	}
	

}
