package co.aquario.socialkit.util;

import android.widget.Toast;

import co.aquario.socialkit.MainApplication;

/**
 * Created by ravikumar on 10/20/2014.
 */
public class AppUtils {

    public static void showToast(String iMessage) {
        Toast.makeText(MainApplication.getAppContext(), iMessage, Toast.LENGTH_SHORT).show();
    }
}
