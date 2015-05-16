package service;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import model.MyHttpClient;
import monitor.HeadSetHelper;
import monitor.HeadSetHelper.OnHeadSetListener;
import myinterface.Watcher;
import android.app.Service;
import android.content.Intent;
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
	private Handler handler = new Handler();
	private App myApp;
	private String mLat;
	private String mLng;
	private String mAddress;// ��ϸ��ַ

	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}

	/**
	 * Service�ĵ��ýӿ�
	 */
	public class MyBinder extends Binder {
		/**
		 * ����������Ϣ
		 */
		public void SendSOS() {
			sendSOS();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("��̨����onCreate()������");

		myApp = ((App) getApplicationContext());
		// ��ӹ۲���
		myApp.addWatcher(this);
		
		init();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("���ٺ�̨���񡣡���");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	private void init() {
		// ע���߿ؼ���
		System.out.println("ע���߿ؼ���");
		HeadSetHelper.getInstance().setOnHeadSetListener(headSetListener);
		HeadSetHelper.getInstance().open(this);
		System.out.println("ע���߿ؼ���over");

	}

	private void upLoadLocation() {

		mLocationManagerProxy = LocationManagerProxy.getInstance(this);

		mLocationManagerProxy.requestLocationData(
				LocationProviderProxy.AMapNetwork, -1, 15, this);

		mLocationManagerProxy.setGpsEnable(false);

		// ��ʱ�ϴ�
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				upLoadLocation();
				handler.postDelayed(this, 1680000);
			}
		}, 1680000);

	}

	OnHeadSetListener headSetListener = new OnHeadSetListener() {
		@Override
		public void onFiveClick() {
			System.out.println("����5��");
			Toast.makeText(getApplicationContext(), "����5��������ť",
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
			mAddress = amapLocation.getAddress();

			RequestParams params = new RequestParams();
			params.addBodyParameter("posx", posx);
			params.addBodyParameter("posy", posy);

			HttpUtils http = MyHttpClient.getInstance().http;
			http.send(HttpRequest.HttpMethod.POST,
					"http://1.eesos.sinaapp.com/setcurrentpos.php", params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							System.out.println("���ӷ�����ʧ��");
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {

							try {
								JSONObject replyObject = new JSONObject(
										arg0.result);
								String state = replyObject.getString("State");
								System.out.println(state);

								if (state.equals("success")) {
									System.out.println("�ϴ�λ�óɹ�");
								} else {
									System.out.println("�ϴ�λ��ʧ��");
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

		RequestParams params = new RequestParams();
		params.addBodyParameter("text", "");
		params.addBodyParameter("locinfo", mAddress);
		params.addBodyParameter("locx", mLat);
		params.addBodyParameter("locy", mLng);

		HttpUtils http = MyHttpClient.getInstance().http;
		http.send(HttpRequest.HttpMethod.POST,
				"http://1.eesos.sinaapp.com/sos.php", params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println("���ӷ�����ʧ��");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							JSONObject replyObject = new JSONObject(arg0.result);
							String state = replyObject.getString("State");
							System.out.println(state);

							if (state.equals("success")) {
								System.out.println("������ȳɹ�");
							} else {
								System.out.println("�������ʧ��");
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
		if (str.contentEquals("��¼�ɹ�")) {

			// �ϴ�λ��
			upLoadLocation();

		}

	}

}
