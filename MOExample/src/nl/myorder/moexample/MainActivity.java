package nl.myorder.moexample;

import java.util.HashSet;
import java.util.Set;

import myorder.MyOrder;
import myorder.MyOrder.MyOrderEnvironment;
import myorder.http.utils.MOApiConnection;
import myorder.order.utils.MOConstant;
import myorder.paymentprovider.PaymentProvider;
import nl.myorder.lib.interfaces.OnFragmentStateChangeListener;
import nl.myorder.lib.ui.MyOrderLoginFragment;
import nl.myorder.lib.ui.handler.MOFragmentStateHandler;
import nl.myorder.lib.utils.MyOrderStateEnum;
import nl.myorder.moexample.ui.MerchantListFragment;
import nl.myorder.moexample.ui.MyOrderStorage;
import nl.myorder.moexample.utils.Installation;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;

import com.api.dao.ApiManager;
import com.api.listener.DataLoadListener;
import com.api.model.MOSession;

public class MainActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, OnFragmentStateChangeListener {

	private Fragment mContent;

	private void initMyOrderSDK() {
		MyOrder order = MyOrder.getInstance();
		order.setEnvironment(MyOrderEnvironment.MyOrderEnvironmentPlayground);
		order.setCredentialStorage(new MyOrderStorage(getApplicationContext()));
		order.setApiKey(getString(R.string.test_key));
		order.setApiSecret(getString(R.string.test_sec));
		order.setIdealReturnUrl(getResources().getString(R.string.ideal_url));
		Set<PaymentProvider> set = new HashSet<PaymentProvider>();

		set.add(new PaymentProvider(true, true, MOConstant.PAYMENT_PROVIDER_MINITIX, 1, false, getResources().getString(
				nl.myorder.lib.R.string.minitix_description), MOConstant.PAYMENT_PROVIDER_MINITIX));
		set.add(new PaymentProvider(true, true, MOConstant.PAYMENT_PROVIDER_IDEAL, 2, false, getResources()
				.getString(nl.myorder.lib.R.string.ideal_description), "iDEAL"));
		set.add(new PaymentProvider(true, true, MOConstant.PAYMENT_PROVIDER_CREDITCARD, 4, false, getResources().getString(
				nl.myorder.lib.R.string.creadit_card_description), MOConstant.PAYMENT_PROVIDER_CREDITCARD));
		set.add(new PaymentProvider(false, false, MOConstant.PAYMENT_PROVIDER_PAYPAL, 3, false, getResources().getString(
				nl.myorder.lib.R.string.paypal_description), MOConstant.PAYMENT_PROVIDER_PAYPAL));
		set.add(new PaymentProvider(true, false, MOConstant.PAYMENT_PROVIDER_CARD, 5, false,
				getResources().getString(nl.myorder.lib.R.string.card_description), MOConstant.PAYMENT_PROVIDER_CARD));
		set.add(new PaymentProvider(true, false, MOConstant.PAYMENT_PROVIDER_OTA, 6, false, getResources().getString(nl.myorder.lib.R.string.ota_description),
				getString(nl.myorder.lib.R.string.ota_payment)));
		set.add(new PaymentProvider(false, false, MOConstant.PAYMENT_PROVIDER_AUTO, 6, false, getResources().getString(
				nl.myorder.lib.R.string.lbl_auto_reload_title), getString(R.string.lbl_auto_reload_title)));
		order.setConfiguredPaymentProviders(set);
	}

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initMyOrderSDK();
		ApiManager.init(getString(R.string.server_url), getApplicationContext(), null, null);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		mContent = new MerchantListFragment();
		MOFragmentStateHandler.getInstance().setResourceId(R.id.container);
		getSupportFragmentManager().beginTransaction().replace(R.id.container, mContent).commit();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		onSectionChange(position);
	}

	public void onSectionChange(int number) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (number) {
		case 0:
			mContent = new MerchantListFragment();
			fragmentManager.beginTransaction().replace(R.id.container, new MerchantListFragment()).commit();
			break;
		case 1:
			fragmentManager.beginTransaction().addToBackStack(mContent.getClass().getName()).commit();
			mContent = new MyOrderLoginFragment();
			fragmentManager.beginTransaction().replace(R.id.container, mContent).commit();
			break;
		default:
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // as you specify a parent activity in AndroidManifest.xml.
	// int id = item.getItemId();
	// if (id == R.id.action_settings) {
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	protected void onResume() {
		super.onResume();
		if (!ApiManager.getInstance().getAuthDAO().hasSession()) {
			performAnonymousLogin();
		}
	}

	private void performAnonymousLogin() {
		ApiManager.getInstance().getAuthDAO().login(null, null, Installation.id(this), new DataLoadListener() {

			@Override
			public void onError(Object errorObject) {
			}

			@Override
			public void onDataLoad(Object arg0) {
				if (arg0 instanceof MOSession) {

				}
			}
		});
	}

	@Override
	public void onStateChange(MyOrderStateEnum arg0, boolean arg1, Bundle arg2) {
		if (MyOrderStateEnum.LOGIN_DONE.equals(arg0)) {
			ApiManager.getInstance().getAuthDAO()
					.login(MOApiConnection.getPhone(), MOApiConnection.getSessionId(), Installation.id(this), new DataLoadListener() {

						@Override
						public void onError(Object arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onDataLoad(Object arg0) {
							// TODO Auto-generated method stub

						}
					});
		}
		MOFragmentStateHandler.getInstance().onFragmentStateChanged(this, arg0, arg1, arg2);
	}

}
