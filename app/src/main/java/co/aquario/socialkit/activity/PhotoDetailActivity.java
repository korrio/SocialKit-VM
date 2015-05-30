package co.aquario.socialkit.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.PagerAdapter;
import co.aquario.socialkit.model.ImageSimpleBean;


public class PhotoDetailActivity extends Activity implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.pager)
    ViewPager mViewPager;

    @InjectView(R.id.love)
    ImageView love;

    @InjectView(R.id.share)
    ImageView share;

    @InjectView(R.id.page)
    TextView page;
    List<ImageSimpleBean> list;
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fragment_photo_detail);
        ButterKnife.inject(this);

        list = (List<ImageSimpleBean>) getIntent().getSerializableExtra("info");
        String url = getIntent().getStringExtra("url");
        mIndex = getIntent().getIntExtra("position", 0);

        PagerAdapter pagerAdapter = new PagerAdapter(this, list, getLayoutInflater());
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mIndex);
        mViewPager.setOnPageChangeListener(this);
        page.setText((mIndex + 1) + "/" + list.size());

    }


    @OnClick({R.id.love, R.id.share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.love:
                love.setBackgroundResource(R.drawable.ic_love_vm_red);
                break;
            case R.id.share:
                break;

        }

    }


    @Override
    public void onPageScrolled(int i, float v, int i2) {

    }

    @Override
    public void onPageSelected(int i) {
        page.setText((i + 1) + "/" + list.size());
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
