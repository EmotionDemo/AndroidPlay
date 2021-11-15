package com.example.test.activity.model;

import androidx.annotation.ColorLong;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_his")
public class SearchHisModel {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "his_info")
    private String hisInfo;
    public SearchHisModel(int id) {
        this.id = id;
    }

    @Ignore
    public SearchHisModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHisInfo() {
        return hisInfo;
    }

    public void setHisInfo(String hisInfo) {
        this.hisInfo = hisInfo;
    }

    @Override
    public String toString() {
        return "SearchHisModel{" +
                "id=" + id +
                ", hisInfo='" + hisInfo + '\'' +
                '}';
    }
}
