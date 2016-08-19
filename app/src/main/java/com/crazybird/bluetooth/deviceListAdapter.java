package com.crazybird.bluetooth;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazybird.R;

/**
 * 鍏充簬ta鐩捐繛鎺ヨ澶囩殑閫傞厤鍣?
 * @author MISSGUO
 *
 */
public class deviceListAdapter extends BaseAdapter {
	//閫傞厤鍣ㄧ殑闆嗗悎
	private ArrayList<deviceListItem> list;
	//鐢ㄦ潵寮曠敤鐩稿叧鐨剎ml鏂囦欢
	private LayoutInflater mInflater;
	//鏋勯?犳柟娉?
	public deviceListAdapter(Context context, ArrayList<deviceListItem> l) {
		list = l;
		mInflater = LayoutInflater.from(context);
	}

	//杩斿洖闆嗗悎鐨勬暟閲?
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	//閫氳繃涓嬫爣鑾峰緱闆嗗悎涓殑鏌愪竴椤?
	public deviceListItem getItem(int position) {
		return list.get(position);
	}

	//鑾峰緱姝ゅ璞＄殑涓嬫爣
	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		return position;
	}

	

	public View getView(int position, View convertView, ViewGroup parent) {
		//涓轰紭鍖朙istView,鏁呰?屽啓浜嗗唴閮ㄧ被
		ViewHolder viewHolder = null;

		if (convertView == null) {
			//寮曠敤甯冨眬
			convertView = mInflater.inflate(R.layout.divices_item, null);
			viewHolder = new ViewHolder();
			viewHolder.img = (ImageView) convertView
					.findViewById(R.id.devices_item_img);
			viewHolder.msg = (TextView) convertView
					.findViewById(R.id.devices_item_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		deviceListItem item = list.get(position);
		//濡傛灉娌℃湁璁惧宸茬粡閰嶅鍦╨istview褰撲腑,鍚庤竟鐨勬墜鏈哄氨涓嶆樉绀?
		if (item.getMessage().equals("娌℃湁璁惧宸茬粡閰嶅")) {
			viewHolder.img.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.img.setVisibility(View.VISIBLE);
		}
		if (item.isSiri) {
			//鑻ユ槸宸茬粡閰嶅鐨?,灏变负鎵嬫満鍥炬爣
			viewHolder.img.setImageResource(R.drawable.bt_jilinwan);
		} else {
			//鑻ヤ笉鏄凡缁忛厤瀵圭殑,灏变负钃濈墮鍥炬爣
			viewHolder.img.setImageResource(R.drawable.bt_lanyahei);
		}

		viewHolder.msg.setText(item.message);

		return convertView;
	}

	class ViewHolder {
		protected TextView msg;
		protected ImageView img;
	}
}
