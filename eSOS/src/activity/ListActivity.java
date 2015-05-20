package activity;

import java.util.List;

import model.EHelp;
import model.SosInfo;

import com.ehelp.esos.R;

import adapter.InboxListAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ListActivity extends ActionBarActivity implements
		OnItemClickListener {

	private Toolbar mToolbar;
	private ListView list_1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		init();

	}

	private void init() {
		SharedPreferences preferences = getSharedPreferences("eSOS",
				Context.MODE_PRIVATE);
		
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		// toolbar.setLogo(R.drawable.ic_launcher);
		mToolbar.setTitle(preferences.getString("nickname", "") + "的推送通知");// 标题的文字需在setSupportActionBar之前，不然会无效
		// toolbar.setSubtitle("副标题");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		list_1 = (ListView) findViewById(R.id.listview);
		List<SosInfo> list = EHelp.getInstance().getSosInfoList();

		list_1.setAdapter(new InboxListAdapter(this, list));
		list_1.setOnItemClickListener(this);
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bundle mbundle = new Bundle();
		mbundle.putString("id", EHelp.getInstance().getSosInfoList()
				.get(position).uid);
		Intent intentInbox = new Intent(ListActivity.this,
				RSOSActivity.class);
		intentInbox.putExtras(mbundle);
		startActivity(intentInbox);

	}
}
