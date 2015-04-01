package co.aquario.socialkit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.UpdateProfileEvent;
import co.aquario.socialkit.fragment.LoginFragment;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.model.UserProfile;
import co.aquario.socialkit.util.Markup;
import co.aquario.socialkit.util.PrefManager;

public class LoginActivity extends ActionBarActivity {

    public PrefManager prefManager;
    public boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        prefManager = MainApplication.get(this).getPrefManager();
        setContentView(R.layout.activity_login);

        isLogin = prefManager.isLogin().getOr(false);

        Log.e("isLogin/LoginActivity","::"+isLogin);
        try {
            Log.v("preg_match", Markup.getLink("[a]https://www.vdomax.com[/a]"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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

    boolean doubleBackToExitPressedOnce = false;

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.search.menu_login, search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
