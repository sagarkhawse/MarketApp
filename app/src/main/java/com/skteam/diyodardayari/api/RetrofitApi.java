package com.skteam.diyodardayari.api;


import com.skteam.diyodardayari.models.HomeData;
import com.skteam.diyodardayari.models.Services;
import com.skteam.diyodardayari.models.User;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitApi {

    @FormUrlEncoded
    @POST("register_login.php")
    Call<User> registerUser(@Field("user_id") String user_id,
                            @Field("email") String email,
                            @Field("name") String name,
                            @Field("phone") String phone,
                            @Field("app_version") String app_version,
                            @Field("signup_type") String signup_type);

    @FormUrlEncoded
    @POST("update_user_data.php")
    Call<User> updateUserData(@Field("user_id") String user_id,
                            @Field("name") String name,
                            @Field("whatsapp") String whatsapp,
                            @Field("shop_name") String shop_name,
                            @Field("shop_address") String shop_address,
                            @Field("business_desc") String business_desc,
                              @Field("shop_time") String shop_time,
                              @Field("services") String services,
                              @Field("category_id") String category_id,
                              @Field("phone") String phone,
                              @Field("email") String email);

    @FormUrlEncoded
    @POST("get_my_data.php")
    Call<User> getMyData(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("DataApi.php")
    Call<HomeData> homeDataApi(@Field("data") String data);

    @FormUrlEncoded
    @POST("fetch_services.php")
    Call<Services> fetchServices(@Field("data") String data);

    @Multipart
    @POST("update_user_image.php")
    Call<User> updateUserImage(@Part MultipartBody.Part image, @Part("user_id") RequestBody user_id);


    //
    @GET("user_data.php")
    Call<User> userList();

    @GET("fetch_service_cat.php?data=service_category")
    Call<Services> fetchServiceCategory();

//    @FormUrlEncoded
//    @POST(Variables.api + "show_posts")
//    Call<Post> show_posts(@Field("last_id") int last_id,
//                            @Field("user_id") String user_id,
//                            @Field("action") String action);

}
