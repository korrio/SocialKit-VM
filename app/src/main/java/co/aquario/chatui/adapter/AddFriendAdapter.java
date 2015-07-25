package co.aquario.chatui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.aquario.socialkit.R;

/**
 * @author alessandro.balocco
 */
public class AddFriendAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private boolean isGrid;

    public Context mContext;

    public AddFriendAdapter(Context context,boolean isGrid) {
        layoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.isGrid = isGrid;
    }

    @Override
    public int getCount() {
        return 2;
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


        //User user = friendsModel.getUsers().get(position);

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
            case 0:
                viewHolder.textView.setText(mContext.getString(R.string.add_from_qr));

                //Picasso.with(mContext).load(R.drawable.ic_love_vm_red).centerCrop().resize(100, 100)
                        //.transform(new RoundedTransformation(50, 4))
                  //      .into(viewHolder.imageView);
                break;
            case 1:
                viewHolder.textView.setText(mContext.getString(R.string.add_from_id));
                //Picasso.with(mContext).load(user.getAvatarUrl()).centerCrop().resize(100, 100)
                  //      .transform(new RoundedTransformation(50, 4)).into(viewHolder.imageView);
                break;
            default:

                break;
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
