package com.campusguide.activities;

import com.campusguide.R;
import com.campusguide.activities.Constants.Extra;
import com.campusguide.utilities.ImageLoader;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends FragmentActivity {

	private static final String STATE_POSITION = "STATE_POSITION";

	ViewPager pager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_pager);

		Bundle bundle = getIntent().getExtras();
		String[] imageUrls = bundle.getStringArray(Extra.IMAGES);
		String[] imageDescs = bundle.getStringArray(Extra.IMAGES_DESC);
		int pagerPosition = 0;

		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls, imageDescs));
		pager.setCurrentItem(pagerPosition);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private String[] descs;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images, String[] descs) {
			this.images = images;
			this.descs = descs;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image,
					view, false);
			ImageView imageView = (ImageView) imageLayout
					.findViewById(R.id.image);
			TextView descView = (TextView) imageLayout.findViewById(R.id.description);

			/*
			 * imageLoader.displayImage(images[position], imageView, options,
			 * new SimpleImageLoadingListener() {
			 * 
			 * @Override public void onLoadingStarted(String imageUri, View
			 * view) { spinner.setVisibility(View.VISIBLE); }
			 * 
			 * @Override public void onLoadingFailed(String imageUri, View view,
			 * FailReason failReason) { String message = null; switch
			 * (failReason.getType()) { case IO_ERROR: message =
			 * "Input/Output error"; break; case DECODING_ERROR: message =
			 * "Image can't be decoded"; break; case NETWORK_DENIED: message =
			 * "Downloads are denied"; break; case OUT_OF_MEMORY: message =
			 * "Out Of Memory error"; break; case UNKNOWN: message =
			 * "Unknown error"; break; } Toast.makeText(ImagePagerActivity.this,
			 * message, Toast.LENGTH_SHORT).show();
			 * 
			 * spinner.setVisibility(View.GONE); }
			 * 
			 * @Override public void onLoadingComplete(String imageUri, View
			 * view, Bitmap loadedImage) { spinner.setVisibility(View.GONE); }
			 * });
			 */
			
			new ImageLoader(imageView).execute(images[position]);
			descView.setText(descs[position]);
			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
}