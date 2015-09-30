package co.aquario.socialkit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.util.VibratorController;

public class IncomingCallReceiver extends BroadcastReceiver  {
	private static final String TAG = "IncomingCallReceiver";

    VibratorController vCtrl;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent == null) {
				Log.d(TAG, "Receiver intent null");
			} else {
				String action = intent.getAction();
				String currentActivity = "LandingActivity";
				Log.d(TAG, "got action " + action + " at activity "
                        + currentActivity);

                vCtrl = VibratorController.getController(context);

                if(intent.getExtras() != null) {
                    JSONObject json = new JSONObject(intent.getExtras()
                            .getString("com.parse.Data"));

                    int notiType = Integer.parseInt(json.optString("type"));
                    if ((action.equals("co.aquario.socialkit.PUSH_NOTIFICATION") && VMApp.applicationOnPause)
                            || (notiType == 504 || notiType == 505)
//						&& !currentActivity.equals("XWalkActivity")
//						&& !currentActivity.equals("XWalkConferenceActivity")
//						&& !currentActivity.equals("XWalkChatRoomActivity")
                            ) {

//                        final Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//                        final long[] pattern = { 0, 100, 500, 100, 500, 100, 500, 100, 500, 100, 500};
//                        v.vibrate(pattern, -1);

                        vCtrl.vibrate();


                        String channel = intent.getExtras().getString(
                                "com.parse.Channel");


                        Intent pupInt = null;
                        Log.d(TAG, "TYPE: " + json.optString("type")
                                + " got action " + action + " on channel "
                                + channel + " with:");
                        switch (Integer.parseInt(json.optString("type"))) {

                            case 500:
                            case 501:
                            case 502:
                            case 503:
                                pupInt = new Intent(context, ShowChatPopUp.class);
                                pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                pupInt.putExtra("title", json.optString("title"));
                                pupInt.putExtra("from_id", json.optString("from_id"));
                                pupInt.putExtra("from_avatar",
                                        json.optString("from_avatar"));
                                pupInt.putExtra("msg", json.optString("from_name"));
                                pupInt.putExtra("type", json.optString("type"));
                                pupInt.putExtra("chat_msg", json.optString("alert"));
                                pupInt.putExtra("customdata", json.optString("customdata"));
                                context.getApplicationContext().startActivity(pupInt);
                                break;
                            case 504:
                            case 505:

                                final long[] pattern2 = {0, 1000, 1000};
                                new Thread(){
                                    @Override
                                    public void run() {
                                        for(int i = 0; i < 3; i++){ //repeat the pattern 5 times
                                            vCtrl.vibratePattern(pattern2);
                                            try {
                                                Thread.sleep(2000); //the time, the complete pattern needs
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }.start();

                                pupInt = new Intent(context, ShowCallPopUp.class);
                                pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                pupInt.putExtra("title", json.optString("title"));
                                pupInt.putExtra("from_id", json.optString("from_id"));
                                pupInt.putExtra("from_avatar",
                                        json.optString("from_avatar"));
                                pupInt.putExtra("msg", json.optString("from_name"));
                                pupInt.putExtra("session", json.optString("session"));
                                pupInt.putExtra("type", json.optString("type"));
                                pupInt.putExtra("customdata", json.optString("customdata"));

                                if(!VMApp.currentActivity.getClass().getSimpleName().equals("CallActivity"))
                                    context.getApplicationContext().startActivity(pupInt);
                                break;

//						pupInt = new Intent(context, ShowChatPopUp.class);
//						pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						pupInt.putExtra("title", json.optString("title"));
//						pupInt.putExtra("from_id", json.optString("from_id"));
//						pupInt.putExtra("from_avatar",
//								json.optString("from_avatar"));
//						pupInt.putExtra("msg", json.optString("from_name"));
//						pupInt.putExtra("type", json.optString("type"));
//						pupInt.putExtra("chat_msg", json.optString("alert"));
//						pupInt.putExtra("cid", json.optString("cid"));
//						pupInt.putExtra("extra", json.optString("extra"));
//                        pupInt.putExtra("customdata", json.optString("customdata"));
//						context.getApplicationContext().startActivity(pupInt);
                            //break;
                            case 506:
                            case 520:
                            case 600:
                                pupInt = new Intent(context, ShowInviteConfPopUp.class);
                                pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                pupInt.putExtra("title", json.optString("title"));
                                pupInt.putExtra("from_id", json.optString("from_id"));
                                pupInt.putExtra("from_avatar",
                                        json.optString("from_avatar"));
                                pupInt.putExtra("msg", json.optString("from_name"));
                                pupInt.putExtra("session", json.optString("session"));
                                pupInt.putExtra("type", json.optString("type"));
                                context.getApplicationContext().startActivity(pupInt);
                                break;
                            default:
                                break;
                        }

                        Iterator itr = json.keys();
                        while (itr.hasNext()) {
                            String key = (String) itr.next();

                            Log.d(TAG, "..." + key + " => " + json.optString(key));
                        }
                }



				}
			}
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
}
