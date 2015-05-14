package com.cldxk.app.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class ActivityHelper {

	
	
	
	public static Dialog createLoadingDialog(Context context)
	{
		
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setMessage("正在读写数据");
		
		return dialog;
		
	}
	
	
}
