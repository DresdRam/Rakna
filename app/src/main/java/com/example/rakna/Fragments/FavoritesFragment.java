package com.example.rakna.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rakna.FavoritesAdapter;
import com.example.rakna.HomeCommunicator;
import com.example.rakna.ItemClickListener;
import com.example.rakna.Pojo.Favorite;
import com.example.rakna.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements ItemClickListener{

    View view;
    ArrayList<Favorite> favoritesData;
    RecyclerView favoritesRecyclerview;
    FavoritesAdapter mAdapter;
    SpinKitView spinKitView;
    HomeCommunicator activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        initComponents();
        getFavoritesData();
        return view;
    }

    private void initComponents() {
        favoritesData = new ArrayList<>();
        spinKitView = view.findViewById(R.id.spinKit_favorites);
        activity = (HomeCommunicator) getActivity();
    }

    private void getFavoritesData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorites").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                spinKitView.setVisibility(View.VISIBLE);
                favoritesData.clear();
                for (DataSnapshot dataSnapshot :
                        snapshot.getChildren()) {
                    Favorite favorite = dataSnapshot.getValue(Favorite.class);
                    favoritesData.add(favorite);
                }
                setUpFavoritesAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpFavoritesAdapter() {
        favoritesRecyclerview = view.findViewById(R.id.recycler_view_favorites);
        mAdapter = new FavoritesAdapter(getActivity(), favoritesData, this);
        favoritesRecyclerview.setAdapter(mAdapter);
        favoritesRecyclerview.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        spinKitView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(LatLng latLng) {
        activity.zoomToParkingLocation(latLng);
    }
}