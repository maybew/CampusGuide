package com.campusguide.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONLoader extends BaseLoader<String, Void, JSONArray> {
	private OnJSONLoadListener listener;
	
	public JSONLoader(OnJSONLoadListener listener) {
		this.listener = listener;
	}
	
	public JSONLoader() {
		this.listener = null;
	}
	
	public void setOnJSONLoadListener(OnJSONLoadListener listener) {
		this.listener = listener;
	}

	protected JSONArray getJSONArrayFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = getInputStream(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

		return new JSONArray(builder.toString());
	}

	@Override
	protected JSONArray doInBackground(String... params) {
		// TODO Auto-generated method stub
try {
	Thread.sleep(3000);
} catch (InterruptedException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}
		try {
			return getJSONArrayFromUrl(params[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(JSONArray result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(listener != null)
			listener.onJSONLoadSuccess(result);
	}

	public interface OnJSONLoadListener {
		void onJSONLoadSuccess(JSONArray result);
	}
}
