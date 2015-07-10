package co.aquario.socialkit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.fragment.ImagePagerFragment;

/**
 * Created by donglua on 15/6/24.
 */
public class CustomPhotoPagerActivity extends AppCompatActivity {

  private ImagePagerFragment pagerFragment;

  public final static String EXTRA_CURRENT_ITEM = "current_item";
  public final static String EXTRA_PHOTOS = "photos";

  private ActionBar actionBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(me.iwf.photopicker.R.layout.activity_photo_pager);

    int currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
    List<String> paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);

    pagerFragment =
        (ImagePagerFragment) getSupportFragmentManager().findFragmentById(me.iwf.photopicker.R.id.photoPagerFragment);
    pagerFragment.setPhotos(paths, currentItem);

    Toolbar mToolbar = (Toolbar) findViewById(me.iwf.photopicker.R.id.toolbar);
    setSupportActionBar(mToolbar);

    actionBar = getSupportActionBar();

    actionBar.setDisplayHomeAsUpEnabled(true);
    updateActionBarTitle();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      actionBar.setElevation(25);
    }


    pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        updateActionBarTitle();
      }

      @Override public void onPageSelected(int i) {

      }

      @Override public void onPageScrollStateChanged(int i) {

      }
    });

  }


  @Override public boolean onCreateOptionsMenu(Menu menu) {
    //getMenuInflater().inflate(me.iwf.photopicker.R.menu.menu_preview, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {

      Intent intent = new Intent();
      intent.putExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
      setResult(RESULT_OK, intent);
      finish();
      return true;
    }

    if (item.getItemId() == me.iwf.photopicker.R.id.delete) {
      final int index = pagerFragment.getCurrentItem();

      final String deletedPath =  pagerFragment.getPaths().get(index);

      Snackbar snackbar = Snackbar.make(pagerFragment.getView(), me.iwf.photopicker.R.string.deleted_a_photo,
          Snackbar.LENGTH_LONG);

      if (pagerFragment.getPaths().size() <= 1) {

        // show confirm dialog
        new AlertDialog.Builder(this)
            .setTitle(me.iwf.photopicker.R.string.confirm_to_delete)
            .setPositiveButton(me.iwf.photopicker.R.string.yes, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setResult(RESULT_OK);
                finish();
              }
            })
            .setNegativeButton(me.iwf.photopicker.R.string.cancel, new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
              }
            })
            .show();

      } else {

        snackbar.show();

        pagerFragment.getPaths().remove(index);
        pagerFragment.getViewPager().removeViewAt(index);

        pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
      }

      snackbar.setAction(me.iwf.photopicker.R.string.undo, new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (pagerFragment.getPaths().size() > 0) {
            pagerFragment.getPaths().add(index, deletedPath);
          } else {
            pagerFragment.getPaths().add(deletedPath);
          }
          pagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
          pagerFragment.getViewPager().setCurrentItem(index, true);
        }
      });

      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void updateActionBarTitle() {
    actionBar.setTitle(
        getString(me.iwf.photopicker.R.string.image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
            pagerFragment.getPaths().size()));
  }
}
