package co.aquario.socialkit.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.event.upload.UpdateAvatarEvent;
import co.aquario.socialkit.event.upload.UpdateCoverEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.handler.PostUploadService;
import co.aquario.socialkit.model.UploadAvatarCallback;
import co.aquario.socialkit.model.UploadCoverCallback;
import co.aquario.socialkit.util.PhotoUtils;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.RoundedTransformation;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


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
    Bitmap bitmap;
    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CAMERA = 2;



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

    public void checkCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("hahaha", jo.toString(4));

        if(jo.optString("status").equals("1"))
            userName.setError("Username is not available");

        Utils.showToast("Username is not available");
    }

    ImageView cameraOverlay;
    ImageView cameraOverlay2;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        ButterKnife.inject(this, rootView);

        if(userName != null)
            userName.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {   //Convert the Text to String
                    String inputText = userName.getText().toString();
                    AQuery aq = new AQuery(getActivity());

                    String url = "http://api.vdomax.com/user/"+inputText+"/available";
                    aq.ajax(url, JSONObject.class, this, "checkCb");

                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // TODO Auto-generated method stub
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // TODO Auto-generated method stub
                }
            });

        cameraOverlay = (ImageView) rootView.findViewById(R.id.camera_overlay);
        cameraOverlay2 = (ImageView) rootView.findViewById(R.id.camera_overlay2);

        if(cameraOverlay != null && cameraOverlay2 != null) {
            cameraOverlay.setVisibility(View.VISIBLE);
            cameraOverlay2.setVisibility(View.VISIBLE);
        }

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

    boolean isAvatar = true;

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
                isAvatar = true;
                selectAvatar();

                Toast.makeText(getActivity(), "Avatar", Toast.LENGTH_SHORT).show();
            }
        });

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAvatar = false;
                selectAvatar();

                Toast.makeText(getActivity(), "Cover", Toast.LENGTH_SHORT).show();
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

    private void uploadFileRetrofit(File file, String timelineId,String type) {
        //FileUploadService service = ServiceGenerator.createService(FileUpload.class, FileUpload.BASE_URL);

        PostUploadService service = buildUploadApi();
        TypedFile typedFile = new TypedFile("multipart/form-data", file);

        Map<String, String> options = new HashMap<String, String>();
        //t=avatar&a=new
        options.put("t", type);
        options.put("a", "new");
        options.put("user_id", VMApp.mPref.userId().getOr("0"));
        options.put("user_pass", VMApp.mPref.password().getOr("0"));
        options.put("token", "123456");
        options.put("mobile", Integer.toString(1));

        if(type.equals("avatar")) {
            service.uploadAvatar(timelineId, typedFile,options, new Callback<UploadAvatarCallback>() {
                @Override
                public void success(UploadAvatarCallback uploadAvatarCallback, Response response) {

                        Picasso.with(getActivity())
                                .load(uploadAvatarCallback.avatarUrl)
                                .centerCrop()
                                .resize(200, 200)
                                .transform(new RoundedTransformation(100, 2))
                                .into(avatar);
                        Log.e("myAvatarUrl", uploadAvatarCallback.avatarUrl + "");
                        Utils.showToast("Update avatar success!");
                        String myAvatar = uploadAvatarCallback.avatarUrl;
                        myAvatar = myAvatar.replace("https://www.vdomax.com/","");
                        ApiBus.getInstance().postQueue(new UpdateAvatarEvent(myAvatar));

                    if(cameraOverlay != null && cameraOverlay2 != null) {
                        cameraOverlay.setVisibility(View.GONE);
                        cameraOverlay2.setVisibility(View.GONE);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("myAvatarUrl", error.getMessage() + "");
                    Utils.showToast("Update avatar failed!");

                }
            });
        } else {
            service.uploadCover(timelineId, typedFile, options, new Callback<UploadCoverCallback>() {
                @Override
                public void success(UploadCoverCallback uploadCoverCallback, Response response) {

                    Picasso.with(getActivity())
                            .load(uploadCoverCallback.coverUrl)
                            .fit().centerCrop()
                            .into(cover);
                    Log.e("myCoverUrl", uploadCoverCallback.coverUrl + "");
                    String myCover = uploadCoverCallback.coverUrl;
                    myCover = myCover.replace("https://www.vdomax.com/", "");
                    Utils.showToast("Update cover success!");
                    ApiBus.getInstance().postQueue(new UpdateCoverEvent(myCover));

                    if(cameraOverlay != null && cameraOverlay2 != null) {
                        cameraOverlay.setVisibility(View.GONE);
                        cameraOverlay2.setVisibility(View.GONE);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("myCoverUrl", error.getMessage() + "");
                    Utils.showToast("Update avatar failed!");

                }
            });
        }


    }

    PostUploadService buildUploadApi() {
        String BASE_URL = "https://www.vdomax.com";

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
                .create(PostUploadService.class);
    }

            private void uploadAvatar(File file) {

                TypedFile typedFile = new TypedFile("multipart/form-data", file);

                AQuery aq = new AQuery(getActivity());
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("timeline_id", userName.getText());
                params.put("image", typedFile);

                aq.ajax(url, params, JSONObject.class, this, "updateProfile");

            }

            private void uploadProfile(String userId) {
                Charset chars = Charset.forName("UTF-8");
                String url = "http://api.vdomax.com/user/update/" + userId;

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

            public void selectAvatar() {
                final CharSequence[] items = {"Choose from Gallery", "Take from Camera",
                        "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if(isAvatar)
                    builder.setTitle("Update avatar");
                else
                    builder.setTitle("Update cover");
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
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode
                    , Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                Log.e("onActivityResult", requestCode + " + " + resultCode);
                if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {

                    String selectedImagePath = PhotoUtils.getImagePath(data,getActivity());

                    //Bitmap bmp = PhotoUtils.createScaledBitmap(selectedImagePath,200,200)
                    Picasso.with(getActivity())
                            .load(selectedImagePath)
                            .centerCrop()
                            .resize(200, 200)
                            .transform(new RoundedTransformation(100, 2))
                            .into(avatar);

                    File file = new File(selectedImagePath);
                    if(isAvatar)
                        uploadFileRetrofit(file, userId,"avatar");
                    else
                        uploadFileRetrofit(file, userId,"cover");


                } else if (requestCode == REQUEST_CAMERA && resultCode == getActivity().RESULT_OK) {
                    //Uri selectedImageUri = data.getData();
                    String selectedImagePath = PhotoUtils.getImagePath(data, getActivity());


                    Picasso.with(getActivity())
                            .load(selectedImagePath)
                            .centerCrop()
                            .resize(200, 200)
                            .transform(new RoundedTransformation(100, 2))
                            .into(avatar);

                    File file = new File(selectedImagePath);
                    if(isAvatar)
                        uploadFileRetrofit(file, userId,"avatar");
                    else
                        uploadFileRetrofit(file, userId,"cover");

                }
            }



        }