package activity;

import java.util.ArrayList;

import io.rong.imkit.RongIM;
import model.MyHttpClient;
import myinterface.Watcher;

import org.json.JSONException;
import org.json.JSONObject;

import service.CoreService;
import service.CoreService.MyBinder;
import view.CircleImageView;
import view.materialedittext.MaterialEditText;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.ehelp.esos.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import application.App;

public class SSOSActivity extends ActionBarActivity implements OnClickListener,
		AMapLocationListener, Watcher {

	private Toolbar mToolbar;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private LocationManagerProxy mLocationManagerProxy;
	private LatLng mLocation;
	private Button exitButton;
	private Button messageButton;
	private Button sendButton;
	private App myApp;
	private View myMarkerIcon;
	private CircleImageView mhead;
	private TextView mnickname;
	private TextView mtime;
	private BitmapUtils bitmapUtils;
	private MaterialEditText soscontent_edit;
	private String mContent;
	private String mLat;
	private String mLng;
	private String mAddress;// 详细地址

	private TextView mTextDialog;
	private Handler mHandler = new Handler();
	private Runnable mRunnable_1;
	private Runnable mRunnable_2;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ssos);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);

		myApp = ((App) getApplicationContext());
		myApp.addWatcher(this);

		// 开启后台服务
		Intent intent = new Intent(this, CoreService.class);
		startService(intent);

		// 绑定后台服务
		Intent bindIntent = new Intent(SSOSActivity.this, CoreService.class);
		bindService(bindIntent, connection, BIND_AUTO_CREATE);

		bitmapUtils = new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.contact_48dp);

		init();

	}

	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			mUiSettings = aMap.getUiSettings();
			setUpMap();
		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		// toolbar.setLogo(R.drawable.ic_launcher);
		mToolbar.setTitle("紧急求救状态");// 标题的文字需在setSupportActionBar之前，不然会无效
		// toolbar.setSubtitle("副标题");
		setSupportActionBar(mToolbar);

		exitButton = (Button) findViewById(R.id.ExitButton);
		messageButton = (Button) findViewById(R.id.MessageButton);
		sendButton = (Button) findViewById(R.id.SendButton);
		exitButton.setOnClickListener(this);
		messageButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);

		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);

		soscontent_edit = (MaterialEditText) findViewById(R.id.soscontent_edit);
		mhead = (CircleImageView) findViewById(R.id.head);
		mnickname = (TextView) findViewById(R.id.nickname);
		mtime = (TextView) findViewById(R.id.time);
		soscontent_edit.setText(preferences.getString("mContent", ""));
		bitmapUtils.display(mhead,
				"http://www.qqzhi.com/uploadpic/2014-11-12/190218795.jpg");
		mnickname.setText(preferences.getString("nickname", ""));
		mtime.setText(preferences.getString("sos_time", ""));

		mTextDialog = (TextView) findViewById(R.id.TextDialog);

		mRunnable_1 = new Runnable() {
			@Override
			public void run() {
				messageButton.setEnabled(false);
				dialogShow();
			}
		};
		mRunnable_2 = new Runnable() {
			@Override
			public void run() {
				messageButton.setEnabled(true);
				dialogHide();
			}
		};

	}

	/**
	 * amap设置
	 */
	private void setUpMap() {

		mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);

		aMap.moveCamera(CameraUpdateFactory.zoomTo((float) 16));

		mUiSettings.setZoomControlsEnabled(true);

		// 设置指南针是否显示
		mUiSettings.setCompassEnabled(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

		mLocationManagerProxy.setGpsEnable(false);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		myApp.removeWatcher(this);
		unbindService(connection);
	}

	// Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		// 退出
		if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.ExitButton:
			ExitSOS();
			Intent intent = new Intent(SSOSActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.MessageButton:
			if (mBinder.getIsConnectRongIM()) {
				System.out.println("打开私信列表成功");
				RongIM.getInstance().startConversationList(SSOSActivity.this);
			} else {
				System.out.println("打开私信列表失败");
				mTextDialog.setText("私信服务无法连接");
				mHandler.post(mRunnable_1);
				mHandler.postDelayed(mRunnable_2, 1000);
			}
			break;

		case R.id.SendButton:
			mContent = soscontent_edit.getText().toString();
			if (mContent.equals("")) {
				mTextDialog.setText("输入内容不能为空");
				mHandler.post(mRunnable_1);
				mHandler.postDelayed(mRunnable_2, 1000);
			} else {
				SharedPreferences preferences = getSharedPreferences("eSOS",
						Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("mContent", mContent);
				editor.putString("mLat", mLat);
				editor.putString("mLng", mLng);
				editor.putString("mAddress", mAddress);
				editor.commit();
				mBinder.SendSOS();
			}
			break;

		}
	}

	private void ExitSOS() {
		System.out.println("exitsos_start");
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("sos_status", 1);
		editor.putString("mContent", null);
		editor.commit();
		HttpUtils http = MyHttpClient.getInstance().http;
		http.send(HttpRequest.HttpMethod.POST,
				"http://1.eesos.sinaapp.com/exitsos.php",
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println("连接服务器失败");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						SharedPreferences preferences = getSharedPreferences(
								"eSOS", Context.MODE_PRIVATE);
						Editor editor = preferences.edit();
						editor.putInt("sos_status", 0);
						editor.commit();
						try {
							JSONObject replyObject = new JSONObject(arg0.result);
							String state = replyObject.getString("State");
							System.out.println(state);
							if (state.equals("success")) {
								System.out.println("success_exitsos");
							} else {
								System.out.println("no_sos");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});
		System.out.println("exitsos_finish");
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (amapLocation != null
				&& amapLocation.getAMapException().getErrorCode() == 0) {

			Double geoLat = amapLocation.getLatitude();
			Double geoLng = amapLocation.getLongitude();

			String posx = "" + geoLat;
			String posy = "" + geoLng;

			mLat = posx;
			mLng = posy;
			mAddress = "位置：" + amapLocation.getStreet();

			SharedPreferences preferences = getSharedPreferences("eSOS",
					Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("mLat", mLat);
			editor.putString("mLng", mLng);
			editor.putString("mAddress", mAddress);
			editor.commit();

			mLocation = new LatLng(geoLat, geoLng);

			aMap.moveCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(mLocation,
							(float) 16, 0, 0)));

			System.out.println("start----setMyMarker()------");
			// 添加我的地图标签
			setMyMarker();
			System.out.println("end----setMyMarker()------");

		}

	}

	private void setMyMarker() {

		myMarkerIcon = LayoutInflater.from(this).inflate(
				R.layout.mymarker_icon, null);

		// 初始化marker内容
		MarkerOptions markerOptions = new MarkerOptions();
		BitmapDescriptor markerIcon = BitmapDescriptorFactory
				.fromView(myMarkerIcon);
		markerOptions.position(mLocation).icon(markerIcon).title("mymaker");

		aMap.clear();
		// 添加到地图上
		aMap.addMarker(markerOptions);
	}

	/**
	 * dialog显示动画
	 */
	private void dialogShow() {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(400);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		ArrayList<Animator> animatorList = new ArrayList<Animator>();
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mTextDialog,
				"ScaleX", 0f, 1.2f, 1f);
		animatorList.add(scaleXAnimator);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mTextDialog,
				"ScaleY", 0f, 1.2f, 1f);
		animatorList.add(scaleYAnimator);
		animatorSet.playTogether(animatorList);
		mTextDialog.setVisibility(View.VISIBLE);
		animatorSet.start();
	}

	/**
	 * dialog消失动画
	 */
	private void dialogHide() {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(100);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		ArrayList<Animator> animatorList = new ArrayList<Animator>();
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(mTextDialog,
				"ScaleX", 1f, 0f);
		animatorList.add(scaleXAnimator);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(mTextDialog,
				"ScaleY", 1f, 0f);
		animatorList.add(scaleYAnimator);
		animatorSet.playTogether(animatorList);
		// mTextDialog.setVisibility(View.INVISIBLE);
		animatorSet.start();

		mHandler.removeCallbacks(mRunnable_1);
		mHandler.removeCallbacks(mRunnable_2);
	}

	@Override
	public void update(String str) {
		if (str.contentEquals("sendSosOK")) {
			mTextDialog.setText("求救状态更新成功");
			mHandler.post(mRunnable_1);
			mHandler.postDelayed(mRunnable_2, 1000);
		}
		
	}

}
