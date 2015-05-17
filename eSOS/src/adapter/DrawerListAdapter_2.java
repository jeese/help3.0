package adapter;

import com.ehelp.esos.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter_2 extends BaseAdapter {

	private Context mContext;

	public DrawerListAdapter_2(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return 2;
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
				R.layout.list_item_2, parent, false);
		TextView text = (TextView) convertView.findViewById(R.id.text);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		
		if(position == 0){
			text.setText("…Ë÷√");
			image.setImageResource(R.drawable.ic_settings_24dp);
		}else{
			text.setText("∞Ô÷˙∫Õ∑¥¿°");
			image.setImageResource(R.drawable.ic_help_24dp);
		}
		
		return convertView;
	}

}
