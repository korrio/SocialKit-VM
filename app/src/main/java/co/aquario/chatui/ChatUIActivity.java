package co.aquario.chatui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.squareup.otto.Subscribe;

import org.appspot.apprtc.CallActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.chatui.adapter.AddFriendAdapter;
import co.aquario.chatui.adapter.SimpleAdapter;
import co.aquario.chatui.event.GetUserEventSuccess;
import co.aquario.chatui.event.retrofit.friend.GetFriendSuccessEvent;
import co.aquario.chatui.fragment.FragmentTabhost.TattooFragment;
import co.aquario.chatui.fragment.a.ContactViewPagerFragment;
import co.aquario.chatui.fragment.a.ConversationViewPagerFragment;
import co.aquario.chatui.fragment.addfriend.AddFriendByIdFragment;
import co.aquario.chatui.model.UserMe;
import co.aquario.chatui.model.friendmodel.FriendsModel;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.activity.search.SearchPagerFragment;
import co.aquario.socialkit.event.ActivityResultEvent;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;


public class ChatUIActivity extends BaseActivity {

    enum TYPE_MENU{
        CONTACT_LIST , MESSAGE_LIST , TATTOO_LIST
    }

    private static final int CONNECTION_REQUEST = 1;

    @InjectView(R.id.navIconLeft)
    ImageView navIconLeft;

    @InjectView(R.id.textNavigationBar)
    TextView textNavigationBar;

    @InjectView(R.id.navIconRight)
    ImageView navIconRight;
    //private UserManager mManager;

    private FloatingActionButton fab0;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private FloatingActionButton fab4;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private FloatingActionMenu menu1;

    private Handler mUiHandler = new Handler();

    boolean isGrid = false;
    boolean expanded = false;
    Holder holder;
    int gravity = 0;

    Activity mActivity;
    PrefManager mPref;

    private static SharedPreferences sharedPref;
    private static String keyprefVideoCallEnabled;
    private static String keyprefResolution;
    private static String keyprefFps;
    private static String keyprefVideoBitrateType;
    private static String keyprefVideoBitrateValue;
    private static String keyprefVideoCodec;
    private static String keyprefAudioBitrateType;
    private static String keyprefAudioBitrateValue;
    private static String keyprefAudioCodec;
    private static String keyprefHwCodecAcceleration;
    private static String keyprefNoAudioProcessingPipeline;
    private static String keyprefCpuUsageDetection;
    private static String keyprefDisplayHud;
    private static String keyprefRoomServerUrl;
    private static String keyprefRoom;
    private static String keyprefRoomList;

    private static boolean commandLineRun = false;
    private static boolean loopback = false;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    Menu menu;
    SearchView mSearchView;

    String oldQuery;

