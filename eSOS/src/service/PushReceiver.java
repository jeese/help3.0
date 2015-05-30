package service;

import java.util.List;

import model.EHelp;
import model.MyHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import utils.DateUtil;

import bean.SosInfo;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import cn.jpush.android.api.JPushInterface;

import activity.RSOSActivity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import application.App;

public class PushReceiver extends BroadcastReceiver {

	private static final String TAG = "PushReceiver";

	private NotificationManager nm;
	
	private App myApp;
	
	private Handler mhandler = new Handler();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (null == nm) {
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		
		myApp = ((App) context.getApplicationContext());

		Bundle bundle = intent.getExtras();
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: "
				+ printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			Log.d(TAG, "JPush用户注册成功");

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "接受到推送下来的自定义消息");

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "接受到推送下来的通知");

			receivingNotification(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			Log.d(TAG, "用户点击打开了通知");

			openNotification(context, bundle);

		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());
		}
	}

	private void receivingNotification(final Context context, Bundle bundle) {
		
		System.out.println("1111111111111111111111111111");
		
		Runnable runnable;
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		try {
			JSONObject extrasJson = new JSONObject(extras);
			final String nickname = extrasJson.optString("nickname");
			final String phonenum = extrasJson.optString("phonenum");
			final String id = extrasJson.optString("id");
			final int time = extrasJson.optInt("time");
			System.out.println("22222222222222222222222222222222222");
			runnable = new Runnable() {

				@Override
				public void run() {
					System.out.println("44444444444444444444444444444");
					writeNewSosInfotoDb(context, id, phonenum, nickname, DateUtil.TimestamptoDate(time));
				}
			};
		} catch (Exception e) {
			Log.w(TAG, "Unexpected: extras is not a valid json", e);
			return;
		}
		
		System.out.println("33333333333333333333333333333333");
		mhandler.post(runnable);
		System.out.println("88888888888888888888888888888888888");
		
		
	}

	private void openNotification(Context context, Bundle bundle) {
		System.out.println("999999999999999999999999999");
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		String id = "";
		try {
			JSONObject extrasJson = new JSONObject(extras);
			id = extrasJson.optString("id");
		} catch (Exception e) {
			Log.w(TAG, "Unexpected: extras is not a valid json", e);
			return;
		}

		Bundle mbundle = new Bundle();
		mbundle.putString("id", id);
		
		Log.d(TAG, "id = " + id);

		Intent mIntent = new Intent(context, RSOSActivity.class);
		mIntent.putExtras(mbundle);
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mIntent);

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	

	private void writeNewSosInfotoDb(final Context context, final String uid, final String phonenum, final String nickname,
			final String time) {
		System.out.println("5555555555555555555555555555555555");
		List<SosInfo> list = EHelp.getInstance().getSosInfoList();
		Boolean isNew = true;
		
		SharedPreferences preferences = context.getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		if(preferences.getInt("SosCount", 0)!=0){
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).uid.equals(uid)) {
					isNew = false;
				}
			}
		}

		if (isNew) {
			System.out.println("66666666666666666666666666666666");
			RequestParams params = new RequestParams();
			params.addBodyParameter("sosid", uid);

			HttpUtils http = MyHttpClient.getInstance().http;
			http.send(HttpRequest.HttpMethod.POST,
					"http://1.eesos.sinaapp.com/getsosinfo.php", params,
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
									System.out.println("获取求救消息成功");
									SosInfo sosinfo = new SosInfo(uid, nickname, phonenum, time, replyObject.getString("text"), replyObject.getString("x"), replyObject.getString("y"));
									EHelp.getInstance().getSosInfoList().add(sosinfo);
									DbUtils db = DbUtils.create(context);

									try {
										db.save(sosinfo);
										SharedPreferences preferences = context.getSharedPreferences("eSOS",
												Context.MODE_PRIVATE);
										int count = preferences.getInt("SosCount", 0);
										count++;
										Editor editor = preferences.edit();
										editor.putInt("SosCount", count);
										editor.commit();
										myApp.refreshInboxButton();
										System.out.println("7777777777777777777777777777777");
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								} else {
									System.out.println("获取求救消息失败");
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
