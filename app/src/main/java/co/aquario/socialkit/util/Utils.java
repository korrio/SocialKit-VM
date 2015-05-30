package co.aquario.socialkit.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class Utils {

    public static char[] alphabets = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static String emoticonize(String oldText) {
        String newText;

        newText = oldText.replace("ā", ":tt0101:").replace("Ă", ":tt0102:")
                .replace("ă", ":tt0103:").replace("Ą", ":tt0104:").replace("ą", ":tt0105:")
                .replace("Ć", ":tt0106:").replace("ć", ":tt0107:").replace("ԁ", ":tt0501:")
                .replace("Ԃ", ":tt0502:").replace("ԃ", ":tt0503:").replace("Ԅ", ":tt0504:")
                .replace("ԅ", ":tt0505:").replace("Ԇ", ":tt0506:").replace("ԇ", ":tt0507:")
                .replace("Ԉ", ":tt0509:").replace("́", ":tt0301:").replace("̂", ":tt0302:")
                .replace("̃", ":tt0303:").replace("̄", ":tt0304:").replace("̅", ":tt0305:")
                .replace("̆", ":tt0306:").replace("̇", ":tt0307:").replace("Ё", ":tt0401:")
                .replace("Ђ", ":tt0402:").replace("Ѓ", ":tt0403:").replace("Є", ":tt0404:")
                .replace("Ѕ", ":tt0405:").replace("І", ":tt0406:").replace("Ї", ":tt0407:")
                .replace("ȁ", ":tt0201:").replace("Ȃ", ":tt0202:").replace("ȃ", ":tt0203:")
                .replace("Ȅ", ":tt0204:").replace("ȅ", ":tt0205:").replace("Ȇ", ":tt0206:")
                .replace("ȇ", ":tt0207:").replace("Ȉ", ":tt0208:").replace("ȉ", ":tt0209:")
                .replace("Ȑ", ":tt0210:").replace("Ј", ":tt0408:").replace("Љ", ":tt0409:")
                .replace("А", ":tt0410:").replace("̈", ":tt0308:").replace("̉", ":tt0309:")
                .replace("̐", ":tt0310:").replace("ԉ", ":tt0509:").replace("Ԑ", ":tt0510:")
                .replace("ć", ":tt0107:").replace("Ĉ", ":tt0108:").replace("ĉ", ":tt0109:")
                .replace("Đ", ":tt0110:")
        ;


        return newText;

    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (dm.densityDpi
                / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    /** Get the current Android API level. */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /** Determine if the device is running API level 8 or higher. */
    public static boolean isFroyo() {
        return getSdkVersion() >= Build.VERSION_CODES.FROYO;
    }

    /** Determine if the device is running API level 11 or higher. */
    public static boolean isHoneycomb() {
        return getSdkVersion() >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * Determine if the device is a tablet (i.e. it has a large screen).
     *
     * @param context The calling context.
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Determine if the device is a HoneyComb tablet.
     *
     * @param context The calling context.
     */
    public static boolean isHoneycombTablet(Context context) {
        return isHoneycomb() && isTablet(context);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null &&
                activity.getCurrentFocus() != null &&
                activity.getCurrentFocus().getWindowToken() != null) {
            InputMethodManager inputManager = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public enum Sort {
        N, // Newest
        F, // Most Follower
        A // Alphabetica
    }

}
