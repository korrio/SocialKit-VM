package co.aquario.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import co.aquario.chatapp.fragment.ChatWidgetFragmentClient;
import co.aquario.chatui.ChatUIActivity;
import co.aquario.chatui.event.GetUserEvent;
import co.aquario.chatui.event.GetUserEventSuccess;
import co.aquario.chatui.model.User;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.ActivityResultEvent;
import co.aquario.socialkit.event.toolbar.SubTitleEvent;
import co.aquario.socialkit.event.toolbar.TitleEvent;
import co.aquario.socialkit.handler.ApiBus;


public class ChatActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    void setupToolbar() {
        toolbar = getToolbar();
        if(toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);

            //toolbar.setSubtitle("Contact");
//            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onBackPressed();
//                }
//            });
        }
    }

    public User user;
    public Context mContext;
    public Activity mActivity;

    public Toolbar getToolbar() {
        if(toolbar != null)
        return toolbar;
        else {
            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            return toolbar;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        mContext = this;
        mActivity = this;

        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
        CustomActivityOnCrash.install(this);

        setupToolbar();

        if(getIntent() != null) {

            final int mPartnerId = getIntent().getExtras().getInt("USER_ID_2");
            final int mUserId = Integer.parseInt(VMApp.mPref.userId().getOr("0"));

            if(getToolbar() != null) {
                if(user != null) {
                    getToolbar().setTitle(user.getName());
                    getToolbar().setSubtitle("@" + user.getUsername());
                } else {
                    ApiBus.getInstance().postQueue(new GetUserEvent(mPartnerId));
                }

                getToolbar().inflateMenu(R.menu.menu_chat);
                getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String roomName = "VM_" + mUserId + "_" + mPartnerId;
                        String jsonObjStr = "{'roomName':'" + roomName + "'}";
                        switch (item.getItemId()) {
                            case R.id.action_audio_call:
                                //attemptSendMessageToServer(Message.MSG_TYPE_AUDIO_CALL,"Video Calling",jsonObjStr);
                                ChatUIActivity.connectToRoom(mActivity, roomName, false);
                                return true;
                            case R.id.action_video_call:
                                //attemptSendMessageToServer(Message.MSG_TYPE_VIDEO_CALL,"Voice Calling",jsonObjStr);
                                ChatUIActivity.connectToRoom(mActivity, roomName, true);
                                return true;
                            case R.id.action_view_contact:
                                NewProfileActivity.startProfileActivity(mActivity, mPartnerId + "");
                                return true;
//            case R.id.action_block:
//                ApiBus.getInstance().postQueue(new BlockUserEvent());
//                return true;
                            case R.id.action_leave:
                                onBackPressed();
                                return true;
                            default:
                                return onOptionsItemSelected(item);
                        }

                        //return false;
                    }
                });
            }


            ChatWidgetFragmentClient fragment = new ChatWidgetFragmentClient();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.sub_container, fragment, "CHAT_MAIN").commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startChatActivity(Activity mActivity, int userId,int partnerId,int chatType) {
        Intent i = new Intent(mActivity,ChatActivity.class);
        i.putExtra("USER_ID_1", userId);
        i.putExtra("USER_ID_2", partnerId);
        i.putExtra("CHAT_TYPE", chatType);
        mActivity.startActivity(i);
    }

    public static void startChatActivity(Activity mActivity,User user, int userId,int partnerId,int chatType) {
        Intent i = new Intent(mActivity,ChatActivity.class);
        i.putExtra("USER",user);
        i.putExtra("USER_ID_1",userId);
        i.putExtra("USER_ID_2",partnerId);
        i.putExtra("CHAT_TYPE",chatType);
        mActivity.startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ApiBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe public void onUpdateUserProfile(GetUserEventSuccess event) {
        if(toolbar != null) {
            if(event.userMe.getUser() != null) {
                toolbar.setTitle(event.userMe.getUser().getName());
                toolbar.setSubtitle("@" + event.userMe.getUser().getUsername());
            }
        }

    }

    @Subscribe public void onUpdateTitle(TitleEvent event) {
        if(getToolbar() != null)
            getToolbar().setTitle(event.str);

    }

    @Subscribe public void onUpdateSubTitle(SubTitleEvent event) {
        if(getToolbar() != null)
            getToolbar().setSubtitle(event.str);
    }


}
