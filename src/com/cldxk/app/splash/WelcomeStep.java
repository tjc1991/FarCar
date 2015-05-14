package com.cldxk.app.splash;

import com.cldxk.farcar.MainActivity;
import com.cldxk.farcar.R;
import com.cldxk.plug.user.LoginActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 欢迎页
 * 
 * @author Liu Dewei
 * 
 */
public class WelcomeStep extends Fragment {

	private View view;
	private int step;
	private Button initmainui;

	public WelcomeStep(int step) {

		this.step = step;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		switch (step) {
		case 1:
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.welcome_step1, null);
			break;
		case 2:
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.welcome_step2, null);
			break;

		case 3:
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.welcome_step3, null);
			break;

		case 4:
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.welcome_step4, null);
			this.initmainui = (Button) view.findViewById(R.id.initmain);
			initmainui.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent init = new Intent(getActivity(), LoginActivity.class);
					startActivity(init);
				}
			});
			break;

		default:
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.welcome_step1, null);
			break;
		}
		return view;
	}

}
