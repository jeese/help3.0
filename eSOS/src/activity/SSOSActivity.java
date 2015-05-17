package activity;

import model.MyHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.ehelp.esos.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import application.App;

public class SSOSActivity extends ActionBarActivity implements OnClickListener, AMapLocationListener {

	private Toolbar mToolbar;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private LatLng mLocation;
	private Button exitButton;
	private App myApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ssos);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);

		myApp = ((App) getApplicationContext());

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
		mToolbar.setTitle("消息详情");// 标题的文字需在setSupportActionBar之前，不然会无效
		// toolbar.setSubtitle("副标题");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		exitButton = (Button) findViewById(R.id.ExitButton);
		exitButton.setOnClickListener(this);

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

		// mLocation = new LatLng(geoLat, geoLng);
		//
		// aMap.addMarker(new MarkerOptions().position(mLocation).icon(
		// BitmapDescriptorFactory
		// .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		//
		// aMap.moveCamera(CameraUpdateFactory
		// .newCameraPosition(new CameraPosition(mLocation,
		// (float) 14.5, 0, 0)));
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
			break;

		}
	}

	private void ExitSOS() {
		System.out.println("exitsos_start");
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
	public void onLocationChanged(AMapLocation arg0) {
		// TODO Auto-generated method stub
		
	}

}
