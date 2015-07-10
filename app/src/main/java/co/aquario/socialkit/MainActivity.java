package co.aquario.socialkit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.soundcloud.android.crop.Crop;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.activity.LoginActivity;
import co.aquario.socialkit.activity.PostPhotoActivity;
import co.aquario.socialkit.activity.PostVideoActivity;
import co.aquario.socialkit.event.ActivityResultEvent;
import co.aquario.socialkit.event.toolbar.SubTitleEvent;
import co.aquario.socialkit.event.toolbar.TitleEvent;
import co.aquario.socialkit.fragment.LiveHistoryFragment;
import co.aquario.socialkit.fragment.PhotoFragmentGrid;
import co.aquario.socialkit.fragment.SettingFragment;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.tabpager.ChannelViewPagerFragment;
import co.aquario.socialkit.fragment.tabpager.HomeViewPagerFragment;
import co.aquario.socialkit.fragment.tabpager.PhotoViewPagerFragment;
import co.aquario.socialkit.fragment.tabpager.SocialViewPagerFragment;
import co.aquario.socialkit.fragment.tabpager.VideoViewPagerFragment;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.util.EndpointManager;
import co.aquario.socialkit.util.PathManager;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.RoundedTransformation;


public class MainActivity extends BaseActivity implements BaseFragment.SearchListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final int RESULT_PICK_VIDEO = 4;
    private static final int RESULT_VIDEO_CAP = 5;
    private static final int PHOTO_SIZE_WIDTH = 100;
    private static final int PHOTO_SIZE_HEIGHT = 100;
    public Toolbar toolbar;
    public ActionBarDrawerToggle toggle;
    public DrawerLayout mDrawer;
    List<WeakReference<Fragment>> fragList = new ArrayList<>();
    File tempFile;
    private Drawer result = null;
    private Context mContext;
    private Activity mActivity;
    private String userId;
    private Uri mFileURI = null;

    public PrefManager pref;

    @Override
    protected void onResume() {
        super.onResume();
        getToolbar().setTitle("VDOMAX");
        getToolbar().setSubtitle("@" + pref.username().getOr("null"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mActivity = this;

        pref = getPref(getApplicationContext());
        userId = pref.userId().getOr("0");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, NewProfileActivity.class);
                i.putExtra("user_id",userId);
                startActivity(i);

            }
        });

        if (savedInstanceState == null) {
            HomeViewPagerFragment fragment = new HomeViewPagerFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.sub_container, fragment);
            transaction.commit();
        }

        initDrawer(savedInstanceState);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<Fragment> getActiveFragments() {
        ArrayList<Fragment> ret = new ArrayList<Fragment>();
        for (WeakReference<Fragment> ref : fragList) {
            Fragment f = ref.get();
            if (f != null) {
                if (f.isVisible()) {
                    ret.add(f);
                }
            }
        }
        return ret;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragList.add(new WeakReference<Fragment>(fragment));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (fragList.size() == 0)
            finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultBus.getInstance().postQueue(
                new ActivityResultEvent(requestCode, resultCode, data));

        if (resultCode == Activity.RESULT_OK) {
            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (requestCode == REQUEST_TAKE_PHOTO) {


                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    tempFile = f;
                    String path = f.getAbsolutePath();
                    bm = BitmapFactory.decodeFile(path, btmapOptions);

                    int rotate = getCameraPhotoOrientation(path);


                    Intent postPhotoIntent = new Intent(this,
                            PostPhotoActivity.class);
                    postPhotoIntent.putExtra("photo", path);
                    postPhotoIntent.putExtra("rotate", rotate + "");
                    // Toast.makeText(context, "rotate:" + rotate + "",
                    // Toast.LENGTH_SHORT).show();

                    startActivity(postPhotoIntent);

                    OutputStream fOut = null;
                    File file = new File(path);
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                        fOut.flush();
                        fOut.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            } else if (requestCode == REQUEST_CHOOSE_PHOTO) {

                Uri selectedImageUri = data.getData();

                String path = PathManager.getRealPathFromURIForKitKat(mContext, selectedImageUri);

                int rotate = getCameraPhotoOrientation(path);
                Intent postPhotoIntent = new Intent(this,
                        PostPhotoActivity.class);

                Log.e("pathpath", path);

                postPhotoIntent.putExtra("photo", path);
                postPhotoIntent.putExtra("rotate", rotate + "");
                startActivity(postPhotoIntent);

            } else if (requestCode == RESULT_PICK_VIDEO) {

                mFileURI = data.getData();
                if (mFileURI != null) {
                    Intent intent = new Intent(mContext,
                            PostVideoActivity.class);
                    intent.setData(mFileURI);
                    startActivity(intent);
                }

            } else if (requestCode == RESULT_VIDEO_CAP) {

                mFileURI = data.getData();
                if (mFileURI != null) {
                    Intent intent = new Intent(mContext,
                            PostVideoActivity.class);
                    intent.setData(mFileURI);
                    startActivity(intent);
                }

            } else if(requestCode == Crop.REQUEST_CROP) {

            }
        }
    }

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Change avatar!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    // intent.putExtra("crop", "true");
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                } else if (items[item].equals("Choose from Library")) {

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
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void startCropImage(String path) {

        //Crop.of(path, path).asSquare().start(activity);
    }

    public void selectVideo() {
        final CharSequence[] items = {"Record Video", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Video!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Record Video")) {
                    recordVideo();
                } else if (items[item].equals("Choose from Library")) {
                    pickFile();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void pickFile() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, RESULT_PICK_VIDEO);
    }

    public void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, RESULT_VIDEO_CAP);
    }

    public int getCameraPhotoOrientation(String imagePath) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    @Override
    public void onSearchQuery(String query) {
        Utils.hideKeyboard(this);
        SearchActivity.startActivity(getApplicationContext(),query);

    }

    public void initDrawer(Bundle savedInstanceState) {

        View header = LayoutInflater.from(getApplication()).inflate(R.layout.header_drawer, null);
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)

                .withHeader(header)
                //.withAccountHeader(headerResult.build())
                .withActionBarDrawerToggle(true)
                .withTranslucentStatusBar(true)
                //.withAnimateDrawerItems(true)
