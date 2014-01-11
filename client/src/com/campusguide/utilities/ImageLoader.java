package com.campusguide.utilities;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader extends BaseLoader<String, Void, Bitmap>{
	private final static String MEDIA_URL = "media/";
	
	private ImageView mImageView;
	
	public ImageLoader(ImageView mImageView) {
		this.mImageView = mImageView;
	}
	
	@Override
	protected InputStream getInputStream(String url) throws IOException {
		// TODO Auto-generated method stub
		return super.getInputStream(MEDIA_URL+url);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getInputStream(params[0]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mImageView != null)
			mImageView.setImageBitmap(result);
	}
}
