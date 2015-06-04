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
package co.aquario.socialkit.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

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

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.VMDataAdapter;
import co.aquario.socialkit.model.VMFeed;
import co.aquario.socialkit.model.VMFetch;
import co.aquario.socialkit.view.ArtbookLayout;
import co.aquario.socialkit.view.TitanicTextView;
import co.aquario.socialkit.widget.Titanic;
import co.aquario.socialkit.widget.Typefaces;

public class VMActivity extends Activity implements OnClickListener {

    public static final String TAG = "ArtbookActivity";
    VMDataAdapter adapter;
    FreeFlowLayout[] layouts;
    int currLayoutIndex = 0;
    private FreeFlowContainer container;
    private VGridLayout grid;
    private ArtbookLayout custom;
    private VMFetch fetch;
    private int itemsPerPage = 25;
    private int pageIndex = 1;

    Titanic titanic;
    TitanicTextView myTitanicTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artbook);

        myTitanicTextView = (TitanicTextView) findViewById(R.id.titanic_tv);
        myTitanicTextView.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));

        titanic = new Titanic();
        titanic.start(myTitanicTextView);
        
        container = (FreeFlowContainer) findViewById(R.id.container);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        findViewById(R.id.load_more).setOnClickListener(this);
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

        adapter = new VMDataAdapter(this);


        container.setLayout(layouts[currLayoutIndex]);
        container.setAdapter(adapter);


        fetch = new VMFetch();

        fetch.load(this, itemsPerPage, pageIndex);

    }

    public void onDataLoaded(VMFeed feed) {

        myTitanicTextView.setVisibility(View.GONE);

        adapter.update(feed);
        container.dataInvalidated();
        container.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AbsLayoutContainer parent, FreeFlowItem item) {

            }
        });





        container.addScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(FreeFlowContainer container) {
                Log.d(TAG, "scroll percent " + container.getScrollPercentY());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.artbook, menu);
        return true;
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
                container.setLayout(layouts[currLayoutIndex]);

                break;

        }

        return true;

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "Loading data");
        pageIndex++;
        fetch.load(this, itemsPerPage, pageIndex);
    }
}
