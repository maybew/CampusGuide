package com.campusguide.fragments;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.campusguide.R;
import com.campusguide.fragments.SearchListFragment.OnSearchItemClickedListener;
import com.campusguide.objects.BaseBuilding;

public class NearbyListFragment extends ListFragment implements
		OnItemSelectedListener {
	final static String ALL_SPINNER = "All";

	OnSearchItemClickedListener mListener;
	Spinner spinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_nearby, container,
				false);
		spinner = (Spinner) rootView.findViewById(R.id.nearby_fragment_spinner);
		List<String> categories = BaseBuilding.getAllCategories();
		if (!categories.contains(ALL_SPINNER))
			categories.add(0, ALL_SPINNER);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, categories);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		getListView().setTextFilterEnabled(true);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mListener = (OnSearchItemClickedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnSearchItemClickedListener");
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		final String pkString = ((TextView) v
				.findViewById(R.id.nearby_list_item_pk)).getText().toString();
		final int pk = Integer.parseInt(pkString);
		mListener.onSearchItemClicked(pk);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		ListView mListView = getListView();
		String text = ((TextView) arg1).getText().toString();
		if (ALL_SPINNER.equals(text)) {
			mListView.clearTextFilter();
		} else {
			mListView.setFilterText(text);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
