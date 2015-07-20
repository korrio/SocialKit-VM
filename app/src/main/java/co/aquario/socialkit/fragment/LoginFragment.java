package co.aquario.socialkit.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.regex.Pattern;

import co.aquario.socialkit.LoginActivity;
import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApplication;
import co.aquario.socialkit.event.FailedNetworkEvent;
import co.aquario.socialkit.event.LoadFbProfileEvent;
import co.aquario.socialkit.event.LoginEvent;
import co.aquario.socialkit.event.LoginFailedAuthEvent;
import co.aquario.socialkit.event.LoginSuccessEvent;
import co.aquario.socialkit.event.UpdateProfileEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.FbProfile;
import co.aquario.socialkit.model.UserProfile;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;

public class LoginFragment extends BaseFragment {

    public PrefManager prefManager;
    private AQuery aq;
    private FacebookHandle handle;
    private FbProfile profile;
    private MaterialEditText userEt;
    private MaterialEditText passEt;

    private ImageView loginBg;

    private TextView loginBtn;
    private TextView registerBtn;

    private LinearLayout fbBtn;
    private String facebookToken;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(getActivity());
        prefManager = VMApplication.get(getActivity()).getPrefManager();

        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(
                    "co.aquario.socialkit",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        FacebookSdk.sdkInitialize(this.getActivity());

        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
        // If using in a fragment
        loginButton.setFragment(this);


        // Callback registration
        loginButton.registerCallback(((LoginActivity)getActivity()).getFbCallbackManager(), new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                Log.e("fbAccessToken", accessToken.getToken());
            }

            @Override
            public void onCancel() {
                // App code
                Log.e("onCancle","laew");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("onError",exception.getLocalizedMessage());
                // App code
            }
        });
        // Other app specific specialization


        loginBg = (ImageView) rootView.findViewById(R.id.imageView);

        if(Utils.isTablet(getActivity()))
            Glide.with(this).load(VMApplication.ENDPOINT+"/imgd.php?src=img/default_bg_login.png&width=600").into(loginBg);
        else
            Glide.with(this).load(VMApplication.ENDPOINT+"/imgd.php?src=img/default_bg_login.png&width=360").into(loginBg);

        userEt = (MaterialEditText) rootView.findViewById(R.id.et_user);
        passEt = (MaterialEditText) rootView.findViewById(R.id.et_pass);

        loginBtn = (TextView) rootView.findViewById(R.id.tv_login);
        registerBtn = (TextView) rootView.findViewById(R.id.tv_reg);

        fbBtn = (LinearLayout) rootView.findViewById(R.id.btn_fb);
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authFacebook();
            }
        });

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                //userEt.setText(possibleEmail);
            }
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userEt.getText().toString().trim().equals("") && !passEt.getText().toString().trim().equals(""))
                    ApiBus.getInstance().post(new LoginEvent(userEt.getText().toString().trim(),
                        passEt.getText().toString().trim()));
                else
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.empty_input),Toast.LENGTH_SHORT).show();

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().add(R.id.login_container, new RegisterFragment(),"REGISTER").addToBackStack(null).commit();
            }
        });

        return rootView;
    }

    LoginButton loginButton;

    public void authFacebook() {





        
        handle = new FacebookHandle(getActivity(), VMApplication.APP_ID, VMApplication.APP_PERMISSIONS);
        String url = "https://graph.facebook.com/me";
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Authenicating...");

        aq.auth(handle).progress(dialog)
                .ajax(url, JSONObject.class, this, "facebookCb");
    }

    public void facebookCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        if (jo != null) {

            Log.e("FB_JSON", jo.toString());
            Gson gson = new Gson();
            profile = gson.fromJson(jo.toString(), FbProfile.class);
            facebookToken = handle.getToken();
            Log.e("FB_AUTHED", handle.authenticated() + "");

            prefManager
                    .fbToken().put(facebookToken)
                    .fbId().put(profile.id).commit();
            getFragmentManager().beginTransaction().add(R.id.login_container, new FbAuthFragment()).commit();
            ApiBus.getInstance().post(new LoadFbProfileEvent(profile,facebookToken));

            Log.e("POSTED", "SENT POST");
        }
    }

    @Subscribe
    public void onLoginSuccess(LoginSuccessEvent event) {
        VMApplication.USER_TOKEN = event.getLoginData().token;
        Log.e("ARAIWA", VMApplication.USER_TOKEN);

        prefManager
                .name().put(event.getLoginData().user.name)
                .username().put(event.getLoginData().user.username)
                .userId().put(event.getLoginData().user.id)
                .token().put(event.getLoginData().token)
                .cover().put(event.getLoginData().user.cover)
                .avatar().put(event.getLoginData().user.avatar)
                .isLogin().put(true)
                .commit();



        //Snackbar.with(getActivity().getApplicationContext()).text(event.getLoginData().token).show(getActivity());

        Log.e("VM_PROFILE",event.getLoginData().user.toString());

        Intent main = new Intent(getActivity(),MainActivity.class);
        startActivity(main);

        UserProfile user = event.getLoginData().user;
        ApiBus.getInstance().post(new UpdateProfileEvent(user));
    }

    @Subscribe
    public void onLoginFailedNetwork(FailedNetworkEvent event) {
        Log.e("ARAIWA", "onLoginFailedNetwork");
        prefManager.clear();
        Toast.makeText(getActivity().getApplicationContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onLoginFailedAuth(LoginFailedAuthEvent event) {
        Log.e("ARAIWA", "onLoginFailedAuth");
        Toast.makeText(getActivity().getApplicationContext(), "Wrong username or password", Toast.LENGTH_SHORT).show();

        //mPref.clear();
    }

    @Produce
    public LoadFbProfileEvent produceProfileEvent() {
        return new LoadFbProfileEvent(profile,facebookToken);
    }
}
