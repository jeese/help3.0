package application;

import io.rong.imkit.RongIM;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.MyHttpClient;
import myinterface.Watched;
import myinterface.Watcher;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

public class App extends Application implements Watched {

	private HttpUtils http;

	//private PreferencesCookieStore preferencesCookieStore;
	private Handler handler = new Handler();
	private Runnable mRunnable;

	private int ONLINE = 1;
	private int OFFLINE = 2;
	private int NETWORK_ERROR = 3;
	public int STATE = 0;

	// 存放观察者
	private List<Watcher> list = new ArrayList<Watcher>();

	@Override
	public void onCreate() {
		super.onCreate();

		//preferencesCookieStore = new PreferencesCookieStore(this);

		// JPush的初始化
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		
		//初始化融云
		RongIM.init(this);
		
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		
		System.out.println("setalias:" + preferences.getBoolean("alias", false));
		System.out.println("login_status:" + preferences.getBoolean("login_status", false));
		System.out.println("setToken:" + preferences.getBoolean("gettoken", false));
		

	}

	public void login() {
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);

//		sessionId = preferences.getString("sessionId", null);
//		BasicClientCookie cookie = new BasicClientCookie("PHPSESSID", sessionId);
//		cookie.setDomain("http://1.eesos.sinaapp.com");
//		cookie.setPath("/");
//		preferencesCookieStore.addCookie(cookie);

		String phone = preferences.getString("cellPhone", null);
		String password = preferences.getString("password", null);

		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phone);
		params.addBodyParameter("password", password);
		params.addBodyParameter("needinfo", "0");

		http = MyHttpClient.getInstance().http;
		//http.configCookieStore(preferencesCookieStore);
		http.send(HttpRequest.HttpMethod.POST,
				"http://1.eesos.sinaapp.com/login.php", params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println("连接服务器失败");
						STATE = NETWORK_ERROR;
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							JSONObject replyObject = new JSONObject(arg0.result);
							String state = replyObject.getString("State");
							if (state.equals("success")) {

//								Cookie cs = preferencesCookieStore
//										.getCookie("PHPSESSID");
//								sessionId = cs.getValue();

								SharedPreferences preferences = getSharedPreferences(
										"eSOS", Context.MODE_PRIVATE);
								Editor editor = preferences.edit();
//								editor.putString("sessionId", sessionId);
								editor.putBoolean("login_status", true);
								editor.commit();
								
								System.out.println("uid=" + replyObject.getString("Userid"));

								notifyWatchers("登录成功");
								System.out.println("登录成功");
								STATE = ONLINE;
								
								//获取融云Token
								if((!preferences.getBoolean("gettoken", false))){
									getIMToken();
								}

								handler.postDelayed(new Runnable() {

									@Override
									public void run() {
										login();
										handler.postDelayed(this, 10000000);
									}
								}, 10000000);
							} else {
								System.out.println("登陆失败");
								STATE = OFFLINE;
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});
	}

	@Override
	public void addWatcher(Watcher watcher) {
		list.add(watcher);

	}

	@Override
	public void removeWatcher(Watcher watcher) {
		list.remove(watcher);

	}

	@Override
	public void notifyWatchers(String str) {

		for (Watcher watcher : list) {
			watcher.update(str);
		}

	}

	public void setAlias() {
		System.out.println("setalias()-----inginging" );
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);

		String userid = preferences.getString("userid", null);
		
		JPushInterface.setAlias(this, userid, mAliasCallback);
		
		handler.removeCallbacks(mRunnable);
	}

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		@Override
		public void gotResult(int code, String alias, Set<String> arg2) {
			System.out.println("setalias_code:" + code);
			switch (code) {
            case 0:
				SharedPreferences preferences = getSharedPreferences(
						"eSOS", Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putBoolean("alias", true);
				editor.commit();
				System.out.println("setalias:success--------------");
                break;
                
            case 6002:
            	mRunnable = new Runnable() {

					@Override
					public void run() {
						setAlias();
						handler.postDelayed(this, 60000);
					};
            	};
            	
				handler.postDelayed(mRunnable, 60000);
                break;
            
            default:

            }

		}

	};
	
	public void getIMToken() {
		System.out.println("getIMToken()-----inginging" );
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);

		String userid = preferences.getString("userid", null);
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", userid);

		http = MyHttpClient.getInstance().http;
		http.send(HttpRequest.HttpMethod.POST,
				"http://1.eesos.sinaapp.com/gettoken.php", params,
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
							if (state.equals("success")) {
								
								String token = replyObject.getString("token");
								System.out.println("获取token成功：" + token);
								
								SharedPreferences preferences = getSharedPreferences(
										"eSOS", Context.MODE_PRIVATE);
								Editor editor = preferences.edit();
								editor.putString("im_token", token);
								editor.putBoolean("gettoken", true);
								editor.commit();
								
								notifyWatchers("获取Token成功");

							} else {
								System.out.println("获取token失败");
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});
		
	}
	

}
