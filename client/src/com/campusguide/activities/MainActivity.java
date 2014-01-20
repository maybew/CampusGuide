package com.campusguide.activities;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.campusguide.R;
import com.campusguide.objects.BaseBuilding;
import com.campusguide.route.Routing;
import com.campusguide.route.RoutingListener;
import com.campusguide.utilities.JSONLoader;
import com.campusguide.utilities.Utils;
import com.campusguide.views.SlidingUpPanelLayout;
import com.campusguide.adapters.MainPagerAdapter;
import com.campusguide.fragments.MainPagerFragment;
import com.campusguide.fragments.NearbyListFragment;
import com.campusguide.fragments.SearchListFragment;
import com.campusguide.fragments.SearchListFragment.OnSearchItemClickedListener;
import com.campusguide.fragments.ToViewListFragment;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

public class MainActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener, OnMarkerClickListener,
		JSONLoader.OnJSONLoadListener, RoutingListener,
		SearchListFragment.OnSearchItemClickedListener,
		MainPagerAdapter.OnMainPagerSelectedListener {

	private static final String MAIN_ACTIVITY_TAG = "main_activity";
	private static final String MAP_FRAGMENT_TAG = "map_fragment";
	private static final String SEARCH_FRAGMENT_TAG = "search_fragment";
	private static final String NEARBY_FRAGMENT_TAG = "nearby_fragment";
	private static final String TOVIEW_FRAGMENT_TAG = "toview_fragment";
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

	private LinearLayout mTitleBar;
	private TextView mSearchEdit;
	private ImageButton mNearbyButton;
	private ImageButton mToviewButton;

	private MainPagerAdapter mMainPagerAdapter;

	private SupportMapFragment mapFragment;
	private GoogleMap mMap;
	private UiSettings mUiSettings;
	private LocationClient mLocationClient;
	// private SearchListFragment mSearchListFragment;

	private static final List<BaseBuilding> mBuildings = new ArrayList<BaseBuilding>();
	private static final SparseArray<Marker> mMarkers = new SparseArray<Marker>();
	private static boolean mPrepared = false;
	private static boolean mIsSlidingPaneShown = false;
	private static Polyline mActivedPolyLine = null;
	private static Marker mActivedMarker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		mSlidingUpPanelLayout.setAnchorPoint(0.6f);

		mPager = (ViewPager) mSlidingUpPanelLayout
				.findViewById(R.id.sliding_pager);

		mTitleBar = (LinearLayout) findViewById(R.id.main_title_bar);
		mSearchEdit = (TextView) findViewById(R.id.main_search_edit);
		mSearchEdit.setOnClickListener(new SearchEditListener());
		mNearbyButton = (ImageButton) findViewById(R.id.main_nearby_btn);
		mNearbyButton.setOnClickListener(new NearbyButtonListener());
		mToviewButton = (ImageButton) findViewById(R.id.main_toview_btn);
		mToviewButton.setOnClickListener(new ToviewButtonListener());

		// It isn't possible to set a fragment's id programmatically so we set a
		// tag instead and
		// search for it using that.
		mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentByTag(MAP_FRAGMENT_TAG);

		// We only create a fragment if it doesn't already exist.
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
		} else {
			// Reincarnated activity. The obtained map is the same map instance
			// in the previous
			// activity life cycle. There is no need to reinitialize it.
			mMap = mapFragment.getMap();
		}

		// We can't be guaranteed that the map is available because Google Play
		// services might
		// not be available.
		setUpMapIfNeeded();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// In case Google Play services has since become available.
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
			getSupportFragmentManager().popBackStack();
			setMainToolViewVisible(true);
		} else {
			super.onBackPressed();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = mapFragment.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				mMap.setOnMarkerClickListener(this);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UWL_CENTER,
						UWL_CENTER_ZOOM));
				mUiSettings = mMap.getUiSettings();
				mUiSettings.setMyLocationButtonEnabled(false);
				mUiSettings.setCompassEnabled(false);
				mUiSettings.setZoomControlsEnabled(false);
			}
		} else {
			mMap.setOnMyLocationButtonClickListener(this);
			mMap.setOnMarkerClickListener(this);
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	@Override
	public void onJSONLoadSuccess(JSONArray result) {
		// TODO Auto-generated method stub
		JSONObject object;
		int pk;
		for (int i = 0, n = result.length(); i < n; i++) {
			try {
				object = result.getJSONObject(i);
				pk = object.getInt(BaseBuilding.Fields.PK.getValue());
				BaseBuilding building = BaseBuilding.newInstance(object, this);

				if (!mBuildings.contains(building))
					mBuildings.add(building);

				// Add markers
				mMarkers.put(pk,
						mMap.addMarker(new MarkerOptions()
								.title(String.valueOf(pk))
								.position(
										new LatLng(building.getLat(), building
												.getLng()))
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.point))));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mMainPagerAdapter = new MainPagerAdapter(this, mBuildings, mPager);
		mPrepared = true;
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		int pk = Integer.parseInt(arg0.getTitle());
		mSlidingUpPanelLayout.showPane();
		mIsSlidingPaneShown = true;
		activateMarker(pk);
		return true;
	}

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		/*
		 * if (!mPrepared) return; if (mActivedPolyLine != null) return; LatLng
		 * start = new LatLng(arg0.getLatitude(), arg0.getLongitude()); Routing
		 * routing = new Routing(Routing.TravelMode.WALKING);
		 * routing.registerListener(this); routing.execute(start,
		 * mMarkers.get(0).getPosition(), mMarkers.get(1) .getPosition(),
		 * mMarkers.get(2).getPosition());
		 */
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingSuccess(PolylineOptions mPolyOptions) {
		// TODO Auto-generated method stub
		if (mActivedPolyLine != null)
			mActivedPolyLine.remove();
		PolylineOptions polyoptions = new PolylineOptions();
		polyoptions.color(Color.HSVToColor(100, new float[] { 200, 1, 1 }));
		polyoptions.width(10);
		polyoptions.addAll(mPolyOptions.getPoints());
		mActivedPolyLine = mMap.addPolyline(polyoptions);

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

	private class SearchEditListener implements OnClickListener {

		private List<? extends Map<String, ?>> generateSearchData() {
			List<Map<String, String>> data = new ArrayList<Map<String, String>>();
			Map<String, String> item = null;
			BaseBuilding temp = null;
			for (int i = 0, n = mBuildings.size(); i < n; i++) {
				temp = mBuildings.get(i);
				item = new HashMap<String, String>();
				item.put(BaseBuilding.Fields.PK.getValue(),
						String.valueOf(temp.getPk()));
				item.put(BaseBuilding.Fields.NAME.getValue(), temp.getName());
				item.put(BaseBuilding.Fields.CATEGPROES.getValue(),
						Utils.stringJoiner(temp.getCategories(), ", "));
				data.add(item);
			}
			return data;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			ListFragment mSearchListFragment = (ListFragment) getSupportFragmentManager()
					.findFragmentByTag(SEARCH_FRAGMENT_TAG);

			if (mSearchListFragment == null)
				mSearchListFragment = new SearchListFragment();

			mSearchListFragment.setListAdapter(new SimpleAdapter(
					getApplicationContext(), generateSearchData(),
					R.layout.search_list_item, new String[] {
							BaseBuilding.Fields.NAME.getValue(),
							BaseBuilding.Fields.CATEGPROES.getValue(),
							BaseBuilding.Fields.PK.getValue() }, new int[] {
							R.id.search_list_item_name,
							R.id.search_list_item_categories,
							R.id.search_list_item_pk }));

			startNewListFragment(mSearchListFragment, SEARCH_FRAGMENT_TAG);
		}

	}

	private class NearbyButtonListener implements OnClickListener {

		private final static String DISTANCE_TAG = "distance";

		private List<? extends Map<String, ?>> generateNearbyData() {
			final Location location = mMap.getMyLocation();
			final LatLng mLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());

			List<Map<String, String>> data = new LinkedList<Map<String, String>>();
			Map<String, String> item = null;
			BaseBuilding temp = null;
			double distance;
			for (int i = 0, n = mBuildings.size(); i < n; i++) {
				temp = mBuildings.get(i);
				distance = SphericalUtil.computeDistanceBetween(mLatLng,
						new LatLng(temp.getLat(), temp.getLng()));

				item = new HashMap<String, String>();
				item.put(BaseBuilding.Fields.PK.getValue(),
						String.valueOf(temp.getPk()));
				item.put(BaseBuilding.Fields.NAME.getValue(), temp.getName());
				item.put(BaseBuilding.Fields.CATEGPROES.getValue(),
						Utils.stringJoiner(temp.getCategories(), ", "));
				item.put(DISTANCE_TAG, String.valueOf(distance));
				data.add(item);
			}
			Collections.sort(data, new Comparator<Map<String, String>>() {
				@Override
				public int compare(Map<String, String> lhs,
						Map<String, String> rhs) {
					// TODO Auto-generated method stub
					double ld = Double.parseDouble(lhs.get(DISTANCE_TAG));
					double rd = Double.parseDouble(rhs.get(DISTANCE_TAG));
					return ld > rd ? 1 : (ld < rd ? -1 : 0);
				}
			});

			// Beatify the distance data
			for (Map<String, String> m : data) {
				m.put(DISTANCE_TAG, Double.valueOf(m.get(DISTANCE_TAG))
						.intValue() + "m");
			}
			return data;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ListFragment mNearbyListFragment = (ListFragment) getSupportFragmentManager()
					.findFragmentByTag(NEARBY_FRAGMENT_TAG);

			if (mNearbyListFragment == null)
				mNearbyListFragment = new NearbyListFragment();

			mNearbyListFragment.setListAdapter(new SimpleAdapter(
					getApplicationContext(), generateNearbyData(),
					R.layout.nearby_list_item, new String[] {
							BaseBuilding.Fields.NAME.getValue(),
							BaseBuilding.Fields.CATEGPROES.getValue(),
							DISTANCE_TAG, BaseBuilding.Fields.PK.getValue() },
					new int[] { R.id.nearby_list_item_name,
							R.id.nearby_list_item_categories,
							R.id.nearby_list_item_distance,
							R.id.nearby_list_item_pk }));

			startNewListFragment(mNearbyListFragment, NEARBY_FRAGMENT_TAG);
		}

	}
	
	private class ToviewButtonListener implements OnClickListener {
		
		

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ToViewListFragment mToViewListFragment = (ToViewListFragment) getSupportFragmentManager()
					.findFragmentByTag(TOVIEW_FRAGMENT_TAG);

			if (mToViewListFragment == null)
				mToViewListFragment = new ToViewListFragment();
			
			mToViewListFragment.setAdapterData(mBuildings);
				
			startNewListFragment(mToViewListFragment, TOVIEW_FRAGMENT_TAG);
		}
		
	}

	@Override
	public void onSearchItemClicked(int pk) {
		// TODO Auto-generated method stub
		if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
			getSupportFragmentManager().popBackStack();
			mSearchEdit.clearFocus();
			mIsSlidingPaneShown = true;
			setMainToolViewVisible(true);
		}
		activateMarker(pk);
	}

	@Override
	public void onMainPagerSelected(int pk) {
		// TODO Auto-generated method stub
		activateMarker(pk);
	}

	private void activateMarker(int pk) {
		if (mActivedMarker != null)
			mActivedMarker.setIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.point));
		Marker marker = mMarkers.get(pk);
		marker.setIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location));
		mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
		mActivedMarker = marker;

		mMainPagerAdapter.setCurrentItemByPk(pk);
	}

	private void setMainToolViewVisible(boolean b) {
		if (b) {
			mTitleBar.setVisibility(View.VISIBLE);
			if (mIsSlidingPaneShown)
				mSlidingUpPanelLayout.showPane();
		} else {
			mTitleBar.setVisibility(View.GONE);
			mSlidingUpPanelLayout.collapsePane();
			mSlidingUpPanelLayout.hidePane();
		}
	}

	private void startNewListFragment(ListFragment mListFragment, String tag) {
		if (!mPrepared) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Loading data ...", Toast.LENGTH_SHORT);
			toast.show();
		} else {
			setMainToolViewVisible(false);

			FragmentTransaction fragmentTransaction = getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.replace(R.id.main_content, mListFragment, tag);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}
}
