package service;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient.ConnectCallback;
import io.rong.imlib.RongIMClient.ErrorCode;

import org.json.JSONException;
import org.json.JSONObject;

import utils.DateUtil;

import bean.SosInfo;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import model.EHelp;
import model.MyHttpClient;
import monitor.HeadSetHelper;
import monitor.HeadSetHelper.OnHeadSetListener;
import myinterface.Watcher;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import application.App;

public class CoreService extends Service implements AMapLocationListener,
		Watcher {

	private LocationManagerProxy mLocationManagerProxy;
	private Handler mhandler = new Handler();
	private Runnable runnable;
	private Runnable runnable_1;// 初始化消息数据
	private Runnable mRunnable_3;// 连接融云服务器
	private App myApp;
	private String mContent;
	private String mLat;
	private String mLng;
	private String mAddress;// 详细地址
	private Boolean isConnectRongIM = false;

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}

	/**
	 * Service的调用接口
	 */
	public class MyBinder extends Binder {
		/**
		 * 发送求助消息
		 */
		public void SendSOS() {
			sendSOS();
		}

		public Boolean getIsConnectRongIM() {
			return isConnectRongIM;
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("后台服务onCreate()。。。");

		myApp = ((App) getApplicationContext());
		// 添加观察者
		myApp.addWatcher(this);

		// 连接融云服务器
		connectRongIM();

		init();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("销毁后台服务。。。");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private void init() {
		// 注册线控监听
		System.out.println("注册线控监听");
		HeadSetHelper.getInstance().setOnHeadSetListener(headSetListener);
		HeadSetHelper.getInstance().open(this);
		System.out.println("注册线控监听over");

		runnable = new Runnable() {

			@Override
			public void run() {
				upLoadLocation();
				mhandler.postDelayed(this, 1680000);

			}
		};

		if (myApp.STATE == 1) {
			upLoadLocation();
		}

	}

	private void connectRongIM() {

		mhandler.removeCallbacks(mRunnable_3);

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
					mhandler.postDelayed(mRunnable_3, 10000);
				}
			});
		}

	}

	private void upLoadLocation() {

		mhandler.removeCallbacks(runnable);

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

		mLocationManagerProxy.setGpsEnable(false);

		// 定时上传
		mhandler.postDelayed(runnable, 1680000);

	}

	OnHeadSetListener headSetListener = new OnHeadSetListener() {
		@Override
		public void onFiveClick() {
			System.out.println("按了5下");
			Toast.makeText(getApplicationContext(), "按了5下求助按钮",
					Toast.LENGTH_SHORT).show();
		}
	};

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

			RequestParams params = new RequestParams();
			params.addBodyParameter("posx", posx);
			params.addBodyParameter("posy", posy);

			HttpUtils http = MyHttpClient.getInstance().http;
			http.send(HttpRequest.HttpMethod.POST,
					"http://1.eesos.sinaapp.com/setcurrentpos.php", params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							System.out.println("连接服务器失败");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							try {
								JSONObject replyObject = new JSONObject(
										arg0.result);
								String state = replyObject.getString("State");
								System.out.println(state);

								if (state.equals("success")) {
									System.out.println("上传位置成功");
								} else {
									System.out.println("上传位置失败");
								}

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					});

		}
	}

	private void sendSOS() {

		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("sos_status", 2);
		editor.putString("sos_time", DateUtil.getDate());
		editor.commit();

		mContent = preferences.getString("mContent", null);
		mLat = preferences.getString("mLat", null);
		mLng = preferences.getString("mLng", null);
		mAddress = preferences.getString("mAddress", null);

		RequestParams params = new RequestParams();
		params.addBodyParameter("text", mContent);
		params.addBodyParameter("locinfo", mAddress);
		params.addBodyParameter("locx", mLat);
		params.addBodyParameter("locy", mLng);

		HttpUtils http = MyHttpClient.getInstance().http;
		http.send(HttpRequest.HttpMethod.POST,
				"http://1.eesos.sinaapp.com/sos.php", params,
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

							if (state.equals("success")) {
								System.out.println("发送求救成功");
								SharedPreferences preferences = getSharedPreferences(
										"eSOS", Context.MODE_PRIVATE);
								Editor editor = preferences.edit();
								editor.putInt("sos_status", 3);
								editor.commit();
								if (!(mContent == null)) {
									myApp.sendSosOK();
								}
							} else {
								System.out.println("发送求救失败");
							}

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

			// 上传位置
			upLoadLocation();

		} else if (str.contentEquals("获取Token成功")) {
			connectRongIM();
		}

	}

}
