package co.aquario.socialkit.fragment.newuser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dd.processbutton.FlatButton;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import co.aquario.socialkit.MyIntro;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.LoginEvent;
import co.aquario.socialkit.event.LoginSuccessEvent;
import co.aquario.socialkit.event.RegisterEvent;
import co.aquario.socialkit.event.RegisterFailedEvent;
import co.aquario.socialkit.event.RegisterSuccessEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.UserProfile;
import co.aquario.socialkit.util.Utils;

public class RegisterFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public int checkedId;
    private String mParam1;
    private String mParam2;
    private LinearLayout btnRegister;
    private String name;
    private MaterialEditText etUsername;
    private MaterialEditText etPassword;
    private MaterialEditText etRepeatPassword;
    private MaterialEditText etEmail;
    private RadioGroup radioGroupGender;
    private RadioButton radioGender;
    private FlatButton btnRequestOtp;
    private MaterialEditText etPhone;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View rootView;

    private Toolbar toolbar;
    void setupToolbar() {
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if(toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitle("Register");
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });


        }
    }

    String genderStr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        this.rootView = rootView;
        setupToolbar();

        etEmail = (MaterialEditText) rootView.findViewById(R.id.et_email);
        etUsername = (MaterialEditText) rootView.findViewById(R.id.et_username);
        etPassword = (MaterialEditText) rootView.findViewById(R.id.et_password);
        etRepeatPassword = (MaterialEditText) rootView.findViewById(R.id.et_repeat_password);
        etPhone = (MaterialEditText) rootView.findViewById(R.id.et_phone);
        radioGroupGender = (RadioGroup) rootView.findViewById(R.id.et_gender);
        btnRequestOtp = (FlatButton) rootView.findViewById(R.id.btn_request_otp);

        btnRequestOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etPhone.getText().toString().trim().equals("")) {
                    String mobile = etPhone.getText().toString().trim();
                    Toast.makeText(getActivity().getApplicationContext(),"OTP is sent to "+mobile,Toast.LENGTH_SHORT).show();
                }
                    //ApiBus.getInstance().post(toolbar RequestOtpEvent("0917366196",""));
            }
        });

        checkedId = radioGroupGender.getCheckedRadioButtonId();
        radioGender = (RadioButton) radioGroupGender.findViewById(checkedId);

        radioGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedId = radioGroupGender.getCheckedRadioButtonId();

                switch (checkedId) {
                    case R.id.selectMale:
                        genderStr = "male";
                        break;
                    case R.id.selectFemale:
                        genderStr = "female";
                        break;

                }
                Toast.makeText(getActivity().getApplicationContext(), "You selected " + genderStr, Toast.LENGTH_SHORT).show();
            }
        });


        btnRegister = (LinearLayout) rootView.findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = etUsername.getText().toString().trim() + " " + System.currentTimeMillis();
                String gender = radioGender.getText().toString().toLowerCase();
                Log.e("gender",gender);
                RegisterEvent event = new RegisterEvent(
                        name,
                        etUsername.getText().toString().trim(),
                        etPassword.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        gender);

                ApiBus.getInstance().post(event);
            }
        });



        return rootView;
    }

    @Subscribe public void onRegisterSuccess(RegisterSuccessEvent event) {
        Utils.showToast("Register Success");
        ApiBus.getInstance().post(new LoginEvent(etUsername.getText().toString().trim(),
                etPassword.getText().toString().trim()));

    }

    @Subscribe
    public void onLoginSuccess(LoginSuccessEvent event) {
        VMApp.USER_TOKEN = event.getLoginData().token;
        Log.e("ARAIWA", VMApp.USER_TOKEN);

        prefManager
                .name().put(event.getLoginData().user.name)
                .username().put(event.getLoginData().user.username)
                .password().put(event.getLoginData().user.password)
                .email().put(event.getLoginData().user.email)
                .userId().put(event.getLoginData().user.id)
                .token().put(event.getLoginData().token)
                .cover().put(event.getLoginData().user.cover)
                .avatar().put(event.getLoginData().user.avatar)
                .isLogin().put(true)
                .commit();


        final ParseInstallation installation = ParseInstallation
                .getCurrentInstallation();

        installation.put("user_id", Integer.parseInt(event.getLoginData().user.id));
        installation.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    System.out.println("Parse noti register ok");
                    //deviceToken = installation.get("deviceToken").toString();
                    //System.out.println(deviceToken);
                } else {
                    System.out.println("Parse noti register not ok: " + e.getLocalizedMessage());
                }
            }
        });



        //Snackbar.with(getActivity().getApplicationContext()).text(event.getLoginData().token).show(getActivity());

        Log.e("VM_PROFILE", event.getLoginData().user.toString());

        //UserProfile registeredUserProfile = event.getLoginData().user;


        Intent main = new Intent(getActivity(), MyIntro.class);
        startActivity(main);
        getActivity().finish();
        //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new RegisterSuccessFragment()).commit();

//        if(event.getLoginData().state != null) {
//            if(event.getLoginData().state.equals("login")) {
//                Intent main = new Intent(getActivity(),MainActivity.class);
//                startActivity(main);
//            } else if(event.getLoginData().state.equals("register")) {
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new RegisterSuccessFragment()).commit();
//            }
//        } else {
//            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container, new RegisterSuccessFragment()).commit();
//        }



        UserProfile user = event.getLoginData().user;
        //ApiBus.getInstance().post(new UpdateProfileEvent(user));

        //getActivity().finish();
    }

    @Subscribe public void onRegisterFailed(RegisterFailedEvent event) {
        Utils.showToast(event.msg);
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
