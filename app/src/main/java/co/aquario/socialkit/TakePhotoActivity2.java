package co.aquario.socialkit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.activity.post.PostPhotoActivity;
import co.aquario.socialkit.util.PathManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.view.RevealBackgroundView;
import me.isming.tools.cvfilter.library.FilterFactory;
import me.isming.tools.cvfilter.library.ICVFilter;
import me.isming.tools.cvfilter.library.ImageData;

/**
 * Created by Miroslaw Stanek on 08.02.15.
 */
public class TakePhotoActivity2 extends AppCompatActivity implements RevealBackgroundView.OnStateChangeListener,
        CameraHostProvider {

    private Bitmap mOriginBitmap, mResultBitmap;
    private ImageData mImageData;
    private boolean isTakenPhoto = false;
    public boolean filterSelected = false;

    private static boolean USE_FFC = false;

    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    public static final String ARG_USE_FFC = "user_front_camera";

    private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final int STATE_TAKE_PHOTO = 0;
    private static final int STATE_SETUP_PHOTO = 1;

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

    private boolean pendingIntro;
    private int currentState;

    private File photoPath;

    @InjectView(R.id.btnChoosePhoto)
    ImageButton btnChoosePhoto;
    @InjectView(R.id.btnFrontCamera)
    ImageButton btnFrontCamera;

    public static int[] myStartingLocation;

    public static void startCameraFromLocation(int[] startingLocation, Activity startingActivity,boolean frontCamera) {
        myStartingLocation = startingLocation;

        Intent intent = new Intent(startingActivity, TakePhotoActivity2.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(ARG_USE_FFC, frontCamera);
        startingActivity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo2);
        ButterKnife.inject(this);
        updateStatusBarColor();
        updateState(STATE_TAKE_PHOTO);
        setupRevealBackground(savedInstanceState);
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
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff111111);
        }
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setFillPaintColor(0xFF16181a);
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
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
        final TakePhotoActivity2.PhotoFiltersAdapter photoFiltersAdapter = new TakePhotoActivity2.PhotoFiltersAdapter(this, FilterFactory.createFilters(this));
        rvFilters.setHasFixedSize(true);
        rvFilters.setAdapter(photoFiltersAdapter);
        rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraView.onResume();
        filterSelected = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    @OnClick(R.id.btnTakePhoto)
    public void onTakePhotoClick() {
        isTakenPhoto = true;
        btnTakePhoto.setEnabled(false);
        cameraView.takePicture(true, true);
        animateShutter();
    }

    @OnClick(R.id.btnAccept)
    public void onAcceptClick()  {



        if(filterSelected) {
            OutputStream fOut = null;

            try {
                fOut = new FileOutputStream(photoPath);
                mImageData.getResult().compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush();
                fOut.close(); // do not forget to close the stream

                // save image in camera roll
                MediaStore.Images.Media.insertImage(getContentResolver(),photoPath.getAbsolutePath(),photoPath.getName(),photoPath.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //PublishActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));
        PostPhotoActivity.openWithPhotoUri(this, Uri.fromFile(photoPath));


    }

    @OnClick(R.id.btnChoosePhoto)
    public void onChoosePhotoClick() {
        isTakenPhoto = false;
        choosePhoto();
        //animateShutter();
    }

    @OnClick(R.id.btnFrontCamera)
    public void onSelectFrontCamera() {
        TakePhotoActivity.startCameraFromLocation(myStartingLocation,this,!USE_FFC);
        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_PHOTO) {

            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                photoPath = new File(PathManager.getPath(getApplicationContext(), selectedImageUri));
                showTakenPicture(bitmap);
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

    @Override
    public CameraHost getCameraHost() {
        return new MyCameraHost(this);
    }

    class MyCameraHost extends SimpleCameraHost {

        private Camera.Size previewSize;

        public MyCameraHost(Context ctxt) {
            super(ctxt);
        }

        @Override
        public boolean useFullBleedPreview() {
            return true;
        }

        @Override
        public Camera.Size getPictureSize(PictureTransaction xact, Camera.Parameters parameters) {
            return previewSize;
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            Camera.Parameters parameters1 = super.adjustPreviewParameters(parameters);
            previewSize = parameters1.getPreviewSize();
            return parameters1;
        }

        @Override
        public void saveImage(PictureTransaction xact, final Bitmap bitmap) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        showTakenPicture(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            super.saveImage(xact, image);
            photoPath = getPhotoPath();
        }
    }

    private void showTakenPicture(Bitmap bitmap) throws IOException {
        vUpperPanel.showNext();
        vLowerPanel.showNext();

        if(!isTakenPhoto)
        bitmap = ImageData.decodeSampledBitmapFromBitmap(photoPath.getPath(),400,600);

        ivTakenPhoto.setImageBitmap(bitmap);

        mOriginBitmap = bitmap;
        mImageData = new ImageData(mOriginBitmap);



        updateState(STATE_SETUP_PHOTO);
    }

    @Override
    public void onBackPressed() {
        if (currentState == STATE_SETUP_PHOTO) {
            btnTakePhoto.setEnabled(true);
            vUpperPanel.showNext();
            vLowerPanel.showNext();
            updateState(STATE_TAKE_PHOTO);
        } else {
            super.onBackPressed();
        }
    }

    private void updateState(int state) {
        currentState = state;
        if (currentState == STATE_TAKE_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_right);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_left);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ivTakenPhoto.setVisibility(View.GONE);
                }
            }, 400);
        } else if (currentState == STATE_SETUP_PHOTO) {
            vUpperPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vLowerPanel.setInAnimation(this, R.anim.slide_in_from_left);
            vUpperPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            vLowerPanel.setOutAnimation(this, R.anim.slide_out_to_right);
            ivTakenPhoto.setVisibility(View.VISIBLE);
        }
    }



    public class PhotoFiltersAdapter extends RecyclerView.Adapter<PhotoFiltersAdapter.ViewHolder> {

        private Context mContext;
        //private int itemsCount = 12;

        private ICVFilter[] mFilters;
        private int mSelectItem;
        private int mCurrentFilterPosition;


        public PhotoFiltersAdapter(Context context, ICVFilter[] filters) {

            this.mContext = context;
            this.mFilters = filters;
        }

        @Override
        public int getItemCount() {
            return mFilters == null ? 0 : mFilters.length;
        }


        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public PhotoFiltersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View view = LayoutInflater.from(mContext).inflate(R.layout.item_filter_preview, parent, false);



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectItem = nowItem;
                    mCurrentFilterPosition = nowItem;


                    // memory leak
                    ImageData d = filterContent.convert(mImageData);
                    d.createResult();
                    mResultBitmap = d.getResult();
                    ivTakenPhoto.setImageBitmap(mResultBitmap);

                    notifyDataSetChanged();
                }
            });


            return new ViewHolder(view);
        }

        public void selectItem(int position) {
            final ICVFilter filterContent = mFilters[position];
            mSelectItem = position;
            mCurrentFilterPosition = position;
            ImageData d = filterContent.convert(mImageData);
            d.createResult();
            mResultBitmap = d.getResult();
            ivTakenPhoto.setImageBitmap(mResultBitmap);
            notifyDataSetChanged();
        }

        int nowItem;
        ICVFilter filterContent;

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            filterContent = mFilters[position];
            nowItem = position;
            holder.imageView.setImageResource(filterContent.getResId());
            holder.titleView.setText(filterContent.getName());
            if (mSelectItem == position) {
                holder.titleView.setTextColor(mContext.getResources().getColor(R.color.md_blue_grey_500));
            } else {
                holder.titleView.setTextColor(mContext.getResources().getColor(R.color.md_blue_grey_400));
            }




        }



        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @InjectView(R.id.image)
            ImageView imageView;
            @InjectView(R.id.title)
            TextView titleView;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.inject(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                selectItem(getPosition());
                filterSelected = true;
            }
        }

    }
}
