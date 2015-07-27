package co.aquario.chatui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import co.aquario.chatui.model.User;
import co.aquario.chatui.model.friendmodel.FriendsModel;
import co.aquario.chatui.utils.RoundedTransformation;
import co.aquario.socialkit.R;

/**
 * @author alessandro.balocco
 */
public class SimpleAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean isGrid;

    public FriendsModel friendsModel;
    public Context mContext;

    public SimpleAdapter(Context context, boolean isGrid,FriendsModel friendsModel) {
        layoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.isGrid = isGrid;
        this.friendsModel = friendsModel;
    }

    @Override
    public int getCount() {
        return friendsModel.getUsers().size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        User user = friendsModel.getUsers().get(position);

        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            if (isGrid) {
                view = layoutInflater.inflate(R.layout.simple_grid_item, parent, false);
            } else {
                view = layoutInflater.inflate(R.layout.simple_list_item, parent, false);
            }

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            default:
                viewHolder.textView.setText(user.getName());
                Picasso.with(mContext).load(user.getAvatarUrl()).centerCrop().resize(100, 100)
                        .transform(new RoundedTransformation(50, 4)).into(viewHolder.imageView);
                break;
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
