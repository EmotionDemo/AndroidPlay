package com.example.test.activity.model;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    /**
     * 获取用户信息
     * 
     * @return
     */
    @GET("user/lg/userinfo/json")
    public Call<UserInfoModel> getUserInfo();

    /**
     * 登出
     *
     * @return
     */
    @GET("/user/logout/json")
    public Call<BaseInfoModel> logOut();

    /**
     * 注册新用户
     * 
     * @param username
     * @param password
     * @param repassword
     * @return
     */
    @FormUrlEncoded
    @POST("/user/register")
    public Call<RegisterModel> register(@Field("username") String username, @Field("password") String password,
            @Field("repassword") String repassword);

    /**
     * 登录接口
     *
     * @param username
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("/user/login")
    public Call<LoginModel> login(@Field("username") String username, @Field("password") String password);

    /**
     * 首页banner
     */
    @GET("/banner/json")
    public Call<HomeBannerBean> getHomeBanner();

    /**
     * 首页文章列表
     *
     * @param page
     * @return
     */
    @GET("/article/list/{page}/json")
    public Call<HomeArticleModel> getHomeArticle(@Path("page") int page);

    /**
     * 热搜接口
     *
     * @return
     */
    @GET("/hotkey/json")
    public Call<HotSearchModel> getHotSearchModel();

    /**
     * 查询接口
     *
     * @param page
     * @param searchInfo
     * @return
     */
    @POST("/article/query/{page}/json")
    public Call<SearchDetailModel> getSearchDetail(@Path("page") int page, @Query("k") String searchInfo);

    /**
     * 收藏文章
     *
     * @param id
     * @return
     */
    @POST("/lg/collect/{id}/json")
    public Call<BaseInfoModel> collectArticle(@Path("id") int id);

    /**
     * 取消收藏
     */
    @POST("/lg/uncollect_originId/{id}/json")
    public Call<BaseInfoModel> unCollectArticle(@Path("id") int id);

    /**
     * 获取项目列表
     * 
     * @param page
     * @return
     */
    @GET("/project/list/{page}/json")
    public Call<ProjectModel> showPjoArticle(@Path("page") int page, @Query("cid") int cid);


    /**
     * 项目分类
     * @return
     */
    @GET("/project/tree/json")
    public Call<PjoTypeModel> showAllPjoTypes();


    /**
     * 获取历史记录数据信息
     * @return
     */
    @GET("/wxarticle/list/{id}/{page}/json")
    public Call<PubNumHisModel> getPubNumHisData(@Path("id") int id, @Path("page") int page);


    /**
     * 获取公众号消息
     * @return
     */
    @GET("/wxarticle/chapters/json")
    public Call<PublisherModel> showPublishers();

    /**
     * 体系
     * @return
     */
    @GET("/tree/json")
    public Call<RapexModel> showRapexs();

    /**
     * 导航
     * @return
     */
    @GET("/navi/json")
    Call<NavModel> showNavData();

    /**
     * 详细的体系信息
     * @param page
     * @param cid
     * @return
     */
    @GET("/article/list/{page}/json")
    public Call<RapexDetailModel> showRapexDetail(@Path("page")int page,@Query("cid") int cid);

}
