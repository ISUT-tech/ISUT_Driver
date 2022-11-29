package com.isut.cabdriver.apiclient;



import com.isut.cabdriver.model.RegisterModel;
import com.isut.cabdriver.model.UpdateResponse;
import com.isut.cabdriver.model.ValidResponse;
import com.isut.cabdriver.model.addcar.AddCarModel;
import com.isut.cabdriver.model.booking.BookingModel;
import com.isut.cabdriver.model.cabdetails.CabDetailsModel;
import com.isut.cabdriver.model.cabimg.CabImgModel;
import com.isut.cabdriver.model.cabs.CabsModel;
import com.isut.cabdriver.model.logins.LogInModels;
import com.isut.cabdriver.model.newuser.NewUserResponse;
import com.isut.cabdriver.model.notibooking.NotiBookingResponse;
import com.isut.cabdriver.model.rewardpoint.RewardResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {


    @POST("driver/add")
    Call<RegisterModel> signup(@Body RequestBody  jsonObject);

   // @POST("cab/addCabDetails")
   @POST("driver/update")
   Call<UpdateResponse> update(@Header("Authorization") String token, @Body RequestBody  jsonObject);
    @POST("cab/add")
    Call<AddCarModel> addcar(@Header("Authorization") String token, @Body RequestBody  jsonObject);

  @POST("driver/active")
    Call<AddCarModel> driverActive(@Header("Authorization") String token,
                                   @Query("active") boolean active, @Query("id") String id);



    @POST("auth/login")
    Call<LogInModels> login(@Body RequestBody  jsonObject);



    //    @POST("file/uploadFile")
    @Multipart
    @POST("file/upload")
    Call<CabImgModel> editsimgfun(@Header("Authorization") String token,
                                        @PartMap HashMap<String, RequestBody> dra,
                                        @Part MultipartBody.Part[] maindoc);

   // @GET("cab/getCabDetailsByUserId")

    @GET
    Call<NewUserResponse> cabdetals(@Header("Authorization") String token,
                                    @Url String url);
 @GET
    Call<RewardResponse> rewardsdetals(@Header("Authorization") String token,
                                       @Url String url);


    //@GET("booking/getBookingListByDriverId")
    @GET
    Call<CabsModel> gercablist(@Header("Authorization") String token, @Url String url);

   @POST("account/validator")
    Call<ValidResponse> validator(@Header("Authorization") String token);

///booking/{id}/updateStatus/{status}
  // @POST("booking/updateBookingStatusById")
    @POST
    Call<NotiBookingResponse> fixbooing(@Header("Authorization") String token,
                                        @Url String url);


   // @GET("file/getFile/")
    @GET
    Call<ResponseBody> geimgpath(@Header("Authorization")
                                         String token,@Url String url);

    @DELETE
    Call<UpdateResponse> deleteAccount(@Header("Authorization") String token, @Url String url);
    @POST("forgotPassword")
    Call<UpdateResponse> forgotpassword(@Header("Authorization") String token, @Body RequestBody  jsonObject);

/*
  @POST
  String token, @PartMap HashMap<String, RequestBody> dra
  ("booking/addBooking")
    Call<LoginModel> booking(@Body RequestBody  jsonObject);
*/

//remark accept reject
}

