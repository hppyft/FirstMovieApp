package com.example.bridge.firstmovieapp.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.entities.Review;
import com.example.bridge.firstmovieapp.fragments.DetailFragment;

public class ReviewListCursorAdapter extends RecyclerView.Adapter<ReviewListCursorAdapter.ViewHolder>{

    private Cursor mCursor;
    public Activity activity;

    public ReviewListCursorAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ReviewListCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_content, parent, false);
        return new ReviewListCursorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewListCursorAdapter.ViewHolder holder, int position) {
        final Review review = getItem(position);
        holder.mReviewAuthor.setText(review.author);
        holder.mReviewContent.setText(review.content);
    }

    @Override
    public int getItemCount() {
        if(mCursor!=null){
            return mCursor.getCount();
        }
        return 0;
    }

    private Review getItem(int position){
        mCursor.moveToPosition(position);
        Review review = new Review();
        review.id = mCursor.getString(DetailFragment.COL_REVIEW_ID);
        review.author = mCursor.getString(DetailFragment.COL_REVIEW_AUTHOR);
        review.content = mCursor.getString(DetailFragment.COL_REVIEW_CONTENT);
        return review;
    }

    public void setMovieCursor(Cursor cursor){
        this.mCursor=cursor;
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