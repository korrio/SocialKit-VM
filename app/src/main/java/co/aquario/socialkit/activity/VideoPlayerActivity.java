package co.aquario.socialkit.activity;

//import co.aquario.socialkit.fragment.VideoPlayerFragment;
//
//public class VideoPlayerActivity extends BaseActivity implements VideoPlayerFragment.Callback {
//
//    private String mQuality;
//    private String mSubtitleLanguage;
//    private String mLocation;
//    private VideoPlayerFragment mVideoPlayerFragment;
//
//    public static Intent startActivity(Activity activity, String streamUrl) {
//        return startActivity(activity, streamUrl, null, 0);
//    }
//
//    public static Intent startActivity(Activity activity, String streamUrl, String quality,  long resumePosition) {
//        Intent i = toolbar Intent(activity, VideoPlayerActivity.class);
//        //i.putExtra(DATA, data);
//        i.putExtra(QUALITY, quality);
//        //i.putExtra(SUBTITLES, subtitleLanguage);
//        i.putExtra(LOCATION, streamUrl);
//        //todo: resume position;
//        activity.startActivity(i);
//        return i;
//    }
//
//    public final static String LOCATION = "stream_url";
//    public final static String DATA = "video_data";
//    public final static String QUALITY = "quality";
//    public final static String SUBTITLES = "subtitles";
//    public final static String RESUME_POSITION = "resume_position";
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        super.setContentView(R.layout.activity_videoplayer);
//
//        //mMedia = getIntent().getParcelableExtra(DATA);
//        //mQuality = getIntent().getStringExtra(QUALITY);
//        //mSubtitleLanguage = getIntent().getStringExtra(SUBTITLES);
//        mLocation = getIntent().getStringExtra(LOCATION);
//
//        mVideoPlayerFragment = (VideoPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment);
//        mVideoPlayerFragment.loadMedia();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                showExitDialog();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onBackPressed() {
//        showExitDialog();
//    }
//
//    private void showExitDialog() {
//        OptionDialogFragment.show(getFragmentManager(), getString(R.string.leave_videoplayer_title), String.format(getString(R.string.leave_videoplayer_message), ""), getString(android.R.string.yes), getString(android.R.string.no), toolbar OptionDialogFragment.Listener() {
//            @Override
//            public void onSelectionPositive() {
//                finish();
//            }
//
//            @Override
//            public void onSelectionNegative() {}
//        });
//    }
//
//    @Override
//    public String getQuality() {
//        return mQuality;
//    }
//
//    @Override
//    public String getSubtitles() {
//        return mSubtitleLanguage;
//    }
//
//    @Override
//    public String getLocation() {
//        return mLocation;
//    }
//}