    void setupToolbar() {
        if(toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_search);
            toolbar.inflateMenu(R.menu.menu_add);
            toolbar.setTitle("Chat");

            //toolbar.inflateMenu(R.menu.menu_add);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_add_friend:

                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.add(R.id.frameFragment, AddFriendByIdFragment.newInstance());
                            transaction.addToBackStack(null);
                            transaction.commitAllowingStateLoss();

                            break;
                        case R.id.action_add_friends_to_group:

                            Intent  i = new Intent(getApplication(),ListViewCheckboxesActivity.class);
                            startActivity(i);

                            //initGridFriendsDialog();
                            break;
                    }
                    return false;
                }
            });
            //toolbar.setSubtitle("Contact");
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });



            menu = toolbar.getMenu();

            mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
            //MenuItemCompat.expandActionView(toolbar.getMenu().findItem(R.id.action_search));
            mSearchView.setQueryHint(oldQuery);
            //mSearchView.setIconifiedByDefault(false);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {

                    if(mActivity != null) {
                        Utils.hideKeyboard(mActivity);

                        SearchPagerFragment fragment = new SearchPagerFragment().newInstance(VMApp.mPref.userId().getOr("0"), s);
                        ((ChatUIActivity) mActivity).getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, fragment, "MAIN_SEARCH").addToBackStack(null).commit();
                        toolbar.setTitle('"' + s + '"');
                        toolbar.setSubtitle("Searching");
                        oldQuery = s;
                    }



                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_ui);
        ButterKnife.inject(this);
        setupToolbar();
        initCallPref(this);
        mActivity = this;

        footerClickFragment(TYPE_MENU.MESSAGE_LIST);

        //mManager  = new UserManager(this);
        //ParseAnalytics.trackAppOpenedInBackground(getIntent());

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//                installation.put("user_id", mManager.getUserId());
//                installation.saveInBackground();
//            }
//        }).start();



        //ApiBus.getInstance().post(new GetUserEvent(VMApp.get(getApplicationContext()).getPrefManager());

    }



    FriendsModel friendsModel;
    @Subscribe
    public void onGetFriendSuccessEvent(GetFriendSuccessEvent event) {
        friendsModel = event.getFriendModel();
    }

    OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

            dialog.dismiss();

        }
    };

    OnDismissListener dismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogPlus dialog) {

        }
    };

    OnCancelListener cancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogPlus dialog) {

        }
    };

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(DialogPlus dialog, View view) {
            switch (view.getId()) {
                case R.id.header_container:
                    break;
                case R.id.footer_confirm_button:
                    break;
                case R.id.footer_close_button:
                    break;
            }
            dialog.dismiss();
        }
    };

    public void initGridFriendsDialog() {




        holder = new GridHolder(3);
        isGrid = true;
        //holder = new ListHolder();
        //isGrid = false;

        gravity = Gravity.CENTER;

        SimpleAdapter adapter = new SimpleAdapter(ChatUIActivity.this, isGrid,friendsModel);

        showCompleteDialog("Choose Friend","",holder, gravity, adapter, clickListener, itemClickListener, dismissListener, cancelListener,
                expanded);
    }

    private void showCompleteDialog(String titleStr, String subTitleStr, Holder holder, int gravity, BaseAdapter adapter,
                                    OnClickListener clickListener, OnItemClickListener itemClickListener,
                                    OnDismissListener dismissListener, OnCancelListener cancelListener,
                                    boolean expanded) {
        final DialogPlus dialog = new DialogPlus.Builder(this)
                .setContentHolder(holder)
                .setHeader(R.layout.dialog_header)
                        //.setFooter(R.layout.dialog_footer)
                .setCancelable(true)
                .setGravity(gravity)
                .setAdapter(adapter)
                .setOnClickListener(clickListener)
                .setOnItemClickListener(itemClickListener)
                .setOnDismissListener(dismissListener)
                .setExpanded(expanded)
                .setOnCancelListener(cancelListener)
                        //                .setOutMostMargin(0, 100, 0, 0)
                .create();

        View headerView = dialog.getHeaderView();
        TextView title = (TextView) headerView.findViewById(R.id.dialog_title);
        TextView subtitle = (TextView) headerView.findViewById(R.id.dialog_subtitle);

        title.setText(titleStr);
        subtitle.setText(subTitleStr);
        View footerView = dialog.getFooterView();

        dialog.show();


    }

    public void initListAddFriendDialog() {
        //holder = new GridHolder(3);
        //isGrid = true;
        holder = new ListHolder();
        isGrid = false;

        gravity = Gravity.CENTER;

        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                Utils.showToast(position + "");

                switch (position) {
                    case 0:

                        break;
                    case 1:
                        new IntentIntegrator(mActivity).initiateScan();
                        break;
                    case 2:
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.add(R.id.frameFragment, AddFriendByIdFragment.newInstance());
                        transaction.addToBackStack(null);
                        transaction.commitAllowingStateLoss()

                        ;
                        break;
                }

                dialog.dismiss();
                //Toast.makeText(MainActivity.this, position + " clicked", Toast.LENGTH_LONG).show();
            }
        };

        AddFriendAdapter adapter = new AddFriendAdapter(ChatUIActivity.this, isGrid);

        showCompleteDialog("Add Friend","",holder, gravity, adapter, clickListener, itemClickListener, dismissListener, cancelListener,
                expanded);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));

        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d("HEYHEYHEY", "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("MainActivity", "Cancelled scan");
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.e("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.frameFragment, AddFriendByIdFragment.newInstance(result.getContents().toLowerCase()));
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        } else {
            Log.e("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static Bitmap encodeToQrCode(String text, int width, int height){
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(text, BarcodeFormat.QR_CODE, width, width);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }



    public static void connectToRoom(Activity activity,String roomName, boolean videoCall) {
        // Get room name (random for loopback).
        String roomId = roomName;
        int runTimeMs = 0;

        initCallPref(activity);


        String roomUrl = "https://apprtc.webrtc.org";

        // Video call enabled flag.
        boolean videoCallEnabled = videoCall;

        // Get default codecs.
        String videoCodec = sharedPref.getString(keyprefVideoCodec,
                activity.getString(R.string.pref_videocodec_default));
        String audioCodec = sharedPref.getString(keyprefAudioCodec,
                activity.getString(R.string.pref_audiocodec_default));

        // Check HW codec flag.
        boolean hwCodec = sharedPref.getBoolean(keyprefHwCodecAcceleration,
                Boolean.valueOf(activity.getString(R.string.pref_hwcodec_default)));

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPref.getBoolean(
                keyprefNoAudioProcessingPipeline,
                Boolean.valueOf(activity.getString(R.string.pref_noaudioprocessing_default)));



        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        String resolution = sharedPref.getString(keyprefResolution,
                activity.getString(R.string.pref_resolution_default));
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
                Log.e("HEYHEYHEY", "Wrong video resolution setting: " + resolution);
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        String fps = sharedPref.getString(keyprefFps,
                activity.getString(R.string.pref_fps_default));
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
                Log.e("HEYHEYHEY", "Wrong camera fps setting: " + fps);
            }
        }

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        String bitrateTypeDefault = activity.getString(
                R.string.pref_startvideobitrate_default);
        String bitrateType = sharedPref.getString(
                keyprefVideoBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefVideoBitrateValue,
                    activity.getString(R.string.pref_startvideobitratevalue_default));
            videoStartBitrate = Integer.parseInt(bitrateValue);
        }
        int audioStartBitrate = 0;
        bitrateTypeDefault = activity.getString(R.string.pref_startaudiobitrate_default);
        bitrateType = sharedPref.getString(
                keyprefAudioBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefAudioBitrateValue,
                    activity.getString(R.string.pref_startaudiobitratevalue_default));
            audioStartBitrate = Integer.parseInt(bitrateValue);
        }

        // Test if CpuOveruseDetection should be disabled. By default is on.
        boolean cpuOveruseDetection = sharedPref.getBoolean(
                keyprefCpuUsageDetection,
                Boolean.valueOf(
                        activity.getString(R.string.pref_cpu_usage_detection_default)));

        // Check statistics display option.
        boolean displayHud = sharedPref.getBoolean(keyprefDisplayHud,
                Boolean.valueOf(activity.getString(R.string.pref_displayhud_default)));

        // Start AppRTCDemo activity.

        Log.d("HEYHEYHEY", "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(activity, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
            intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED,
                    noAudioProcessing);
            intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(CallActivity.EXTRA_CPUOVERUSE_DETECTION,
                    cpuOveruseDetection);
            intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);

            activity.startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }

    public static boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }
        return false;
    }

    public static void initCallPref(Activity activity) {
        // Get setting keys.
        PreferenceManager.setDefaultValues(activity, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        keyprefVideoCallEnabled = activity.getString(R.string.pref_videocall_key);
        keyprefResolution = activity.getString(R.string.pref_resolution_key);
        keyprefFps = activity.getString(R.string.pref_fps_key);
        keyprefVideoBitrateType = activity.getString(R.string.pref_startvideobitrate_key);
        keyprefVideoBitrateValue = activity.getString(R.string.pref_startvideobitratevalue_key);
        keyprefVideoCodec = activity.getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = activity.getString(R.string.pref_hwcodec_key);
        keyprefAudioBitrateType = activity.getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = activity.getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = activity.getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = activity.getString(R.string.pref_noaudioprocessing_key);
        keyprefCpuUsageDetection = activity.getString(R.string.pref_cpu_usage_detection_key);
        keyprefDisplayHud = activity.getString(R.string.pref_displayhud_key);
        keyprefRoomServerUrl = activity.getString(R.string.pref_room_server_url_key);
        keyprefRoom = activity.getString(R.string.pref_room_key);
        keyprefRoomList = activity.getString(R.string.pref_room_list_key);
    }

    public void initFAB() {
//        menu1 = (FloatingActionMenu) findViewById(R.id.menu1);
//
//        /*
//        final FloatingActionButton programFab1 = new FloatingActionButton(this);
//        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
//        programFab1.setLabelText("Conference Chat");
//        programFab1.setImageResource(R.drawable.conference);
//        programFab1.setColorNormal(R.color.fab_conference);
//        menu1.addMenuButton(programFab1);
//        programFab1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(FloatingMenusActivity.this, programFab1.getLabelText(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        */
//
//        menus.add(menu1);
//        menu1.hideMenuButton(false);
//        int delay = 400;
//        for (final FloatingActionMenu menu : menus) {
//            mUiHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    menu.showMenuButton(true);
//                }
//            }, delay);
//            delay += 150;
//        }
//
//        menu1.setClosedOnTouchOutside(true);
//        fab0 = (FloatingActionButton) findViewById(R.id.fab0);
//        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
//        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
//        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
//        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
//        //fab1.setEnabled(false);

    }


    @Subscribe public void onGetUserSuccess(GetUserEventSuccess event) {


        final UserMe.UserEntity user = event.userMe.getUser();

        if(user != null) {
            //VMChatApp.mUsername = user.getUsername();
            //VMChatApp.mUserId = Integer.parseInt(user.getId());
        }

//        initFAB();
//
//        View.OnClickListener fabClickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String text = "clicked";
//
//
//
//                switch (v.getId()) {
//                    case R.id.fab0:
//                        String imageProfile = user.getAvatar();
//                        String coverProfile = user.getCover();
//                        String nameProfile = user.getName();
//                        String username = user.getUsername();
//
//                        String ImageProfileFillUrl = "https://www.vdomax.com/" + imageProfile + "";
//                        final String ImageCoverFillUrl = "https://www.vdomax.com/" + coverProfile + "";
//                        final Dialog dialog = new Dialog(mActivity, R.style.FullHeightDialog);
//                        dialog.setContentView(R.layout.dialog_followers);
//
//                        CircularImageView proFileImage;
//                        final ImageView coverImage;
//                        final TextView titleName;
//                        final TextView userName;
//                        final TextView nameAt;
//                        final TextView txtChat;
//
//                        proFileImage = (CircularImageView) dialog.findViewById(R.id.avatar);
//                        titleName = (TextView) dialog.findViewById(R.id.name_title);
//                        coverImage = (ImageView) dialog.findViewById(R.id.cover);
//                        userName = (TextView) dialog.findViewById(R.id.name_username);
//                        nameAt = (TextView) dialog.findViewById(R.id.name_at);
//                        txtChat = (TextView) dialog.findViewById(R.id.txtChat);
//
//                        nameAt.setText(nameProfile);
//                        userName.setText(username);
//                        titleName.setText(nameProfile);
//                        Picasso.with(mActivity)
//                                .load(ImageProfileFillUrl)
//                                .into(proFileImage);
//
//                        Picasso.with(mActivity)
//                                .load(ImageCoverFillUrl)
//                                .into(coverImage);
//                        break;
//                    case R.id.fab1:
//                        UserManager user = new UserManager(getApplicationContext());
//                        Bundle data = new Bundle();
//                        data.putInt("USER_ID_1",user.getUserId());
//                        data.putInt("USER_ID_2", 3082);
//                        ChatFragment fragment = new ChatFragment();
//                        fragment.setArguments(data);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, fragment, "CHAT_MAIN").addToBackStack(null).commit();
//                        break;
//                    case R.id.fab2:
//
//                        break;
//                    case R.id.fab3:
//
//                        break;
//                    default:
//                        break;
//
//                }
//
//                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        fab0.setOnClickListener(fabClickListener);
//        fab1.setOnClickListener(fabClickListener);
//        fab2.setOnClickListener(fabClickListener);
//        fab3.setOnClickListener(fabClickListener);
//        fab4.setOnClickListener(fabClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Toast.makeText(this , "Main onResume" , Toast.LENGTH_SHORT).show();
        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            String notificationData = bundle.getString("com.parse.Data");
//            if (notificationData != null) {
//                UserManager user = new UserManager(this);
//                Bundle data = new Bundle();
//                data.putInt("USER_ID_1",user.getPref().getInt(UserManager.USER_ID, 1));
//                data.putInt("USER_ID_2", 3082);
//                ChatWidgetFragmentClient fragment = ChatWidgetFragmentClient.newInstance(3,6,0);
//                fragment.setArguments(data);
//                getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, fragment, "CHAT_MAIN").addToBackStack(null).commit();
//            }
//
//        }
        //ApiBus.getInstance().register(ChatUIActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // Toast.makeText(this , "Main onPause" , Toast.LENGTH_SHORT).show();
        //ApiBus.getInstance().unregister(ChatUIActivity.this);
    }

    @OnClick(R.id.navIconLeft)
    public void onClickNavIconLeft(){

        //initGridFriendsDialog();
        initListAddFriendDialog();

        /*

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frameFragment, AddFriendFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
        //Toast.makeText(MainActivity.this , "navIconLeft" , Toast.LENGTH_SHORT).show();

        textNavigationBar.setText(getResources().getString(R.string.txtNavigationBar_Contact));
        */

        //ApiBus.getInstance().post(new GetFollowSuggestionEvent());
    }
    @OnClick(R.id.navIconRight)
    public void onClickNavIconRight(){
        initGridFriendsDialog();
//        FragmentManager manager = getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.fragment, ConversationViewPagerFragment.newInstance());
//        transaction.addToBackStack(null);
//        transaction.commit();
//
//        textNavigationBar.setText(getResources().getString(R.string.txtNavigationBar_Chat));

        //Toast.makeText(MainActivity.this , "navIconRight" , Toast.LENGTH_SHORT).show();
    }


    @OnClick(R.id.layoutMessageListBt)
    public void onClickMenuMessageList(){
        footerClickFragment(TYPE_MENU.CONTACT_LIST);
    }

    @OnClick(R.id.layoutChatBt)
    public void onClickMenuChat(){
        footerClickFragment(TYPE_MENU.MESSAGE_LIST);
    }

    @OnClick(R.id.layoutContactBt)
    public void onClickMenuContact(){
        footerClickFragment(TYPE_MENU.TATTOO_LIST);
    }
    private void footerClickFragment(TYPE_MENU type_menu){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (type_menu == TYPE_MENU.CONTACT_LIST){
            transaction.replace(R.id.fragment, ContactViewPagerFragment.newInstance());
            //transaction.addToBackStack(null);
            textNavigationBar.setText(getResources().getString(R.string.txtNavigationBar_Contact));
        } else if (type_menu == TYPE_MENU.MESSAGE_LIST){
            transaction.replace(R.id.fragment, ConversationViewPagerFragment.newInstance());
            //transaction.addToBackStack(null);
            textNavigationBar.setText(getResources().getString(R.string.txtNavigationBar_Chat));
        } else if(type_menu == TYPE_MENU.TATTOO_LIST) {
            transaction.replace(R.id.frameFragment, TattooFragment.newInstance());
            transaction.addToBackStack(null);
            textNavigationBar.setText(getResources().getString(R.string.txtNavigationBar_Tattoo));
        }

        transaction.commit();
    }
}
