package com.jialin.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MessageAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<Message> mMessages = null;
	
	public MessageAdapter(Context context, List<Message> messages) {
		super();
		this.mContext = context;
		this.mMessages = messages;
	}

	@Override
	public int getCount() {
		return mMessages != null ? mMessages.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return this.mMessages.get(position).getIsSender() ? 1 : 0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}


	
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {

		final Message m = mMessages.get(position);
		boolean isSend = m.getIsSender();

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (isSend) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_item_right, null);
			} else {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_item_left, null);
			}
			viewHolder.sendDateTextView = (TextView) convertView.findViewById(R.id.sendDateTextView);
			viewHolder.sendTimeTextView = (TextView) convertView.findViewById(R.id.sendTimeTextView);
			viewHolder.userAvatarImageView = (ImageView) convertView.findViewById(R.id.userAvatarImageView);
			viewHolder.userNameTextView = (TextView) convertView.findViewById(R.id.userNameTextView);
			viewHolder.textTextView = (TextView) convertView.findViewById(R.id.textTextView);
			viewHolder.photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
			viewHolder.faceImageView = (ImageView) convertView.findViewById(R.id.faceImageView);
			viewHolder.failImageView = (ImageView) convertView.findViewById(R.id.failImageView);
			viewHolder.sendingProgressBar = (ProgressBar) convertView.findViewById(R.id.sendingProgressBar);
			
			
			viewHolder.isSend = isSend;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		try {
			String dateString = DateFormat.format("yyyy-MM-dd h:mmaa", m.getTime()).toString();
			String [] t = dateString.split(" ");
			viewHolder.sendDateTextView.setText(t[0]);
			viewHolder.sendTimeTextView.setText(t[1]);
			
			if(position == 0){
				viewHolder.sendDateTextView.setVisibility(View.VISIBLE);
			}else{
				//TODO is same day ?
				Message pmsg = mMessages.get(position-1);
				if(inSameDay(pmsg.getTime(), m.getTime())){
					viewHolder.sendDateTextView.setVisibility(View.GONE);
				}else{
					viewHolder.sendDateTextView.setVisibility(View.VISIBLE);
				}
				
			}
			
		} catch (Exception e) {
		}
		
		viewHolder.userNameTextView.setText(m.getFromUserName());
		Picasso.with(mContext).load(m.getFromUserAvatar()).centerCrop().resize(200,200)
                .transform(new RoundedTransformation(100, 4)).into(viewHolder.userAvatarImageView);
        viewHolder.textTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


		JSONObject dataObj;
		dataObj = null;
		try {
			dataObj = new JSONObject(m.getData());
		} catch (JSONException e) {
			e.printStackTrace();
		}

        //Log.e("dataObj["+position+"]",m.getData());

		switch (m.getType()) {
		case 0://text

			viewHolder.textTextView.setText(m.getContent());
			viewHolder.textTextView.setVisibility(View.VISIBLE);
			viewHolder.photoImageView.setVisibility(View.GONE);
			viewHolder.faceImageView.setVisibility(View.GONE);
			if(m.getIsSender()){
				LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				sendTimeTextViewLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.textTextView);
				viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
				
				LayoutParams layoutParams = (LayoutParams) viewHolder.failImageView.getLayoutParams();
				layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.textTextView);
				if( m.getSendSuccess() != null && !m.getSendSuccess()){
					//viewHolder.failImageView.setVisibility(View.VISIBLE);
					viewHolder.failImageView.setLayoutParams(layoutParams);
				}else{
					viewHolder.failImageView.setVisibility(View.GONE);
				}
				
				if(m.getState() != null && m.getState() == 0){
					viewHolder.sendingProgressBar.setVisibility(View.VISIBLE);
					viewHolder.sendingProgressBar.setLayoutParams(layoutParams);
				}else{
					viewHolder.sendingProgressBar.setVisibility(View.GONE);
				}
				
			}else{
				viewHolder.failImageView.setVisibility(View.GONE);
				viewHolder.sendingProgressBar.setVisibility(View.GONE);
				
				LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				sendTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.textTextView);
				viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
			}
			
			
			break;
		case 2://photo & clip (attach file)
			viewHolder.textTextView.setVisibility(View.GONE);
			viewHolder.photoImageView.setVisibility(View.VISIBLE);
			viewHolder.faceImageView.setVisibility(View.GONE);


            if(dataObj != null) {
                // from chat history


                if(((dataObj.optString("fileType").equals("image/jpeg") || dataObj.optString("fileType").equals("image/png"))) && dataObj.optString("thumb") != null) {
                   // String imageUrl = "https://chat.vdomax.com:1314" + dataObj.optString("url");
                    String imageUrl = dataObj.optString("thumb");
                    Log.e("myurl", imageUrl);
                    if(imageUrl != null && !imageUrl.equals(""))
                        Picasso.with(mContext).load(imageUrl).fit().centerCrop().into(viewHolder.photoImageView);
                } else if((dataObj.optString("fileType").equals("video/mp4") || dataObj.optString("fileType").equals("video/quicktime")) && dataObj.optString("thumb") != null) {
                    String imageUrl = dataObj.optString("thumb");
                    Log.e("myurl", imageUrl);
                    if(imageUrl != null && !imageUrl.equals(""))
                        Picasso.with(mContext).load(imageUrl).fit().centerCrop().into(viewHolder.photoImageView);
                } else {
                    // from local photo taken or picked
                    String uriStr = dataObj.optString("imageUriPhoto");
                    URI imageUri = null;
                    try {
                        imageUri = new URI(uriStr);
                        Bitmap myImg = BitmapFactory.decodeFile(imageUri.getPath());
                        if(imageUri.getPath() != null && !imageUri.getPath().equals(""))
                            Picasso.with(mContext).load(imageUri.getPath()).resize(400,400).into(viewHolder.photoImageView);
                        //viewHolder.photoImageView.setImageBitmap(myImg);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }


            }


			if(m.getIsSender() ){
				LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				sendTimeTextViewLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.photoImageView);
				viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
				
				LayoutParams layoutParams = (LayoutParams) viewHolder.failImageView.getLayoutParams();
				layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.photoImageView);
				if(m.getSendSuccess() != null && !m.getSendSuccess()){
					//viewHolder.failImageView.setVisibility(View.VISIBLE);
					viewHolder.failImageView.setLayoutParams(layoutParams);
				}else{
					viewHolder.failImageView.setVisibility(View.GONE);
				}
				
				if(m.getState() != null && m.getState() == 0){
					viewHolder.sendingProgressBar.setVisibility(View.VISIBLE);
					viewHolder.sendingProgressBar.setLayoutParams(layoutParams);
				}else{
					viewHolder.sendingProgressBar.setVisibility(View.GONE);
				}
				
			}else{
				viewHolder.failImageView.setVisibility(View.GONE);
				LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				sendTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.photoImageView);
				viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
			}
			
			
			break;
			
		case 1://face
			viewHolder.photoImageView.setVisibility(View.GONE);
			viewHolder.textTextView.setVisibility(View.GONE);
			viewHolder.faceImageView.setVisibility(View.VISIBLE);
			//int resId = mContext.getResources().getIdentifier(m.getContent(), "drawable", mContext.getPackageName());
			//viewHolder.faceImageView.setImageResource(resId);

			if(dataObj != null && !dataObj.optString("tattooUrl").equals(""))
				Picasso.with(mContext).load(dataObj.optString("tattooUrl")).into(viewHolder.faceImageView);

			
			if(m.getIsSender()){
				LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				sendTimeTextViewLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.faceImageView);
				viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
				
				LayoutParams layoutParams = (LayoutParams) viewHolder.failImageView.getLayoutParams();
				layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.faceImageView);
				if(m.getSendSuccess() != null && !m.getSendSuccess()){
					//viewHolder.failImageView.setVisibility(View.VISIBLE);
					viewHolder.failImageView.setLayoutParams(layoutParams);
				}else{
					viewHolder.failImageView.setVisibility(View.GONE);
				}
				
				if(m.getState() != null && m.getState() == 0){
					viewHolder.sendingProgressBar.setVisibility(View.VISIBLE);
					viewHolder.sendingProgressBar.setLayoutParams(layoutParams);
				}else{
					viewHolder.sendingProgressBar.setVisibility(View.GONE);
				}
				
			}else{
				viewHolder.failImageView.setVisibility(View.GONE);
				
				LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams();
				sendTimeTextViewLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.faceImageView);
				viewHolder.sendTimeTextView.setLayoutParams(sendTimeTextViewLayoutParams);
			}
			
			break;

            case 3:
                viewHolder.textTextView.setVisibility(View.GONE);
                viewHolder.photoImageView.setVisibility(View.VISIBLE);
                viewHolder.faceImageView.setVisibility(View.GONE);

                if(dataObj.optString("imageUriVdoThumb") == null) {
                    String imageUrl = dataObj.optString("thumb");
                    if(imageUrl != null && !imageUrl.equals(""))
                        Picasso.with(mContext).load(imageUrl).fit().centerCrop().into(viewHolder.photoImageView);
                } else {
                    try {
                        URI imageUri = new URI(dataObj.optString("imageUriVdoThumb"));
                        Bitmap myImg = BitmapFactory.decodeFile(imageUri.getPath());
                        viewHolder.photoImageView.setImageBitmap(myImg);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                }



                //if(dataObj != null)
                  //  Picasso.with(mContext).load().into(viewHolder.photoImageView);

                break;
            case 31:

                viewHolder.textTextView.setVisibility(View.GONE);
                viewHolder.photoImageView.setVisibility(View.VISIBLE);
                viewHolder.faceImageView.setVisibility(View.GONE);

                if(dataObj != null)
                    Picasso.with(mContext).load(dataObj.optString("ytImage")).into(viewHolder.photoImageView);

                break;

            case 32:

                viewHolder.textTextView.setVisibility(View.GONE);
                viewHolder.photoImageView.setVisibility(View.VISIBLE);
                viewHolder.faceImageView.setVisibility(View.GONE);

                if(dataObj != null)
                    Picasso.with(mContext).load(dataObj.optString("trackImage")).into(viewHolder.photoImageView);

                break;

            case 4:

                viewHolder.textTextView.setVisibility(View.VISIBLE);
                viewHolder.photoImageView.setVisibility(View.GONE);
                viewHolder.faceImageView.setVisibility(View.GONE);

                viewHolder.textTextView.setText("Audio Call");
                viewHolder.textTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call_white_24dp, 0, 0, 0);

                break;
            case 5:

                viewHolder.textTextView.setVisibility(View.VISIBLE);
                viewHolder.photoImageView.setVisibility(View.GONE);
                viewHolder.faceImageView.setVisibility(View.GONE);

                viewHolder.textTextView.setText("Video Call");
                viewHolder.textTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_videocam_white_24dp, 0, 0, 0);

                break;

            case 6:

                viewHolder.textTextView.setVisibility(View.GONE);
                viewHolder.photoImageView.setVisibility(View.VISIBLE);
                viewHolder.faceImageView.setVisibility(View.GONE);

                if(dataObj != null) {
                    String lat = dataObj.optString("latitude");
                    String lon = dataObj.optString("longtitude");
                    String regionName = dataObj.optString("regionName");
                    String mapImage = "https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x400&maptype=roadmap&markers=color:blue%7Clabel:"+regionName+"%7C"+lat+","+lon;

                    Picasso.with(mContext).load(mapImage).resize(400, 400).into(viewHolder.photoImageView);
                }


                break;



		default:
			viewHolder.textTextView.setText(m.getContent());
			viewHolder.photoImageView.setVisibility(View.GONE);
			viewHolder.faceImageView.setVisibility(View.GONE);
			break;
		}
		
//		viewHolder.textTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		
		return convertView;
	}


	public List<Message> getData() {
		return mMessages;
	}

	public void setData(List<Message> data) {
		this.mMessages = data;
	}


	public static boolean inSameDay(Date date1, Date Date2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int year1 = calendar.get(Calendar.YEAR);
		int day1 = calendar.get(Calendar.DAY_OF_YEAR);

		calendar.setTime(Date2);
		int year2 = calendar.get(Calendar.YEAR);
		int day2 = calendar.get(Calendar.DAY_OF_YEAR);

		if ((year1 == year2) && (day1 == day2)) {
			return true;
		}
		return false;
	}


	static class ViewHolder {
		
		public ImageView	userAvatarImageView;
		public TextView 	sendDateTextView;
		public TextView 	userNameTextView;
		
		public TextView 	textTextView;
		public ImageView 	photoImageView;
		public ImageView 	faceImageView;
		
		public ImageView 	failImageView;
		public TextView 	sendTimeTextView;
		public ProgressBar 	sendingProgressBar;
		
		public boolean 		isSend = true;
	}


}
