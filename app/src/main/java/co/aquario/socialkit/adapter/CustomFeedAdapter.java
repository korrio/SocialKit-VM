package co.aquario.socialkit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Mac on 3/12/15.
 */
public class CustomFeedAdapter extends RecyclerView.Adapter<CustomFeedAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> mItems;

    public CustomFeedAdapter(Context context, ArrayList<String> items) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mContext, mInflater.inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.textView.setText(mItems.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Context context;

        public ViewHolder(Context context, View view) {
            super(view);
            this.context = context;
            this.textView = (TextView) view.findViewById(android.R.id.text1);
            this.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click(getPosition() + 1);
                }
            });
        }

        private void click(int i) {
            Toast.makeText(context, "Button " + i + " is clicked", Toast.LENGTH_SHORT).show();
        }
    }
}
