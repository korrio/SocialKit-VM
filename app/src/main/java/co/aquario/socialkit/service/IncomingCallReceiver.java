package co.aquario.socialkit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import co.aquario.socialkit.VMApp;

public class IncomingCallReceiver extends BroadcastReceiver  {
	private static final String TAG = "IncomingCallReceiver";

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
				if (action.equals("co.aquario.socialkit.PUSH_NOTIFICATION") && VMApp.applicationOnPause
//						&& !currentActivity.equals("XWalkActivity")
//						&& !currentActivity.equals("XWalkConferenceActivity")
//						&& !currentActivity.equals("XWalkChatRoomActivity")
                        ) {
					String channel = intent.getExtras().getString(
							"com.parse.Channel");
					JSONObject json = new JSONObject(intent.getExtras()
							.getString("com.parse.Data"));

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
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
}
