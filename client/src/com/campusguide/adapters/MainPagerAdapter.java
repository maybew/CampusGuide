package com.campusguide.adapters;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.campusguide.fragments.MainPagerFragment;
import com.campusguide.objects.BaseBuilding;

public class MainPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{
	public interface OnMainPagerSelectedListener {
		public void onMainPagerSelected(int pk);
	}
	
	private final List<BaseBuilding> mBuildings;
	private final ViewPager mPager;
	private final OnMainPagerSelectedListener l;

	public MainPagerAdapter(FragmentActivity activity, List<BaseBuilding> buildings, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mBuildings = buildings;
		mPager = pager;
		mPager.setAdapter(this);
		mPager.setOnPageChangeListener(this);
		try {
			l = (OnMainPagerSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnMainPagerSelectedListener");
		}
	}
	
	public void setCurrentItemByPk(int pk) {
		for(int i=0,n=mBuildings.size();i<n;i++) {
			if(pk == mBuildings.get(i).getPk())
				mPager.setCurrentItem(i);
		}
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return MainPagerFragment.newInstance(mBuildings.get(arg0));
	}

	@Override
	public int getCount() { // TODO Auto-generated method stub
		return mBuildings.size();
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
		l.onMainPagerSelected(mBuildings.get(arg0).getPk());
	}

}