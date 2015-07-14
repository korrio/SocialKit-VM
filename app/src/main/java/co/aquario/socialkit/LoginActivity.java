package co.aquario.socialkit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;
import com.parse.ui.ParseLoginFragment;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import co.aquario.chatapp.ChatApp;
import co.aquario.chatapp.event.login.LoginFailedAuthEvent;
import co.aquario.chatapp.event.login.LoginSuccessEvent;
import co.aquario.chatapp.model.login.LoginData;
import co.aquario.socialkit.event.UpdateProfileEvent;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.UserProfile;
import co.aquario.socialkit.util.PrefManager;

public class LoginActivity extends AppCompatActivity {

    public PrefManager prefManager;
    public boolean isLogin;
    boolean doubleBackToExitPressedOnce = false;

    Activity mActivity;

    public ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        prefManager = VMApplication.get(this).getPrefManager();
        setContentView(R.layout.activity_login);
        mActivity = this;
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

            isLogin = prefManager.isLogin().getOr(false);

        if (savedInstanceState == null && !isLogin) {
            //getSupportFragmentManager().beginTransaction().add(R.id.login_container, new LoginFragment()).commit();
            ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
                    LoginActivity.this);
            Intent parseLoginIntent = loginBuilder.setParseLoginEnabled(true)
                    .setParseLoginButtonText("Login")
                    .setParseSignupButtonText("Register")
                    .setParseLoginHelpText("Forgot password?")
                    .setParseLoginInvalidCredentialsToastText("You email and/or password is not correct")
                    .setParseLoginEmailAsUsername(true)
                    .setParseSignupSubmitButtonText("Submit registration")
                    .setFacebookLoginEnabled(true)
                    .setFacebookLoginButtonText("Facebook")
                    .setFacebookLoginPermissions(Arrays.asList("user_status", "read_stream"))
                    .setTwitterLoginEnabled(false)
                            //.setTwitterLoginButtontext("Twitter")
                    .build();
            startActivityForResult(parseLoginIntent, 12123);
        } else {
            Intent main = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(main);
            ActivityResultBus.getInstance().postQueue(new UpdateProfileEvent(new UserProfile()));
            finish();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setResult(resultCode);
        Log.e("asdf555", requestCode + " " + resultCode);


        if(resultCode == RESULT_OK) {
            user = ParseUser.getCurrentUser();

            if(user != null) {

                HashMap fbObj = (HashMap) user.get("authData");



                if(fbObj != null) {
                    HashMap facebook = (HashMap) fbObj.get("facebook");
                    String fbAccessToken = facebook.get("access_token").toString();
                    String fbId = facebook.get("id").toString();

                    Log.e("fbAccessToken", fbId + " "  + fbAccessToken);
                    try {
                        fbAuthVM(fbId,fbAccessToken);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Log.e("normalLogin",user.getEmail() + " "  +   user.getUsername());
                    authVM(ParseLoginFragment.HACKUSERNAME,ParseLoginFragment.HACKPASSWORD);
                }




            }



        }

    }

    private void authVM(final String username, final String password) {
        final RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .build();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Request request = new Request.Builder()
                        .url("http://api.vdomax.com/1.0/auth")
                        .post(formBody)
                        .build();
                Response response = null;
                try {
                    response = VMApplication.getHttpClient().newCall(request).execute();
                    if (!response.isSuccessful()) {
                        ApiBus.getInstance().post(new LoginFailedAuthEvent());
                    } else {

                        Gson gson = new Gson();
                        LoginData loginData = gson.fromJson(response.body().charStream(), LoginData.class);

                        LoginSuccessEvent event = new LoginSuccessEvent(loginData);

                        ChatApp.USER_TOKEN = event.getLoginData().token;
                        Log.e("USER_TOKEN", ChatApp.USER_TOKEN);

                        user.put("user_id",Integer.parseInt(event.getLoginData().user.id));
                        user.put("name", event.getLoginData().user.name);
                        user.setUsername(event.getLoginData().user.username);
                        user.setEmail(event.getLoginData().user.email);

                        prefManager
                                .name().put(event.getLoginData().user.name)
                                .username().put(event.getLoginData().user.username)
                                .userId().put(event.getLoginData().user.id)
                                .token().put(event.getLoginData().token)
                                .cover().put(event.getLoginData().user.cover)
                                .avatar().put(event.getLoginData().user.avatar)
                                .isLogin().put(true)
                                .commit();

                        try {
                            user.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        VMApplication.updateParseInstallation(Integer.parseInt(event.getLoginData().user.id));

                        Intent main = new Intent(mActivity,MainActivity.class);
                        startActivity(main);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });




    }

    private void fbAuthVM(final String fbId, final String fbToken) throws IOException {

        final RequestBody formBody = new FormEncodingBuilder()
                .add("access_token", fbToken)
                .build();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("http://api.vdomax.com/1.0/fbAuth")
                        .post(formBody)
                        .build();

                Response response = null;
                try {
                    response = VMApplication.getHttpClient().newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!response.isSuccessful()) {
                    ApiBus.getInstance().post(new LoginFailedAuthEvent());
                } else {
                    Gson gson = new Gson();
                    LoginData loginData = null;
                    try {
                        loginData = gson.fromJson(response.body().charStream(), LoginData.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    LoginSuccessEvent event = new LoginSuccessEvent(loginData);

                    ChatApp.USER_TOKEN = event.getLoginData().token;
                    Log.e("USER_TOKEN", ChatApp.USER_TOKEN);

                    user.put("user_id",Integer.parseInt(event.getLoginData().user.id));
                    user.setUsername(event.getLoginData().user.username);

                    try {
                        user.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    prefManager
                            .name().put(event.getLoginData().user.name)
                            .username().put(event.getLoginData().user.username)
                            .userId().put(event.getLoginData().user.id)
                            .token().put(event.getLoginData().token)
                            .cover().put(event.getLoginData().user.cover)
                            .avatar().put(event.getLoginData().user.avatar)
                            .isLogin().put(true)
                            .commit();

                    prefManager
                            .fbToken().put(fbToken)
                            .fbId().put(fbId).commit();

                    Log.e("VM_PROFILE", event.getLoginData().user.toString());

                    VMApplication.updateParseInstallation(Integer.parseInt(event.getLoginData().user.id));

                    Intent main = new Intent(mActivity,MainActivity.class);
                    startActivity(main);

                    //Intent main = new Intent(getActivity(),LandingActivity.class);
                    //getActivity().startActivity(main);

                    //UserProfile user = event.getLoginData().user;
                    //ApiBus.getInstance().post(new UpdateProfileEvent(user));

                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        } else {
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

}
