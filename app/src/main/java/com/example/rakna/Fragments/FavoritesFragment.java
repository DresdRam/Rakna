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
import com.example.rakna.FavoritesItemClickListener;
import com.example.rakna.LoadingDialog;
import com.example.rakna.Pojo.Favorite;
import com.example.rakna.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements FavoritesItemClickListener {

    View view;
    ArrayList<Favorite> favoritesData;
    RecyclerView favoritesRecyclerview;
    FavoritesAdapter mAdapter;
    LoadingDialog loadingDialog;
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
        loadingDialog = new LoadingDialog(getActivity());
        activity = (HomeCommunicator) getActivity();
    }

    private void getFavoritesData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorites").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingDialog.show(getResources().getString(R.string.loadingFavorites));
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
        loadingDialog.dismiss();
    }

    @Override
    public void onItemClick(LatLng latLng) {
        activity.zoomToParkingLocation(latLng);
    }
}