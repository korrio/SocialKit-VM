package co.aquario.socialkit.util;

import android.content.Context;

import co.aquario.socialkit.MainApplication;

/**
 * Created by Mac on 4/3/15.
 */
public class PrefUtils {
    public static long get(Context context, String key, long value) {
        return MainApplication.get(context).getPrefManager().resumePosition().getOr(value);
    }
    public static void save(Context context, String key, long value) {
        MainApplication.get(context).getPrefManager().resumePosition().put(value).commit();
    }
}