//                .addStickyDrawerItems(
//                        toolbar PrimaryDrawerItem().withName("Channels").withIcon(FontAwesome.Icon.faw_terminal),
//                        toolbar PrimaryDrawerItem().withName("Social").withIcon(FontAwesome.Icon.faw_users),
//                        toolbar PrimaryDrawerItem().withName("Videos").withIcon(FontAwesome.Icon.faw_video_camera),
//                        toolbar PrimaryDrawerItem().withName("Photos").withIcon(FontAwesome.Icon.faw_camera_retro)
//                        )
                .addDrawerItems(

                        new SectionDrawerItem().withName("Menu"),
                        new SecondaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home),
                        new SecondaryDrawerItem().withName("Live History").withIcon(FontAwesome.Icon.faw_history),
                        new SecondaryDrawerItem().withName("Setting").withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName("Maxpoint").withIcon(FontAwesome.Icon.faw_btc),
                        new SecondaryDrawerItem().withName("Tattoo Store").withIcon(FontAwesome.Icon.faw_shopping_cart).setEnabled(false),
                        new SecondaryDrawerItem().withName("Term & Policies").withIcon(FontAwesome.Icon.faw_terminal),
                        new SecondaryDrawerItem().withName("Log Out").withIcon(FontAwesome.Icon.faw_sign_out)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {

                        if (((Nameable) drawerItem).getName().equals("Channels")) {

                            ChannelViewPagerFragment fragment = new ChannelViewPagerFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "CHANNEL_MAIN").addToBackStack(null).commit();

                        } else if (((Nameable) drawerItem).getName().equals("Social")) {

                            SocialViewPagerFragment fragment = new SocialViewPagerFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "SOCIAL_MAIN").addToBackStack(null).commit();

                        } else if (((Nameable) drawerItem).getName().equals("Videos")) {

                            VideoViewPagerFragment fragment = new VideoViewPagerFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "VIDEO_MAIN").addToBackStack(null).commit();

                        } else if (((Nameable) drawerItem).getName().equals("Photos")) {

                            PhotoFragmentGrid fragment = new PhotoFragmentGrid();
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "PHOTO_MAIN").addToBackStack(null).commit();

                            //GalleryFragment fragment = GalleryFragment.newInstance("","","");
                            //getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "PHOTO_MAIN").addToBackStack(null).commit();

