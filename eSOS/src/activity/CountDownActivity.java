package activity;

import java.util.ArrayList;

import service.CoreService;
import service.CoreService.MyBinder;
import utils.DateUtil;
import utils.VibratorUtil;
import view.RippleBackground;

import com.ehelp.esos.R;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;

public class CountDownActivity extends ActionBarActivity {

	private Button centerNum;
	private int time;
	private int T;
	Handler countDown;
	Runnable myRunnable;

	/* 通过Binder，实现Activity与Service通信 */
	private MyBinder mBinder;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = (MyBinder) service;
			System.out.println("绑定成功");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_countdown);

		// 绑定后台服务
		Intent bindIntent = new Intent(CountDownActivity.this,
				CoreService.class);
		bindService(bindIntent, connection, BIND_AUTO_CREATE);

		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(connection);
		countDown.removeCallbacks(myRunnable);
	}

	@Override
	protected void onResume() {
		super.onResume();
		final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
		rippleBackground.startRippleAnimation();

		time = 4;
		T = 4;

		countDown = new Handler();
		myRunnable = new Runnable() {
			@Override
			public void run() {
				if (time == 0) {
					mBinder.SendSOS();
					SharedPreferences preferences = getSharedPreferences(
							"eSOS", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putInt("sos_status", 2);
					editor.putString("sos_time", DateUtil.getDate());
					editor.commit();
					Intent intent = new Intent(CountDownActivity.this,
							SSOSActivity.class);
					startActivity(intent);

					finish();
				} else if (time == T) {
					T++;
					countDown.postDelayed(this, 500);
				} else {
					time--;
					centerNum.setText("" + time);
					centerNum();
					VibratorUtil.Vibrate(CountDownActivity.this, 400);
					countDown.postDelayed(this, 1000);
				}
			}
		};

		countDown.post(myRunnable);
	}

	private void init() {

		centerNum = (Button) findViewById(R.id.centerNum);

		Button cencel = (Button) findViewById(R.id.cencel_button);
		cencel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CountDownActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}

	private void centerNum() {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(400);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		ArrayList<Animator> animatorList = new ArrayList<Animator>();
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(centerNum,
				"ScaleX", 0f, 1.2f, 1f);
		animatorList.add(scaleXAnimator);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(centerNum,
				"ScaleY", 0f, 1.2f, 1f);
		animatorList.add(scaleYAnimator);
		animatorSet.playTogether(animatorList);
		centerNum.setVisibility(View.VISIBLE);
		animatorSet.start();
	}
}
