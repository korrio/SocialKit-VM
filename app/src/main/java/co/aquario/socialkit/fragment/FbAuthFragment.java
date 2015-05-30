package co.aquario.socialkit.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.FbAuthEvent;
import co.aquario.socialkit.event.LoadFbProfileEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.PrefManager;

/**
 * Created by Mac on 3/4/15.
 */
public class FbAuthFragment extends BaseFragment {

    PrefManager prefManager;

    public FbAuthFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = MainApplication.get(getActivity().getApplicationContext()).getPrefManager();
        Log.e("FB_SEND_VM_API",prefManager.fbToken().getOr(""));
        Log.e("FB_SEND_VM_API",prefManager.fbId().getOr(""));

        String fbToken = prefManager.fbToken().getOr("");

        ApiBus.getInstance().register(this);


        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_thinking, container, false);
    }



    @Subscribe
    public void onLoadFbProfileEvent(LoadFbProfileEvent event) {
        Log.e("FB_SEND_VM_API", "START FB LOGIN WITH VM");
        Log.e("FB_SEND_VM_API",event.facebookToken);
        Log.e("FB_SEND_VM_API",event.profile.id);
        ApiBus.getInstance().post(new FbAuthEvent(event.facebookToken));
    }




}
