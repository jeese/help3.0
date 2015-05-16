package auth;

import org.json.JSONException;
import org.json.JSONObject;

import utils.MD5Util;
import view.MaterialDialog;
import view.materialedittext.MaterialEditText;

import com.ehelp.esos.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import activity.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import application.App;

public class RegisterPasswordActivity extends ActionBarActivity {

	public static Activity RegisterPasswordActivity;
	private Toolbar mToolbar;
	private MaterialEditText password_edit;
	private MaterialEditText nickname_edit;
	private String phone;
	private String password;
	private String nickname;
	private MaterialDialog mMaterialDialog_1;
	private MaterialDialog mMaterialDialog_2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_password);
		RegisterPasswordActivity = this;
		Bundle extras = getIntent().getExtras();
		phone = extras.getString("phone");
		init();
	}

	private void init() {
		setToolBar();

		password_edit = (MaterialEditText) findViewById(R.id.password_edit);
		nickname_edit = (MaterialEditText) findViewById(R.id.nickname_edit);

		Button finishButton = (Button) findViewById(R.id.finish_button);

		finishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (password_edit.getText().toString().isEmpty() == false
						&& nickname_edit.getText().toString().isEmpty() == false)
					// 访问服务器注册
					register();
			}
		});

	}

	private void setToolBar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		// toolbar.setLogo(R.drawable.ic_launcher);
		mToolbar.setTitle("填写注册信息");// 标题的文字需在setSupportActionBar之前，不然会无效
		// toolbar.setSubtitle("副标题");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

	private void register() {

		try {
			password = MD5Util.md5Encode(password_edit.getText().toString());
			nickname = nickname_edit.getText().toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		RequestParams params = new RequestParams();
		params.addBodyParameter("phonenum", phone);
		params.addBodyParameter("password", password);
		params.addBodyParameter("nickname", nickname);

		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.POST,
				"http://1.eesos.sinaapp.com/regist.php", params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						System.out.println("访问服务器失败");
						mMaterialDialog_2 = new MaterialDialog(
								RegisterPasswordActivity.this)
								.setTitle("网络不畅")
								.setMessage("您的设备网络连接不畅，请检查您的网络。")
								.setPositiveButton("确认",
										new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												mMaterialDialog_2.dismiss();
											}
										});
						mMaterialDialog_2.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							JSONObject replyObject = new JSONObject(arg0.result);
							String state = replyObject.getString("State");
							if (state.equals("success")) {
								SharedPreferences preferences = getSharedPreferences(
										"eSOS", Context.MODE_PRIVATE);
								Editor editor = preferences.edit();
								editor.putString("cellPhone", phone);
								editor.putString("password", password);
								editor.putString("userid", replyObject.getString("Userid"));
								editor.commit();

								System.out.println("注册成功");

								// 执行登陆 修改别名
								 App myApp = ((App) getApplicationContext());
								 myApp.setAlias();
								 myApp.login();

								// 页面跳转到主页面
								Intent intent = new Intent(
										RegisterPasswordActivity.this,
										MainActivity.class);
								RegisterPasswordActivity.this
										.startActivity(intent);
								StartActivity.StartActivity.finish();
								finish();

							} else {
								System.out.println("注册失败");
								mMaterialDialog_1 = new MaterialDialog(
										RegisterPasswordActivity.this)
										.setTitle("注册失败")
										.setMessage("您的注册尝试失败，请检查您的注册信息后重试。")
										.setPositiveButton("确认",
												new View.OnClickListener() {
													@Override
													public void onClick(View v) {
														mMaterialDialog_1
																.dismiss();

													}
												});
								mMaterialDialog_1.show();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				});
	}

}
