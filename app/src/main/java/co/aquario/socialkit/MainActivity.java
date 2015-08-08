package co.aquario.socialkit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.crossfader.util.UIUtils;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.parse.ParseAnalytics;
import com.soundcloud.android.crop.Crop;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import co.aquario.chatui.ChatUIActivity;
import co.aquario.chatui.fragment.FragmentTabhost.TattooFragment;
import co.aquario.socialkit.activity.post.PostPhotoActivity;
import co.aquario.socialkit.activity.post.PostVideoActivity;
import co.aquario.socialkit.activity.search.SearchPagerFragment;
import co.aquario.socialkit.event.ActivityResultEvent;
import co.aquario.socialkit.event.toolbar.SubTitleEvent;
import co.aquario.socialkit.event.toolbar.TitleEvent;
import co.aquario.socialkit.event.upload.UpdateAvatarEvent;
import co.aquario.socialkit.event.upload.UpdateCoverEvent;
import co.aquario.socialkit.fragment.LiveHistoryFragment;
import co.aquario.socialkit.fragment.NotiFragment;
import co.aquario.socialkit.fragment.SettingFragment;
import co.aquario.socialkit.fragment.WebViewFragment;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.fragment.pager.ChannelViewPagerFragment;
import co.aquario.socialkit.fragment.pager.HomeViewPagerNiceTabFragment;
import co.aquario.socialkit.fragment.pager.PhotoViewPagerFragment;
import co.aquario.socialkit.fragment.pager.SocialViewPagerFragment;
import co.aquario.socialkit.fragment.pager.VideoViewPagerFragment;
import co.aquario.socialkit.handler.ActivityResultBus;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.EndpointManager;
import co.aquario.socialkit.util.PathManager;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.RoundedTransformation;


public class MainActivity extends BaseActivity implements BaseFragment.SearchListener {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;

    public Toolbar toolbar;
    public ActionBarDrawerToggle toggle;
    public DrawerLayout mDrawer;

    List<WeakReference<Fragment>> fragList = new ArrayList<>();
    File tempFile;
    Drawer result = null;
    MiniDrawer miniResult = null;
    //private Drawer result = null;
    private Context mContext;
    private Activity mActivity;
    private String userId;
    private Uri mFileURI = null;

    PrefManager mPref;
    //Crossfader crossFader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
        CustomActivityOnCrash.install(this);

        mContext = this;
        mActivity = this;

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        mPref = getPref(getApplicationContext());
        userId = mPref.userId().getOr("0");
        VMApp.saveInstallation(Integer.parseInt(userId));

