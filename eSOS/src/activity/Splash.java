package activity;

import com.ehelp.esos.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import application.App;
import auth.StartActivity;

public class Splash extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// 获取登录状态数据
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		Boolean login_status = preferences.getBoolean("login_status", false);
		/**
		 * 求救状态
		 * 0----未发出求救或者取消求救成功
		 * 1----取消求救，但服务器未响应
		 * 2----发出求救，但服务器未响应
		 * 3----发出求救成功
		 */
		int sos_status = preferences.getInt("sos_status", 0);

		// 判断登录状态
		if (login_status) {
			// 已登录状态

			// 执行登陆
			App myApp = ((App) getApplicationContext());
			myApp.login();

			if (sos_status == 2 || sos_status == 3) {
				// 页面跳转到求救页面
				Intent sosIntent = new Intent(Splash.this, SSOSActivity.class);
				Splash.this.startActivity(sosIntent);
				Splash.this.finish();
			} else {
				// 页面跳转到主页面
				Intent mainIntent = new Intent(Splash.this, MainActivity.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
			}

		} else {
			// 未登录状态

			// 页面跳转到开始页面
			Intent startIntent = new Intent(Splash.this, StartActivity.class);
			Splash.this.startActivity(startIntent);
			Splash.this.finish();

		}

	}

}
