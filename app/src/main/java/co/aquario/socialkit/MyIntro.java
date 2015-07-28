package co.aquario.socialkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import co.aquario.socialkit.fragment.RegisterSuccessFragment;
import co.aquario.socialkit.fragment.pager.SocialViewPagerFragment;

public class MyIntro extends AppIntro {

    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
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
        showSkipButton(true);
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
        finish();
    // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed() {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        finish();
    // Do something when users tap on Done button.
    }
}