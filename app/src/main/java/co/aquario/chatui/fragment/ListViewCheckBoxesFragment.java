package co.aquario.chatui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
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
import co.aquario.chatui.Country;
import co.aquario.chatui.event_chat.request.InviteFriendToGroupChatEvent;
import co.aquario.chatui.event_chat.response.ConversationEventSuccess;
import co.aquario.chatui.ui.CircleTransform;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.mention.LoadMentionListSuccessEvent;
import co.aquario.socialkit.event.mention.MentionListEvent;
import co.aquario.socialkit.handler.ApiBus;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class ListViewCheckBoxesFragment extends BaseDialogFragment {
    ListView listView;
    MyCustomAdapter dataAdapter = null;
    Button myButton;
    EditText editTextGroup;
    public static ListViewCheckBoxesFragment newInstance() {
        ListViewCheckBoxesFragment fragment = new ListViewCheckBoxesFragment();
        return fragment;
    }

    public static ListViewCheckBoxesFragment newInstance(String username) {
        ListViewCheckBoxesFragment fragment = new ListViewCheckBoxesFragment();
        Bundle data = new Bundle();
        data.putString("USERNAME", username);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Remove the title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cart2, container, false);
         listView = (ListView) v.findViewById(R.id.listView1);
         editTextGroup = (EditText) v.findViewById(R.id.group_name_tv);
        ApiBus.getInstance().post(new MentionListEvent(Integer.parseInt(VMApp.mPref.userId().getOr("0"))));
         myButton = (Button) v.findViewById(R.id.findSelected);

        checkButtonClick();
        ImageView cancelButton = (ImageView) v.findViewById(R.id.x_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                getDialog().dismiss();
            }
        });
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        // Make Dialog fill parent width and height
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Subscribe
    public void onRequestMention(LoadMentionListSuccessEvent event) {
        List<MentionUser> users = new ArrayList<>();
        ArrayList<MentionUser> mentionList = event.response.mentions;
        for (int i = 0; i < mentionList.size(); i++) {
            countryList.add(new Country(mentionList.get(i).id, mentionList.get(i).username, mentionList.get(i).name, mentionList.get(i).getAvatarUrl(), false));
            //users.add(new MentionUser(mentionList.get(i).name, mentionList.get(i).username, mentionList.get(i).getAvatarUrl()));
        }

        dataAdapter = new MyCustomAdapter(getActivity(),
                R.layout.country_info, countryList);

        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Country country = (Country) parent.getItemAtPosition(position);

            }
        });

    }

    ArrayList<Country> countryList = new ArrayList<Country>();

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
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.country_info, null);

                holder = new ViewHolder();
                holder.fullnameTv = (TextView) convertView.findViewById(R.id.fullname);
                holder.usernameTv = (TextView) convertView.findViewById(R.id.username);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.avatar = (ImageView) convertView.findViewById(R.id.photo);

                convertView.setTag(holder);

                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Country country = (Country) cb.getTag();

                        country.setSelected(cb.isChecked());
                    }
                });
            } else {
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

        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Country> countryList = dataAdapter.countryList;

                ArrayList<String> userIds = new ArrayList<String>();

                for (int i = 0; i < countryList.size(); i++) {
                    Country country = countryList.get(i);
                    if (country.isSelected()) {
                        responseText.append("\n" + country.userId);
                        userIds.add(country.userId);
                    }
                }

                //Utils.showToast(responseText.toString());

                String groupName = "CHONLAKANT GROUP";

                ApiBus.getInstance().postQueue(new InviteFriendToGroupChatEvent(VMApp.mPref.userId().getOr("0"), userIds, editTextGroup.getText().toString()));


            }
        });

    }

    @Subscribe
    public void onCreateGroupChatSuccess(ConversationEventSuccess event) {

        ChatActivity.startChatActivity(getActivity(), event.mCid, 2);
    }


}
