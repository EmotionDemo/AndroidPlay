package com.example.test.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.test.activity.model.SearchHisModel;

/**
 * 声明从SearchHisModel中创建数据库，并声明版本号为1
 */
@Database(entities = {SearchHisModel.class}, version = 1)
public abstract class SearchHisModelBase extends RoomDatabase {
    public abstract SearchHisDao getSearchDao();
}