        if (savedInstanceState == null) {
            //HomeViewPagerFragment fragment = new HomeViewPagerFragment();
            HomeViewPagerNiceTabFragment fragment = new HomeViewPagerNiceTabFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.sub_container, fragment);
            transaction.commit();
        }

        if(getIntent().getExtras() != null) {
            Integer postId = getIntent().getExtras().getInt("post_id");
            if(postId != null) {
                FeedFragment fragment = new FeedFragment().newInstance(postId);
                FragmentManager manager = ((BaseActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commitAllowingStateLoss();
                ApiBus.getInstance().postQueue(new TitleEvent("VDOMAX"));
                //ApiBus.getInstance().postQueue(new SubTitleEvent("PostID:" + postId));
            }

        }

        initDrawer(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        //outState = headerResult.saveInstanceState(outState);
        //add the values which need to be saved from the crossFader to the bundle
        //outState = crossFader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
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

    /*
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
    */

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    private static final int RESULT_PICK_VIDEO = 4;
    private static final int RESULT_VIDEO_CAP = 5;

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



    @Override
    public void onSearchQuery(String query) {
        Utils.hideKeyboard(this);
        SearchPagerFragment fragment = new SearchPagerFragment().newInstance(VMApp.mPref.userId().getOr("0"),query);
        getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "MAIN_SEARCH").addToBackStack(null).commit();

        //SearchActivity.startActivity(getApplicationContext(),query);
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                mPref.isNoti().put(true).commit();



                Toast.makeText(MainActivity.this, "turn on notification", Toast.LENGTH_SHORT).show();
            } else{
                mPref.isNoti().put(false).commit();

                Toast.makeText(MainActivity.this, "turn off notification", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void initDrawer(Bundle savedInstanceState) {

        View header = LayoutInflater.from(getApplication()).inflate(R.layout.header_drawer, null);
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(getToolbar())
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

                        //new SectionDrawerItem().withName("Menu"),
                        new SecondaryDrawerItem().withName("Search").withIcon(FontAwesome.Icon.faw_search).withIdentifier(7),
                        new SecondaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(0),
                        new SecondaryDrawerItem().withName("Chat").withIcon(FontAwesome.Icon.faw_envelope).withIdentifier(1),
                        new SecondaryDrawerItem().withName("Live History").withIcon(FontAwesome.Icon.faw_history).withIdentifier(2),
                        new SecondaryDrawerItem().withName("Setting").withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3),
                       // new SecondaryDrawerItem().withName("Maxpoint").withIcon(FontAwesome.Icon.faw_btc).withIdentifier(4),
                        new SecondaryDrawerItem().withName("Tattoo Store").withIcon(FontAwesome.Icon.faw_shopping_cart).withIdentifier(5),
                        new SecondaryDrawerItem().withName("Term & Policies").withIcon(FontAwesome.Icon.faw_terminal).withIdentifier(6)
                        //new SecondaryDrawerItem().withName("Log Out").withIcon(FontAwesome.Icon.faw_sign_out)

                )
                .addStickyDrawerItems(
                        //new SwitchDrawerItem().withName(R.string.action_notification).withIcon(FontAwesome.Icon.faw_newspaper_o).withIdentifier(9),
                        new SecondaryDrawerItem().withName(R.string.action_logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(10)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem.getIdentifier() == 0) {

                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Home");

                            //HomeViewPagerFragment fragment = new HomeViewPagerFragment();
                            HomeViewPagerNiceTabFragment fragment = new HomeViewPagerNiceTabFragment();
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.sub_container, fragment);
                            transaction.commit();

                        } else if(drawerItem.getIdentifier() == 1) {
                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Chat");
                            //getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, ConversationViewPagerFragment.newInstance(), "CHAT").addToBackStack(null).commit();

                            getToolbar().getMenu().clear();
                            Intent intent = new Intent(MainActivity.this, ChatUIActivity.class);
                            startActivity(intent);

                        } else if (drawerItem.getIdentifier() == 2) {

                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Live History");

                            getToolbar().getMenu().clear();
                            LiveHistoryFragment fragment = LiveHistoryFragment.newInstance(userId);
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "LIVE_HISTORY").addToBackStack(null).commit();

                        } else if (drawerItem.getIdentifier() == 3) {

                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Setting");

                            getToolbar().getMenu().clear();
                            SettingFragment fragment = SettingFragment.newInstance(userId);
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "SETTINGS").addToBackStack(null).commit();

                        } else if (drawerItem.getIdentifier() == 4) {
                            // Maxpoint
                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Maxpoint");

                        } else if(drawerItem.getIdentifier() == 5){
                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Tattoo Store");

                            getToolbar().getMenu().clear();
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, TattooFragment.newInstance(), "TATTOO_STORE").addToBackStack(null).commit();


                        } else if(drawerItem.getIdentifier() == 6){
                            // Term & Policy
                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Term & Policy");
                            String termUrl = "https://www.vdomax.com/ajax.php?t=getDisclaimer&lang=en";

                            getToolbar().getMenu().clear();
                            WebViewFragment fragment = WebViewFragment.newInstance(termUrl);
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "TERM_POLICY").addToBackStack(null).commit();

                        } else if(drawerItem.getIdentifier() == 7){
                            // Term & Policy
                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Search");

                            getToolbar().getMenu().clear();
                            SearchPagerFragment fragment = new SearchPagerFragment().newInstance(VMApp.mPref.userId().getOr("0"),"youlove");
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "MAIN_SEARCH").addToBackStack(null).commit();


                        } else if(drawerItem.getIdentifier() == 9) {

                            getToolbar().setTitle("VDOMAX");
                            getToolbar().setSubtitle("Notification");

                            getToolbar().getMenu().clear();
                            NotiFragment fragment = NotiFragment.newInstance(userId,"ALL");
                            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "NOTIFICATION").addToBackStack(null).commit();

                        } else if(drawerItem.getIdentifier() == 10) {
                            getToolbar().getMenu().clear();
                            VMApp.logout(getApplicationContext());
                            Intent login = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(login);
                            finish();
                        }

//                        if (crossFader.isCrossFaded()) {
//                            crossFader.crossFade();
//                            miniResult.update();
//                        }

                        result.closeDrawer();

                        return true;
                    }

