package com.example.test.callback;

import com.example.test.activity.model.UserInfoModel;

public class Event {
    private UserInfoModel model;
    public Event(UserInfoModel model){
        this.model = model;
    }

    public UserInfoModel getModel(){
        return this.model;
    }
}
