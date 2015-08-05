package co.aquario.socialkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.fragment.newuser.EditProfileFragment;
import co.aquario.socialkit.fragment.newuser.RegisterSuccessFragment;
import co.aquario.socialkit.fragment.pager.SocialViewPagerFragment;
import co.aquario.socialkit.util.Utils;

public class MyIntro extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        EditProfileFragment fragment0 = new EditProfileFragment();
        addSlide(fragment0);
        RegisterSuccessFragment fragment = new RegisterSuccessFragment();
        addSlide(fragment);
        SocialViewPagerFragment fragment2 = new SocialViewPagerFragment();
        addSlide(fragment2);
        addSlide(AppIntroFragment.newInstance("Completed !", "You are now ready to Live on VDOMAX", R.drawable.tt0101, R.color.white));
        //addSlide(fragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
       // addSlide(AppIntroFragment.newInstance(title, description, image, background_colour));

        // OPTIONAL METHODS
        // Override bar/separator color
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button
        showSkipButton(false);
        showDoneButton(true);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        uploadProfile(VMApp.mPref.userId().getOr("0"));
    // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        uploadProfile(VMApp.mPref.userId().getOr("0"));
    // Do something when users tap on Done button.
    }

    private void uploadProfile(String userId) {
        Charset chars = Charset.forName("UTF-8");
        String url = "http://api.vdomax.com/user/update/" + userId;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", VMApp.mPref.username().getOr(""));
        params.put("name", VMApp.mPref.name().getOr(""));
        params.put("about", VMApp.mPref.about().getOr(""));
        params.put("email", VMApp.mPref.email().getOr(""));

        AQuery aq = new AQuery(this);
        aq.ajax(url, params, JSONObject.class, this, "updateProfileCb");
    }
    public void updateProfileCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("hahaha", jo.toString(4));
        Utils.showToast("Update complete!");
        finish();
    }
}