package com.learn2crack.imageupload;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @Multipart
    @POST("/images/upload")
    Call<Response> uploadImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("api/auth/profile_update/{uid}")
    Call<ProfileUpdateRes> updateProfile(@Part MultipartBody.Part image, @Part("name") String name, @Part("email") String email,
                                         @Part("mobile") String mobile, @Part("address") String address, @Path("uid")  int id);    //token required
}
