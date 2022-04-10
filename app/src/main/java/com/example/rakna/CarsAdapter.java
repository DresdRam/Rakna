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

import java.util.List;

class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {

    private final List<Car> carsData;
    private final Context mContext;
    private ItemClickListener mClickListener;
    private final int columns;
    private int c = 1;

    // data is passed into the constructor
    CarsAdapter(Context context, List<Car> data, ItemClickListener itemClickListener, int columns) {
        this.mContext = context;
        this.carsData = data;
        this.mClickListener = itemClickListener;
        this.columns = columns;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.car_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the views in each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = carsData.get(position);

        if(c == 1 && c != columns){
            holder.layout.setBackgroundResource(R.drawable.ic_cardview_background_left);
            c++;
        }
        else if(c == columns){
            holder.layout.setBackgroundResource(R.drawable.ic_cardview_background_right);
            holder.carImage.setScaleX(-1f);
            c = 1;
        }
        else
        {
            holder.layout.setBackgroundResource(R.drawable.ic_cardview_background_middle);
            holder.carImage.setScaleX(-1f);
            c++;
        }

        if (car.isBusy)
        {
            holder.carImage.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.carImage.setVisibility(View.INVISIBLE);
            if(holder.carImage.getScaleX() == 1f){
                holder.carImage.setTranslationX(50);
            }else{
                holder.carImage.setTranslationX(-50);
            }
        }
        holder.carImage.setImageResource(car.carImageResource);
        holder.parkNumber.setText(String.valueOf(position + 1));

    }

    // total number of items
    @Override
    public int getItemCount() {
        return carsData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView carImage;
        TextView parkNumber;
        ConstraintLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.iv_car);
            parkNumber = itemView.findViewById(R.id.tv_park_number);
            layout = itemView.findViewById(R.id.car_item_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(getAdapterPosition() + 1);
        }
    }

    // convenience method for getting data at click position
    Car getItem(int id) {
        return carsData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}