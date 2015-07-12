package co.aquario.socialkit.fragment.main;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;

import com.squareup.otto.Subscribe;

import co.aquario.socialkit.VMApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.ActivityResultEvent;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.PrefManager;

public abstract class BaseFragment extends Fragment {

    public PrefManager prefManager;
    public boolean mSearchCheck = false;
    private Object mActivityResultSubscriber = new Object() {
        @Subscribe
        public void onActivityResultReceived(ActivityResultEvent event) {
            int requestCode = event.getRequestCode();
            int resultCode = event.getResultCode();
            Intent data = event.getData();
            onActivityResult(requestCode, resultCode, data);
        }
    };
    private Bundle savedState;


    public BaseFragment() {
        super();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = VMApplication.get(getActivity()).getPrefManager();
    }

    @Override
    public void onResume() {
        ApiBus.getInstance().register(this);
        ActivityResultBus.getInstance().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        ApiBus.getInstance().unregister(this);
        ActivityResultBus.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Restore State Here
        if (!restoreStateFromArguments()) {
            // First Time, Initialize something here
            onFirstTimeLaunched();
        }
    }

    protected void onFirstTimeLaunched() {

    }

    ////////////////////
    // Don't Touch !!
    ////////////////////

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save State Here
        saveStateToArguments();
    }


    ////////////////////
    // Don't Touch !!
    ////////////////////

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Save State Here
        saveStateToArguments();
    }

    /////////////////////////////////
    // Restore Instance State Here
    /////////////////////////////////

    private void saveStateToArguments() {
        if (getView() != null)
            savedState = saveState();
        if (savedState != null) {
            Bundle b = getArguments();
            //b.putBundle("internalSavedViewState8954201239547", savedState);
        }
    }

    private boolean restoreStateFromArguments() {
        Bundle b = getArguments();
        //savedState = b.getBundle("internalSavedViewState8954201239547");
        if (savedState != null) {
            restoreState();
            return true;
        }
        return false;
    }

    //////////////////////////////
    // Save Instance State Here
    //////////////////////////////

    private void restoreState() {
        if (savedState != null) {
            // For Example
            //tv1.setText(savedState.getString("text"));
            onRestoreState(savedState);
        }
    }

    protected void onRestoreState(Bundle savedInstanceState) {

    }

    private Bundle saveState() {
        Bundle state = new Bundle();
        // For Example
        //state.putString("text", tv1.getText().toString());
        onSaveState(state);
        return state;
    }

    protected void onSaveState(Bundle outState) {

    }

    public void toggleFollowing(Button v) {
        v.setTextColor(Color.parseColor("#ffffff"));
        //v.setText(Html.fromHtml("&#x2713; FOLLOWING"));
        v.setText(Html.fromHtml("FOLLOWING"));

        // change state
        v.setSelected(true);
        v.setPressed(false);

    }

    public void toggleUnfollow(Button v) {
        v.setTextColor(Color.parseColor("#2C6497"));
        v.setText("+ FOLLOW");

        // change state
        v.setSelected(false);
        v.setPressed(false);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                mSearchCheck = true;
                break;
        }
        return true;
    }

    public interface SearchListener {
        void onSearchQuery(String query);
    }





}



