package nl.myorder.moexample.ui;

import java.util.ArrayList;
import java.util.List;

import nl.myorder.moexample.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.api.dao.ApiManager;
import com.api.listener.DataLoadListener;
import com.api.model.Merchant;
import com.api.model.ProductsResponse;

public class ProductListFragment extends Fragment {

	private ListView productList;
	private Merchant merchant;
	private ProductListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			merchant = (Merchant) getArguments().getSerializable("merchant");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.product_list_fragment, container, false);
		productList = (ListView) rootView.findViewById(R.id.product_list);
		adapter = new ProductListAdapter(getActivity());
		productList.setAdapter(adapter);
		getProductList();
		return rootView;
	}

	private void getProductList() {
		if (merchant != null) {
			List<String> list = new ArrayList<String>();
			list.add(merchant.getId());
			ApiManager.getInstance().getCatalogDAO().getProducts(null, list, null, new DataLoadListener() {

				@Override
				public void onError(Object arg0) {
					Toast.makeText(getActivity(), arg0.toString(), Toast.LENGTH_LONG).show();
				}

				@Override
				public void onDataLoad(Object arg0) {
					if (arg0 instanceof ProductsResponse)
						adapter.notifyDataSetChanged(((ProductsResponse) arg0).getProducts());
				}
			});
		}
	}

}
