package co.aquario.socialkit.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

import co.aquario.socialkit.VMApp;

public class URLImageParser implements Html.ImageGetter {
    Context c;
    TextView container;
    int size;

    /***
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     * @param t
     * @param c
     */
    public URLImageParser(View t, Context c,int size) {
        this.c = c;
        this.container = (TextView) t;
        this.size = size;
    }

    public Drawable getDrawable(String source) {
        URLDrawable urlDrawable = new URLDrawable();

        // get the actual source
        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask( urlDrawable);

        asyncTask.execute(source);

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable d) {
            this.urlDrawable = d;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {
            // set the correct bound according to the result from HTTP call
            if(result != null) {
                urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth() * size, 0
                        + result.getIntrinsicHeight() * size);

                // change the reference of the current drawable to the result
                // from the HTTP call
                urlDrawable.drawable = result;

                // redraw the image by invalidating the container
                URLImageParser.this.container.invalidate();

                // For ICS
                URLImageParser.this.container.setHeight((URLImageParser.this.container.getHeight()
                        + result.getIntrinsicHeight()));

                // Pre ICS
                URLImageParser.this.container.setEllipsize(null);
            }



        }

        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        public Drawable fetchDrawable(String urlString) {

            Log.v("tattooUrl",urlString);

            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth() * size, 0
                        + drawable.getIntrinsicHeight() * size);
                return drawable;
            } catch (Exception e) {
                return null;
            }
        }

        private InputStream fetch(String urlString) throws  IOException {
            OkHttpClient client = VMApp.getHttpClient();
            Request request = new Request.Builder().url(urlString).build();
            Response response = client.newCall(request).execute();

            return response.body().byteStream();
 
            /*
            DefaultHttpClient httpClient = toolbar DefaultHttpClient();
            HttpGet request = toolbar HttpGet(urlString);
            HttpResponse response = httpClient.execute(request);
            return response.getEntity().getContent();
            */
        }
    }
}