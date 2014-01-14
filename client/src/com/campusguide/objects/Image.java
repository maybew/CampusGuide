package com.campusguide.objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.ImageView;

public class Image {
	protected String description;
	protected String url;
	protected ImageView image;
	
	public static List<Image> newInstanceList(JSONArray array, Context context) {
		List<Image> list = new ArrayList<Image>();
		for(int i=0,n=array.length();i<n;i++) {
			try {
				list.add(Image.newInstance(array.getJSONObject(i), context));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public static Image newInstance(JSONObject object, Context context) {
		Image i = null;
		try {
			i = new Image(object.getString("desc"), object.getString("url"), context);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	
	private Image(String description, String url, Context context) {
		this.description = description;
		this.url = url;
		this.image = new ImageView(context);
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public ImageView getImageView() {
		return image;
	}
}
