package co.aquario.chatapp.picker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Mac on 7/8/15.
 */
public class MusicPickerIntent extends Intent {
    private MusicPickerIntent() {
    }

    private MusicPickerIntent(Intent o) {
        super(o);
    }

    private MusicPickerIntent(String action) {
        super(action);
    }

    private MusicPickerIntent(String action, Uri uri) {
        super(action, uri);
    }

    private MusicPickerIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }

    public MusicPickerIntent(Context packageContext) {
        super(packageContext, MusicPickerActivity.class);
    }
}
