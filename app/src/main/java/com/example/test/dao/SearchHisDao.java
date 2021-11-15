package com.example.test.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.test.activity.model.SearchHisModel;

import java.util.List;

@Dao
public interface SearchHisDao {
    /**
     * 查询所有信息
     *
     * @return
     */
    @Query("SELECT * FROM search_his ")
    public List<SearchHisModel> getAllHisInfo();

    /**
     * 添加一条历史记录
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long addHisModel(SearchHisModel searchHisModel);

    /**
     * 删除一条历史信息
     * @param id
     * @return
     */
    @Query("DELETE FROM search_his where his_info = :info")
    public int deleteHisModel(String info);

    /**
     * 删除所有
     */
    @Query("DELETE FROM search_his")
    public void deleteAll();


}
