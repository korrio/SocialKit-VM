package co.aquario.socialkit.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import co.aquario.chatapp.ChatActivity;
import co.aquario.chatapp.push.DialogService;
import co.aquario.chatui.ChatUIActivity;
import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;


public class PushManage extends Activity {

	private String TAG = "MANAGE PUST";
	// public static $hostPath = "https://www.vdomax.com/noti/";
	// message category
	public static int CATEGORYS_common = 0;
	public static int CATEGORYS_chat = 1;
	// message type
	public static int TYPES_likeFeed = 100;
	public static int TYPES_commentFeed = 101;
    public static int TYPES_shareFeed = 102;
    public static int TYPES_reportFeed = 103;
	public static int TYPES_liveNow = 200;
	public static int TYPES_followedYou = 300;
	public static int TYPES_chatMessage = 500;
	public static int TYPES_chatSticker = 501;
	public static int TYPES_chatFile = 502;
	public static int TYPES_chatLocation = 503;
	public static int TYPES_chatFreeCall = 504;
	public static int TYPES_chatVideoCall = 505;
	public static int TYPES_chatInviteGroup = 506;
    public static int TYPES_chatInviteGroup2 = 520;
	public static int TYPES_confInvite = 600;
	public static int TYPES_confCreate = 601;
	public static int TYPES_confJoin = 602;
    public static int TYPES_NotifyBadge = 700;

	// message format
	// public static $NOTIFORMATS = array(
	// "100" => "{0} like your post",
	// "101" => "{0} comment on {1} post {2}",
	// "200" => "{0} live now",
	// "300" => "{0} has followed you",
	// "500" => "{0}: {1}",
	// "501" => "{0} sent you a sticker",
	// "502" => "{0} sent you a file ",
	// );

