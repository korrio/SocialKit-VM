/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.aquario.socialkit.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.FeedAdapter;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;

public class SlidingUpRecyclerViewActivity extends SlidingUpBaseActivity<ObservableRecyclerView> implements ObservableScrollViewCallbacks {

    public ArrayList<PostStory> list = new ArrayList<>();
    private FeedAdapter adapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_profile2;
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }
    */

    @Override
    protected ObservableRecyclerView createScrollable() {
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) findViewById(R.id.scroll);
        recyclerView.setScrollViewCallbacks(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new FeedAdapter(this, list);
        recyclerView.setAdapter(adapter);
        ApiBus.getInstance().register(this);


        /*
        ViewPagerFragment fragment = new ViewPagerFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.view_pager_container, fragment);
        transaction.commit();

        */

        return recyclerView;
    }

    @Subscribe
    public void onLoadTimelineSuccess(LoadTimelineSuccessEvent event) {
        list.addAll(event.getTimelineData().getPosts());
        adapter.notifyDataSetChanged();
        Log.e("itemCountAfterNotify",adapter.getItemCount() + "");



    }
}
