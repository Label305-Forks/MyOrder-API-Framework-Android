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
import com.api.model.Product;

public class ProductListAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private List<Product> productList;

	public ProductListAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.productList = new ArrayList<Product>();
	}

	@Override
	public int getCount() {
		if (productList != null) {
			return productList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Product getItem(int position) {
		return productList.get(position);
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
		final Product prod = getItem(position);
		viewHolder.txtMerchantName.setText(prod.getName());
		return view;
	}

	private static final class ViewHolder {
		private ViewHolder() {
		}

		private TextView txtMerchantName;

	}

	public void notifyDataSetChanged(List<Product> productList) {
		if (productList != null) {
			this.productList = productList;
		} else {
			this.productList.clear();
		}

		super.notifyDataSetChanged();
	}

}
