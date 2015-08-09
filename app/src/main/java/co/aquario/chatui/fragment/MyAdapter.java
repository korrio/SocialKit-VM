package co.aquario.chatui.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.chatui.VMChatApp;
import co.aquario.chatui.model.User;
import co.aquario.chatui.ui.CircleTransform;
import co.aquario.socialkit.R;

/**
 * Created by veinhorn on 8.3.15.
 */
public class MyAdapter extends SearchAdapter<User> {
    class ViewHolder {
        @InjectView(R.id.textUser) TextView title;
        @InjectView(R.id.bioUser) TextView enTitle;
        @InjectView(R.id.picProfile) ImageView avatar;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    public MyAdapter(List<User> movies, Context context) {
        super(movies, context);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_item_child, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.title.setText(filteredContainer.get(position).getName());
        viewHolder.enTitle.setText("@" + filteredContainer.get(position).getUsername());
        //viewHolder.poster.setImageDrawable(context.getResources().getDrawable(filteredContainer.get(position).getPoster()));

        Picasso.with(context)
                .load(VMChatApp.IMAGE_ENDPOINT + filteredContainer.get(position).getAvatar())
                        //.resize(sizePicProfile , sizePicProfile)
                .transform(new CircleTransform())
                .into(viewHolder.avatar);
        return convertView;
    }
}