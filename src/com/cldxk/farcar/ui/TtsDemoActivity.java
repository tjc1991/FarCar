package com.cldxk.farcar.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.cldxk.app.base.EBaseActivity;
import com.cldxk.farcar.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.iflytek.speech.setting.TtsSettings;

public class TtsDemoActivity extends EBaseActivity implements OnClickListener {
	private static String TAG = "TtsDemo"; 	
	// 语音合成对象
	private SpeechSynthesizer mTts;

	// 默认云端发音人
	public static String voicerCloud="xiaoyan";
			
	//缓冲进度
	private int mPercentForBuffering = 0;	
	//播放进度
	private int mPercentForPlaying = 0;
	
	private RelativeLayout actionBarlv = null;
	private ImageView back_btn = null;
	private TextView title_tx = null;
	
	private RelativeLayout from_golv = null;
	private TextView from_tx = null;
	private TextView go_tx = null;
	private String mesg = null;
	
	private Button cancel_btn = null;
	
	// 引擎类型
	private String mEngineType = SpeechConstant.TYPE_CLOUD;
	
	private Toast mToast;
	
	private Handler myhandler = null;
	
	@SuppressLint("ShowToast")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayout();

		// 初始化合成对象
		mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
		mEngineType = SpeechConstant.TYPE_CLOUD;
		
		//mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
		
		String from = getIntent().getExtras().getString("cityfrom");
		String go = getIntent().getExtras().getString("citygo");
		
		mesg = "快来抢单,从"+from+""
				+"出发去往"+go+""+"方向";
		
		from_tx.setText(from);
		go_tx.setText(go);
		
//		cancel_btn = (Button) findViewById(R.id.tts_cancel);
//		cancel_btn.setOnClickListener(this);
		
		//Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
				
		// 设置参数
		setParam();
		
		int code = mTts.startSpeaking(mesg, mTtsListener);
		if (code != ErrorCode.SUCCESS) {
			//showTip("语音合成失败,错误码: " + code);	
			//Log.i("tjc", "----error"+code+"");
		}
				
	}

	/**
	 * 初始化Layout。
	 */
	private void initLayout() {
		findViewById(R.id.tts_play).setOnClickListener(this);
		
		findViewById(R.id.tts_cancel).setOnClickListener(this);
		findViewById(R.id.tts_pause).setOnClickListener(this);
		findViewById(R.id.tts_resume).setOnClickListener(this);
		
		actionBarlv = this.findRelativeLayoutById(R.id.action_bar);
		back_btn = (ImageView) actionBarlv.findViewById(R.id.fragment_actionbar_back);
		title_tx = (TextView) actionBarlv.findViewById(R.id.actionbar_title);
		from_golv = this.findRelativeLayoutById(R.id.from_go);
		from_tx = (TextView) from_golv.findViewById(R.id.tx_from);
		go_tx = (TextView) from_golv.findViewById(R.id.tx_go);
		back_btn.setOnClickListener(this);
		title_tx.setText("实时订单");
		
	}	

	@Override
	public void onClick(View view) {
		switch(view.getId()) {

		// 取消合成
		case R.id.tts_cancel:
			break;
		// 暂停播放
		case R.id.tts_pause:
			break;
		// 继续播放
		case R.id.tts_resume:
			break;
		case R.id.fragment_actionbar_back:
			finish();
			break;	
		default:
			break;
		}
	}

	/**
	 * 初始化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "InitListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
        		showTip("初始化失败,错误码："+code);
        	}		
		}
	};

	/**
	 * 合成回调监听。
	 */
	private SynthesizerListener mTtsListener = new SynthesizerListener() {
		@Override
		public void onSpeakBegin() {
			//showTip("开始播放");
		}

		@Override
		public void onSpeakPaused() {
			//showTip("暂停播放");
		}

		@Override
		public void onSpeakResumed() {
			//showTip("继续播放");
		}

		@Override
		public void onBufferProgress(int percent, int beginPos, int endPos,
				String info) {
//			mPercentForBuffering = percent;
//			mToast.setText(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
//			
//			mToast.show();
		}

		@Override
		public void onSpeakProgress(int percent, int beginPos, int endPos) {
//			mPercentForPlaying = percent;
//			showTip(String.format(getString(R.string.tts_toast_format),
//					mPercentForBuffering, mPercentForPlaying));
		}

		@Override
		public void onCompleted(SpeechError error) {
			if(error == null)
			{
				//showTip("播放完成");
			}
			else if(error != null)
			{
				//showTip(error.getPlainDescription(true));
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			// TODO Auto-generated method stub
			
		}
	};

	private void showTip(final String str){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}

	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	private void setParam(){
		// 清空参数
		mTts.setParameter(SpeechConstant.PARAMS, null);
		//设置合成
		//设置使用云端引擎
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
			//设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);
		
		//设置语速
		mTts.setParameter(SpeechConstant.SPEED,"50");

		//设置音调
		mTts.setParameter(SpeechConstant.PITCH,"50");

		//设置音量
		mTts.setParameter(SpeechConstant.VOLUME,"50");
		
		//设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE,"3");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTts.stopSpeaking();
		// 退出时释放连接
		mTts.destroy();
	}

	@Override
	public int setLayout() {
		// TODO Auto-generated method stub
		return R.layout.ttsdemo;
	}
}
