package com.example.bridge.firstmovieapp.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bridge.firstmovieapp.R;
import com.example.bridge.firstmovieapp.adapters.ReviewListCursorAdapter;
import com.example.bridge.firstmovieapp.adapters.TrailerListCursorAdapter;
import com.example.bridge.firstmovieapp.entities.Movie;
import com.example.bridge.firstmovieapp.interfaces.IDetailPres;
import com.example.bridge.firstmovieapp.interfaces.IDetailView;
import com.example.bridge.firstmovieapp.presenters.DetailPresenter;

import static com.example.bridge.firstmovieapp.data.Provider.ARG_MOVIE_URI;

public class DetailFragment extends Fragment implements IDetailView {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private IDetailPres mPresenter;
    public Movie mMovie;
    private ShareActionProvider mShareActionProvider;
    public static final String TRAILER_CHANGED = "trailer_changed";
    public static final int COL_TRAILER_ID = 0;
    public static final int COL_TRAILER_KEY = 1;
    public static final String REVIEW_CHANGED = "review_changed";
    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_AUTHOR = 1;
    public static final int COL_REVIEW_CONTENT = 2;
    private RecyclerView mTrailerRecyclerView;
    private TrailerListCursorAdapter mTrailerRecyclerAdapter;
    private RecyclerView mReviewRecyclerView;
    private ReviewListCursorAdapter mReviewRecyclerAdapter;
    private CheckBox favoriteCheckBox;
    private TextView title;
    private ImageView poster;
    private TextView overviewLabel;
    private TextView overview;
    private TextView rateLabel;
    private TextView rate;
    private TextView releaseLabel;
    private TextView release;
    private TextView trailerLabel;
    private ProgressBar trailerProgressBar;
    private TextView reviewLabel;
    private ProgressBar reviewProgressBar;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        Uri uri = null;
        if (args!=null && args.getParcelable(ARG_MOVIE_URI)!=null) {
            uri = args.getParcelable(ARG_MOVIE_URI);
        }
        mPresenter = new DetailPresenter(this, getContext(), getLoaderManager(), uri);
        mPresenter.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        mPresenter = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        this.favoriteCheckBox = (CheckBox) rootView.findViewById(R.id.favorite_check_box);
        this.title = (TextView) rootView.findViewById(R.id.detail_title);
        this.poster = ((ImageView) rootView.findViewById(R.id.detail_poster));
        this.overviewLabel = (TextView)rootView.findViewById(R.id.detail_overview_label);
        this.overview = (TextView) rootView.findViewById(R.id.detail_overview);
        this.rateLabel = (TextView) rootView.findViewById(R.id.detail_rate_label);
        this.rate = (TextView) rootView.findViewById(R.id.detail_rate);
        this.releaseLabel = (TextView) rootView.findViewById(R.id.detail_release_label);
        this.release = (TextView) rootView.findViewById(R.id.detail_release);
        this.trailerLabel = (TextView) rootView.findViewById(R.id.detail_trailer_label);
        this.trailerProgressBar = (ProgressBar) rootView.findViewById(R.id.trailer_progress_bar);
        this.mTrailerRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_trailer_list);
        this.mTrailerRecyclerAdapter = new TrailerListCursorAdapter(getActivity());
        this.mTrailerRecyclerView.setAdapter(mTrailerRecyclerAdapter);
        this.reviewLabel = (TextView) rootView.findViewById(R.id.detail_review_label);
        this.reviewProgressBar = (ProgressBar) rootView.findViewById(R.id.review_progress_bar);
        this.mReviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_review_list);
        this.mReviewRecyclerAdapter = new ReviewListCursorAdapter(getActivity());
        this.mReviewRecyclerView.setAdapter(mReviewRecyclerAdapter);
        this.favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mPresenter.setFavorite(mMovie, true);
                }
                else{
                    mPresenter.setFavorite(mMovie, false);
                }
            }
        });
        if(null==mMovie) {
            this.favoriteCheckBox.setVisibility(View.GONE);
            this.trailerProgressBar.setVisibility(View.GONE);
            this.reviewProgressBar.setVisibility(View.GONE);
        }
        return rootView;
    }
    @Override
    public void showMovie(Movie movie) {
        mMovie = movie;
        this.favoriteCheckBox.setVisibility(View.VISIBLE);
        this.favoriteCheckBox.setText(R.string.favorite_label);
        this.title.setText(movie.title);
//        Utility ut = new Utility(getActivity());
//        if(ut.isConnectionAvailable(getContext())) {
        Glide.with(getActivity().getBaseContext()).
                load("http://image.tmdb.org/t/p/w185" +
                        movie.poster_path)
                .fitCenter()
                .placeholder(R.drawable.blank_poster)
                .crossFade()
                .into(this.poster);
//        }
//        else{
//            this.poster.setImageResource(R.drawable.blank_poster);
//        }
        this.overviewLabel.setText(R.string.overview_label);
        this.overview.setText(movie.overview);
        this.rateLabel.setText(R.string.rate_label);
        this.rate.setText(""+movie.vote_average);
        this.releaseLabel.setText(R.string.release_label);
        this.release.setText(movie.release_date);
        this.trailerProgressBar.setVisibility(View.GONE);
        this.reviewProgressBar.setVisibility(View.GONE);
        if (movie.favorite==1){
            this.favoriteCheckBox.setChecked(true);
        }
        else if (movie.favorite==0){
            this.favoriteCheckBox.setChecked(false);
        }
        else{
            Log.d(LOG_TAG, "THE VALUE OF FAVORITE OBJECT IS "+movie.favorite);
        }
    }

    @Override
    public void updatersStarted(){
        if(mTrailerRecyclerAdapter.getItemCount()==0) {
            this.trailerLabel.setText(R.string.searching_trailers_label);
            this.trailerProgressBar.setVisibility(View.VISIBLE);
        }
        if(mReviewRecyclerAdapter.getItemCount()==0) {
            this.reviewLabel.setText(R.string.searching_reviews_label);
            this.reviewProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUpdatedReviewList(Cursor cursor){
        mReviewRecyclerAdapter.setMovieCursor(cursor);
        if (mReviewRecyclerAdapter.getItemCount() != 0) {
            this.reviewLabel.setText(R.string.detail_review_label);
            this.reviewProgressBar.setVisibility(View.GONE);
        }
        else{
            this.reviewLabel.setText(R.string.no_reviews_label);
            this.reviewProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUpdatedTrailerList(Cursor cursor) {
        mTrailerRecyclerAdapter.setMovieCursor(cursor);
        if (mTrailerRecyclerAdapter.getItemCount() != 0) {
            this.trailerLabel.setText(R.string.trailer_label);
            this.trailerProgressBar.setVisibility(View.GONE);
        }
        else{
            this.trailerLabel.setText(R.string.no_trailers_label);
            this.trailerProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail_fragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mMovie!=null){
            mShareActionProvider.setShareIntent(mPresenter.createShareIntent());
        }
    }
}