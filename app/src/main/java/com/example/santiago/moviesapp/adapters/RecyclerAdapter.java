package com.example.santiago.moviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.santiago.moviesapp.Models.Movie;
import com.example.santiago.moviesapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Santiago on 17/11/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MovieViewHolder> {
    private List<Movie> mDataList;
    Context context1;

    final private ListItemClickListener mOnClickListener;
    public interface ListItemClickListener{
        void onClick(Movie movieClicked);
    }

    public RecyclerAdapter(Context context,ListItemClickListener listener)  {
        context1 = context;
        mOnClickListener = listener;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView imageMovie;

        public MovieViewHolder(View view) {
            super(view);
            imageMovie =  view.findViewById(R.id.imageMovie);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosiion = getAdapterPosition();
            mOnClickListener.onClick(mDataList.get(clickedPosiion));
        }
    }


    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie actualMovie = mDataList.get(position);
        Picasso.with(context1).load(actualMovie.getUrlImage()).into(holder.imageMovie);
    }

    @Override
    public int getItemCount() {
        if (null == mDataList) return 0;
        return mDataList.size();
    }
    public void setData (List<Movie> data){
        mDataList = data;
        notifyDataSetChanged();
    }
}
