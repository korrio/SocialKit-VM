package co.aquario.socialkit.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.RoundedTransformation;
import retrofit.mime.TypedFile;


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
    Bitmap bitmap;
    public static final int REQUEST_GALLERY = 1;
    public static final int CAMERA_REQUEST_CODE = 2;


    public static SettingFragment newInstance(String userId) {
        SettingFragment mFragment = new SettingFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID, userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @OnClick(R.id.btn_save_profile)
    public void submit(View view) {
        uploadProfile(userId);
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
    String genderStr = "";

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

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectVideo();
                Toast.makeText(getActivity(), "Hey", Toast.LENGTH_SHORT).show();
            }
        });

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
                //String numeral = null;
                switch (checkedId) {
                    case R.id.selectMale:
                        genderStr = "male";
                        break;
                    case R.id.selectFemale:
                        genderStr = "female";
                        break;

                }
                //Toast.makeText(getActivity().getApplicationContext(), "You selected the " + numeral + " radio button.", Toast.LENGTH_SHORT).show();
            }
        });
        city.setText("");
        timeZone.setText(event.getUser().getTimezone());

    }

    private void uploadAvatar(File file){

        TypedFile typedFile = new TypedFile("multipart/form-data",file);

        AQuery aq = new AQuery(getActivity());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("timeline_id", userName.getText());
        params.put("image", typedFile);


        aq.ajax(url, params, JSONObject.class, this, "updateProfile");

    }

    private void uploadProfile(String postId) {
        Charset chars = Charset.forName("UTF-8");
        String url = "http://api.vdomax.com/user/update/" + postId;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", userName.getText());
        params.put("name", fullName.getText());
        params.put("about", about.getText());
        params.put("email", email.getText());
        params.put("birthday[]", birthDay.getText().toString().substring(0, 2));
        params.put("birthday[]", birthDay.getText().toString().substring(3, 5));
        params.put("birthday[]", birthDay.getText().toString().substring(6, 10));

        params.put("gender", "male");
        params.put("current_city", city.getText());

        params.put("hometown", homeTown.getText());
        params.put("timezone", timeZone.getText());

        AQuery aq = new AQuery(getActivity());
        aq.ajax(url, params, JSONObject.class, this, "updateProfile");
    }
    public void updateProfile(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("hahaha", jo.toString(4));
        Utils.showToast("Update complete!");
    }

    public void selectVideo() {
        final CharSequence[] items = {"Choose from Gallery", "Take from Camera",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("Add Video!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose from Gallery")) {
                    pickImage();
                } else if (items[item].equals("Take from Camera")) {
                    pickCamera();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void pickImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALLERY);
    }

    public void pickCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode
            , Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {
            Uri selectedImageUri = data.getData();

            String path = getRealPathFromURI(getActivity(), selectedImageUri);
            File file = imagePathToFile(selectedImageUri, path);
            uploadAvatar(file);
            Log.e("CheckImage:",file+"");

           /* try {
                //bitmap = Media.getBitmap(getActivity().getContentResolver(), uri);
                //Log.e("CheckImage:",bitmap+"");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }else if(requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Log.e("CheckCamera:", bitmap + "");



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

}
