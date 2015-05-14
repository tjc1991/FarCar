package com.cldxk.farcar.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.cldxk.farcar.R;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.DateUtils;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalenderActivity extends Activity {

	private Button btn;
	private TextView text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calender);
		btn = (Button) findViewById(R.id.button1);
		text = (TextView) findViewById(R.id.textView1);

		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);
		final Calendar month = Calendar.getInstance();
		month.add(Calendar.MONTH, 1);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final CalendarPickerView dialogView = (CalendarPickerView) getLayoutInflater()
						.inflate(R.layout.dialog, null, false);
				if (text.getText().toString().equals("")) {
					dialogView.init(new Date(), month.getTime())
							.withSelectedDate(new Date());
				} else {
					Date date = DateUtils.getDate(text.getText().toString(),
							"yyyy-MM-dd");
					dialogView.init(new Date(), month.getTime())
							.withSelectedDate(date);
				}

				new AlertDialog.Builder(CalenderActivity.this)
						.setTitle("时间选择控件")
						.setView(dialogView)
						.setNeutralButton("返回",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {
										dialogInterface.dismiss();
									}
								})
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {
										long sd = dialogView.getSelectedDate()
												.getTime();
										Date dat = new Date(sd);
										GregorianCalendar gc = new GregorianCalendar();
										gc.setTime(dat);
										java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
												"yyyy-MM-dd ");
										String sb = format.format(gc.getTime());
										System.out.println(sb);
										text.setText(sb);
										dialogInterface.dismiss();
									}
								}).create().show();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
