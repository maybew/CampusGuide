package com.campusguide.fragments;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.campusguide.R;
import com.campusguide.activities.ImagePagerActivity;
import com.campusguide.activities.Constants.Extra;
import com.campusguide.objects.BaseBuilding;
import com.campusguide.utilities.ImageLoader;
import com.campusguide.utilities.Utils;

public class MainPagerFragment extends Fragment implements View.OnClickListener {

	public interface MainPagerListener {
		public void onAddToViewClicked(int pk);

		public void onGoClicked(int pk);

		public void onToViewListClicked();
	}

	private int pk;
	private String mName;
	private String mCategories;
	private String mOpenTime;
	private String mAddress;
	private List<String> mPhotos;
	private List<String> mPhotosDesc;
	private String mDescription;
	private String mOccupant;
	private String mEvent;

	private MainPagerListener mListener;

	public static MainPagerFragment newInstance(BaseBuilding b) {
		MainPagerFragment f = new MainPagerFragment();

		Bundle args = new Bundle();
		args.putInt("pk", b.getPk());
		args.putString("name", b.getName());
		args.putString("categories",
				Utils.stringJoiner(b.getCategories(), ", "));
		args.putString("opentime", b.getOpenTime());
		args.putString("address", b.getAddress());
		args.putStringArrayList("photos", b.getURLs());
		args.putStringArrayList("photos_desc", b.getImagesDesc());
		args.putString("description", b.getDescription());
		args.putString("occupant", b.getOccupant());
		args.putString("event", b.getEvent());
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		pk = getArguments() != null ? getArguments().getInt("pk") : 0;
		mName = getArguments() != null ? getArguments().getString("name") : "";
		mCategories = getArguments() != null ? getArguments().getString(
				"categories") : "";
		mOpenTime = getArguments() != null ? getArguments().getString(
				"opentime") : "";
		mAddress = getArguments() != null ? getArguments().getString("address")
				: "";
		mPhotos = getArguments() != null ? getArguments().getStringArrayList(
				"photos") : null;
		mPhotosDesc = getArguments() != null ? getArguments()
				.getStringArrayList("photos_desc") : null;
		mDescription = getArguments() != null ? getArguments().getString(
				"description") : "";
		mOccupant = getArguments() != null ? getArguments().getString(
				"occupant") : "";
		mEvent = getArguments() != null ? getArguments().getString(
				"event") : "";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setRetainInstance(true);
		View view = inflater.inflate(R.layout.fragment_main_pager, container,
				false);
		TextView nameTv = (TextView) view.findViewById(R.id.main_pager_name);
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
		photoTitleTv.setOnClickListener(this);

		LinearLayout mAddBtn = (LinearLayout) view
				.findViewById(R.id.fragment_main_pager_toview_add);
		mAddBtn.setOnClickListener(this);
		LinearLayout mToViewBtn = (LinearLayout) view
				.findViewById(R.id.fragment_main_pager_toview_list);
		mToViewBtn.setOnClickListener(this);
		ImageButton mGoBtn = (ImageButton) view
				.findViewById(R.id.main_pager_button);
		mGoBtn.setOnClickListener(this);

		TextView descriptionTv = (TextView) view
				.findViewById(R.id.main_pager_description);
		descriptionTv.setText(mDescription);

		if ("".equals(mOccupant)) {
			LinearLayout occupantFrame = (LinearLayout) view
					.findViewById(R.id.main_pager_occpant_frame);
			occupantFrame.setVisibility(View.GONE);
		} else {
			TextView occupantTv = (TextView) view
					.findViewById(R.id.main_pager_occpant);
			occupantTv.setText(mOccupant);
		}
		
		if ("".equals(mEvent)) {
			LinearLayout eventFrame = (LinearLayout) view
					.findViewById(R.id.main_pager_event_frame);
			eventFrame.setVisibility(View.GONE);
		} else {
			TextView eventTv = (TextView) view
					.findViewById(R.id.main_pager_event);
			eventTv.setText(mEvent);
		}
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mListener = (MainPagerListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement MainPagerListener");
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.main_pager_photos_title:
			Intent intent = new Intent(getActivity(), ImagePagerActivity.class);
			intent.putExtra(Extra.IMAGES,
					mPhotos.toArray(new String[mPhotos.size()]));
			intent.putExtra(Extra.IMAGES_DESC,
					mPhotosDesc.toArray(new String[mPhotosDesc.size()]));
			startActivity(intent);
			break;
		case R.id.fragment_main_pager_toview_add:
			mListener.onAddToViewClicked(pk);
			break;
		case R.id.fragment_main_pager_toview_list:
			mListener.onToViewListClicked();
			break;
		case R.id.main_pager_button:
			mListener.onGoClicked(pk);
			break;
		}
	}
}
