package com.campusguide.objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class BaseBuilding {
	protected String name;
	protected List<String> categories;
	protected String address;
	protected double lat;
	protected double lng;
	protected String description;
	protected List<Image> images;
	protected String openTime;

	public static BaseBuilding newInstance(JSONObject object, Context context) {
		BaseBuilding b = null;
		try {
			b = new BaseBuilding(object.getString("name"),
					BaseBuilding.newStringList(object
							.getJSONArray("categories")),
					object.getString("address"), object.getDouble("lat"),
					object.getDouble("lng"), object.getString("desc"),
					Image.newInstanceList(object.getJSONArray("images"),
							context), object.getString("open_time"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	private BaseBuilding(String name, List<String> categories, String address,
			double lat, double lng, String description, List<Image> images,
			String openTime) {
		this.name = name;
		this.categories = categories;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.description = description;
		this.images = images;
		this.openTime = openTime;
	}

	public static List<String> newStringList(JSONArray array) {
		List<String> list = new ArrayList<String>();
		for (int i = 0, n = array.length(); i < n; i++) {
			try {
				list.add(array.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	public String getName() {
		return name;
	}

	public List<String> getCategories() {
		return categories;
	}

	public String getAddress() {
		return address;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}

	public String getDescription() {
		return description;
	}

	public List<Image> getImages() {
		return images;
	}
	
	public ArrayList<String> getURLs() {
		ArrayList<String> urls = new ArrayList<String>();
		for(int i=0,n=images.size();i<n;i++) {
			urls.add(images.get(i).getUrl());
		}
		return urls;
	}
	
	public ArrayList<String> getImagesDesc() {
		ArrayList<String> desc = new ArrayList<String>();
		for(int i=0,n=images.size();i<n;i++) {
			desc.add(images.get(i).getDescription());
		}
		return desc;
	}

	public String getOpenTime() {
		return openTime;
	}
}
