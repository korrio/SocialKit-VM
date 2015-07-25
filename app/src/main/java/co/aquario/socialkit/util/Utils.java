package co.aquario.socialkit.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class Utils {

    public static char[] alphabets = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }

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

    public static Spannable spanMyText(String text,TextView tv) {

        int i1 = text.indexOf("#[");
        int i2 = text.indexOf("]");
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(text, TextView.BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable) tv.getText();
        ClickableSpan myClickableSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) { Log.e("aaa", "clickWidget");}
        };
        mySpannable.setSpan(myClickableSpan, i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return mySpannable;
    }

    public static String bbcode(String text)  {
        String html = text;

        Map<String,String> bbMap = new HashMap<String , String>();


//        bbMap.put("(\r\n|\r|\n|\n\r)", "<br/>");
//        bbMap.put("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>");
//        bbMap.put("\\[i\\](.+?)\\[/i\\]", "<span style='font-style:italic;'>$1</span>");
//        bbMap.put("\\[u\\](.+?)\\[/u\\]", "<span style='text-decoration:underline;'>$1</span>");
//        bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");
//        bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");
//        bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");
//        bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");
//        bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");
//        bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");
//        bbMap.put("\\[quote\\](.+?)\\[/quote\\]", "<blockquote>$1</blockquote>");
//        bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");
//        bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style='text-indent:$1px;line-height:$2%;'>$3</p>");
//        bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1");
//        bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2");
//        bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<span style='color:$1;'>$2</span>");
//        bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<span style='font-size:$1;'>$2</span>");
//        bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img src='$1' />");
//        bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />");
//        bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>");
//        bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>");
//        bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>");

        //[a]http%3A%2F%2Fwww.google.com[/a]
        bbMap.put("\\[a\\](.+?)\\[/a\\]", "$1");
        //#[1121]
        bbMap.put("\\#\\[(.+?)\\]", "#$1");
        //@[6]
        bbMap.put("\\@\\{(.+?)\\}", "@$1");
//        bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1'>$2</a>");
//        bbMap.put("\\[youtube\\](.+?)\\[/youtube\\]", "<object width='640' height='380'><param name='movie' value='http://www.youtube.com/v/$1'></param><embed src='http://www.youtube.com/v/$1' type='application/x-shockwave-flash' width='640' height='380'></embed></object>");
//        bbMap.put("\\[video\\](.+?)\\[/video\\]", "<video src='$1' />");

        for (Map.Entry entry: bbMap.entrySet()) {
            try {
                if(entry.getKey().toString().equals("\\[a\\](.+?)\\[/a\\]"))
                    html = URLDecoder.decode(html.replaceAll(entry.getKey().toString(), entry.getValue().toString()), "UTF-8");
                else
                    html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return html;
    }

    public static void showToast(String iMessage) {
        Toast.makeText(VMApp.getAppContext(), iMessage, Toast.LENGTH_SHORT).show();
    }

    public static void showAlert(Context context,String title,String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static void notify(Context context,String ticker, String title, String message,
                        Intent intent) {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

        int icon = R.drawable.ic_vm;
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, ticker, when);

        notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;

        int id = 123;

        PendingIntent contentIntent = PendingIntent.getActivity(context, id,
                intent, 0);

        notification.setLatestEventInfo(context, title, message,
                contentIntent);

        mNotificationManager.cancelAll();

        mNotificationManager.notify(id, notification);

    }

}
