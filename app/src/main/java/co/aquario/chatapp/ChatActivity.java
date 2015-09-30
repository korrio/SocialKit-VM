package co.aquario.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.chatapp.event.response.GetChatInfoSuccess;
import co.aquario.chatapp.fragment.ChatWidgetFragmentClient;
import co.aquario.chatui.event.GetUserEventSuccess;
import co.aquario.chatui.model.User;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
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

    int mCid;
    int chatType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        mContext = this;
        mActivity = this;

        setupToolbar();

//        CustomActivityOnCrash.setShowErrorDetails(true);
//        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
//        CustomActivityOnCrash.install(this);

        if(getIntent() != null) {

            mCid = getIntent().getExtras().getInt("CONVERSATION_ID");
            chatType = getIntent().getExtras().getInt("CHAT_TYPE");

            Log.e("CONVERSATION_ID",mCid + "");
            Log.e("CHAT_TYPE",chatType + "");
            ChatWidgetFragmentClient fragment = new ChatWidgetFragmentClient();

            switch (chatType) {
                case 0:
                    // 1-1 chat

                    fragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.sub_container, fragment, "CHAT_MAIN").commit();
                    break;
                case 1:
                    // public group chat

                    fragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.sub_container, fragment, "CHAT_MAIN").commit();
                    break;
                case 2:
                    // privae group chat

                    fragment.setArguments(getIntent().getExtras());
                    getSupportFragmentManager().beginTransaction().add(R.id.sub_container, fragment, "CHAT_MAIN").commit();
                    break;
                default:
                    break;

            }





        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // FOR GROUP CLICK
    public static void startChatActivity(Activity mActivity, int cid,int chatType) {
        Intent i = new Intent(mActivity,ChatActivity.class);
        i.putExtra("CONVERSATION_ID", cid);
        i.putExtra("CHAT_TYPE", chatType);
        mActivity.startActivity(i);
    }

    // PUSH CLICK
    public static void startChatActivity(Activity mActivity, int userId,int partnerId,int chatType) {
        Intent i = new Intent(mActivity,ChatActivity.class);
        i.putExtra("USER_ID_1", userId);
        i.putExtra("USER_ID_2", partnerId);
        i.putExtra("CONVERSATION_ID", 0);
        i.putExtra("CHAT_TYPE", chatType);
        mActivity.startActivity(i);
    }

    // FOR CONVERSATION LIST CLICK
    public static void startChatActivity(Activity mActivity,int cid, int userId,int partnerId,int chatType) {
        Intent i = new Intent(mActivity,ChatActivity.class);
        i.putExtra("USER_ID_1", userId);
        i.putExtra("USER_ID_2", partnerId);
        i.putExtra("CONVERSATION_ID", cid);
        i.putExtra("CHAT_TYPE", chatType);
        mActivity.startActivity(i);
    }

    // FOR FRIEND DIALOG CLICK
    public static void startChatActivity(Activity mActivity,User user, int userId,int partnerId,int chatType) {
        Intent i = new Intent(mActivity,ChatActivity.class);
        i.putExtra("USER",user);
        i.putExtra("USER_ID_1",userId);
        i.putExtra("USER_ID_2",partnerId);
        i.putExtra("CONVERSATION_ID", 0);
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

    @Subscribe public void onGetChatInfo(GetChatInfoSuccess event) {



        setupToolbar();

    }

    @Subscribe public void onUpdateUserProfile(GetUserEventSuccess event) {
        if(toolbar != null) {
            if(event.userMe.getUser() != null && chatType != 1 && chatType != 2) {
                getToolbar().setTitle(event.userMe.getUser().getName());
                getToolbar().setSubtitle("@" + event.userMe.getUser().getUsername());
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
