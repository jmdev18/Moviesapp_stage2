package com.example.santiago.moviesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.santiago.moviesapp.R;

/**
 * Created by Santiago on 26/12/2017.
 */

public class RecyclerTrailersAdapter extends RecyclerView.Adapter<RecyclerTrailersAdapter.TrailersViewHolder> {

    Context mContext;
    String[] trailersDataList;
    private final TrailersAdapterOnClickHandler mTrailersAdapterOnClickHandler;

    public interface TrailersAdapterOnClickHandler {
        void onClickTrailers(String url);
    }

    public RecyclerTrailersAdapter(Context context, TrailersAdapterOnClickHandler trailersAdapterOnClickHandler) {
        mContext = context;
        mTrailersAdapterOnClickHandler = trailersAdapterOnClickHandler;
    }
    //implementar la clase extend view holder ,y luego extender el adapter a recyclerview.adpter<a la clase creada>
    //implementar los metodos requeridos

    public class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView trailerNumber;

        public TrailersViewHolder(View itemView) {
            super(itemView);
            trailerNumber = itemView.findViewById(R.id.trailerNumber);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mTrailersAdapterOnClickHandler.onClickTrailers(trailersDataList[clickedPosition]);
            //aqui en el onclick se pasa al objeto de tipo interfaz trailersadapteronclickhandler
            //en el metodo onclick de esa interfaz la lista String[] en la posicion clickeada
            //y en la actividad simplemente se pasa el intent correctos
            //qque en este caso es el de abrir youtube
        }
    }

    @Override
    public void onBindViewHolder(final TrailersViewHolder holder, int position) {
        final String actualUrlTrailer = trailersDataList[position];
        holder.trailerNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_trailers, parent, false);
        return new TrailersViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (null == trailersDataList) return 0;
        return trailersDataList.length;
    }

    public void setData(String[] data) {
        trailersDataList = data;
        notifyDataSetChanged();
    }
}
