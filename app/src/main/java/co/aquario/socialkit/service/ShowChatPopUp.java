package co.aquario.socialkit.service;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.widget.RoundedTransformation;

public class ShowChatPopUp extends Activity implements OnClickListener {
	boolean click = true;
	
	Button ok;

	TextView msgTv, msgChat;
	ImageView avatar;

	String title;
	String msg;

    String from_avatar;
	String from_id;
	String type;
	String chat_msg;
	String cid;
	String extra;

	LinearLayout chat_dialog;
	Context context;



	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.popchatdialog);
		
		context = this;

		title = getIntent().getStringExtra("title");
		msg = getIntent().getStringExtra("msg");
        from_avatar = getIntent().getStringExtra("from_avatar");

		from_id = getIntent().getStringExtra("from_id");
		type = getIntent().getStringExtra("type");
		chat_msg = getIntent().getStringExtra("chat_msg");
		cid = getIntent().getStringExtra("cid");
		extra = getIntent().getStringExtra("extra");

		setTitle(title);

		chat_dialog = (LinearLayout) findViewById(R.id.chat_dialog);
		
		ok = (Button) findViewById(R.id.buttonOk);

		msgTv = (TextView) findViewById(R.id.msg_tv);
		msgChat = (TextView) findViewById(R.id.chat_msg_coming);
		avatar = (ImageView) findViewById(R.id.avatar_img);
        Picasso.with(this).load(VMApp.IMAGE_ENDPOINT + from_avatar).centerCrop().resize(100, 100).transform(new RoundedTransformation(50, 4)).into(avatar);

        msgTv.setText(msg);
		msgChat.setText(chat_msg);
		
		ok.setOnClickListener(this);
		chat_dialog.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		if (view == ok || view == chat_dialog) {
            Toast.makeText(this, type, Toast.LENGTH_LONG).show();

//			if(!type.equals("506")) {
//				String chatUrl = "https://www.armymax.com/mobile.php?id="
//						+ DataUser.VM_USER_ID + "&token=" + DataUser.VM_USER_TOKEN
//						+ "&type=user&fid=" + from_id;
//				Intent callRoom = new Intent(ShowChatPopUp.this,
//						XWalkActivity.class);
//				callRoom.putExtra("url", chatUrl);
//				callRoom.putExtra("title", msg);
//				startActivity(callRoom);
//			} else {
//				String chatUrl = "https://www.armymax.com/mobile.php?id="
//						+ DataUser.VM_USER_ID + "&token=" + DataUser.VM_USER_TOKEN
//						+ "&type=group&rid=" + cid;
//				Intent callRoom = new Intent(ShowChatPopUp.this,
//						XWalkActivity.class);
//				Toast.makeText(context, chatUrl, Toast.LENGTH_SHORT).show();
//				callRoom.putExtra("url", chatUrl);
//				callRoom.putExtra("title", extra);
//				startActivity(callRoom);
//			}
			

		}
		finish();
	}
}
