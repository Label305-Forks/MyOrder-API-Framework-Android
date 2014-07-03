package nl.myorder.moexample.ui;

import java.util.concurrent.TimeUnit;

import nl.myorder.moexample.R;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.api.dao.ApiManager;
import com.api.listener.DataLoadListener;
import com.api.model.Merchant;
import com.api.model.MerchantsResponse;

public class MerchantListFragment extends Fragment implements LocationListener, OnItemClickListener {

	private ListView merchantList;
	private MerchantListAdapter adapter;
	private LocationManager locationManager;
	private Location lastKnowLocation;
	private static final double NL_LATITUDE = 52.1326330;
	private static final double NL_LONGITUDE = 5.2912659;
	private static final long MIN_TIME_FOR_UPDATE_INTERVAL = TimeUnit.SECONDS.toMillis(60);
	private static final float MIN_DISTANCE_FOR_UPDATE_INTERVAL = 20;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onStart() {
		super.onStart();
		turnOnLocationUpdate(getActivity());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		turnOffLocationUpdate(getActivity());
	}

	public void turnOnLocationUpdate(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		this.lastKnowLocation = getLastLocation();

		boolean enableGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean enableNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (enableNetwork) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATE_INTERVAL, MIN_DISTANCE_FOR_UPDATE_INTERVAL, this);

		}

		if (enableGPS) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE_INTERVAL, MIN_DISTANCE_FOR_UPDATE_INTERVAL, this);
		}

		if (!enableGPS && !enableNetwork) {
			turnOffLocationUpdate(context);
		}

	}

	public void turnOffLocationUpdate(Context context) {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.merchant_list_fragment, container, false);
		merchantList = (ListView) rootView.findViewById(R.id.merchant_list);
		adapter = new MerchantListAdapter(getActivity());
		merchantList.setAdapter(adapter);
		merchantList.setOnItemClickListener(this);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		activity.setTitle(R.string.title_login);
	}

	public Location getLastLocation() {
		// if(locationManager != null){
		Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		long GPSLocationTime = 0;
		long NetLocationTime = 0;

		if (locationGPS != null) {
			GPSLocationTime = locationGPS.getTime();
		}

		if (locationNet != null) {
			NetLocationTime = locationNet.getTime();
		}

		if (0 < GPSLocationTime - NetLocationTime) {
			return locationGPS;
		} else {
			return locationNet;
		}

		// }
		// return null;
	}

	public double getLatitude() {
		if (lastKnowLocation != null && lastKnowLocation.getLatitude() > 0) {
			return lastKnowLocation.getLatitude();
		} else {
			return NL_LATITUDE;
		}

	}

	public double getLongitude() {
		if (lastKnowLocation != null && lastKnowLocation.getLongitude() > 0) {
			return lastKnowLocation.getLongitude();
		} else {
			return NL_LONGITUDE;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		lastKnowLocation = location;
		getMerchants();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	private void getMerchants() {
		ApiManager.getInstance().getMerchantDAO().getMerchants(null, getLatitude(), getLongitude(), null, null, null, new DataLoadListener() {

			@Override
			public void onError(Object arg0) {
				Toast.makeText(getActivity(), arg0.toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onDataLoad(Object arg0) {
				if (arg0 instanceof MerchantsResponse) {
					adapter.notifyDataSetChanged(((MerchantsResponse) arg0).getMerchants());
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Merchant merchant = adapter.getItem(position);
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.addToBackStack(getFragmentManager().findFragmentById(R.id.container).getClass().getName());
		ProductListFragment mContent = new ProductListFragment();
		Bundle args = new Bundle();
		args.putSerializable("merchant", merchant);
		mContent.setArguments(args);
		ft.replace(R.id.container, mContent);
		ft.commit();
	}

}
