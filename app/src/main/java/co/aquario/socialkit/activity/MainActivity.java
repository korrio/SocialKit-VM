package co.aquario.socialkit.activity;

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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.nispok.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.view.TimelinePagerAdapter;
import co.aquario.socialkit.event.ActivityResultEvent;
import co.aquario.socialkit.fragment.LiveHistoryFragment;
import co.aquario.socialkit.fragment.MainFragment;
import co.aquario.socialkit.fragment.HomeViewPagerFragment;
import co.aquario.socialkit.fragment.main.ChannelViewPagerFragment;
import co.aquario.socialkit.fragment.main.SocialViewPagerFragment;
import co.aquario.socialkit.fragment.main.VideoViewPagerFragment;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.PathManager;


public class MainActivity extends ActionBarActivity {

    private Drawer.Result result = null;
    private Context context;
    private Activity activity;

    private TimelinePagerAdapter timelinePagerAdapter;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;

        userId = MainApplication.get(this).getPrefManager().userId().getOr("3");
        //new Drawer().withActivity(this).build();
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View header = LayoutInflater.from(getApplication()).inflate(R.layout.header, null);

        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHeader(header)
                .withActionBarDrawerToggle(true)
                .withTranslucentStatusBar(false)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Channels").withIcon(FontAwesome.Icon.faw_terminal),
                        new PrimaryDrawerItem().withName("Social").withIcon(FontAwesome.Icon.faw_users),
                        new PrimaryDrawerItem().withName("Videos").withIcon(FontAwesome.Icon.faw_video_camera),
                        new PrimaryDrawerItem().withName("Photos").withIcon(FontAwesome.Icon.faw_camera_retro),
                        new SectionDrawerItem().withName("Account"),
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
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            Snackbar.with(getApplicationContext()).text(((Nameable) drawerItem).getName()).show(activity);
                        }

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
//                            PhotoFragmentGrid fragment = new PhotoFragmentGrid();
//
//                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                            transaction.replace(R.id.sub_container, fragment, "PHOTO_MAIN");
//                            transaction.addToBackStack(null);
//                            transaction.commit();

                            Intent i = new Intent(getApplicationContext(), MainPhotoViewPager.class);
                            startActivity(i);
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

                        } else if (((Nameable) drawerItem).getName().equals("Log Out")) {
                            MainApplication.logout();
                            Intent login = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(login);
                            finish();
                        }


                    }
                }).build();


        if (savedInstanceState == null) {
            // FeedFragmentWithHeader feedFragment = new FeedFragmentWithHeader();
            MainFragment mainFragment = new MainFragment();
            HomeViewPagerFragment fragment = new HomeViewPagerFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.sub_container, fragment);
            transaction.commit();
            /*
            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, transaction, mainFragment, fragment, R.id.sub_container);
            fragmentTransactionExtended.addTransition(FragmentTransactionExtended.GLIDE);
            fragmentTransactionExtended.commit();
            */


            //Snackbar.with(this).text("this is main social section").show(this);
            //ApiBus.getInstance().post(new SomeEvent("var1",
            //      2));
        }
    }

    File tempFile;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final int RESULT_PICK_VIDEO = 4;
    private static final int RESULT_VIDEO_CAP = 5;
    private Uri mFileURI = null;

    boolean doubleBackToExitPressedOnce = false;

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

                try {
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

                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path);
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                        fOut.flush();
                        fOut.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_CHOOSE_PHOTO) {

                Uri selectedImageUri = data.getData();

                String path = PathManager.getRealPathFromURIForKitKat(context, selectedImageUri);

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
                    Intent intent = new Intent(context,
                            PostVideoActivity.class);
                    intent.setData(mFileURI);
                    startActivity(intent);
                }

            } else if (requestCode == RESULT_VIDEO_CAP) {

                mFileURI = data.getData();
                if (mFileURI != null) {
                    Intent intent = new Intent(context,
                            PostVideoActivity.class);
                    intent.setData(mFileURI);
                    startActivity(intent);
                }

            }
        }
    }

    private static final int PHOTO_SIZE_WIDTH = 100;
    private static final int PHOTO_SIZE_HEIGHT = 100;

    public void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
    public void onResume() {
        super.onResume();
        ApiBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ApiBus.getInstance().unregister(this);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<WeakReference<Fragment>> fragList = new ArrayList<WeakReference<Fragment>>();

    @Override
    public void onAttachFragment(Fragment fragment) {
        fragList.add(new WeakReference(fragment));
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

}
