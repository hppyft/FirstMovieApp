package com.example.bridge.firstmovieapp.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.entities.Review;
import com.example.bridge.firstmovieapp.entities.ReviewList;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder>{

    private List<Review> reviewList;
    public Activity activity;

    public ReviewListAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_content, parent, false);
        return new ReviewListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewListAdapter.ViewHolder holder, int position) {
        final Review review = reviewList.get(position);
        holder.mReviewAuthor.setText(review.author);
        holder.mReviewContent.setText(review.content);
    }

    @Override
    public int getItemCount() {
        if(reviewList!=null){
            return reviewList.size();
        }
        return 0;
    }

    public void setReviewList(ReviewList reviewList){
        this.reviewList=reviewList.results;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public ViewHolder(View view){
            super(view);
            mReviewAuthor = (TextView) view.findViewById(R.id.review_author);
            mReviewContent = (TextView) view.findViewById(R.id.review_content);
        }
    }
}