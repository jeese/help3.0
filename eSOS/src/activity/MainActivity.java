package activity;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ErrorCode;

import java.util.ArrayList;

import model.MyHttpClient;
import myinterface.Watcher;

import org.json.JSONException;
import org.json.JSONObject;

import service.CoreService;
import service.CoreService.MyBinder;
import view.CircleImageView;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.ehelp.esos.R;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import adapter.DrawerListAdapter_1;
import adapter.DrawerListAdapter_2;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import application.App;

public class MainActivity extends ActionBarActivity implements
		AMapLocationListener, OnClickListener, Watcher, OnMapLoadedListener,
		OnCameraChangeListener {

	private DrawerLayout mDrawerLayout;
	private ListView list_1;
	private ListView list_2;
	private String[] str;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private LatLng mLocation;
	private LatLng left;
	private LatLng right;
	private LocationManagerProxy mLocationManagerProxy;
	private ImageButton meButton;
	private Button inboxButton;
	private ImageButton messageButton;
	private Button sButton;
	private Button hButton;
	private Button aButton;
	private BitmapUtils bitmapUtils;
	private CircleImageView head;
	private View myMarkerIcon;
	private TextView mTextDialog;

	private Handler mHander = new Handler();
	private Runnable mRunnable_1;
	private Runnable mRunnable_2;
	private Runnable mRunnable_3;// 连接融云服务器

	private Boolean getNearUser_login = false;
	private Boolean getNearUser_map = false;
	private Boolean getNearUser_location = false;
	private App myApp;

	private int getInboxCount = 0;
	private Boolean isConnectRongIM = false;

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
		setContentView(R.layout.activity_main);

		// 开启后台服务
		Intent intent = new Intent(this, CoreService.class);
		startService(intent);

		// 绑定后台服务
		Intent bindIntent = new Intent(MainActivity.this, CoreService.class);
		bindService(bindIntent, connection, BIND_AUTO_CREATE);

		// 添加观察者
		myApp = ((App) getApplicationContext());
		myApp.addWatcher(this);

		// 连接融云服务器
		connectRongIM();

		// 设置通知栏样式
		setStyleCustom();

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);
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

		head = (CircleImageView) findViewById(R.id.head);
		bitmapUtils.display(head,
				"http://www.qqzhi.com/uploadpic/2014-11-12/190218795.jpg");

		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);

		list_1 = (ListView) findViewById(R.id.listview_1);
		list_1.setAdapter(new DrawerListAdapter_1(this));
		list_1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:

					break;
				case 1:

					break;
				case 2:

					break;
				case 3:

					break;
				case 4:

					break;
				}
				mDrawerLayout.closeDrawers();

			}
		});

		list_2 = (ListView) findViewById(R.id.listview_2);
		list_2.setAdapter(new DrawerListAdapter_2(this));
		list_2.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:

					break;
				case 1:

					break;

				}
				mDrawerLayout.closeDrawers();
				
			}
		});

		meButton = (ImageButton) findViewById(R.id.Me_Button);
		inboxButton = (Button) findViewById(R.id.Inbox_Button);
		messageButton = (ImageButton) findViewById(R.id.Message_Button);
		sButton = (Button) findViewById(R.id.S_Button);
		hButton = (Button) findViewById(R.id.H_Button);
		aButton = (Button) findViewById(R.id.A_Button);
		meButton.setOnClickListener(this);
		inboxButton.setOnClickListener(this);
		messageButton.setOnClickListener(this);
		sButton.setOnClickListener(this);
		hButton.setOnClickListener(this);
		aButton.setOnClickListener(this);

		if (getInboxCount == 0) {

		} else {
			inboxButton.setBackgroundResource(R.drawable.inbox_bg_red);
			inboxButton.setText(getInboxCount + "条推送消息");
			inboxButton.setTextColor(getResources().getColor(R.color.white));
			inboxButton.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_bell_white_40dp, 0, 0, 0);
		}

		mTextDialog = (TextView) findViewById(R.id.TextDialog);

		mRunnable_1 = new Runnable() {
			@Override
			public void run() {
				inboxButton.setEnabled(false);
				dialogShow();
			}
		};
		mRunnable_2 = new Runnable() {
			@Override
			public void run() {
				inboxButton.setEnabled(true);
				dialogHide();
			}
		};

	}

	private void connectRongIM() {

		mHander.removeCallbacks(mRunnable_3);

		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);

		if ((preferences.getBoolean("gettoken", false))) {
			String token = preferences.getString("im_token", null);
			RongIM.connect(token, new ConnectCallback() {

				@Override
				public void onSuccess(String arg0) {
					isConnectRongIM = true;
					System.out.println("连接融云服务器成功");
				}

				@Override
				public void onError(ErrorCode arg0) {
					System.out.println("连接融云服务器失败");
					mRunnable_3 = new Runnable() {
						@Override
						public void run() {
							connectRongIM();
						}
					};
					mHander.postDelayed(mRunnable_3, 10000);
				}
			});
		}

	}

	/**
	 * amap设置
	 */
	private void setUpMap() {

		mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);

		aMap.moveCamera(CameraUpdateFactory.zoomTo((float) 16));

		aMap.setOnMapLoadedListener(this);
		aMap.setOnCameraChangeListener(this);

		mUiSettings.setZoomControlsEnabled(false);
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
		unbindService(connection);
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
			mLocation = new LatLng(geoLat, geoLng);

			aMap.moveCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(mLocation,
							(float) 16, 0, 0)));

			System.out.println("start----setMyMarker()------");
			// 添加我的地图标签
			setMyMarker();
			System.out.println("end----setMyMarker()------");

			getNearUser_location = true;
			System.out.println("getNearUser_location = true;");

			if (isgetNearUserOK()) {
				System.out.println("start----getNearUser()------");
				// 获取附近用户
				getNearUser();
				System.out.println("end----getNearUser()------");
			}

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

		// 添加到地图上
		aMap.addMarker(markerOptions);
	}

	private void setMarker(String x, String y) {

		LatLng xy = new LatLng(Double.valueOf(x), Double.valueOf(y));

		View MarkerIcon = LayoutInflater.from(this).inflate(
				R.layout.marker_icon, null);

		// 初始化marker内容
		MarkerOptions markerOptions = new MarkerOptions();
		BitmapDescriptor markerIcon = BitmapDescriptorFactory
				.fromView(MarkerIcon);
		markerOptions.position(xy).icon(markerIcon).title("maker");

		// 添加到地图上
		aMap.addMarker(markerOptions);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.Me_Button:
			mDrawerLayout.openDrawer(Gravity.LEFT);
			break;

		case R.id.Inbox_Button:
			if (getInboxCount == 0) {
				mTextDialog.setText("暂无推送消息");
				mHander.post(mRunnable_1);
				mHander.postDelayed(mRunnable_2, 1000);
			} else if (getInboxCount == 1) {
				Intent intentInbox = new Intent(MainActivity.this,
						RSOSActivity.class);
				startActivity(intentInbox);
			} else {
				Intent intentInbox = new Intent(MainActivity.this,
						ListActivity.class);
				startActivity(intentInbox);
			}
			break;

		case R.id.Message_Button:
			if (isConnectRongIM) {
				System.out.println("打开私信列表成功");
				RongIM.getInstance().startConversationList(MainActivity.this);
			} else {
				System.out.println("打开私信列表失败");
				mTextDialog.setText("私信服务无法连接");
				mHander.post(mRunnable_1);
				mHander.postDelayed(mRunnable_2, 1000);
			}
			break;

		case R.id.S_Button:
			Intent intentS = new Intent(MainActivity.this,
					CountDownActivity.class);
			startActivity(intentS);
			break;

		case R.id.H_Button:
			// Intent intentH = new Intent(MainActivity.this,
			// CountDownActivity.class);
			// startActivity(intentH);
			// RongIM.getInstance().startConversation(MainActivity.this,
			// Conversation.ConversationType.PRIVATE, "103", "jeese");
			break;

		case R.id.A_Button:
			// Intent intentA = new Intent(MainActivity.this,
			// CountDownActivity.class);
			// startActivity(intentA);
			break;

		}

	}

	private Boolean isgetNearUserOK() {
		Boolean isOK = false;

		if (getNearUser_login && getNearUser_map && getNearUser_location) {
			isOK = true;
		}

		return isOK;

	}

	private void getNearUser() {

		left = aMap.getProjection().fromScreenLocation(new Point(0, 0));
		right = aMap.getProjection().fromScreenLocation(
				new Point(mapView.getWidth(), mapView.getHeight()));

		String x1 = "" + left.latitude;
		String y1 = "" + left.longitude;
		String x2 = "" + right.latitude;
		String y2 = "" + right.longitude;

		RequestParams params = new RequestParams();
		params.addBodyParameter("x1", x2);
		params.addBodyParameter("x2", x1);
		params.addBodyParameter("y1", y1);
		params.addBodyParameter("y2", y2);

		System.out.println(x1);
		System.out.println(y1);
		System.out.println(x2);
		System.out.println(y2);

		HttpUtils http = MyHttpClient.getInstance().http;
		http.send(HttpRequest.HttpMethod.POST,
				"http://1.eesos.sinaapp.com/getsurround.php", params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println("连接服务器失败");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							JSONObject replyObject = new JSONObject(arg0.result);
							String state = replyObject.getString("State");
							System.out.println(state);

							aMap.clear();

							if (state.equals("success")) {
								if (replyObject.length() > 1) {
									for (int i = 0; i < replyObject.length() - 1; i++) {
										setMarker(((JSONObject) replyObject
												.get("" + i)).get("x")
												.toString(),
												((JSONObject) replyObject
														.get("" + i)).get("y")
														.toString());
										System.out
												.println(((JSONObject) replyObject
														.get("" + i))
														.get("Userid"));
										System.out
												.println(((JSONObject) replyObject
														.get("" + i)).get("x"));
										System.out
												.println(((JSONObject) replyObject
														.get("" + i)).get("y"));
									}
								}
							} else {
								System.out.println("附近没有用户");
							}

							setMyMarker();

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});
		System.out.println("----getNearUser()---444444---");
	}

	@Override
	public void update(String str) {
		if (str.contentEquals("登录成功")) {

			getNearUser_login = true;
			System.out.println("getNearUser_login = true;");

			if (isgetNearUserOK()) {
				System.out.println("start----getNearUser()------");
				// 获取附近用户
				getNearUser();
				System.out.println("end----getNearUser()------");
			}

		} else if (str.contentEquals("获取Token成功")) {
			connectRongIM();
		}

	}

	@Override
	public void onMapLoaded() {

		getNearUser_map = true;
		System.out.println("getNearUser_map = true;");

		if (isgetNearUserOK()) {
			System.out.println("start----getNearUser()------");
			// 获取附近用户
			getNearUser();
			System.out.println("end----getNearUser()------");
		}

	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {

		if (isgetNearUserOK()) {
			System.out.println("start----getNearUser()------");
			// 获取附近用户
			getNearUser();
			System.out.println("end----getNearUser()------");
		}

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

		mHander.removeCallbacks(mRunnable_1);
		mHander.removeCallbacks(mRunnable_2);
	}

	/**
	 * 设置通知栏样式 - 定义通知栏Layout
	 */
	private void setStyleCustom() {

		BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(
				MainActivity.this);
		builder.statusBarDrawable = R.drawable.im1;
		builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为自动消失
		builder.notificationDefaults = Notification.DEFAULT_SOUND
				| Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS; // 设置为铃声与震动都要
		JPushInterface.setPushNotificationBuilder(2, builder);

		// // 指定定制的 Notification Layout
		// CustomPushNotificationBuilder builder = new
		// CustomPushNotificationBuilder(
		// StartActivity.this, R.layout.customer_notitfication_layout,
		// R.id.icon, R.id.title, R.id.text);
		//
		// // 指定最顶层状态栏小图标
		// builder.statusBarDrawable = R.drawable.im1;
		//
		// // 指定下拉状态栏时显示的通知图标
		// builder.layoutIconDrawable = R.drawable.im2;
		//
		// JPushInterface.setPushNotificationBuilder(2, builder);
	}

}
