package com.example.test.callback;


import android.widget.ImageView;

public interface CollectEventListener<T extends ImageView> {
    void onCollectEvent(T view,int id);
}
