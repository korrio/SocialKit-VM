package co.aquario.chatui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.arabagile.typeahead.model.MentionUser;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.aquario.chatapp.ChatActivity;
import co.aquario.chatui.event_chat.request.InviteFriendToGroupChatEvent;
import co.aquario.chatui.event_chat.response.ConversationEventSuccess;
import co.aquario.chatui.ui.CircleTransform;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.mention.LoadMentionListSuccessEvent;
import co.aquario.socialkit.event.mention.MentionListEvent;
import co.aquario.socialkit.handler.ApiBus;

public class ListViewCheckboxesActivity extends BaseActivity {

    MyCustomAdapter dataAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_main);

        //Generate list View from ArrayList
        //displayListView();
        ApiBus.getInstance().post(new MentionListEvent(Integer.parseInt(VMApp.mPref.userId().getOr("0"))));


        checkButtonClick();

    }

    @Subscribe
    public void onRequestMention(LoadMentionListSuccessEvent event) {
        List<MentionUser> users = new ArrayList<>();
        ArrayList<MentionUser> mentionList = event.response.mentions;
        for(int i = 0 ; i < mentionList.size() ; i++) {
            countryList.add(new Country(mentionList.get(i).id,mentionList.get(i).username,mentionList.get(i).name,mentionList.get(i).getAvatarUrl(),false));
            //users.add(new MentionUser(mentionList.get(i).name, mentionList.get(i).username, mentionList.get(i).getAvatarUrl()));
        }

        dataAdapter = new MyCustomAdapter(this,
                R.layout.country_info, countryList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Country country = (Country) parent.getItemAtPosition(position);

            }
        });

    }

    ArrayList<Country> countryList = new ArrayList<Country>();

//    private void displayListView() {
//
//        //Array list of countries
//
//        Country country = new Country("AFG","Afghanistan",false);
//        countryList.add(country);
//        country = new Country("ALB","Albania",true);
//        countryList.add(country);
//        country = new Country("DZA","Algeria",false);
//        countryList.add(country);
//        country = new Country("ASM","American Samoa",true);
//        countryList.add(country);
//        country = new Country("AND","Andorra",true);
//        countryList.add(country);
//        country = new Country("AGO","Angola",false);
//        countryList.add(country);
//        country = new Country("AIA","Anguilla",false);
//        countryList.add(country);
//
//        //create an ArrayAdaptar from the String Array
//        dataAdapter = new MyCustomAdapter(this,
//                R.layout.country_info, countryList);
//        ListView listView = (ListView) findViewById(R.id.listView1);
//        // Assign adapter to ListView
//        listView.setAdapter(dataAdapter);
//
//
//        listView.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                // When clicked, show a toast with the TextView text
//                Country country = (Country) parent.getItemAtPosition(position);
//                Toast.makeText(getApplicationContext(),
//                        "Clicked on Row: " + country.getName(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });
//
//    }

    private class MyCustomAdapter extends ArrayAdapter<Country> {

        private ArrayList<Country> countryList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Country> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Country>();
            this.countryList.addAll(countryList);
        }

        private class ViewHolder {
            TextView fullnameTv;
            TextView usernameTv;
            CheckBox checkBox;
            ImageView avatar;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.country_info, null);

                holder = new ViewHolder();
                holder.fullnameTv = (TextView) convertView.findViewById(R.id.fullname);
                holder.usernameTv = (TextView) convertView.findViewById(R.id.username);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.avatar = (ImageView) convertView.findViewById(R.id.photo);

                convertView.setTag(holder);

                holder.checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Country country = (Country) cb.getTag();

                        country.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Country country = countryList.get(position);
            //holder.code.setText(" (" +  country.getCode() + ")");
            holder.fullnameTv.setText(country.getName());
            holder.usernameTv.setText(country.getUsername());
            Picasso.with(getContext()).load(country.getAvatar()).resize(150, 150).transform(new CircleTransform()).into(holder.avatar);
            holder.checkBox.setChecked(country.isSelected());
            holder.checkBox.setTag(country);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Country> countryList = dataAdapter.countryList;

                ArrayList<String> userIds = new ArrayList<String>();

                for(int i=0;i<countryList.size();i++){
                    Country country = countryList.get(i);
                    if(country.isSelected()){
                        responseText.append("\n" + country.userId);
                        userIds.add(country.userId);
                    }
                }

                //Utils.showToast(responseText.toString());
                EditText editTextGroup = (EditText) findViewById(R.id.group_name_tv);
                String groupName = "CHONLAKANT GROUP";

                ApiBus.getInstance().postQueue(new InviteFriendToGroupChatEvent(VMApp.mPref.userId().getOr("0"),userIds, editTextGroup.getText().toString()));



            }
        });

    }

    @Subscribe public void onCreateGroupChatSuccess(ConversationEventSuccess event) {

        ChatActivity.startChatActivity(this,event.mCid,2);
    }

}