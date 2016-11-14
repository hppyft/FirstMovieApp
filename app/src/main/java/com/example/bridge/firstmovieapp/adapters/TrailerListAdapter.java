package com.example.bridge.firstmovieapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.entities.Trailer;
import com.example.bridge.firstmovieapp.entities.TrailerList;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by bridge on 31/10/2016.
 */

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder>{

    private List<Trailer> trailerList;
    public Activity activity;

    public TrailerListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Trailer trailer = trailerList.get(position);
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
        holder.mTrailerImage.setImageResource(R.color.transparent);
        Picasso.with(activity.getBaseContext()).load("http://img.youtube.com/vi/"+trailer.key+"/0.jpg").into(holder.mTrailerImage);
    }

    @Override
    public int getItemCount() {
        if(trailerList!=null){
            return trailerList.size();
        }
        return 0;
    }

    public void setTrailerList(TrailerList trailerList){
        this.trailerList=trailerList.results;
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
