package com.example.rakna;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakna.Pojo.Car;
import com.example.rakna.Pojo.CarSpot;

import java.util.ArrayList;
import java.util.List;

class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {

    private final List<Car> carsData;
    private final ArrayList<CarSpot> bookingList;
    private final Context context;
    private ParkingItemClickListener clickListener;
    private final int columns;
    private int c = 1;

    // data is passed into the constructor
    CarsAdapter(Context context, List<Car> data, ArrayList<CarSpot> bookingList, int columns, ParkingItemClickListener clickListener) {
        this.context = context;
        this.carsData = data;
        this.columns = columns;
        this.bookingList = bookingList;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.car_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the views in each item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car car = carsData.get(position);
        CarSpot booking = bookingList.get(position);
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

        if (car.getBusy())
        {
            holder.carImage.setVisibility(View.VISIBLE);
            holder.textView.setAlpha(0);
            holder.textView.setText(context.getResources().getString(R.string.select));
        }
        else
        {
            holder.carImage.setVisibility(View.INVISIBLE);
            if(holder.carImage.getScaleX() == 1f){
                holder.carImage.setTranslationX(50);
            }else{
                holder.carImage.setTranslationX(-50);
            }
            if(booking.isBooked()){
                holder.textView.setAlpha(1f);
                holder.textView.setText(context.getResources().getString(R.string.booked));
            }
        }
        holder.carImage.setImageResource(car.getCarImageResource());
    }

    // total number of items
    @Override
    public int getItemCount() {
        return carsData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView carImage;
        TextView textView;
        ConstraintLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            carImage = itemView.findViewById(R.id.iv_car);
            textView = itemView.findViewById(R.id.button_book_select);
            layout = itemView.findViewById(R.id.car_item_layout);
            initButtonListener();
            initCarListener();
        }

        private void initCarListener() {
            carImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null){
                        clickListener.onCarClicked(getBindingAdapterPosition());
                    }
                }
            });
        }

        private void initButtonListener() {
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null){
                        clickListener.onButtonClicked(getBindingAdapterPosition());
                    }
                }
            });
        }
    }
}