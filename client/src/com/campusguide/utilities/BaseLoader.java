package com.campusguide.utilities;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public abstract class BaseLoader<Params, Progress, Result> extends
		AsyncTask<Params, Progress, Result> {
	private final static String BASE_URL = "http://96.42.50.149:8000";

	private HttpClient httpClient;

	public BaseLoader() {
		this.httpClient = new DefaultHttpClient();
	}

	protected InputStream getInputStream(String url) throws IOException {
		HttpGet httpRequest = new HttpGet(BASE_URL + url);
		HttpResponse response = httpClient.execute(httpRequest);
		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}

}
