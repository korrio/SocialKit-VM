package co.aquario.socialkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.WindowManager;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.UpdateProfileEvent;
import co.aquario.socialkit.fragment.LoginFragment;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.model.UserProfile;
import co.aquario.socialkit.util.PrefManager;

public class LoginActivity extends ActionBarActivity {

    public PrefManager prefManager;
    public boolean isLogin;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        prefManager = MainApplication.get(this).getPrefManager();
        setContentView(R.layout.activity_login);

        isLogin = prefManager.isLogin().getOr(false);

        if (savedInstanceState == null && !isLogin) {
            getSupportFragmentManager().beginTransaction().add(R.id.login_container, new LoginFragment()).commit();
        } else {
            Intent main = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(main);
            // Updrate drawer
            ActivityResultBus.getInstance().postQueue(new UpdateProfileEvent(new UserProfile()));
            finish();

        }

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
