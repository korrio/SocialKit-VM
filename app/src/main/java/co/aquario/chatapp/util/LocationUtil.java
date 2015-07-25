package co.aquario.chatapp.util;

import android.location.Location;

import java.text.MessageFormat;

/**
 * Created by Mac on 7/22/15.
 */
public class LocationUtil {
    public static String formatLocation(String s, Location l) {
        // Hack to get around MessageFormat precision weirdness
        return MessageFormat.format(s, "" + l.getLatitude(), "" + l.getLongitude(), "" + l.getAccuracy());
    }
}
