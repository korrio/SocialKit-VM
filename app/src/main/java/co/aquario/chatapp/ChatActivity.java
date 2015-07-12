package co.aquario.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import co.aquario.chatapp.event.ActivityResultEvent;
import co.aquario.chatapp.fragment.ChatWidgetFragment;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.handler.ApiBus;


public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_chat);

        if(getIntent() != null) {
            ChatWidgetFragment fragment = new ChatWidgetFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, "CHAT_MAIN").commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startChatActivity(Activity mActivity, int userId,int partnerId,int chatType) {
        Intent i = new Intent(mActivity,ChatActivity.class);
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


}
