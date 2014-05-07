package com.campusguide.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.campusguide.R;
import com.campusguide.fragments.SearchListFragment.OnSearchItemClickedListener;
import com.campusguide.objects.BaseBuilding;
import com.campusguide.utilities.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ToViewListFragment extends ListFragment {
	/**
	 * Listener, the activity call this fragment need to implement this listener.
	 * @author Boheng
	 *
	 */
	public interface ToViewListFragmentListener {
		public void OnGoAllClicked(List<BaseBuilding> toViewList);
	}

	OnSearchItemClickedListener mListener;
	ToViewListFragmentListener mGoListener;
	List<BaseBuilding> toViewList;

	private void setupAdapter() {
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Map<String, String> item = null;
		BaseBuilding temp = null;
		for (int i = 0, n = toViewList.size(); i < n; i++) {
			temp = toViewList.get(i);
			item = new HashMap<String, String>();
			item.put(BaseBuilding.Fields.PK.getValue(),
					String.valueOf(temp.getPk()));
			item.put(BaseBuilding.Fields.NAME.getValue(), temp.getName());
			item.put(BaseBuilding.Fields.CATEGPROES.getValue(),
					Utils.stringJoiner(temp.getCategories(), ", "));
			data.add(item);
		}
		this.setListAdapter(new ToViewSimpleAdapter(this, getActivity(),
				data, R.layout.toview_list_item, new String[] {
						BaseBuilding.Fields.NAME.getValue(),
						BaseBuilding.Fields.CATEGPROES.getValue(),
						BaseBuilding.Fields.PK.getValue() }, new int[] {
						R.id.toview_list_item_name,
						R.id.toview_list_item_categories,
						R.id.toview_list_item_pk }));
	}

	public void setAdapterData(List<BaseBuilding> toViewList) {
		this.toViewList = toViewList;
	}

	public void removeToViewItem(int pk) {
		for (int i = 0, n = toViewList.size(); i < n; i++) {
			if (toViewList.get(i).getPk() == pk) {
				toViewList.remove(i);
				return;
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_toview, container,
				false);
		LinearLayout mGo = (LinearLayout) rootView
				.findViewById(R.id.fragment_toview_go);
		mGo.setOnClickListener(new ToViewGoListener());
		LinearLayout mRemoveAll = (LinearLayout) rootView
				.findViewById(R.id.fragment_toview_remove_all);
		mRemoveAll.setOnClickListener(new ToViewRemoveAllListener());
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		setupAdapter();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mListener = (OnSearchItemClickedListener) activity;
			mGoListener = (ToViewListFragmentListener) activity;
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
				.findViewById(R.id.toview_list_item_pk)).getText().toString();
		final int pk = Integer.parseInt(pkString);
		mListener.onSearchItemClicked(pk);
	}

	private class ToViewGoListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mGoListener.OnGoAllClicked(toViewList);
		}
	}

	private class ToViewRemoveAllListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			toViewList.clear();
			setupAdapter();
		}
	}

	public static class ToViewSimpleAdapter extends SimpleAdapter implements
			OnClickListener {
		ToViewListFragment mToViewListFragment;
		List<? extends Map<String, ?>> data;

		public ToViewSimpleAdapter(ToViewListFragment fragment,
				Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.data = data;
			mToViewListFragment = fragment;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View rootView = super.getView(position, convertView, parent);
			ImageButton remove = (ImageButton) rootView
					.findViewById(R.id.toview_list_item_remove);
			String pk = ((TextView) rootView
					.findViewById(R.id.toview_list_item_pk)).getText()
					.toString();
			remove.setOnClickListener(new OnRemoveClickListener(position,
					Integer.parseInt(pk)));
			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String pk = ((ImageButton) v).getContentDescription().toString();
			data.remove(Integer.parseInt(pk));
			this.notifyDataSetChanged();
		}

		private class OnRemoveClickListener implements OnClickListener {
			private int position, pk;

			private OnRemoveClickListener(int position, int pk) {
				this.position = position;
				this.pk = pk;
			}

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				data.remove(position);
				mToViewListFragment.removeToViewItem(pk);
				notifyDataSetChanged();
			}
		}
	}
}