//                    @Override
//                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
//
//
//                    }
                })
                //.withTranslucentActionBarCompatibility(false)
                .withSavedInstance(savedInstanceState)
                .build();

        miniResult = new MiniDrawer().withDrawer(result);
                //.withAccountHeader(headerResult);

        toggle = result.getActionBarDrawerToggle();
        mDrawer = result.getDrawerLayout();

        int firstWidth = (int) UIUtils.convertDpToPixel(200, this);
        int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

//        crossFader = new Crossfader()
//                .withContent(findViewById(R.id.crossfade_content))
//                .withFirst(result.getSlider(), firstWidth)
//                .withSecond(miniResult.build(this), secondWidth)
//                .withSavedInstance(savedInstanceState)
//                .build();

        ImageView channelMenu = (ImageView) result.getHeader().findViewById(R.id.channel_menu);
        ImageView sociallMenu = (ImageView) result.getHeader().findViewById(R.id.social_menu);
        ImageView videoMenu = (ImageView) result.getHeader().findViewById(R.id.video_menu);
        ImageView photoMenu = (ImageView) result.getHeader().findViewById(R.id.photo_menu);

        avatarMenu = (ImageView) result.getHeader().findViewById(R.id.header_avatar);
        coverMenu = (ImageView) result.getHeader().findViewById(R.id.header_cover);
        TextView nameMenu = (TextView) result.getHeader().findViewById(R.id.header_name);
        TextView usernameMenu = (TextView) result.getHeader().findViewById(R.id.header_username);

        Picasso.with(this).load(EndpointManager.getAvatarPath(mPref.avatar().getOr(""))).placeholder(R.drawable.avatar_default).centerCrop()
                .resize(100, 100).transform(new RoundedTransformation(50, 4)).into(avatarMenu);
        Picasso.with(this).load(EndpointManager.getAvatarPath(mPref.cover().getOr(""))).placeholder(R.drawable.cover_default).centerCrop()
                .resize(360, 80).into(coverMenu);
        usernameMenu.setText("@" + mPref.username().getOr("null"));
        nameMenu.setText(mPref.name().getOr("null"));


        usernameMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewProfileActivity.startProfileActivity(mActivity, mPref.userId().getOr("0"));
            }
        });

        nameMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewProfileActivity.startProfileActivity(mActivity, mPref.userId().getOr("0"));
            }
        });
        coverMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewProfileActivity.startProfileActivity(mActivity,mPref.userId().getOr("0"));
            }
        });

        avatarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewProfileActivity.startProfileActivity(mActivity,mPref.userId().getOr("0"));
            }
        });

        avatarMenu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        channelMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChannelViewPagerFragment fragment = new ChannelViewPagerFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "CHANNEL_MAIN").addToBackStack(null).commit();

                getToolbar().getMenu().clear();
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

                getToolbar().getMenu().clear();
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

                getToolbar().getMenu().clear();
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

                getToolbar().getMenu().clear();
                getToolbar().setTitle("VDOMAX");
                getToolbar().setSubtitle("Photos");
                result.closeDrawer();
            }
        });
    }

    ImageView avatarMenu;
    ImageView coverMenu;

    @Subscribe public void onUpdateAvatar(UpdateAvatarEvent event) {
        Picasso.with(this).load(EndpointManager.getAvatarPath(event.avatar)).placeholder(R.drawable.avatar_default).centerCrop()
                .resize(100, 100).transform(new RoundedTransformation(50, 4)).into(avatarMenu);
        mPref.avatar().put(event.avatar).commit();

    }

    @Subscribe public void onUpdateCover(UpdateCoverEvent event) {
        Picasso.with(this).load(EndpointManager.getAvatarPath(event.cover)).placeholder(R.drawable.cover_default).centerCrop()
                .resize(360, 80).into(coverMenu);
        mPref.cover().put(event.cover).commit();
    }

    @Subscribe public void onUpdateTitle(TitleEvent event) {
        if(getToolbar() != null)
        getToolbar().setTitle(event.str);

    }

    @Subscribe public void onUpdateSubTitle(SubTitleEvent event) {
        if(getToolbar() != null)
        getToolbar().setSubtitle(event.str);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 12345) {
            //toolbar.getMenu().clear();
            if(getToolbar() != null) {
                getToolbar().setTitle("VDOMAX");
                getToolbar().setSubtitle("Notification");
            }

            NotiFragment notiFragment = NotiFragment.newInstance(VMApp.mPref.userId().getOr(""),"ALL");
            getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, notiFragment, "NOTIFICATION").addToBackStack(null).commit();
            ActionItemBadge.update(item, VMApp.getNotiBadge());
        }

        return super.onOptionsItemSelected(item);

    }
}
