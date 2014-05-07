package com.campusguide.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader extends BaseLoader<String, Void, Bitmap>{
	private static final Map<String, Bitmap> CACHE = new HashMap<String, Bitmap>();
	
	private ImageView mImageView;
	
	public ImageLoader(ImageView mImageView) {
		this.mImageView = mImageView;
	}
	
	@Override
	protected InputStream getInputStream(String url) throws IOException {
		// TODO Auto-generated method stub
		return super.getInputStream(url);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub
		Bitmap bitmap = CACHE.get(params[0]);
		if(bitmap != null)
			return bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(getInputStream(params[0]));
			saveImageToSD(params[0], bitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("sd image", params[0]);
			bitmap = readImageFromSD(params[0]);
		}
		CACHE.put(params[0], bitmap);
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(mImageView != null)
			mImageView.setImageBitmap(result);
	}
	
	private void saveImageToSD(String url, Bitmap bitmap) {
		try {
			File newFolder = new File(Environment.getExternalStorageDirectory(), "CampusGuidCache/images");
			if(!newFolder.exists())
				newFolder.mkdir();
			File file = new File(newFolder, parseUrl(url));
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Bitmap readImageFromSD(String url) {
		try {
			File newFolder = new File(Environment.getExternalStorageDirectory(), "CampusGuidCache/images");
			File file = new File(newFolder, parseUrl(url));
			Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}
	
	private String parseUrl(String url) {
		int index = url.lastIndexOf("/");
		return url.substring(index+1);
	}
}
