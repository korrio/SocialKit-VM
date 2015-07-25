package co.aquario.socialkit.handler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.event.SomeEvent;
import co.aquario.socialkit.event.upload.ClipPostUploadEvent;
import co.aquario.socialkit.model.UploadPostCallback;
import co.aquario.socialkit.util.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class PostUploadHandler {

    private Context context;
    private PostUploadService api;
    private ApiBus apiBus;

    public PostUploadHandler(Context context, PostUploadService api,
                             ApiBus apiBus) {

        this.context = context;
        this.api = api;
        this.apiBus = apiBus;
    }

    public void registerForEvents() {
        apiBus.register(this);
    }

    @Subscribe public void onClipPostUploadEvent(ClipPostUploadEvent event) {

        final Intent intent = new Intent(context, MainActivity.class);

        api.uploadPostClip(event.text, event.fromUserId, event.toUserId, event.file, new retrofit.Callback<UploadPostCallback>() {
            @Override
            public void success(UploadPostCallback uploadCallback, Response response) {
                if (uploadCallback.status == 200) {
                    Utils.notify(context, "Upload completed", "Upload service", "Upload video completed", intent);
                } else {
                    Utils.notify(context, "Upload completed", "Upload service", "Upload video failed, please try again", intent);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Utils.notify(context, "Upload error", "Upload service", error.getMessage(),intent);
            }
        });
    }

    @Subscribe public void onSomeEvent(SomeEvent event) {
        Log.e("HEY2!","SomeEvent");

        Map<String, String> options = new HashMap<String, String>();
        options.put("key1", event.getVar1());
        options.put("key2", Integer.toString(event.getVar2()));

    /*
        api.getRandomImage2(options,toolbar Callback<SomeData>() {
            @Override
            public void success(SomeData randomImage, Response response) {
                apiBus.post(toolbar SuccessEvent(randomImage));
            }

            @Override
            public void failure(RetrofitError error) {
                apiBus.post(toolbar FailedEvent());
            }
        });
        */
    }
}
