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

		// ��ȡ��¼״̬����
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		Boolean login_status = preferences.getBoolean("login_status", false);
		/**
		 * ���״̬
		 * 0----δ������Ȼ���ȡ����ȳɹ�
		 * 1----ȡ����ȣ���������δ��Ӧ
		 * 2----������ȣ���������δ��Ӧ
		 * 3----������ȳɹ�
		 */
		int sos_status = preferences.getInt("sos_status", 0);

		// �жϵ�¼״̬
		if (login_status) {
			// �ѵ�¼״̬

			// ִ�е�½
			App myApp = ((App) getApplicationContext());
			myApp.login();

			if (sos_status == 2 || sos_status == 3) {
				// ҳ����ת�����ҳ��
				Intent sosIntent = new Intent(Splash.this, SSOSActivity.class);
				Splash.this.startActivity(sosIntent);
				Splash.this.finish();
			} else {
				// ҳ����ת����ҳ��
				Intent mainIntent = new Intent(Splash.this, MainActivity.class);
				Splash.this.startActivity(mainIntent);
				Splash.this.finish();
			}

		} else {
			// δ��¼״̬

			// ҳ����ת����ʼҳ��
			Intent startIntent = new Intent(Splash.this, StartActivity.class);
			Splash.this.startActivity(startIntent);
			Splash.this.finish();

		}

	}

}
