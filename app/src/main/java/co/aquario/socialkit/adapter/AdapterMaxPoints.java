package co.aquario.socialkit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Maxpoint;


public class AdapterMaxPoints extends BaseAdapter {
    private Context activity;
    public ArrayList<Maxpoint> list = new ArrayList<Maxpoint>();
    String[] strName;
    String[] bath;
    int[] resId;
    public AdapterMaxPoints(Context a, String[] strName, int[] resId, String[] bath) {
        activity = a;
        this.strName = strName;
        this.resId = resId;
        this.bath = bath;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return strName.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        //Maxpoint item = list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.item_maxpoint, null);
            holder.text_coins = (TextView) convertView.findViewById(R.id.text_coins);
            holder.text_save = (TextView) convertView.findViewById(R.id.text_save);
            holder.baht = (TextView) convertView.findViewById(R.id.baht);
            holder.coins = (ImageView) convertView.findViewById(R.id.coins);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.text_coins.setText(strName[position]);

        holder.baht.setText(bath[position]);

        holder.coins.setBackgroundResource(resId[position]);

switch (position) {
    case 0:
        holder.text_save.setVisibility(View.GONE);
        break;
    case 1:
        holder.text_save.setVisibility(View.VISIBLE);
        holder.text_save.setText("Save " + 10 + "%");
        break;
    case 2:
        holder.text_save.setVisibility(View.VISIBLE);
        holder.text_save.setText("Save " + 15 + "%");
        break;
    case 3:
        holder.text_save.setVisibility(View.VISIBLE);
        holder.text_save.setText("Save " + 20 + "%");
        break;
    case 4:
        holder.text_save.setVisibility(View.VISIBLE);
        holder.text_save.setText("Save " + 25 + "%");
        break;
    case 5:
        holder.text_save.setVisibility(View.VISIBLE);
        holder.text_save.setText("Save " + 30 + "%");
        break;
    default:
        holder.text_save.setVisibility(View.GONE);
        break;
}



        return convertView;
    }


    public static class ViewHolder {
        TextView text_coins;
        TextView baht;
        ImageView coins;
        TextView text_save;

    }
}