	private String fromId;
    private String toId;
	private String fromName;
	private String roomName;
	private String postId;
    private String customdata;
    private int mCid;
	String currentActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);
		this.startService(new Intent(this, PushManage.class));
		//aq = new AQuery(this);
		currentActivity = "";
		getPush();
		
	}

	boolean mIsBound = false;
	private co.aquario.chatapp.push.DialogService mBoundService;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((co.aquario.chatapp.push.DialogService.LocalBinder) service).getService();
			mBoundService.createDialogIn(2000);
			doUnbindService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

	void doBindService() {
		if (!mIsBound) {
			bindService(new Intent(PushManage.this, DialogService.class),
					mConnection, Context.BIND_AUTO_CREATE);
			mIsBound = true;
		}
	}

	void doUnbindService() {
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	void getPush() {

		//Intent intent = getIntent();
		try {
			// String action = intent.getAction();
			// String channel =
			// intent.getExtras().getString("com.parse.Channel");
			// JSONObject json = new
			// JSONObject(intent.getExtras().getString("com.parse.Data"));

			Intent intent2 = getIntent();
			Bundle extras = intent2.getExtras();
			String jsonData = extras.getString("com.parse.Data");
			System.out.println("JSON DATA : " + jsonData);
            Log.e("HEYJUDE",jsonData);

			JSONObject json = new JSONObject(jsonData);
			String type = json.getString("type");

			if (type.equals(TYPES_chatMessage + "")
					|| type.equals(TYPES_chatSticker + "")
					|| type.equals(TYPES_chatFile + "")
					|| type.equals(TYPES_chatLocation + "")) {
				fromId = json.optString("from_id", json.optInt("from_id") + "");
				fromName = json.getString("from_name");
                toId = json.optString("to_id", json.optInt("to_id") + "");
                mCid = Integer.parseInt(json.optString("conversation_id", json.optInt("conversation_id") + ""));



                /*
                chatUrl += fromId;
				String m = fromName + "%20ได้ส่งข้อความหาคุณ";
				String notiUrl = "http://armymax.com/api/noti/noti.php?a=insert&f="
						+ fromId
						+ "&n="
						+ fromName
						+ "&t="
						+ json.getString("to_id")
						+ "&msg="
						+ m
						+ "&type="
						+ type;
				//aq.ajax(notiUrl, JSONObject.class, this, "notiCb");
				int n = 3;
				n++;
				*/
				//DataUser.VM_CHAT_N = n;
			} else if (type.equals(TYPES_confInvite + "")) {
				roomName = json.optString("room_name");
			} else if (type.equals(TYPES_confCreate + "")
					|| type.equals(TYPES_confJoin + "")) {
				roomName = json.optString("room_name");
			} else if (type.equals(TYPES_liveNow + "")) {
                fromId = json.optString("from_id", json.optInt("from_id") + "");
                fromName = json.getString("from_name");
//				postId = json.optString("post_id", json.optInt("post_id") + "");
			} else if (type.equals(TYPES_commentFeed + "")
					|| type.equals(TYPES_likeFeed + "")) {
				fromId = json.optString("from_id", json.optInt("from_id") + "");
				fromName = json.getString("from_name");
				postId = json.optString("post_id", json.optInt("post_id") + "");
			} else if (type.equals(TYPES_chatInviteGroup + "") || type.equals(TYPES_chatInviteGroup2 + "")) {
				Log.e("chatInvite",json.toString());
			} else if (type.equals(TYPES_chatVideoCall + "")) {
				customdata = json.optString("customdata");
			} else if (type.equals(TYPES_chatFreeCall + "")) {
                customdata = json.optString("customdata");
			}

			intentManage(Integer.parseInt(type));

			// Log.d(TAG, "got action " + action + " on channel " + channel
			// + " with:");
			// Iterator itr = json.keys();
			// while (itr.hasNext()) {
			// String key = (String) itr.next();
			// Log.d(TAG, "..." + key + " => " + json.getString(key));
			// }
		} catch (JSONException e) {
			Log.d(TAG, "JSONException: " + e.getMessage());
		}

	}

	public void notiCb(String url, JSONObject jo, AjaxStatus status)
			throws JSONException {

	}

	public void intentManage(int type) {
		Intent toDetail = null;
        if (type == TYPES_likeFeed) {
			Intent routeIntent = new Intent(this, MainActivity.class);
			routeIntent.putExtra("type", "post");
			routeIntent.putExtra("post_id", postId);
			startActivity(routeIntent);
		} else if (type == TYPES_commentFeed) {
			Intent routeIntent = new Intent(this, MainActivity.class);
			routeIntent.putExtra("type", "post");
			routeIntent.putExtra("post_id", postId);
			startActivity(routeIntent);
		} else if(type == TYPES_shareFeed) {
            Intent routeIntent = new Intent(this, MainActivity.class);
            routeIntent.putExtra("type", "post");
            routeIntent.putExtra("post_id", postId);
            startActivity(routeIntent);
        } else if (type == TYPES_liveNow) {
            Intent i = new Intent(this, NewProfileActivity.class);
            i.putExtra("USER_ID",fromId + "");
            startActivity(i);


			/*
			 * toDetail = new Intent(ManagePush.this, PlayActivity.class);
			 * toDetail.putExtra("isPlay", "1"); toDetail.putExtra("roomId",
			 * fromName); toDetail.putExtra("roomTag", "0");
			 * startActivity(toDetail);
			 */

		} else if (type == TYPES_followedYou) {
            NewProfileActivity.startProfileActivity(this,fromId + "");

//			Intent profileIntent = new Intent(this, LandingActivity.class);
//			profileIntent.putExtra("type", "profile");
//			profileIntent.putExtra("user_id", fromId + "");
//			startActivity(profileIntent);

		} else if (type == TYPES_chatMessage || type == TYPES_chatSticker
				|| type == TYPES_chatFile || type == TYPES_chatLocation) {

            ChatActivity.startChatActivity(this,Integer.parseInt(postId),Integer.parseInt(VMApp.mPref.userId().getOr("0")) ,Integer.parseInt(fromId),0);

		}
//        else if (type == TYPES_confCreate || type == TYPES_confJoin
//				|| type == TYPES_confInvite) {
//			// intent Lobby
//			toDetail = new Intent(PushManage.this,
//                    LandingActivity.class);
//			toDetail.putExtra("roomName", roomName);
//			startActivity(toDetail);
//
//		}
        else if (type == TYPES_chatFreeCall) {
            ChatActivity.startChatActivity(PushManage.this, Integer.parseInt(VMApp.mPref.userId().getOr("0")) ,Integer.parseInt(fromId),0);
            notifyUser(TYPES_chatFreeCall, customdata,Integer.parseInt(fromId));
            ChatUIActivity.connectToRoom(PushManage.this, customdata, false);
		} else if (type == TYPES_chatVideoCall) {
            ChatActivity.startChatActivity(PushManage.this,Integer.parseInt(VMApp.mPref.userId().getOr("0")) ,Integer.parseInt(fromId),0);
            notifyUser(TYPES_chatVideoCall, customdata,Integer.parseInt(fromId));
            ChatUIActivity.connectToRoom(PushManage.this, customdata, true);
		} else if (type == TYPES_chatInviteGroup || type == TYPES_chatInviteGroup2) {
            ChatActivity.startChatActivity(PushManage.this,mCid, Integer.parseInt(VMApp.mPref.userId().getOr("0")) ,Integer.parseInt(fromId),2);
		}

		this.finish();
	}

    private void notifyUser(int notiType,String roomName,int toId) {

        String fromName = VMApp.mPref.username().getOr("");

        String title = "VDOMAX";
        String message = "";
        if(notiType == TYPES_chatFreeCall)
            message = fromName + " is calling you (Audio Call)";
        else
            message = fromName + " is calling you (Video Call)";

        AQuery aq = new AQuery(this);
        String url = "http://api.vdomax.com/noti/index.php?" +
                "title=" + title +
                "&m=" + message  +
                "&f=" + fromId +
                "&n=" + fromName +
                "&t=" + toId +
                "&type=" + notiType +
                "&conversation_id=" + mCid +
                "&customdata=" + roomName;

        aq.ajax(url, JSONObject.class, this, "notifyCb");
    }

    public void getjson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        if(jo != null)
            Log.e("notiJson",jo.toString(4));
    }
}
