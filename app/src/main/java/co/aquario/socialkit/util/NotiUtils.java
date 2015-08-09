package co.aquario.socialkit.util;

import android.app.Activity;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import co.aquario.socialkit.VMApp;

/**
 * Created by Mac on 8/10/15.
 */
public class NotiUtils {
    public static void notifyUser(Activity mActivity,int notiType,int mUserId,int mPartnerId,String roomName) {

        //jsonObjStr = "{'roomName':'" + roomName + "'}";

        String fromName = VMApp.mPref.username().getOr("");

        String title = "VDOMAX";
        String message;
        if(notiType == 504)
            message = fromName + " is calling you (Audio Call)";
        else
            message = fromName + " is calling you (Video Call)";

        AQuery aq = new AQuery(mActivity);
        String url = "http://api.vdomax.com/noti/index.php?" +
                "title=" + title +
                "&m=" + message  +
                "&f=" + mUserId +
                "&n=" + fromName +
                "&t=" + mPartnerId +
                "&type=" + notiType +
                //"&conversation_id=" + mCid +
                "&customdata=" + roomName;

        aq.ajax(url, JSONObject.class, mActivity, "notifyCb");
    }

    public void notifyCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        if(jo != null)
            Log.e("notiJson", jo.toString(4));
    }
}
