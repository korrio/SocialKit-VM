package co.aquario.chatapp.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jialin.chat.Message;
import com.jialin.chat.MessageAdapter;
import com.jialin.chat.MessageInputToolBox;
import com.jialin.chat.OnOperationListener;
import com.jialin.chat.Option;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import co.aquario.chatapp.ChatActivity;
import co.aquario.chatapp.event.request.ConversationGroupEvent;
import co.aquario.chatapp.event.request.ConversationOneToOneEvent;
import co.aquario.chatapp.event.request.HistoryEvent;
import co.aquario.chatapp.event.response.ConversationEventSuccess;
import co.aquario.chatapp.event.response.HistoryEventSuccess;
import co.aquario.chatapp.handler.FileUploadService;
import co.aquario.chatapp.model.ChatMessage;
import co.aquario.chatapp.model.UploadCallback;
import co.aquario.chatapp.picker.LocationPickerIntent;
import co.aquario.chatapp.picker.MusicPickerIntent;
import co.aquario.chatapp.picker.YoutubePickerIntent;
import co.aquario.chatapp.util.ChatUtil;
import co.aquario.chatui.ChatUIActivity;
import co.aquario.chatui.event.GetUserEvent;
import co.aquario.chatui.event.GetUserEventSuccess;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.toolbar.SubTitleEvent;
import co.aquario.socialkit.event.toolbar.TitleEvent;
import co.aquario.socialkit.fragment.ExoSurfaceFragment;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.search.soundcloud.SoundCloudService;
import co.aquario.socialkit.search.youtube.OpenYouTubePlayerActivity;
import co.aquario.socialkit.util.EndpointManager;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.EndlessListOnScrollTopListener;
import me.iwf.photopicker.PhotoPagerActivity;
import me.iwf.photopicker.PhotoPickerActivity;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class ChatWidgetFragmentClient extends BaseFragment  {

    private List<Message> listMessages = new ArrayList<>();
    private List<String> optionName = new ArrayList<>();

    private MessageInputToolBox box;
    private ListView listView;
    private MessageAdapter adapter;

    private Integer mUserId;
    private String mName;
    private String mUsername;
    private String mAvatarUrl;

    private Integer mPartnerId;
    private String mPartnerName;
    private String mPartnerUsername;
    private String mPartnerAvatarUrl;

    private Integer mCid;
    private Integer mChatType; // 0 = 1-1 chat, 1 = public group chat, 2 = private group chat

    public boolean isConnected = false;

    public View rootView;
    public PrefManager mPref;
    private Uri mFileURI = null;

    private String socketUrl = VMApp.CHAT_SERVER;
    private Socket mSocket;
    {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        IO.setDefaultSSLContext(sc);
        IO.Options opts = new IO.Options();
        opts.secure = true;
        opts.sslContext = sc;
        try {
            mSocket = IO.socket(socketUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChatWidgetFragmentClient newInstance(int cid, int chatType) {
        ChatWidgetFragmentClient mFragment = new ChatWidgetFragmentClient();
        Bundle mBundle = new Bundle();
        mBundle.putInt("CONVERSATION_ID", cid);
        mBundle.putInt("CHAT_TYPE", chatType);
        mFragment.setArguments(mBundle);
        Log.e("BUNDLE RECEIVED", mBundle.toString());
        return mFragment;
    }

    public static ChatWidgetFragmentClient newInstance(int userId, int partnerId, int chatType) {
        ChatWidgetFragmentClient mFragment = new ChatWidgetFragmentClient();
        Bundle mBundle = new Bundle();
        mBundle.putInt("USER_ID_1", userId);
        mBundle.putInt("USER_ID_2", partnerId);
        mBundle.putInt("CHAT_TYPE", chatType);
        mFragment.setArguments(mBundle);
        Log.e("BUNDLE RECEIVED", mBundle.toString());
        return mFragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new MessageAdapter(getActivity(), listMessages);
        listView.setAdapter(adapter);

        if (mCid == null || mCid == 0)
            getConversationId();
        else {
            ApiBus.getInstance().post(new HistoryEvent(mCid, 20, 1));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUserId = getArguments().getInt("USER_ID_1");
            mPartnerId = getArguments().getInt("USER_ID_2");
            mChatType = getArguments().getInt("CHAT_TYPE", 0);
            mCid = getArguments().getInt("CONVERSATION_ID", 0);
        }

        ApiBus.getInstance().postQueue(new GetUserEvent(mPartnerId));

        mPref = VMApp.mPref;

        mName = mPref.name().getOr("null");
        mUsername = mPref.username().getOr("null");
        mAvatarUrl = "https://www.vdomax.com" + "/" + mPref.avatar().getOr("null");
        if(mUserId == null)
            mUserId = Integer.parseInt(mPref.userId().getOr("0"));

//        mName = "TESTER";
//        mUsername = "tester";
//        mAvatarUrl = "https://avatars3.githubusercontent.com/u/2325299?v=3&s=40";

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String roomName = "VM_" + mUserId + "_" + mPartnerId;
        String jsonObjStr = "{'roomName':'" + roomName + "'}";
        switch (item.getItemId()) {
            case R.id.action_audio_call:
                Message audioMsg = new Message(Message.MSG_TYPE_AUDIO_CALL, Message.MSG_STATE_SUCCESS, "", mAvatarUrl, "", "", "", roomName, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                listMessages.add(audioMsg);

                attemptSendMessageToServer(Message.MSG_TYPE_AUDIO_CALL, "Video Calling", jsonObjStr);
                ChatUIActivity.connectToRoom(getActivity(), roomName, false);
                return true;
            case R.id.action_video_call:
                Message videoMsg = new Message(Message.MSG_TYPE_VIDEO_CALL, Message.MSG_STATE_SUCCESS, "", mAvatarUrl, "", "", "", roomName, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                listMessages.add(videoMsg);


                attemptSendMessageToServer(Message.MSG_TYPE_VIDEO_CALL,"Voice Calling",jsonObjStr);
                ChatUIActivity.connectToRoom(getActivity(), roomName, true);
                return true;
            case R.id.action_view_contact:
                NewProfileActivity.startProfileActivity(getActivity(),mPartnerId + "");
                return true;
//            case R.id.action_block:
//                ApiBus.getInstance().postQueue(new BlockUserEvent());
//                return true;
            case R.id.action_leave:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_widget, container, false);
        rootView = view;
        listView = (ListView) view.findViewById(R.id.messageListview);
        box = (MessageInputToolBox) view.findViewById(R.id.messageInputToolBox);

        initMessageInputToolBox();
        initMusicPlayer();
        initConnect();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Message m = listMessages.get(i);
                int msgType = m.getType();
                Log.e("msgTypeClicked", msgType + "");
                String jsonDataStr = m.getData();
                Log.e("clickedJsonData", jsonDataStr);
                JSONObject dataObj = null;
                try {
                    dataObj = new JSONObject(jsonDataStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (msgType) {
                    case 0:
                        break;
                    case 1:
//                        getActivity().getSupportFragmentManager()
//                                .beginTransaction().replace(R.id.sub_container, TattooFragment.newInstance(), "TATTOO_STORE")
//                                .addToBackStack(null).commit();

                        break;
                    case 2:
                        String fileType = dataObj.optString("fileType");

                        if (fileType.equals("image/jpeg") || fileType.equals("image/png")) {
                            Intent intent = new Intent(getActivity(), PhotoPagerActivity.class);

                            ArrayList<String> urls = new ArrayList<>();
                            String imageUrl = dataObj.optString("url");
                            urls.add(0, imageUrl);

                            // view all images in chat (not complete)
//                        int y = 0;
//                        for (int z = 0; z < listMessages.size(); i++) {
//                            if (listMessages.get(z).getType() == 2) {
//                                listMessages.get(z).getData();
//                                String jsonDataStr = m.getData();
//                                try {
//                                    JSONObject dataObj = new JSONObject(jsonDataStr);
//                                    String imageUrl = "https://chat.vdomax.com:1314" + dataObj.optString("url");
//                                    urls.add(y, imageUrl);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                        }

                            intent.putExtra("current_item", 1);
                            intent.putStringArrayListExtra("photos", urls);

                            getActivity().startActivity(intent);
                        } else {

                            String clipPath = dataObj.optString("url");

                            Bundle data2 = new Bundle();
                            data2.putString("PATH", clipPath);

                            ExoSurfaceFragment surfaceFragment;
                            surfaceFragment = new ExoSurfaceFragment();
                            surfaceFragment.setArguments(data2);

                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().replace(R.id.sub_container, surfaceFragment, "VIDEO_PLAYER")
                                    .addToBackStack(null).commit();
                        }

                        break;
                    case 3:
                        // intent video player
                        break;
                    case 31:
                        // intent youtube player activity
                        String ytUrl = dataObj.optString("ytUrl");
                        Log.e("myYtUrl", ytUrl);
                        String[] split = ytUrl.split("v=");
                        String yid = split[1];
                        //String yid = YouTubeData.getYouTubeVideoId(ytUrl);
                        Intent lVideoIntent = new Intent(null, Uri.parse("ytv://" + yid), getActivity(), OpenYouTubePlayerActivity.class);
                        startActivity(lVideoIntent);
                        break;
                    case 32:
                        // play soundcloud
                        Log.e("playTrack.params", dataObj.optString("trackUrl") + " " + dataObj.optString("trackTitle"));
                        playTrack(dataObj.optString("trackUrl"), dataObj.optString("trackTitle"), dataObj.optString("trackImage"));
                        break;
                    case 4:
                        // intent audio call
                        break;
                    case 5:
                        // intent video call
                        break;
                    case 6:
                        String lat = dataObj.optString("latitude");
                        String lon = dataObj.optString("longtitude");
                        viewLocation(lat, lon);
                        break;
                }
            }
        });



//        listView.setSelectionAfterHeaderView();

        listView.setOnScrollListener(new EndlessListOnScrollTopListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!loading) {
                    ApiBus.getInstance().post(new HistoryEvent(mCid, 20, page));
                }

            }
        });
        return view;
    }

    boolean loading = false;

    File imagePathToFile(Uri selectedImageUri, String path) {
        Bitmap bm;
        BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

        bm = BitmapFactory.decodeFile(path, btmapOptions);
        OutputStream fOut = null;
        File file = new File(path);
        try {
            fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", requestCode + " + " + resultCode);
        if (resultCode == Activity.RESULT_OK) {

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            // choose photo and video from Dialog
            if (requestCode == REQUEST_TAKE_PHOTO) {

                String path = f.getAbsolutePath();
                Uri selectedImageUri = Uri.parse(path);
                File file = imagePathToFile(selectedImageUri, path);

                // String fileType= "image/jpeg";
                String dataJson = "{'imageUriPhoto':'" + path + "'" +
                        //",'fileType':'"+fileType+"'" +
                        "}";
                Message message = new Message(
                        Message.MSG_TYPE_PHOTO,
                        Message.MSG_STATE_SENDING,
                        mUsername, mAvatarUrl,
                        "", "", "",
                        dataJson,
                        true,
                        false,
                        new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                listMessages.add(message);
                //uploadFile(file);
                uploadFileRetrofit(file,Message.MSG_TYPE_PHOTO);

            } else if (requestCode == REQUEST_CHOOSE_PHOTO) {

                Uri selectedImageUri = data.getData();

                String path = getRealPathFromURI(getActivity(), selectedImageUri);
                File file = imagePathToFile(selectedImageUri, path);

                String fileType= "image/jpeg";
                String dataJson = "{'imageUriPhoto':'" + path + "'" +
                        //",'fileType':'"+fileType+"'" +
                        "}";
                Message message = new Message(Message.MSG_TYPE_PHOTO,
                        Message.MSG_STATE_SENDING, mUsername, mAvatarUrl, "", "", "",
                        dataJson, true, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                listMessages.add(message);

                //uploadFile(file);
                uploadFileRetrofit(file,Message.MSG_TYPE_PHOTO);

            } else if (requestCode == RESULT_PICK_VIDEO) {

                mFileURI = data.getData();
                if (mFileURI != null) {
                    String vdoThumb = ChatUtil.getThumbnailPathForLocalFile(getActivity(), mFileURI);
                    String dataJson = "";
                    if (vdoThumb != null)
                        dataJson = "{'imageUriVdoThumb':'" + vdoThumb + "'}";

                    Message sendingMessage = new Message(Message.MSG_TYPE_CLIP,
                            Message.MSG_STATE_SENDING, "",
                            mAvatarUrl, "", "", "",
                            dataJson, true, false,
                            new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                    listMessages.add(sendingMessage);


                    File clip = new File(getRealPathFromURI(getActivity(), mFileURI));
                    uploadFileRetrofit(clip,Message.MSG_TYPE_PHOTO);
                }

            } else if (requestCode == RESULT_VIDEO_CAP) {

                mFileURI = data.getData();

                if (mFileURI != null) {
                    String vdoThumb = ChatUtil.getThumbnailPathForLocalFile(getActivity(), mFileURI);
                    String dataJson = "";
                    if (vdoThumb != null)
                        dataJson = "{'imageUriVdoThumb':'" + vdoThumb + "'}";

                    Message sendingMessage = new Message(Message.MSG_TYPE_CLIP,
                            Message.MSG_STATE_SENDING, "", mAvatarUrl, "", "", "",
                            dataJson,
                            true, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                    listMessages.add(sendingMessage);

                    File clip = new File(getRealPathFromURI(getActivity(), mFileURI));
                    uploadFileRetrofit(clip,Message.MSG_TYPE_PHOTO);
                }

            }

            List<String> photos = null;
            // choose photo from PhotoPickerActivity
            if (requestCode == 100) {
                if (data != null) {
                    photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                    for (int i = 0; i < photos.size(); i++) {
                        String dataJson = "{'imageUriPhoto':'" + photos.get(i) + "'}";
                        Message message = new Message(Message.MSG_TYPE_PHOTO,
                                Message.MSG_STATE_SUCCESS, mUsername, mAvatarUrl, "", "", "",
                                dataJson, true, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                        Uri selectedImageUri = Uri.parse(photos.get(i));

                        String path = getRealPathFromURI(getActivity(), selectedImageUri);
                        File file = imagePathToFile(selectedImageUri, path);

                        listMessages.add(message);
                        uploadFileRetrofit(file, Message.MSG_TYPE_PHOTO);
                    }
                }
            } else
            if (requestCode == 200 || requestCode == 201) {

                if(data != null) {
                    Uri mFileURI = data.getData();

                    String vdoThumb = ChatUtil.getThumbnailPathForLocalFile(getActivity(), mFileURI);
                    String dataJson = "";
                    if (vdoThumb != null)
                        dataJson = "{'imageUrl':'" + vdoThumb + "'}";

                    Message message = new Message(Message.MSG_TYPE_CLIP, Message.MSG_STATE_SUCCESS, "", mAvatarUrl, "", "", "", dataJson, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                    listMessages.add(message);
                }


            } else if (requestCode == 300) {

                String trackUri = data.getStringExtra("soundcloud_uri");
                String trackTitle = data.getStringExtra("soundcloud_title");
                String artwork_url = data.getStringExtra("artwork_url");

                String dataJson = "{'trackUrl':'" + trackUri + "'" +
                        ",'imageUrl':'" + artwork_url + "'" +
                        ",'trackTitle':'" + trackTitle + "'" +
                        ",'trackImage':'" + artwork_url + "'}";

                Log.e("myArtworkUrl", artwork_url);

                Message message = new Message(Message.MSG_TYPE_SOUNDCLOUD_OBJ, Message.MSG_STATE_SUCCESS, "", mAvatarUrl, "", "", "", dataJson, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                listMessages.add(message);

                attemptSendMessageToServer(Message.MSG_TYPE_SOUNDCLOUD_OBJ, "", dataJson);

            } else if(requestCode == 400) {
                // youtube
                String yid = data.getStringExtra("yid");
                String title = data.getStringExtra("title");
                String desc = data.getStringExtra("desc");
                String thumb = data.getStringExtra("thumb");

                String dataJson = "{'yid':'" + yid + "'" +
                        ",'ytTitle':'" + title + "'" +
                        ",'ytUrl':'" + "https://www.youtube.com/?v=" + yid + "'" +
                        ",'ytImage':'" + thumb + "'}";

                Log.e("sendDataJson", dataJson);

                Message message = new Message(Message.MSG_TYPE_YOUTUBE_OBJ, Message.MSG_STATE_SUCCESS, "", "https://www.vdomax.com/photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc_thumb.jpg", "", "", "", dataJson, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                listMessages.add(message);

                attemptSendMessageToServer(Message.MSG_TYPE_YOUTUBE_OBJ, "", dataJson);

                //{"ytTitle":"LEGO RANCOR PIT Lego Star Wars Set 75005 - Time-lapse Build, Unboxing & Review in 1080p HD","ytUrl":"https://www.youtube.com/watch?v=q0vNY0596QA","ytImage":"https://i.ytimg.com/vi/q0vNY0596QA/mqdefault.jpg"}

            } else if (requestCode == 500) {

                String lat = data.getStringExtra("LAT");
                String lon = data.getStringExtra("LON");
                String locationName = data.getStringExtra("LOCATION_NAME");
                //Location location = data.getParcelableExtra("LOCATION");

                String mapImage = "https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x400&maptype=roadmap&markers=color:blue%7Clabel:" + locationName + "%7C" + lat + "," + lon;

                //{"cityName":"??????","regionName":"??????","latitude":"13.837663","longtitude":"100.616730"}

                String dataJson = "{'cityName':'" + locationName + "'" +
                        ",'imageUrl':'" + mapImage + "'" +
                        ",'regionName':'" + locationName + "'" +
                        ",'latitude':'" + lat + "'" +
                        ",'longtitude':'" + lon + "'}";

                Log.e("sendDataJson", dataJson);


                Message message = new Message(Message.MSG_TYPE_LOCATION, Message.MSG_STATE_SUCCESS, "", "https://www.vdomax.com/photos/2015/04/pr8af_108899_c04356ab5e9726bb6e650e5b9cc17cbc_thumb.jpg", "", "", "", dataJson, false, false, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                listMessages.add(message);

                attemptSendMessageToServer(Message.MSG_TYPE_LOCATION, "", dataJson);
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(listView.getBottom());
        }
        // Don't forget to check requestCode before continuing your job

    }

    @Subscribe
    public void onGetConversationId(ConversationEventSuccess event) {
        mCid = event.mCid;

        //setChatSubTitle(mUserId + ":" + mPartnerId + " in " + mCid);

        Log.i("mCid", mCid + "");
        Log.i("chatType", mChatType + "");

        ApiBus.getInstance().post(new SubTitleEvent("Room:" + mCid));
        ApiBus.getInstance().post(new HistoryEvent(mCid, 20, 1));
    }

    @Subscribe
    public void onGetHistory(HistoryEventSuccess event) {
        Log.e("HEY666", event.content.size() + "");

        if (event.content.size() > 0)
            loadHistory(event.content);

    }

    public void getConversationId() {
        switch (mChatType) {
            case 0:
                ApiBus.getInstance().post(new ConversationOneToOneEvent(mUserId, mPartnerId));
                break;
            case 1:
                ApiBus.getInstance().post(new ConversationGroupEvent(mPartnerId));
                break;
            case 2:
                ApiBus.getInstance().post(new ConversationGroupEvent(mPartnerId));
                break;
        }
    }

    public void interpretMessage(final JSONObject obj) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = obj;
                JSONObject sender;
                String username;
                String message;
                String dataJson;
                int messageType;
                int senderId;

                String senderName;
                String senderAvatar;

                try {
                    //data.getString("time");
                    Log.e("JSON RECEIVED:", data.toString(4));
                    username = mUsername;
                    senderId = data.optInt("senderId");
                    message = data.optString("message");
                    messageType = data.optInt("messageType");
                    dataJson = data.optString("data");

                    //if(data.optJSONObject("sender") != null ) {
                    //  sender = data.optJSONObject("sender");
                    senderName = data.optString("senderName");
                    senderAvatar = EndpointManager.prefix + "/" + data.optString("senderAvatar") + "." + data.optString("senderExtension");
                    Log.e("avatarUrl", senderAvatar);
                    //} else {
                    //  senderName = mName;
                    //senderAvatar = mAvatarUrl;
                    //}


                    if (messageType != 0) {
                        //message = message.concat("(" + data.optJSONObject("data").toString(4) + ")");
                    }

                    if (mUserId != senderId) {
                        Message msgObj = new Message(messageType, Message.MSG_STATE_SUCCESS, senderName, senderAvatar, "", "", message, dataJson, false, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                        listMessages.add(msgObj);
                        adapter.notifyDataSetChanged();
                        listView.setSelection(listView.getBottom());
                    }

                } catch (JSONException e) {
                    return;
                }
                //removeTyping(username);
                //addMessage(messageType,senderId,username, message,dataJson);
            }
        });
    }

    private void initMessageInputToolBox() {

        box.setOnOperationListener(new OnOperationListener() {

            @Override
            public void send(String content) {
                Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, mName, mAvatarUrl, "", "", content, "{}", true, true, new Date());
                adapter.getData().add(message);
                listView.setSelection(listView.getBottom());

                attemptSendMessageToServer(Message.MSG_TYPE_TEXT, content,"{}");

            }

            @Override
            public void selectedFace(String content) {
                System.out.println("===============" + content);
                String jsonObjStr = "{'tattooUrl':'" + content + "'}";
                System.out.println("===============" + jsonObjStr);
                Message message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, mName, mAvatarUrl, "", "", content, jsonObjStr, true, true, new Date());
                adapter.getData().add(message);
                listView.setSelection(listView.getBottom());

                attemptSendMessageToServer(Message.MSG_TYPE_FACE, content,jsonObjStr);
            }


            @Override
            public void selectedFunction(int index) {

                System.out.println("===============" + index);

                switch (index) {
                    case 0:
                        // pick photos
                        //Snackbar.make(rootView,optionName.get(index) , Snackbar.LENGTH_SHORT).show();
//                        PhotoPickerIntent intent = new PhotoPickerIntent(getActivity());
//                        intent.setPhotoCount(9);
//                        intent.setShowCamera(true);
//                        getActivity().startActivityForResult(intent, 100);
                        buildPhotoDialog();

                        break;
                    case 1:
                        // pick clip
                        buildVideoDialog();

                        break;
                    case 2:
                        //pick music
                        MusicPickerIntent musicPickerIntent = new MusicPickerIntent(getActivity());
                        musicPickerIntent.setAction("chat");
                        getActivity().startActivityForResult(musicPickerIntent, 300);
                        break;
                    case 3:
                        //pick youtube
                        YoutubePickerIntent ytPickerIntent = new YoutubePickerIntent(getActivity());
                        ytPickerIntent.setAction("chat");
                        getActivity().startActivityForResult(ytPickerIntent, 400);

                        break;
                    case 4:
                        //pick location
                        statusCheck();
                        LocationPickerIntent intent6 = new LocationPickerIntent(getActivity());
                        getActivity().startActivityForResult(intent6, 500);

                        break;

                    default:
                        break;
                }

            }

        });

        // Add tattoo
        ArrayList<String> faceNameList5 = new ArrayList<>();
        for (int x = 1; x <= 10; x++) {
            faceNameList5.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt05/tt05" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList4 = new ArrayList<>();
        for (int x = 1; x <= 10; x++) {
            faceNameList4.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt04/tt04" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList3 = new ArrayList<>();
        for (int x = 1; x <= 10; x++) {
            faceNameList3.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt03/tt03" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList2 = new ArrayList<>();
        for (int x = 1; x <= 10; x++) {
            faceNameList2.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt02/tt02" + String.format("%02d", x) + ".png");
        }

        ArrayList<String> faceNameList1 = new ArrayList<>();
        for (int x = 1; x <= 10; x++) {
            faceNameList1.add("https://www.vdomax.com/themes/vdomax1.1/emoticons/tt01/tt01" + String.format("%02d", x) + ".png");
        }

        Map<Integer, ArrayList<String>> faceData = new HashMap<>();
        faceData.put(R.drawable.tt0101, faceNameList1);
        faceData.put(R.drawable.tt0201, faceNameList2);
        faceData.put(R.drawable.tt0301, faceNameList3);
        faceData.put(R.drawable.tt0401, faceNameList4);
        faceData.put(R.drawable.tt0501, faceNameList5);
        box.setFaceData(faceData);

        optionName.add(0, getString(R.string.action_photo));
        optionName.add(1, getString(R.string.action_video));
        optionName.add(2, getString(R.string.action_voice));
        optionName.add(3, getString(R.string.action_contact));
        optionName.add(4, getString(R.string.action_music));
        optionName.add(5, getString(R.string.action_youtube));
        optionName.add(6, getString(R.string.action_location));

        List<Option> functionData = new ArrayList<Option>();

        Option choosePhoto = new Option(getActivity(), getString(R.string.action_photo), R.drawable.action_camera);
        Option chooseVideo = new Option(getActivity(), getString(R.string.action_video), R.drawable.action_clip);
        //Option recordVoice = new Option(getActivity(), getString(R.string.action_voice), R.drawable.tt0501);
        //Option shareContact = new Option(getActivity(), getString(R.string.action_contact), R.drawable.tt0501);
        Option shareMusic = new Option(getActivity(), getString(R.string.action_music), R.drawable.action_music);
        Option shareYoutube = new Option(getActivity(), getString(R.string.action_youtube), R.drawable.ic_action_youtube);
        Option shareLocation = new Option(getActivity(), getString(R.string.action_location), R.drawable.action_location);
        //Option shareMusic = new Option(getActivity(), "Contact", R.drawable.tt0501);
        functionData.add(choosePhoto);
        functionData.add(chooseVideo);
        //functionData.add(recordVoice);
        //functionData.add(shareContact);
        functionData.add(shareMusic);
        functionData.add(shareYoutube);
        functionData.add(shareLocation);
        //functionData.add(shareMusic);

        box.setFunctionData(functionData);
    }

    private void initConnect() {
        if (!isConnected) {
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    //mSocket.emit("OnlineUser");
                    JSONObject jObj = new JSONObject();
                    try {
                        jObj.put("userId", mUserId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mSocket.emit("Authenticate", jObj);
                    //addUser(mUsername); //username
                }
            });
        }


        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("Authenticate:Success", onAuthSuccess);
        mSocket.on("Authenticate:Failure", onAuthFailure);
        mSocket.on("JoinRoomSuccess", onUserJoined);
        //mSocket.on("JoinRoomFailure", null);

        mSocket.on("SendMessage", onSendMessage);
        mSocket.on("LeaveRoom", onUserLeft);
        //mSocket.on("Typing", onTyping);
        //mSocket.on("StopTyping", onStopTyping);
        //mSocket.on("Read",null);
        //mSocket.on("login" , onLogin);
        mSocket.on("OnlineUser", onOnlineUser);
        mSocket.connect();
    }

    TextView trackTitleTv;
    TextView trackSubTitleTv;
    ImageView trackThumb;

    private void initMusicPlayer() {
        mPlayerToolbar = (Toolbar) rootView.findViewById(R.id.player_toolbar);
        mPlayerStateButton = (ImageView)rootView.findViewById(R.id.player_state);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.player_progress);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mPlayerStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSongState();
            }
        });

        trackTitleTv = (TextView) rootView.findViewById(R.id.selected_title);
        trackSubTitleTv = (TextView) rootView.findViewById(R.id.selected_subtitle);
        trackThumb = (ImageView) rootView.findViewById(R.id.selected_thumbnail);

        mPlayerToolbar.setVisibility(View.GONE);

        mMediaPlayer = new MediaPlayer();

        //toggleProgressBar();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                toggleSongState();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerStateButton.setImageResource(R.drawable.ic_play);
            }
        });
    }

    private void attemptSendMessageToServer(int messageType, String theMessage, String jsonObjStr) {
        if (null == mUsername) return;
        if (!mSocket.connected()) return;

        //mTyping = false;

        String message = theMessage;
        JSONObject jObj = new JSONObject();
        JSONObject jObj2 = null;
        try {
            jObj2 = new JSONObject(jsonObjStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject senderObj = new JSONObject();

        Log.e("SendMessage", "yung");

        try {

            senderObj.put("senderId", mUserId);
            senderObj.put("id", mUserId);
            senderObj.put("username", mUserId);
            senderObj.put("name", mUserId);
            senderObj.put("avatarUrl", mUserId);


            if (messageType == 1) {
                jObj2.put("tattooUrl", message);
                jObj.put("message", "::tt0101::");
            } else {
                jObj.put("message", message);
            }


            jObj.put("senderId", mUserId);
            jObj.put("conversationId", mCid);
            jObj.put("messageType", messageType);
            jObj.put("data", jObj2);
            jObj.put("sender", senderObj);

            Log.e("SendMessage", jObj.toString(4));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("SendMessage", "laew");


        mSocket.emit("SendMessage", jObj);
    }

    private void uploadFileRetrofit(File file, final int msgType) {
        //FileUploadService service = ServiceGenerator.createService(FileUpload.class, FileUpload.BASE_URL);

        FileUploadService service = buildUploadApi();
        TypedFile typedFile = new TypedFile("multipart/form-data", file);
        String description = "hello, this is description speaking";

        service.upload(typedFile, description, new Callback<UploadCallback>() {
            @Override
            public void success(UploadCallback cb, Response response) {
                Log.e("Upload", "success " + cb.toString());
                String dataJson;
                if (cb.getFileType() != null) {
                    if (cb.getFileType().equals("image/jpeg") || cb.getFileType().equals("image/png") || cb.getFileType().equals("image/gif"))
                        dataJson = "{'url':'" + cb.getFull_path() + "'" +
                                ",'fileName':'" + cb.getFileName() + "'" +
                                ",'id':'" + cb.getId() + "'" +
                                ",'active':'" + cb.getActive() + "'" +
                                ",'thumb':'" + cb.getThumb() + "'" +
                                ",'fileType':'" + cb.getFileType() + "'}";
                    else
                        dataJson = "{'url':'" + cb.getFull_path() + "'" +
                                ",'fileName':'" + cb.getFileName() + "'" +
                                ",'id':'" + cb.getId() + "'" +
                                ",'active':'" + cb.getActive() + "'" +
                                ",'thumb':'" + cb.getThumb() + "'" +
                                ",'fileType':'" + cb.getFileType() + "'}";


                    attemptSendMessageToServer(msgType, "", dataJson);
                } else {
                    Utils.showToast("UploadCallback is null");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("Upload", "error " + error);
            }
        });
    }

    FileUploadService buildUploadApi() {
        String BASE_URL = "http://chat.vdomax.com";

        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)

                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        //request.addQueryParam("p1", "var1");
                        //request.addQueryParam("p2", "");
                    }
                })
                .build()
                .create(FileUploadService.class);
    }

    public void viewLocation(String lat, String lon) {
        String uri = MessageFormat.format("geo:{0},{1}?q={0},{1}", lat, lon);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.view_via)));
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    public void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, RESULT_PICK_VIDEO);
    }

    public void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, RESULT_VIDEO_CAP);
    }

    public void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                "image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", PHOTO_SIZE_WIDTH);
        intent.putExtra("outputY", PHOTO_SIZE_HEIGHT);
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment
                .getExternalStorageDirectory(), "temp.jpg");

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        // intent.putExtra("crop", "true");
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    public void buildVideoDialog() {
        final CharSequence[] items = {"Record Video", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("Add Video!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Record Video")) {
                    recordVideo();
                } else if (items[item].equals("Choose from Library")) {
                    pickVideo();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void buildPhotoDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("Change avatar!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    takePhoto();
                } else if (items[item].equals("Choose from Library")) {
                    pickPhoto();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void loadHistory(List<ChatMessage> jsonMessages) {
        Collections.reverse(jsonMessages);
        for (int i = 0; i < jsonMessages.size(); i++) {
            ChatMessage m = jsonMessages.get(i);
            Message message = null;
            boolean isSender = false;
            if (m.senderId == mUserId) {
                isSender = true;
            } else {
                setChatTitle("@" + m.sender.username);
            }

            if(m.data != null)
                Log.e("JSONOBJHISTORY", m.data);
            else
                m.data = "{}";

            if (m.messageType == 0) {

                // 0 = normal message
                message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
                //addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.message,m.data);

            } else if (m.messageType == 1) {

                // 1 = tattoo
                message = new Message(Message.MSG_TYPE_FACE, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

                //addMessage(m.messageType,m.senderId,m.sender.username + "(type:"+m.messageType+")",m.data,m.data);
            } else if (m.messageType == 2) {

                // 2 = image
                message = new Message(Message.MSG_TYPE_PHOTO, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.message, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            } else if (m.messageType == 3) {

                // 3 = youtube link
                message = new Message(Message.MSG_TYPE_CLIP, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if (m.messageType == 31) {

                // 31 = youtube object
                message = new Message(Message.MSG_TYPE_YOUTUBE_OBJ, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if (m.messageType == 32) {

                // 32 = soundcloud object
                message = new Message(Message.MSG_TYPE_SOUNDCLOUD_OBJ, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if (m.messageType == 4) {

                // 4 = audio call
                message = new Message(Message.MSG_TYPE_AUDIO_CALL, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));
            } else if (m.messageType == 5) {

                // 5 = video call
                message = new Message(Message.MSG_TYPE_VIDEO_CALL, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            } else if (m.messageType == 6) {

                // 6 = location

                message = new Message(Message.MSG_TYPE_LOCATION, Message.MSG_STATE_SUCCESS, m.sender.username, m.sender.getAvatarPath(), "", "", m.data, m.data, isSender, true, new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24) * 8));

            } else if (m.messageType == 7) {

                // 7 = contact

            } else if (m.messageType == 8) {

                // 8 = voice message

            }

            listMessages.add(message);

        }


        adapter.notifyDataSetChanged();

        listView.setSelection(listView.getBottom());

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                box.hide();
                return false;
            }
        });


    }

    // EMITTER GROUP
    private Emitter.Listener onAuthSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject jObj = new JSONObject();
            isConnected = true;

            try {
                jObj.put("conversationId", mCid);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.emit("JoinRoom", jObj);
        }
    };

    private Emitter.Listener onAuthFailure = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };


    private Emitter.Listener onOnlineUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            JSONObject data = (JSONObject) args[0];
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];

//                    try {
//                        Toast.makeText(getActivity(),data.toString(4),Toast.LENGTH_LONG).show();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    //addLog(getResources().getString(R.string.message_user_joined, username));
                    //addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            //Log.e("6666",args.toString());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //ApiBus.getInstance().post(new ChatTitleEvent("Joined Room !"));
                    Toast.makeText(getActivity(), "เข้าแล้ว", Toast.LENGTH_SHORT).show();
                    //addLog(getResources().getString(R.string.message_user_joined, username));
                    //addParticipantsLog(numUsers);
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {
                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");
                    } catch (JSONException e) {
                        return;
                    }

                    //addLog(getResources().getString(R.string.message_user_left, username));
                    //addParticipantsLog(numUsers);
                    //removeTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String cid = data.optString("conversationId");
                    //addTyping(username);
                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String cid = data.optString("conversationId");
                    //removeTyping(username);
                }
            });
        }
    };


    private Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            Log.e("onSendMessage", args.toString());

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Default Signature Fail", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            if (getActivity() == null)
                return;

            interpretMessage((JSONObject) args[0]);
        }
    };

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            //if (!mTyping) return;

            //mTyping = false;
            mSocket.emit("stop typing");
        }
    };

    // all about upload
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;

    private static final int RESULT_PICK_VIDEO = 4;
    private static final int RESULT_VIDEO_CAP = 5;

    private static final int PHOTO_SIZE_WIDTH = 100;
    private static final int PHOTO_SIZE_HEIGHT = 100;

    private void startCropImage(String path) {

        //Crop.of(path, path).asSquare().start(activity);
    }

    public int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURIForKitKat(Context context, Uri uri) {
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return getRealPathFromURI(context, uri);
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void setChatTitle(String title) {
        ApiBus.getInstance().postQueue(new TitleEvent(title));
//        if(((BaseActivity) getActivity()).getToolbar() != null) {
//            ((BaseActivity) getActivity()).getToolbar().setTitle(title);
        //  }
//        if(((BaseActivity) getActivity()).getToolbar() != null) {
//            switch (mChatType) {
//                case 0:
//                    ((BaseActivity) getActivity()).getToolbar().setTitle("CHAT_TYPE: 1-1 :" + title);
//                    break;
//                case 1:
//                    ((BaseActivity) getActivity()).getToolbar().setTitle("CHAT_TYPE: LIVE CHAT :" + title);
//                    break;
//                case 2:
//                    ((ChatActivity) getActivity()).getToolbar().setTitle("CHAT_TYPE: GROUP CHAT :" + title);
//                    break;
//            }
//        }
    }

    public void setChatSubTitle(String subTitle) {
        //((BaseActivity) getActivity()).getToolbar().setSubtitle(subTitle);
    }

    // music player
    private Toolbar mPlayerToolbar;
    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerStateButton;
    private ProgressBar mProgressBar;

    private void toggleSongState() {
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            mPlayerStateButton.setImageResource(R.drawable.ic_play);
        }else{
            mMediaPlayer.start();
            toggleProgressBar();
            mPlayerStateButton.setImageResource(R.drawable.ic_pause);
        }
    }

    private void toggleProgressBar() {
        if (mMediaPlayer.isPlaying()){
            mProgressBar.setVisibility(View.INVISIBLE);
            mPlayerStateButton.setVisibility(View.VISIBLE);
        }else{
            mProgressBar.setVisibility(View.VISIBLE);
            mPlayerStateButton.setVisibility(View.INVISIBLE);
        }
    }

    public void playTrack(String uri,String title,String thumbUrl) {
        if(mMediaPlayer != null) {
            mPlayerToolbar.setVisibility(View.VISIBLE);

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            toggleProgressBar();

            trackTitleTv.setText(title);
            trackSubTitleTv.setText("by ");
            Picasso.with(getActivity()).load(thumbUrl).centerCrop().resize(200,200).into(trackThumb);

            try {
                //uri = uri.replace("https","http");
                uri = uri + "?client_id=" + SoundCloudService.CLIENT_ID;
                Log.e("playMusic", uri);
                mMediaPlayer.setDataSource(uri);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(),"Music Player is Null",Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Subscribe public void onUpdateUserProfile(GetUserEventSuccess event) {

        Toolbar toolbar = ((ChatActivity) getActivity()).getToolbar();
        if(toolbar != null) {
            if(event.userMe.getUser() != null) {
                toolbar.setTitle(event.userMe.getUser().getName());
                toolbar.setSubtitle("@" + event.userMe.getUser().getUsername());
            }
        }

    }
}
