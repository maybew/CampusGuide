package com.campusguide.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.campusguide.R;

public class SearchListFragment extends ListFragment implements TextWatcher {
	public interface OnSearchItemClickedListener {
		public void onSearchItemClicked(int pk);
	}

	OnSearchItemClickedListener mListener;
	EditText mEditText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_search, container,
				false);
		mEditText = (EditText) rootView.findViewById(R.id.fragment_search_edit);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		getListView().setTextFilterEnabled(true);
		mEditText.addTextChangedListener(this);
		mEditText.requestFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
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
				.findViewById(R.id.search_list_item_pk)).getText().toString();
		final int pk = Integer.parseInt(pkString);
		mEditText.clearFocus();
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), 0);
		mListener.onSearchItemClicked(pk);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		ListView mListView = getListView();
		if (TextUtils.isEmpty(s.toString())) {
			mListView.clearTextFilter();
		} else {
			mListView.setFilterText(s.toString());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

}
