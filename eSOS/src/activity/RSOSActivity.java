package activity;

import java.util.List;

import model.EHelp;
import model.SosInfo;
import utils.DateUtil;
import view.CircleImageView;
import view.materialedittext.MaterialEditText;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RSOSActivity extends ActionBarActivity implements OnClickListener {

	private Toolbar mToolbar;
	private MapView mapView;
	private AMap aMap;
	private UiSettings mUiSettings;
	private LatLng mLocation;
	private Button messageButton;
	public String uid;
	public String nickname;
	public String phonenum;
	public String time;
	public String soscontent;
	public String lat;
	public String lng;

	private CircleImageView mhead;
	private TextView mnickname;
	private TextView mtime;
	private BitmapUtils bitmapUtils;
	private MaterialEditText sostext;
	private View MarkerIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rsos);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();

		uid = bundle.getString("id");
//		nickname = bundle.getString("nickname");
//		phonenum = bundle.getString("phonenum");
//		time = bundle.getInt("time");
		
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
		mToolbar.setTitle("求救详情");// 标题的文字需在setSupportActionBar之前，不然会无效
		// toolbar.setSubtitle("副标题");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		messageButton = (Button) findViewById(R.id.MessageButton);
		messageButton.setOnClickListener(this);
		
		List<SosInfo> list = EHelp.getInstance().getSosInfoList();
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).uid.equals(uid)){
				System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				nickname = list.get(i).nickname;
				phonenum = list.get(i).phonenum;
				time = list.get(i).time;
				soscontent = list.get(i).soscontent;
				lat = list.get(i).lat;
				lng = list.get(i).lng;
				mLocation = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
				aMap.moveCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition(mLocation,
								(float) 16, 0, 0)));
				System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
				break;
			}
		}
		
		System.out.println("start----setMyMarker()------");
		// 添加地图标签
		setMarker();
		System.out.println("end----setMyMarker()------");

		sostext = (MaterialEditText) findViewById(R.id.soscontent);
		mhead = (CircleImageView) findViewById(R.id.head);
		mnickname = (TextView) findViewById(R.id.nickname);
		mtime = (TextView) findViewById(R.id.time);
		sostext = (MaterialEditText) findViewById(R.id.soscontent);
		
		System.out.println("soscontent=========" + soscontent);

		if(soscontent.equals("")||soscontent.equals("null")){
			sostext.setText("求救状态暂无");
		}else{
			sostext.setText(soscontent);
		}

		bitmapUtils.display(mhead,
				"http://www.qqzhi.com/uploadpic/2014-11-12/190218795.jpg");
		mnickname.setText(nickname);
		mtime.setText(time);
		
	}
	
	private void setMarker() {

		MarkerIcon = LayoutInflater.from(this).inflate(
				R.layout.marker_icon, null);

		// 初始化marker内容
		MarkerOptions markerOptions = new MarkerOptions();
		BitmapDescriptor markerIcon = BitmapDescriptorFactory
				.fromView(MarkerIcon);
		markerOptions.position(mLocation).icon(markerIcon).title("mymaker");

		aMap.clear();
		// 添加到地图上
		aMap.addMarker(markerOptions);
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

		case R.id.MessageButton:

			if (!uid.equals(null)) {
				RongIM.getInstance().startConversation(RSOSActivity.this,
						Conversation.ConversationType.PRIVATE, uid, "私信");
			}

			break;

		}
	}

}
