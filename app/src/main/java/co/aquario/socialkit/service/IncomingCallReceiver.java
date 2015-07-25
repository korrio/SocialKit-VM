package co.aquario.socialkit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class IncomingCallReceiver extends BroadcastReceiver {
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
				if (action.equals("co.aquario.socialkit.PUSH_NOTIFICATION")
//						&& !currentActivity.equals("XWalkActivity")
//						&& !currentActivity.equals("XWalkConferenceActivity")
//						&& !currentActivity.equals("XWalkChatRoomActivity")
                        ) {
					String channel = intent.getExtras().getString(
							"com.parse.Channel");
					JSONObject json = new JSONObject(intent.getExtras()
							.getString("com.parse.Data"));

					Intent pupInt = null;
					Log.d(TAG, "TYPE: " + json.getString("type")
                            + " got action " + action + " on channel "
                            + channel + " with:");
					switch (Integer.parseInt(json.getString("type"))) {

					case 500:
					case 501:
					case 502:
					case 503:
						pupInt = new Intent(context, ShowChatPopUp.class);
						pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						pupInt.putExtra("title", json.getString("title"));
						pupInt.putExtra("from_id", json.getString("from_id"));
						pupInt.putExtra("from_avatar",
								json.getString("from_avatar"));
						pupInt.putExtra("msg", json.getString("from_name"));
						pupInt.putExtra("type", json.getString("type"));
						pupInt.putExtra("chat_msg", json.getString("alert"));
                        pupInt.putExtra("customdata", json.getString("customdata"));
						context.getApplicationContext().startActivity(pupInt);
						break;
					case 504:
					case 505:
						pupInt = new Intent(context, ShowCallPopUp.class);
						pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						pupInt.putExtra("title", json.getString("title"));
						pupInt.putExtra("from_id", json.getString("from_id"));
						pupInt.putExtra("from_avatar",
								json.getString("from_avatar"));
						pupInt.putExtra("msg", json.getString("from_name"));
						pupInt.putExtra("session", json.getString("session"));
						pupInt.putExtra("type", json.getString("type"));
                        pupInt.putExtra("customdata", json.getString("customdata"));
						context.getApplicationContext().startActivity(pupInt);
						break;
					case 506:
						pupInt = new Intent(context, ShowChatPopUp.class);
						pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						pupInt.putExtra("title", json.getString("title"));
						pupInt.putExtra("from_id", json.getString("from_id"));
						pupInt.putExtra("from_avatar",
								json.getString("from_avatar"));
						pupInt.putExtra("msg", json.getString("from_name"));
						pupInt.putExtra("type", json.getString("type"));
						pupInt.putExtra("chat_msg", json.getString("alert"));
						pupInt.putExtra("cid", json.getString("cid"));
						pupInt.putExtra("extra", json.getString("extra"));
                        pupInt.putExtra("customdata", json.getString("customdata"));
						context.getApplicationContext().startActivity(pupInt);
						break;
					case 600:
						pupInt = new Intent(context, ShowInviteConfPopUp.class);
						pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						pupInt.putExtra("title", json.getString("title"));
						pupInt.putExtra("from_id", json.getString("from_id"));
						pupInt.putExtra("from_avatar",
								json.getString("from_avatar"));
						pupInt.putExtra("msg", json.getString("from_name"));
						pupInt.putExtra("session", json.getString("session"));
						pupInt.putExtra("type", json.getString("type"));
						context.getApplicationContext().startActivity(pupInt);
						break;
					default:
						break;
					}

					Iterator itr = json.keys();
					while (itr.hasNext()) {
						String key = (String) itr.next();

						Log.d(TAG, "..." + key + " => " + json.getString(key));
					}

				}
			}
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}
	}
}
