package co.aquario.socialkit.util;

import android.content.Context;

import co.aquario.socialkit.VMApplication;

/**
 * Created by Mac on 4/3/15.
 */
public class PrefUtils {
    public static long get(Context context, String key, long value) {
        return VMApplication.get(context).getPrefManager().resumePosition().getOr(value);
    }
    public static void save(Context context, String key, long value) {
        VMApplication.get(context).getPrefManager().resumePosition().put(value).commit();
    }
}
