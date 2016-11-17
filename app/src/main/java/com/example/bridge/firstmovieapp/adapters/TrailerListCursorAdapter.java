package com.example.bridge.firstmovieapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.entities.Trailer;
import com.example.bridge.firstmovieapp.entities.Utility;
import com.example.bridge.firstmovieapp.fragments.DetailFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by bridge on 31/10/2016.
 */

public class TrailerListCursorAdapter extends RecyclerView.Adapter<TrailerListCursorAdapter.ViewHolder>{

    private Cursor mCursor;
    public Activity activity;

    public TrailerListCursorAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Trailer trailer = getItem(position);
        holder.mTrailerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/watch?v="+trailer.key));
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    activity.startActivity(intent);
                }
            }
        });

        //To get a thumbnail from a youtube video just go to http://img.youtube.com/vi/[video-id]/[thumbnail-number].jpg
        //thumbnail-number can go from 0 to 3
//        holder.mTrailerImage.setImageResource(R.color.transparent);
        Utility ut = new Utility(activity);
        if(ut.isConnectionAvailable()) {
            Picasso.with(activity.getBaseContext()).load("http://img.youtube.com/vi/" + trailer.key + "/0.jpg").into(holder.mTrailerImage);
        }
        else{
            holder.mTrailerImage.setImageResource(R.drawable.blank_youtube_video);
        }
    }

    @Override
    public int getItemCount() {
        if(mCursor!=null){
            return mCursor.getCount();
        }
        return 0;
    }

    private Trailer getItem(int position){
        mCursor.moveToPosition(position);
        Trailer trailer = new Trailer();
        trailer.id = mCursor.getString(DetailFragment.COL_TRAILER_ID);
        trailer.key = mCursor.getString(DetailFragment.COL_TRAILER_KEY);
        return trailer;
    }

    public void setMovieCursor(Cursor cursor){
        this.mCursor=cursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mTrailerImage;

        public ViewHolder(View view){
            super(view);
            mTrailerImage = (ImageView) view.findViewById(R.id.trailer_image);
        }
    }
}