//                            PhotoFragmentGrid fragment = toolbar PhotoFragmentGrid();
//
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                            transaction.replace(R.id.sub_container, fragment, "PHOTO_MAIN");
//                            transaction.addToBackStack(null);
//                            transaction.commit();

                            // Intent i = toolbar Intent(MainActivity.this, PhotoDetailActivity.class);
                            //startActivity(i);
                        } else if (((Nameable) drawerItem).getName().equals("Home")) {

                            HomeViewPagerFragment fragment = new HomeViewPagerFragment();
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.sub_container, fragment);
                            transaction.commit();

                        } else if (((Nameable) drawerItem).getName().equals("Live History")) {

                            LiveHistoryFragment fragment = LiveHistoryFragment.newInstance(userId);
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "LIVE_HISTORY").addToBackStack(null).commit();

                        } else if (((Nameable) drawerItem).getName().equals("Setting")) {

                            SettingFragment fragment = SettingFragment.newInstance(userId);
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "SETTINGS").addToBackStack(null).commit();

                        } else if (((Nameable) drawerItem).getName().equals("Log Out")) {
                            MainApplication.logout();
                            Intent login = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(login);
                            finish();
                        }

                        return true;
                    }
                })
                .withTranslucentActionBarCompatibility(false)
                .withSavedInstance(savedInstanceState)
                .build();

        toggle = result.getActionBarDrawerToggle();
        mDrawer = result.getDrawerLayout();

        ImageView channelMenu = (ImageView) result.getHeader().findViewById(R.id.channel_menu);
        ImageView sociallMenu = (ImageView) result.getHeader().findViewById(R.id.social_menu);
        ImageView videoMenu = (ImageView) result.getHeader().findViewById(R.id.video_menu);
        ImageView photoMenu = (ImageView) result.getHeader().findViewById(R.id.photo_menu);

        ImageView avatarMenu = (ImageView) result.getHeader().findViewById(R.id.header_avatar);
        TextView nameMenu = (TextView) result.getHeader().findViewById(R.id.header_name);
        TextView usernameMenu = (TextView) result.getHeader().findViewById(R.id.header_username);

        Picasso.with(this).load(EndpointManager.getAvatarPath(pref.avatar().getOr(""))).placeholder(R.drawable.avatar_default).centerCrop()
                .resize(100, 100).transform(new RoundedTransformation(50, 4)).into(avatarMenu);
        usernameMenu.setText("@" + pref.username().getOr("null"));
        nameMenu.setText(pref.name().getOr("null"));

        avatarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, NewProfileActivity.class);
                i.putExtra("user_id", pref.userId().getOr("0"));
                mActivity.startActivity(i);

            }
        });


        getToolbar().setTitle("VDOMAX");
        //getToolbar().setSubtitle("@" + pref.username().getOr("null"));

        channelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChannelViewPagerFragment fragment = new ChannelViewPagerFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "CHANNEL_MAIN").addToBackStack(null).commit();

                getToolbar().setTitle("VDOMAX");
                getToolbar().setSubtitle("Channels");
                result.closeDrawer();

            }
        });

        sociallMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocialViewPagerFragment fragment = new SocialViewPagerFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "SOCIAL_MAIN").addToBackStack(null).commit();

                getToolbar().setTitle("VDOMAX");
                getToolbar().setSubtitle("Social");
                result.closeDrawer();
            }
        });

        videoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoViewPagerFragment fragment = new VideoViewPagerFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "VIDEO_MAIN").addToBackStack(null).commit();

                getToolbar().setTitle("VDOMAX");
                getToolbar().setSubtitle("Videos");
                result.closeDrawer();
            }
        });

        photoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                PhotoViewPagerFragment fragment = new PhotoViewPagerFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "PHOTO_MAIN").addToBackStack(null).commit();


                getToolbar().setTitle("VDOMAX");
                getToolbar().setSubtitle("Photos");
                result.closeDrawer();
            }
        });
    }

    @Subscribe
    public void onSetTitle(TitleEvent event) {
        getToolbar().setTitle(event.str);
    }

    @Subscribe public void onSetTitle(SubTitleEvent event) {
        getToolbar().setSubtitle(event.str);

    }
}
