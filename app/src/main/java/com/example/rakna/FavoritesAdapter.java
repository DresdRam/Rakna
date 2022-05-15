package com.example.rakna;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakna.Pojo.Car;
import com.example.rakna.Pojo.Favorite;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final ArrayList<Favorite> favoritesData;
    private final Context mContext;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public FavoritesAdapter(Context context, ArrayList<Favorite> data, ItemClickListener mClickListener) {
        this.mContext = context;
        this.favoritesData = data;
        this.mClickListener = mClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favorites_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the views in each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Favorite favorite = favoritesData.get(position);
        holder.locationTextView.setText(favorite.getLocation());
        holder.locationTextView.setSelected(true);
        holder.totalTextView.setText(favorite.getTotalSpots());
        holder.latitudeTextView.setText(String.valueOf(favorite.getLatitude()));
        holder.longitudeTextView.setText(String.valueOf(favorite.getLongitude()));
    }

    // total number of items
    @Override
    public int getItemCount() {
        return favoritesData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView locationTextView, totalTextView, latitudeTextView, longitudeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            locationTextView = itemView.findViewById(R.id.textView_location_favorites);
            totalTextView = itemView.findViewById(R.id.textView_totalPlaces_favorites);
            latitudeTextView = itemView.findViewById(R.id.textView_latitude_favorites);
            longitudeTextView = itemView.findViewById(R.id.textView_longitude_favorites);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Favorite favorite = favoritesData.get(getBindingAdapterPosition());
            if (mClickListener != null) mClickListener.onItemClick(new LatLng(favorite.getLatitude(), favorite.getLongitude()));
        }
    }

}