package co.aquario.socialkit.activity.search;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;

/**
 * Created by Mac on 7/30/15.
 */
public class SearchActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_main_search);
        SearchPagerFragment fragment = new SearchPagerFragment().newInstance(VMApp.mPref.userId().getOr("0"),"ทดสอบ");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
        transaction.commit();
    }
}
