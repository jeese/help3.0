package adapter;

import com.ehelp.esos.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter_1 extends BaseAdapter {

	private Context mContext;

	public DrawerListAdapter_1(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.list_item_1, parent, false);
		TextView text = (TextView) convertView.findViewById(R.id.text);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		
		switch(position){
		case 0:
			text.setText("我的信息");
			image.setImageResource(R.drawable.ic_account_box_black_24dp);
			break;
		case 1:
			text.setText("紧急求救卡");
			image.setImageResource(R.drawable.ic_verified_user_black_24dp);
			break;
		case 2:
			text.setText("人脉");
			image.setImageResource(R.drawable.ic_people_black_24dp);
			break;
		case 3:
			text.setText("爱心银行");
			image.setImageResource(R.drawable.ic_favorite_black_24dp);
			break;
		case 4:
			text.setText("历史记录");
			image.setImageResource(R.drawable.ic_recent_black_24dp);
			break;
		
		}
		
		return convertView;
	}

}
