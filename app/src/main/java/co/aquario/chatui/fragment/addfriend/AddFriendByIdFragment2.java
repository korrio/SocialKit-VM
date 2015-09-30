package co.aquario.chatui.fragment.addfriend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.chatapp.event.request.SearchUserEvent;
import co.aquario.chatui.QRFragment;
import co.aquario.chatui.event_chat.response.SearchUserNotFoundEvent;
import co.aquario.chatui.utils.EndpointManager;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.ActivityResultEvent;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.widget.RoundedTransformation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class AddFriendByIdFragment2 extends DialogFragment {
    public static AddFriendByIdFragment2 newInstance() {
        AddFriendByIdFragment2 fragment = new AddFriendByIdFragment2();
        return fragment;
    }

    public static AddFriendByIdFragment2 newInstance(String username) {
        AddFriendByIdFragment2 fragment = new AddFriendByIdFragment2();
        Bundle data = new Bundle();
        data.putString("USERNAME",username);
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            userId = VMApp.mPref.userId().getOr("0");
            searchTerm = getArguments().getString("USERNAME");

            performSearch(searchTerm);
        }
        prefManager = VMApp.get(getActivity()).getPrefManager();
        // Initialize items
    }
    @InjectView(R.id.qr)
    public ImageView qr;

    @InjectView(R.id.usernameTv)
    public TextView usernameTv;

    @InjectView(R.id.searchUsernameEt)
    public EditText searchUsernameEt;

    @InjectView(R.id.user_detail_frame)
    public RelativeLayout userDetailFrame;

    @InjectView(R.id.avatar)
    public ImageView avatar;

    @InjectView(R.id.searchUsernameTv)
    public TextView searchUsernameTv;

    @InjectView(R.id.btn_follow)
    public Button btnFollow;

    @OnClick(R.id.btn_follow)
    public void onFollow() {
        if (isFollowing) {
            toggleUnfollow(btnFollow);
        } else {
            toggleFollowing(btnFollow);
        }
        ApiBus.getInstance().post(new FollowRegisterEvent(searchUserId + ""));
        isFollowing = !isFollowing;
    }

    @OnClick(R.id.avatar)
    public void avatarClick(){
        Intent i = new Intent(getActivity(), NewProfileActivity.class);
        i.putExtra("USER_ID",searchUserId + "");
        getActivity().startActivity(i);
    }

    String searchTerm;
    String userId;

    void performSearch(String searchTerm) {
        ApiBus.getInstance().postQueue(new SearchUserEvent(searchTerm, userId));
    }

    @InjectView(R.id.scanBtn)
    Button scanBtn;

    @InjectView(R.id.showBtn)
    Button showBtn;

    @OnClick(R.id.scanBtn)
    public void goToScanQR() {
        new IntentIntegrator(mActivity).initiateScan();
        dismiss();
    }

    @OnClick(R.id.showBtn)
    public void goToMyQR() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.frameFragment, QRFragment.newInstance(VMApp.mPref.userId().getOr("")));
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }



    public Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Remove the title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.inject(this, v);
        Toast.makeText(getActivity(),"QR",Toast.LENGTH_LONG).show();
        mActivity = getActivity();

        userDetailFrame.setVisibility(View.GONE);
        Picasso.with(getActivity()).load(EndpointManager.getPath(VMApp.mPref.avatar().getOr("null"))).centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(qr);

        //qr.setImageBitmap(ChatUIActivity.encodeToQrCode(VMApp.mPref.username().getOr(""),100,100));

        usernameTv.setText(VMApp.mPref.username().getOr("null"));
        searchUsernameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchTerm = searchUsernameEt.getText().toString().trim();
                    performSearch(searchTerm);
                    return true;
                }
                return false;
            }
        });

        ImageView cancelButton = (ImageView) v.findViewById(R.id.x_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                getDialog().dismiss();
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make Dialog fill parent width and height
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ApiBus.getInstance().register(mActivityResultSubscriber);
    }

    @Subscribe
    public void onSearchUsernameNotFound(SearchUserNotFoundEvent event) {
        userDetailFrame.setVisibility(View.GONE);
    }

    boolean isFollowing = false;
    int searchUserId;

    public void initButton(boolean following, View v) {
        Button button = (Button) v;

        isFollowing = following;

        if (following) {
            toggleFollowing(button);
        } else {
            toggleUnfollow(button);
        }

        //isFollowing = !isFollowing;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onStop() {
        super.onStop();
        ApiBus.getInstance().unregister(mActivityResultSubscriber);
    }

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
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save State Here
        saveStateToArguments();
    }
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

}
