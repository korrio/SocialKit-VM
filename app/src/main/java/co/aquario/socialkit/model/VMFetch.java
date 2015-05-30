/**
 * ****************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package co.aquario.socialkit.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import co.aquario.socialkit.activity.VMActivity;
import co.aquario.socialkit.connections.RetrofitHttpClient;

public class VMFetch {

    public static final String TAG = "DribbbleFetch";


    public void load(final Activity caller, int itemsPerPage, int page) {

        String myurl = "http://api.vdomax.com/search/photo?sort=V&page=" + page;

        new AsyncTask<String, Void, String>() {

            RetrofitHttpClient client = new RetrofitHttpClient();

            private Exception exception;

            protected String doInBackground(String... urls) {
                try {
                    return get(new URL(urls[0]));

                } catch (Exception e) {
                    this.exception = e;
                    Log.e(TAG, "Exception: " + e);
                    return null;
                }
            }

            protected void onPostExecute(String data) {
                Log.e("mydata", data);
                VMFeed feed = new Gson().fromJson(data, VMFeed.class);
                ((VMActivity) caller).onDataLoaded(feed);
            }

            String get(URL url) throws IOException {

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url.toString());

                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    return EntityUtils.toString(r_entity);
                } else {
                    return "Error occurred! Http Status Code: "
                            + statusCode;
                }

				/*
                HttpURLConnection connection = client.open(url);
				InputStream in = null;
				try {
					in = connection.getInputStream();
					byte[] response = readFully(in);
					return new String(response, "UTF-8");
				} finally {
					if (in != null)
						in.close();
				}
				*/
            }

            byte[] readFully(InputStream in) throws IOException {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int count; (count = in.read(buffer)) != -1; ) {
                    out.write(buffer, 0, count);
                }
                return out.toByteArray();
            }

        }
                //.execute("http://api.vdomax.com/posts/photo?per_page=50&type=photo&page=1&sort=V&page="+page);
                .execute(myurl);
    }


}
