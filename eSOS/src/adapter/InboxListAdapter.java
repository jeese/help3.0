package adapter;

import java.util.List;

import view.CircleImageView;

import com.ehelp.esos.R;
import com.lidroid.xutils.BitmapUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InboxListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Integer> list;
	private BitmapUtils bitmapUtils;
	

	public InboxListAdapter(Context context, List<Integer> list) {
		this.mContext = context;
		this.list = list;
		bitmapUtils = new BitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.contact_48dp);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_3, null);
			viewHolder.headimage = (CircleImageView) convertView.findViewById(R.id.image);
			viewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			viewHolder.message = (TextView) convertView.findViewById(R.id.message);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		bitmapUtils.display(viewHolder.headimage, "http://www.qqzhi.com/uploadpic/2014-11-12/190218795.jpg");
		viewHolder.nickname.setText("王金鑫");
		viewHolder.message.setText("安全求救");
		
		return convertView;
	}
	
	final static class ViewHolder {
		CircleImageView headimage;
		TextView nickname;
		TextView message;
	}
}
