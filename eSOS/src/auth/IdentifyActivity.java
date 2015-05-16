package auth;

import com.ehelp.esos.R;

import view.MaterialDialog;
import view.materialedittext.MaterialEditText;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class IdentifyActivity extends ActionBarActivity {

	public static Activity IdentifyActivity;
	private Toolbar mToolbar;
	private String phone;
	private MaterialDialog mMaterialDialog;
	private MaterialEditText code_edit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identify_activity);
		IdentifyActivity = this;
		Bundle extras = getIntent().getExtras();
		phone = extras.getString("phone");
		init();
	}

	private void init() {
		setToolBar();

		final Button codeButton = (Button) findViewById(R.id.code_button);
		codeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SMSSDK.submitVerificationCode("86", phone, code_edit.getText()
						.toString());
			}
		});

		code_edit = (MaterialEditText) findViewById(R.id.code_edit);
		TextWatcher watcher = new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() == 0) {
					codeButton.setEnabled(false);
				} else {
					codeButton.setEnabled(true);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		};

		code_edit.addTextChangedListener(watcher);

	}

	EventHandler eh = new EventHandler() {

		@Override
		public void afterEvent(int event, int result, Object data) {

			Message msg = new Message();
			msg.arg1 = event;
			msg.arg2 = result;
			msg.obj = data;
			handler.sendMessage(msg);
		}

	};

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int event = msg.arg1;
			int result = msg.arg2;
			Object data = msg.obj;
			Log.e("event", "event=" + event);
			if (result == SMSSDK.RESULT_COMPLETE) {

				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
					Toast.makeText(getApplicationContext(), "提交验证码成功",
							Toast.LENGTH_SHORT).show();
					// 页面跳转到注册个人信息页面
					Intent intent = new Intent(IdentifyActivity.this,
							RegisterPasswordActivity.class);
					intent.putExtra("phone", phone);
					IdentifyActivity.this.startActivity(intent);
					RegisterPhoneActivity.RegisterPhoneActivity.finish();
					finish();
				}
			} else {
				((Throwable) data).printStackTrace();
				// 验证码错误
				mMaterialDialog = new MaterialDialog(IdentifyActivity.this)
						.setTitle("验证码错误")
						.setMessage(
								"您的验证码错误，请检查后重新填写。")
						.setPositiveButton("确认", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mMaterialDialog.dismiss();

							}
						});
				mMaterialDialog.show();
			}
		}

	};

	private void setToolBar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		// toolbar.setLogo(R.drawable.ic_launcher);
		mToolbar.setTitle("填写验证码");// 标题的文字需在setSupportActionBar之前，不然会无效
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

	public void onResume() {
		super.onResume();
		SMSSDK.registerEventHandler(eh);
	}

	public void onPause() {
		super.onPause();
		SMSSDK.unregisterEventHandler(eh);
	}
}
