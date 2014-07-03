package nl.myorder.moexample.ui;

import java.util.ArrayList;
import java.util.List;

import nl.myorder.moexample.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.api.model.Merchant;

public class MerchantListAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private List<Merchant> merchantList;

	public MerchantListAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.merchantList = new ArrayList<Merchant>();
	}

	@Override
	public int getCount() {
		if (merchantList != null) {
			return merchantList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Merchant getItem(int position) {
		return merchantList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.txtMerchantName = (TextView) view.findViewById(R.id.merchant_name);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		final Merchant merchant = getItem(position);
		viewHolder.txtMerchantName.setText(merchant.getName());
		return view;
	}

	private static final class ViewHolder {
		private ViewHolder() {
		}

		private TextView txtMerchantName;

	}

	public void notifyDataSetChanged(List<Merchant> merchantList) {
		if (merchantList != null) {
			this.merchantList = merchantList;
		} else {
			this.merchantList.clear();
		}

		super.notifyDataSetChanged();
	}

}
