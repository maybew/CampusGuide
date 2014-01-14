package com.campusguide.activities;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.campusguide.R;
import com.campusguide.objects.BaseBuilding;
import com.campusguide.utilities.ImageLoader;
import com.campusguide.utilities.JSONLoader;
import com.campusguide.utilities.Utils;
import com.campusguide.views.SlidingUpPanelLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener, OnMarkerClickListener,
		JSONLoader.OnJSONLoadListener, ViewPager.OnPageChangeListener {

	private static final String MAIN_ACTIVITY_TAG = "main_activity";
	private static final String MAP_FRAGMENT_TAG = "map_fragment";
	private static final LatLng UWL_CENTER = new LatLng(43.81604278,
			-91.23061895);
	private static final int UWL_CENTER_ZOOM = 15;
	private static final String titles[] = { "Normal", "Satellite",
			"Hide Panel", "Show Panel" };

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private SlidingUpPanelLayout mSlidingUpPanelLayout;

	private ViewPager mPager;
	private MainPagerAdapter mMainPagerAdapter = new MainPagerAdapter(
			getSupportFragmentManager());

	private SupportMapFragment mapFragment;
	private GoogleMap mMap;
	private UiSettings mUiSettings;
	private LocationClient mLocationClient;

	private List<ImageView> mImageViews = new ArrayList<ImageView>();
	private List<Marker> mMarkers = new ArrayList<Marker>();
	private Marker mActivedMarker;

	/**
	 * This activity loads a map and then displays the route and pushpins on it.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up drawer
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, titles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mSlidingUpPanelLayout = (SlidingUpPanelLayout) mDrawerLayout
				.findViewById(R.id.sliding_layout);
		mSlidingUpPanelLayout.setShadowDrawable(getResources().getDrawable(
				R.drawable.above_shadow));
		mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);
		mSlidingUpPanelLayout.setDragPeekingViewOnly(true);
		mSlidingUpPanelLayout.setPanelHeight((int) getResources().getDimension(
				R.dimen.slideing_up_panel_height));
		mSlidingUpPanelLayout.setAnchorPoint(0.5f);

		mPager = (ViewPager) mSlidingUpPanelLayout
				.findViewById(R.id.sliding_pager);

		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentByTag(MAP_FRAGMENT_TAG);

		if (mapFragment == null) {
			// To programmatically add the map, we first create a
			// SupportMapFragment.
			mapFragment = SupportMapFragment.newInstance();

			// Then we add it using a FragmentTransaction.
			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.add(R.id.main_content, mapFragment,
					MAP_FRAGMENT_TAG);
			fragmentTransaction.commit();
		}

		if (savedInstanceState == null) {
			// First incarnation of this activity.
			mapFragment.setRetainInstance(true);
			JSONLoader jl = new JSONLoader(this);
			jl.execute("/all");
			setUpMapIfNeeded();
		} else {
			// Reincarnated activity. The obtained map is the same map instance
			// in the previous
			// activity life cycle. There is no need to reinitialize it.
			mMap = mapFragment.getMap();
		}

		/*
		 * new Routing(this, map, Color.parseColor("#ff0000"),
		 * Routing.Start.BLUE, Routing.Destination.ORANGE).execute(new
		 * LatLng(43.81764526, -91.23378396), new LatLng(43.81371639,
		 * -91.23000741));
		 */

	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		setUpPagerIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UWL_CENTER,
				UWL_CENTER_ZOOM));
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = mapFragment.getMap();
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				mMap.setOnMarkerClickListener(this);
				mUiSettings = mMap.getUiSettings();
				mUiSettings.setMyLocationButtonEnabled(false);
				mUiSettings.setCompassEnabled(false);
				mUiSettings.setZoomControlsEnabled(false);
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	private void setUpPagerIfNeeded() {
		mPager.setAdapter(mMainPagerAdapter);
		mPager.setOnPageChangeListener(this);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				mMap.setMapType(MAP_TYPE_NORMAL);
				break;
			case 1:
				mMap.setMapType(MAP_TYPE_HYBRID);
				break;
			case 2:
				mSlidingUpPanelLayout.hidePane();
				break;
			case 3:
				mSlidingUpPanelLayout.showPane();
				break;
			}
			mDrawerLayout.closeDrawers();
		}
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onJSONLoadSuccess(JSONArray result) {
		// TODO Auto-generated method stub

		JSONObject object;
		List<BaseBuilding> buildings = new ArrayList<BaseBuilding>();
		for (int i = 0, n = result.length(); i < n; i++) {
			try {
				object = result.getJSONObject(i).getJSONObject("fields");

				BaseBuilding building = BaseBuilding.newInstance(object, this);

				buildings.add(building);

				// Add markers
				mMarkers.add(mMap
						.addMarker(new MarkerOptions()
								.position(
										new LatLng(building.getLat(), building
												.getLng()))
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.point))));

				mMainPagerAdapter.addFragment(MainPagerFragment
						.newInstance(building));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setUpPagerIfNeeded();
	}

	private static class MainPagerAdapter extends FragmentStatePagerAdapter {
		private List<Fragment> mFragmentList;

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentList = new ArrayList<Fragment>();
		}

		public void addFragment(MainPagerFragment newInstance) {
			mFragmentList.add(newInstance);
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return mFragmentList.get(arg0);
		}

		@Override
		public int getCount() { // TODO Auto-generated method stub
			return mFragmentList.size();
		}

	}

	public static class MainPagerFragment extends Fragment {
		private String mName;
		private String mCategories;
		private String mOpenTime;
		private String mAddress;
		private List<String> mPhotos;
		private String mDescription;

		public static MainPagerFragment newInstance(BaseBuilding b) {
			MainPagerFragment f = new MainPagerFragment();

			Bundle args = new Bundle();
			args.putString("name", b.getName());
			args.putString("categories",
					Utils.stringJoiner(b.getCategories(), ", "));
			args.putString("opentime", b.getOpenTime());
			args.putString("address", b.getAddress());
			args.putStringArrayList("photos", b.getURLs());
			args.putString("description", b.getDescription());
			f.setArguments(args);

			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			mName = getArguments() != null ? getArguments().getString("name")
					: "";
			mCategories = getArguments() != null ? getArguments().getString(
					"categories") : "";
			mOpenTime = getArguments() != null ? getArguments().getString(
					"opentime") : "";
			mAddress = getArguments() != null ? getArguments().getString(
					"address") : "";
			mPhotos = getArguments() != null ? getArguments()
					.getStringArrayList("photos") : null;
			mDescription = getArguments() != null ? getArguments().getString(
					"description") : "";
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			setRetainInstance(true);
			View view = inflater.inflate(R.layout.fragment_main_pager,
					container, false);
			TextView nameTv = (TextView) view
					.findViewById(R.id.main_pager_name);
			nameTv.setText(mName);
			TextView categoriesTv = (TextView) view
					.findViewById(R.id.main_pager_categories);
			categoriesTv.setText(mCategories);
			TextView openTimeTv = (TextView) view
					.findViewById(R.id.main_pager_openhour);
			openTimeTv.setText(mOpenTime);
			TextView addressTv = (TextView) view
					.findViewById(R.id.main_pager_address);
			addressTv.setText(mAddress);
			if (mPhotos.size() != 0) {
				ImageView firstIv = (ImageView) view
						.findViewById(R.id.main_pager_first_photo);
				new ImageLoader(firstIv).execute(mPhotos.get(0));

				if (mPhotos.size() != 1) {
					ImageView secondIv = (ImageView) view
							.findViewById(R.id.main_pager_second_photo);
					new ImageLoader(secondIv).execute(mPhotos.get(1));
				}
			}
			TextView photoTitleTv = (TextView) view
					.findViewById(R.id.main_pager_photos_title);
			photoTitleTv.setText(mPhotos.size() + " photos");
			TextView descriptionTv = (TextView) view
					.findViewById(R.id.main_pager_description);
			descriptionTv.setText(mDescription);
			return view;
		}
	}

	public static class SearchListFragment extends ListFragment {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		mMap.animateCamera(CameraUpdateFactory.newLatLng(mMarkers.get(arg0)
				.getPosition()));
		activateMarker(mMarkers.get(arg0));
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		mSlidingUpPanelLayout.showPane();
		mPager.setCurrentItem(mMarkers.indexOf(arg0));
		activateMarker(arg0);
		return false;
	}

	private void activateMarker(Marker marker) {
		if (mActivedMarker != null)
			mActivedMarker.setIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.point));
		marker.setIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location));
		mActivedMarker = marker;
	}
}
