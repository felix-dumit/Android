package br.unicamp.busfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

public class ServerOperations {

	public void function() {

		Log.d("onClick", "ok");

		StringBuilder builder = new StringBuilder();

		HttpGet get = new HttpGet("http://mc933.lab.ic.unicamp.br:8010");

		HttpClient client = new DefaultHttpClient();

		try {

			HttpResponse response = client.execute(get);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e("ERRRO", "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d("RESP:", builder.toString());

		/*
		 * try { JSONObject json = new JSONObject(builder.toString());
		 * Log.d("JSON:",json.toString()); Log.d("JLAT:",json.getString("lat"));
		 * 
		 * 
		 * } catch (JSONException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		try {
			JSONArray jsonArray = new JSONArray(builder.toString());

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Log.i("JPARSE:", jsonObject.getString("time"));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
