package com.skteam.diyodardayari.api;


import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.models.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {

    @FormUrlEncoded
    @POST("register_and_login.php")
    Call<User> registerUser(@Field("user_id") String user_id,
                            @Field("email") String email,
                            @Field("name") String name,
                            @Field("phone") String phone,
                            @Field("app_version") String app_version,
                            @Field("signup_type") String signup_type);


    @FormUrlEncoded
    @POST("login.php")
    Call<User> loginUser(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("DataApi.php")
    Call<HomeData> homeDataApi(@Field("data") String data);

//
//    @GET(Variables.api + "show_category")
//    Call<Category> show_category();
//
//    @FormUrlEncoded
//    @POST(Variables.api + "show_posts")
//    Call<Post> show_posts(@Field("last_id") int last_id,
//                            @Field("user_id") String user_id,
//                            @Field("action") String action);

}
