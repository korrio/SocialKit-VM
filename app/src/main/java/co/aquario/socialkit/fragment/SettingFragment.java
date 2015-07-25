package co.aquario.socialkit.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;

import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.widget.RoundedTransformation;



//

/**
 * Created by Mac on 5/20/15.
 */
public class SettingFragment extends BaseFragment {
    private static final String USER_ID = "USER_ID";
    public RadioButton radioGender;
    public int checkedId;
    public String url = "";
    public String userId = "";
    public long totalSize;
    @InjectView(R.id.et_username)
    EditText userName;
    @InjectView(R.id.et_fullname)
    EditText fullName;
    @InjectView(R.id.et_about)
    EditText about;
    @InjectView(R.id.et_email)
    EditText email;
    @InjectView(R.id.et_birthday)
    EditText birthDay;
    @InjectView(R.id.et_gender)
    RadioGroup radioGroupGender;
    @InjectView(R.id.et_city)
    EditText city;
    @InjectView(R.id.et_hometown)
    EditText homeTown;
    @InjectView(R.id.et_timezone)
    EditText timeZone;
    @InjectView(R.id.btn_save_profile)
    TextView btnSave;
    PrefManager pref;
    Activity mActivity;
    int day, month, year;
    ProgressDialog dialog;
    private String currentBirthday = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();

    public static SettingFragment newInstance(String userId) {
        SettingFragment mFragment = new SettingFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID, userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @OnClick(R.id.btn_save_profile)
    public void submit(View view) {
        updateProfile();
    }

    View myHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        }

        ApiBus.getInstance().post(new GetUserProfileEvent(userId));

        mActivity = getActivity();
    }

    ImageView avatar;
    ImageView cover;
    TextView titleTv;
    LinearLayout countLayout;
    Button btnFollow;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.inject(this, rootView);

        btnFollow = (Button) rootView.findViewById(R.id.btn_follow);
        btnFollow.setVisibility(View.GONE);
        countLayout = (LinearLayout) rootView.findViewById(R.id.count_layout);
        countLayout.setVisibility(View.GONE);
        avatar = (ImageView) rootView.findViewById(R.id.user_avatar);
        cover = (ImageView) rootView.findViewById(R.id.user_cover);
        titleTv = (TextView) rootView.findViewById(R.id.user_name);

        checkedId = radioGroupGender.getCheckedRadioButtonId();
        radioGender = (RadioButton) radioGroupGender.findViewById(checkedId);

        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (!s.toString().equals(currentBirthday)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = currentBirthday.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        day = Integer.parseInt(clean.substring(0, 2));
                        month = Integer.parseInt(clean.substring(2, 4));
                        year = Integer.parseInt(clean.substring(4, 8));

                        if (month > 12) month = 12;
                        cal.set(Calendar.MONTH, month - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, month, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    currentBirthday = clean;
                    birthDay.setText(currentBirthday);
                    birthDay.setSelection(sel < currentBirthday.length() ? sel : currentBirthday.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };


        birthDay.addTextChangedListener(tw);

        ArrayList<EditText> firstList = new ArrayList<EditText>();
        firstList.add(birthDay);
        firstList.add(birthDay);
        firstList.add(birthDay);

        return rootView;
    }

    String profileName = "";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiBus.getInstance().post(new GetUserProfileEvent(userId));
    }

    @Override
    public void onResume() {
        super.onResume();
        ApiBus.getInstance().post(new GetUserProfileEvent(userId));
    }

    @Subscribe
    public void onGetUserProfile(GetUserProfileSuccessEvent event) {

        titleTv.setText(Html.fromHtml(event.getUser().getName()));

        Picasso.with(getActivity())
                .load(event.getUser().getCoverUrl())
                .fit().centerCrop()
                .into(cover);

        Picasso.with(getActivity())
                .load(event.getUser().getAvatarUrl())
                .centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 2))
                .into(avatar);

        userId = event.getUser().getId();
        url = "http://api.vdomax.com/user/update/" + userId + "";

        userName.setText(event.getUser().getUsername());
        fullName.setText(event.getUser().getName());
        about.setText(event.getUser().getAbout());
        email.setText(event.getUser().getEmail());

        birthDay.setText("01011990");


        radioGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String numeral = null;
                switch (checkedId) {
                    case R.id.selectMale:
                        numeral = "male";
                        break;
                    case R.id.selectFemale:
                        numeral = "female";
                        break;

                }
                //Toast.makeText(getActivity().getApplicationContext(), "You selected the " + numeral + " radio button.", Toast.LENGTH_SHORT).show();
            }
        });
        //city.setText(event.getUser());
        timeZone.setText(event.getUser().getTimezone());

    }

    public void updateProfile() {


        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Updating profile");
        dialog.setMessage("กำลังอัพเดทข้อมูลส่วนตัว..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);


        new UploadFileToServer().execute();

    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {

            dialog.show();
            dialog.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            dialog.setProgress(progress[0]);


            dialog.setTitle(String.valueOf(progress[0]) + "%");

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                                dialog.setProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });


                Charset chars = Charset.forName("UTF-8");

//                entity.addPart("username", new StringBody(userName.getText().toString(), chars));
//                entity.addPart("name", new StringBody(fullName.getText().toString(), chars));
//                entity.addPart("about", new StringBody(about.getText().toString(), chars));
//                entity.addPart("email", new StringBody(email.getText().toString(), chars));
//
//                entity.addPart("birthday[]", new StringBody(birthDay.getText().toString().substring(0, 2), chars));
//                entity.addPart("birthday[]", new StringBody(birthDay.getText().toString().substring(3, 5), chars));
//                entity.addPart("birthday[]", new StringBody(birthDay.getText().toString().substring(6, 10), chars));
//
//
//                entity.addPart("gender", new StringBody("male", chars));
//                entity.addPart("current_city", new StringBody(city.getText().toString(), chars));
//                entity.addPart("hometown", new StringBody(homeTown.getText().toString(), chars));
//                entity.addPart("timezone", new StringBody(timeZone.getText().toString(), chars));
//
//                //entity.addPart("photos[]", toolbar FileBody(sourceFile));
//
//                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();

            super.onPostExecute(result);
        }

    }
}
