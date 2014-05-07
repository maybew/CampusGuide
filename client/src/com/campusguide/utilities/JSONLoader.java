package com.campusguide.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Environment;

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

	protected JSONArray getJSONArrayFromUrl (String url) throws JSONException {
		try {
		InputStream is = getInputStream(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}

			saveToSD(builder.toString());
			return new JSONArray(builder.toString());
		} catch (Exception e) {
			return new JSONArray(readFromSD());
		}
	}
	
	private String readFromSD() {
		try {
			File newFolder = new File(Environment.getExternalStorageDirectory(), "CampusGuidCache");
			File file = new File(newFolder, "cache");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader( new InputStreamReader(fis));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			br.close();
			return builder.toString();
		} catch(Exception e) {
			return "";
		}
	}
	
	private void saveToSD(String jsonString) {
		try {
			File newFolder = new File(Environment.getExternalStorageDirectory(), "CampusGuidCache");
			if(!newFolder.exists())
				newFolder.mkdir();
			File file = new File(newFolder, "cache");
			file.createNewFile();
			FileOutputStream fout = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fout);
			osw.write(jsonString);
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
