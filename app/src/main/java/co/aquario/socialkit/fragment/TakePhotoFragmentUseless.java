package co.aquario.socialkit.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.camera.CameraView;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.PostPhotoActivity;
import co.aquario.socialkit.adapter.view.PhotoFiltersAdapter;
import co.aquario.socialkit.util.PathManager;
import co.aquario.socialkit.view.RevealBackgroundView;


public class TakePhotoFragmentUseless extends BaseFragment implements RevealBackgroundView.OnStateChangeListener
         {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

    private static boolean KEY_USE_FFC = false;

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.vPhotoRoot)
    View vTakePhotoRoot;
    @InjectView(R.id.vShutter)
    View vShutter;
    @InjectView(R.id.ivTakenPhoto)
    ImageView ivTakenPhoto;
    @InjectView(R.id.vUpperPanel)
    ViewSwitcher vUpperPanel;
    @InjectView(R.id.vLowerPanel)
    ViewSwitcher vLowerPanel;
    @InjectView(R.id.cameraView)
    CameraView cameraView;
    @InjectView(R.id.rvFilters)
    RecyclerView rvFilters;
    @InjectView(R.id.btnTakePhoto)
    Button btnTakePhoto;

    @InjectView(R.id.btnChoosePhoto)
    ImageButton btnChoosePhoto;

    private boolean pendingIntro;
    private int currentState;

    private File photoPath;



    public static TakePhotoFragmentUseless newInstance(int[] startingLocation,Activity startingActivity){
        //startCameraFromLocation(startingLocation,startingActivity);
        TakePhotoFragmentUseless mFragment = new TakePhotoFragmentUseless();
        Bundle mBundle = new Bundle();
        //mBundle.putIntArray("AAA", startingLocation);
        //mBundle.put("BBB", startingLocation);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (currentState == STATE_SETUP_PHOTO) {
                    btnTakePhoto.setEnabled(true);
                    vUpperPanel.showNext();
                    vLowerPanel.showNext();
                    updateState(STATE_TAKE_PHOTO);
                } else {
                    getActivity().onBackPressed();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_take_photo, container, false);
        ButterKnife.inject(this,rootView);

        updateState(STATE_TAKE_PHOTO);
        //setupRevealBackground(savedInstanceState);
        setupPhotoFilters();

        vUpperPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                vUpperPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                pendingIntro = true;
                vUpperPanel.setTranslationY(-vUpperPanel.getHeight());
                vLowerPanel.setTranslationY(vLowerPanel.getHeight());
                return true;
            }
        });
        return rootView;
    }


    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getActivity().getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
        }
    }

    private void setupPhotoFilters() {
        PhotoFiltersAdapter photoFiltersAdapter = new PhotoFiltersAdapter(getActivity());
        rvFilters.setHasFixedSize(true);
        rvFilters.setAdapter(photoFiltersAdapter);
        rvFilters.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }



    @Override
    public void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    @OnClick(R.id.btnTakePhoto)
    public void onTakePhotoClick() {
        btnTakePhoto.setEnabled(false);
        cameraView.takePicture(true, true);
        animateShutter();
    }

    @OnClick(R.id.btnAccept)
    public void onAcceptClick() {
        PostPhotoActivity.openWithPhotoUri(getActivity(), Uri.fromFile(photoPath));
        getActivity().finish();
    }

    @OnClick(R.id.btnChoosePhoto)
    public void onChoosePhotoClick() {
        choosePhoto();
        //animateShutter();
    }

    private static final int PHOTO_SIZE_WIDTH = 100;
    private static final int PHOTO_SIZE_HEIGHT = 100;
    private static final int REQUEST_CHOOSE_PHOTO = 2;

    private void choosePhoto() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                "image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        // intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", PHOTO_SIZE_WIDTH);
        intent.putExtra("outputY", PHOTO_SIZE_HEIGHT);
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PHOTO) {

            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                showTakenPicture(bitmap);
                photoPath = new File(PathManager.getPath(getActivity().getApplicationContext(), selectedImageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void animateShutter() {
        vShutter.setVisibility(View.VISIBLE);
        vShutter.setAlpha(0.f);

        ObjectAnimator alphaInAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0f, 0.8f);
        alphaInAnim.setDuration(100);
        alphaInAnim.setStartDelay(100);
        alphaInAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator alphaOutAnim = ObjectAnimator.ofFloat(vShutter, "alpha", 0.8f, 0f);
        alphaOutAnim.setDuration(200);
        alphaOutAnim.setInterpolator(DECELERATE_INTERPOLATOR);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                vShutter.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            vTakePhotoRoot.setVisibility(View.VISIBLE);
            if (pendingIntro) {
                startIntroAnimation();
            }
        } else {
            vTakePhotoRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void startIntroAnimation() {
        vUpperPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR);
        vLowerPanel.animate().translationY(0).setDuration(400).setInterpolator(DECELERATE_INTERPOLATOR).start();
    }





    private void showTakenPicture(Bitmap bitmap) {
        vUpperPanel.showNext();
        vLowerPanel.showNext();
        ivTakenPhoto.setImageBitmap(bitmap);
        updateState(STATE_SETUP_PHOTO);
    }

    private void updateState(int state) {
        currentState = state;
        if (currentState == STATE_TAKE_PHOTO) {
            vUpperPanel.setInAnimation(getActivity(), R.anim.slide_in_from_right);
            vLowerPanel.setInAnimation(getActivity(), R.anim.slide_in_from_right);
            vUpperPanel.setOutAnimation(getActivity(), R.anim.slide_out_to_left);
            vLowerPanel.setOutAnimation(getActivity(), R.anim.slide_out_to_left);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);
        } else if (currentState == STATE_SETUP_PHOTO) {
            vUpperPanel.setInAnimation(getActivity(), R.anim.slide_in_from_left);
            vLowerPanel.setInAnimation(getActivity(), R.anim.slide_in_from_left);
            vUpperPanel.setOutAnimation(getActivity(), R.anim.slide_out_to_right);
            vLowerPanel.setOutAnimation(getActivity(), R.anim.slide_out_to_right);
            ivTakenPhoto.setVisibility(View.VISIBLE);
        }
    }
}
