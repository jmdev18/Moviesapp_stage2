package com.example.santiago.moviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.santiago.moviesapp.Models.Review;
import com.example.santiago.moviesapp.R;

import java.util.List;

/**
 * Created by Santiago on 30/12/2017.
 */

public class RecyclerReviewsAdapter extends RecyclerView.Adapter<RecyclerReviewsAdapter.ReviewsViewHolder> {
    Context mContext;
    List<Review> mReviewsList;

    public RecyclerReviewsAdapter(Context context) {
        mContext = context;
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewAuthor;
        public TextView reviewContent;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.authorReview);
            reviewContent = itemView.findViewById(R.id.reviewContent);
        }
    }

    @Override
    public ReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_reviews, parent, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsViewHolder holder, int position) {
        Review actualReview = mReviewsList.get(position);
        holder.reviewAuthor.setText(actualReview.getAuthor());
        holder.reviewContent.setText(actualReview.getReview());
    }

    @Override
    public int getItemCount() {
        if (null == mReviewsList) return 0;
        return mReviewsList.size();
    }

    public void setData(List<Review> reviewsList) {
        mReviewsList = reviewsList;
        notifyDataSetChanged();
    }
}
