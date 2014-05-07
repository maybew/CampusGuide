package com.campusguide.utilities;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;

public abstract class BaseLoader<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {
	private final static String BASE_URL = "http://10.110.131.15:8000";// "http://96.42.60.165:8000";//"http://10.0.3.2:8000";//"http://192.168.43.196:8000";

	private HttpClient httpClient;

	public BaseLoader() {

		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 2000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSo = 3000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSo);

		this.httpClient = new DefaultHttpClient(httpParameters);
	}

	protected InputStream getInputStream(String url) throws IOException {
		HttpGet httpRequest = new HttpGet(BASE_URL + url);
		HttpResponse response = httpClient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}

}