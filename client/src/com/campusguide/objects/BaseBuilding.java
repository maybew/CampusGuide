package com.campusguide.objects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class BaseBuilding {
	private static final List<String> allCategories = new ArrayList<String>();
	protected int pk;
	protected String name;
	protected List<String> categories;
	protected String address;
	protected double lat;
	protected double lng;
	protected String description;
	protected List<Image> images;
	protected String openTime;
	protected String occupant;
	protected String event;

	public enum Fields {
		PK("pk"), NAME("name"), CATEGPROES("categories"), ADDRESS("address"), LAT(
				"lat"), LNG("lng"), DESC("desc"), IMAGES("images"), OPENTIME(
				"open_time"), OCCUPANT("occupant"), EVENT("event");

		protected String _sValue;

		private Fields(String sValue) {
			this._sValue = sValue;
		}

		public String getValue() {
			return _sValue;
		}
	}

	public static BaseBuilding newInstance(JSONObject object, Context context) {
		BaseBuilding b = null;
		try {
			JSONObject fields = object.getJSONObject("fields");
			b = new BaseBuilding(object.getInt(Fields.PK.getValue()),
					fields.getString(Fields.NAME.getValue()),
					BaseBuilding.newStringList(fields
							.getJSONArray(Fields.CATEGPROES.getValue())),
					fields.getString(Fields.ADDRESS.getValue()),
					fields.getDouble(Fields.LAT.getValue()),
					fields.getDouble(Fields.LNG.getValue()),
					fields.getString(Fields.DESC.getValue()),
					fields.getString(Fields.OCCUPANT.getValue()),
					fields.getString(Fields.EVENT.getValue()),
					Image.newInstanceList(
							fields.getJSONArray(Fields.IMAGES.getValue()),
							context), fields.getString(Fields.OPENTIME
							.getValue()));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	public static List<String> getAllCategories() {
		return allCategories;
	}

	private BaseBuilding(int pk, String name, List<String> categories,
			String address, double lat, double lng, String description, String occupant, String event,
			List<Image> images, String openTime) {
		this.pk = pk;
		this.name = name;
		this.categories = categories;
		this.address = address;
		this.lat = lat;
		this.lng = lng;
		this.description = description;
		this.images = images;
		this.openTime = openTime;
		this.occupant = occupant;
		this.event = event;
		for (String s : categories) {
			if (!allCategories.contains(s))
				allCategories.add(s);
		}
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

	public int getPk() {
		return pk;
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
		for (int i = 0, n = images.size(); i < n; i++) {
			urls.add(images.get(i).getUrl());
		}
		return urls;
	}

	public ArrayList<String> getImagesDesc() {
		ArrayList<String> desc = new ArrayList<String>();
		for (int i = 0, n = images.size(); i < n; i++) {
			desc.add(images.get(i).getDescription());
		}
		return desc;
	}

	public String getOpenTime() {
		return openTime;
	}
	
	public String getOccupant() {
		return occupant;
	}
	
	public String getEvent() {
		return event;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return ((BaseBuilding) o).pk == pk;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return pk;
	}
}
