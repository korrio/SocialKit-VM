package co.aquario.chatapp.picker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Mac on 7/8/15.
 */
public class YoutubePickerIntent extends Intent {
    private YoutubePickerIntent() {
    }

    private YoutubePickerIntent(Intent o) {
        super(o);
    }

    private YoutubePickerIntent(String action) {
        super(action);
    }

    private YoutubePickerIntent(String action, Uri uri) {
        super(action, uri);
    }

    private YoutubePickerIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public YoutubePickerIntent(Context packageContext) {
        super(packageContext, YoutubePickerActivity.class);
    }
}
