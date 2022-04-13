package com.example.rakna.pojo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private MutableLiveData<UserModel> mutableLiveData;
    private Repository mRepository;

    public void init()
    {
        if (mutableLiveData!=null){
        return;
        }
        mRepository=Repository.getInstance();
        mutableLiveData=Repository.getInstance().getData();

    }
    public LiveData<UserModel> getProfile(){
        return mutableLiveData;
    }
}
