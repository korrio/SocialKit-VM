/**
 * ****************************************************************************
 * Copyright 2013 Comcast Cable Communications Management, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */
package co.aquario.socialkit.fragment.main;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.comcast.freeflow.core.AbsLayoutContainer;
import com.comcast.freeflow.core.AbsLayoutContainer.OnItemClickListener;
import com.comcast.freeflow.core.FreeFlowContainer;
import com.comcast.freeflow.core.FreeFlowContainer.OnScrollListener;
import com.comcast.freeflow.core.FreeFlowItem;
import com.comcast.freeflow.layouts.FreeFlowLayout;
import com.comcast.freeflow.layouts.HLayout;
import com.comcast.freeflow.layouts.VGridLayout;
import com.comcast.freeflow.layouts.VGridLayout.LayoutParams;
import com.comcast.freeflow.layouts.VLayout;
import com.squareup.otto.Subscribe;

import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.VMDataAdapter;
import co.aquario.socialkit.event.LoadGalleryEvent;
import co.aquario.socialkit.fragment.PhotoZoomFragment;
import co.aquario.socialkit.model.VMFeed;
import co.aquario.socialkit.model.VMFetch;
import co.aquario.socialkit.view.ArtbookLayout;
import co.aquario.socialkit.view.TitanicTextView;
import co.aquario.socialkit.widget.Titanic;
import co.aquario.socialkit.widget.Typefaces;

public class PhotoFreeFlowFragment extends BaseFragment implements OnClickListener {


    public static String SORT = "SORT";

    VMDataAdapter adapter;
    FreeFlowLayout[] layouts;
    int currLayoutIndex = 0;
    private FreeFlowContainer ffContainer;
    private VGridLayout grid;
    private ArtbookLayout custom;
    private VMFetch fetch;
    private int itemsPerPage = 25;
    private int pageIndex = 1;
    private String sort = "";

    Activity mActivity;

    Titanic titanic;
    TitanicTextView myTitanicTextView;

    public static PhotoFreeFlowFragment newInstance(String sort){
        PhotoFreeFlowFragment mFragment = new PhotoFreeFlowFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(SORT, sort);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sort = getArguments().getString(SORT);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_freeflow_gallery, container, false);


        mActivity = getActivity();

        myTitanicTextView = (TitanicTextView) rootView.findViewById(R.id.titanic_tv);
        myTitanicTextView.setTypeface(Typefaces.get(getActivity(), "Satisfy-Regular.ttf"));

        titanic = new Titanic();
        titanic.start(myTitanicTextView);
        
        ffContainer = (FreeFlowContainer) rootView.findViewById(R.id.container);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        rootView.findViewById(R.id.load_more).setOnClickListener(this);
        //Our new layout
        custom = new ArtbookLayout();

        //Grid Layout
        grid = new VGridLayout();
        LayoutParams params = new LayoutParams(size.x / 2, size.x / 2);
        grid.setLayoutParams(params);

        //Vertical Layout
        VLayout vlayout = new VLayout();
        VLayout.LayoutParams params2 = new VLayout.LayoutParams(size.x);
        vlayout.setLayoutParams(params2);

        //HLayout
        HLayout hlayout = new HLayout();
        hlayout.setLayoutParams(new HLayout.LayoutParams(size.x));


        layouts = new FreeFlowLayout[]{custom, grid, vlayout};

        adapter = new VMDataAdapter(getActivity());


        ffContainer.setLayout(layouts[currLayoutIndex]);
        ffContainer.setAdapter(adapter);


        fetch = new VMFetch();

        fetch.load(getActivity(), itemsPerPage, pageIndex, sort);

        return rootView;

    }

    @Subscribe
    public void onDataLoaded(LoadGalleryEvent event) {

        final VMFeed feed = event.feed;

        if(event.sort.equals(sort)) {
            myTitanicTextView.setVisibility(View.GONE);
            adapter.update(feed);
            ffContainer.dataInvalidated();
        }



        ffContainer.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AbsLayoutContainer parent, FreeFlowItem item) {
               // Toast.makeText(getApplicationContext(),item.itemIndex + " : " + ((VMFeed) item.data).getShots().get(item.itemIndex).media.getFullUrl(),Toast.LENGTH_SHORT).show();

                String url = feed.getShots().get(item.itemIndex).media.getFullUrl();
                String name = feed.getShots().get(item.itemIndex).author.name;
                String text = feed.getShots().get(item.itemIndex).text;


                PhotoZoomFragment fragment = new PhotoZoomFragment().newInstance(url,name,text);
                FragmentManager manager =((MainActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commit();

            }
        });





        ffContainer.addScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(FreeFlowContainer container) {
                Toast.makeText(getActivity(), "scroll percent " + container.getScrollPercentY(), Toast.LENGTH_SHORT).show();
            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (R.id.action_change_layout):
                currLayoutIndex++;
                if (currLayoutIndex == layouts.length) {
                    currLayoutIndex = 0;
                }
                ffContainer.setLayout(layouts[currLayoutIndex]);

                break;

        }

        return true;

    }

    @Override
    public void onClick(View v) {

        pageIndex++;
        fetch.load(getActivity(), itemsPerPage, pageIndex,sort);
        myTitanicTextView.setVisibility(View.VISIBLE);
    }
}
