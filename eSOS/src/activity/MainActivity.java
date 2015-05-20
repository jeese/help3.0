package activity;

import io.rong.imkit.RongIM;

import java.util.ArrayList;
import java.util.List;

import model.EHelp;
import model.MyHttpClient;
import model.SosInfo;
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
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
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
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
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
	private CircleImageView mhead;
	private TextView mnickname;
	private TextView mphone;
	private View myMarkerIcon;
	private TextView mTextDialog;

	private Handler mHandler = new Handler();
	private Runnable mRunnable_1;
	private Runnable mRunnable_2;
	private Runnable mRunnable_3;

	private Boolean getNearUser_login = false;
	private Boolean getNearUser_map = false;
	private Boolean getNearUser_location = false;
	private App myApp;

	private int InboxCount;
	private int refreshcount = 0;
	List<SosInfo> newlist_sosinfo = new ArrayList<SosInfo>();

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

		if (myApp.STATE == 1)
			getNearUser_login = true;

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

		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);

		mhead = (CircleImageView) findViewById(R.id.head);
		mnickname = (TextView) findViewById(R.id.nickname);
		mphone = (TextView) findViewById(R.id.phone);
		bitmapUtils.display(mhead,
				"http://www.qqzhi.com/uploadpic/2014-11-12/190218795.jpg");
		mnickname.setText(preferences.getString("nickname", ""));
		mphone.setText(preferences.getString("cellPhone", ""));

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

		refreshInboxButton();

		mRunnable_3 = new Runnable() {

			@Override
			public void run() {
				SharedPreferences preferences = getSharedPreferences("eSOS",
						Context.MODE_PRIVATE);
				// 目前只有sos消息，有其他类型的消息后需要相加
				InboxCount = preferences.getInt("SosCount", 0);

				if (InboxCount > 0) {
					DbUtils db = DbUtils.create(MainActivity.this);
					List<SosInfo> list_sosinfo = new ArrayList<SosInfo>();

					try {
						list_sosinfo = db.findAll(Selector.from(SosInfo.class));
						InboxCount = list_sosinfo.size();
						Editor editor = preferences.edit();
						editor.putInt("SosCount", InboxCount);
						editor.commit();
						System.out.println("从数据库读取list.size = "
								+ list_sosinfo.size());
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					EHelp.getInstance().setSosInfoList(list_sosinfo);
					newlist_sosinfo.clear();
					try {
						db.deleteAll(SosInfo.class);
					} catch (DbException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					for (int i = 0; i < list_sosinfo.size(); i++) {
						RequestParams params = new RequestParams();
						params.addBodyParameter("sosid",
								list_sosinfo.get(i).uid);

						HttpUtils http = MyHttpClient.getInstance().http;
						http.send(HttpRequest.HttpMethod.POST,
								"http://1.eesos.sinaapp.com/getsosinfo.php",
								params, new RequestCallBack<String>() {

									@Override
									public void onFailure(HttpException arg0,
											String arg1) {
										System.out.println("连接服务器失败");
										refreshcount();
									}

									@Override
									public void onSuccess(
											ResponseInfo<String> arg0) {

										try {
											JSONObject replyObject = new JSONObject(
													arg0.result);
											String state = replyObject
													.getString("State");
											System.out.println(state);

											if (state.equals("success")) {
												System.out.println("获取求救消息成功");
												SosInfo sosinfo = new SosInfo(
														replyObject
																.getString("id"),
														null,
														null,
														null,
														replyObject
																.getString("text"),
														replyObject
																.getString("x"),
														replyObject
																.getString("y"));
												refreshSosInfo(sosinfo);
												refreshcount();

											} else {
												System.out
														.println("获取求救消息失败或者消息已取消");
												refreshcount();
											}

										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}

									}

								});
					}

				}

			}
		};

		mTextDialog = (TextView) findViewById(R.id.TextDialog);

		mRunnable_1 = new Runnable() {
			@Override
			public void run() {
				inboxButton.setEnabled(false);
				messageButton.setEnabled(false);
				dialogShow();
			}
		};
		mRunnable_2 = new Runnable() {
			@Override
			public void run() {
				inboxButton.setEnabled(true);
				messageButton.setEnabled(true);
				dialogHide();
			}
		};

	}

	private void refreshcount() {
		refreshcount++;
		if (refreshcount == InboxCount) {
			refreshcount = 0;
			refreshDb();
		}
	}

	private void refreshSosInfo(SosInfo sosinfo) {
		newlist_sosinfo.add(sosinfo);
	}

	private void refreshDb() {
		DbUtils db = DbUtils.create(this);
		List<SosInfo> list_sosinfo = EHelp.getInstance().getSosInfoList();
		for (int i = 0; i < newlist_sosinfo.size(); i++) {
			for (int j = 0; j < list_sosinfo.size(); j++) {
				if (newlist_sosinfo.get(i).uid.equals(list_sosinfo.get(j).uid)) {
					newlist_sosinfo.get(i).nickname = list_sosinfo.get(j).nickname;
					newlist_sosinfo.get(i).phonenum = list_sosinfo.get(j).phonenum;
					newlist_sosinfo.get(i).time = list_sosinfo.get(j).time;
					try {
						db.save(newlist_sosinfo.get(i));
					} catch (DbException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}

		EHelp.getInstance().setSosInfoList(newlist_sosinfo);
		System.out.println("刷新数据库list.size = " + list_sosinfo.size());

		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("SosCount", newlist_sosinfo.size());
		editor.commit();

		refreshInboxButton();
	}

	private void refreshInboxButton() {
		mHandler.removeCallbacks(mRunnable_3);
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		// 目前只有sos消息，有其他类型的消息后需要相加
		InboxCount = preferences.getInt("SosCount", 0);

		if (InboxCount == 0) {
			inboxButton.setBackgroundResource(R.drawable.inbox_bg_black);
			inboxButton.setText("暂无推送通知");
			inboxButton.setTextColor(Color.parseColor("#999999"));
			inboxButton.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_bell_40dp, 0, 0, 0);
		} else {
			inboxButton.setBackgroundResource(R.drawable.inbox_bg_red);
			inboxButton.setText(InboxCount + "条推送消息");
			inboxButton.setTextColor(getResources().getColor(R.color.white));
			inboxButton.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_bell_white_40dp, 0, 0, 0);
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

		mHandler.post(mRunnable_3);

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
			if (InboxCount == 0) {
				mTextDialog.setText("暂无推送消息");
				mHandler.post(mRunnable_1);
				mHandler.postDelayed(mRunnable_2, 1000);
			} else if (InboxCount == 1) {
				Bundle mbundle = new Bundle();
				mbundle.putString("id", EHelp.getInstance().getSosInfoList()
						.get(0).uid);
				Intent intentInbox = new Intent(MainActivity.this,
						RSOSActivity.class);
				intentInbox.putExtras(mbundle);
				startActivity(intentInbox);
			} else {
				Intent intentInbox = new Intent(MainActivity.this,
						ListActivity.class);
				startActivity(intentInbox);
			}
			break;

		case R.id.Message_Button:
			if (mBinder.getIsConnectRongIM()) {
				System.out.println("打开私信列表成功");
				RongIM.getInstance().startConversationList(MainActivity.this);
			} else {
				System.out.println("打开私信列表失败");
				mTextDialog.setText("私信服务无法连接");
				mHandler.post(mRunnable_1);
				mHandler.postDelayed(mRunnable_2, 1000);
			}
			break;

		case R.id.S_Button:
			Intent intentS = new Intent(MainActivity.this,
					CountDownActivity.class);
			startActivity(intentS);
			finish();
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

		} else if (str.contentEquals("获取userinfo成功")) {
			SharedPreferences preferences = getSharedPreferences("eSOS",
					Context.MODE_PRIVATE);
			bitmapUtils.display(mhead,
					"http://www.qqzhi.com/uploadpic/2014-11-12/190218795.jpg");
			mnickname.setText(preferences.getString("nickname", ""));
			mphone.setText(preferences.getString("cellPhone", ""));
		} else if (str.contentEquals("refreshInboxButton")) {
			refreshInboxButton();
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

		mHandler.removeCallbacks(mRunnable_1);
		mHandler.removeCallbacks(mRunnable_2);
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
