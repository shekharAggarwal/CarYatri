package com.caryatri.caryatri.retrofit;


import com.caryatri.caryatri.model.Cab;
import com.caryatri.caryatri.model.CheckUserResponse;
import com.caryatri.caryatri.model.ImageUser;
import com.caryatri.caryatri.model.LocalDatabaseOfUser;
import com.caryatri.caryatri.model.RatingCab;
import com.caryatri.caryatri.model.RattingDriverInfo;
import com.caryatri.caryatri.model.Token;
import com.caryatri.caryatri.model.Trip;
import com.caryatri.caryatri.model.User;
import com.caryatri.caryatri.model.cabDetails;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ICarYatri {

    @FormUrlEncoded
    @POST("checkuser.php")
    Call<CheckUserResponse> CheckUserExists(@Field("email") String email, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("registeruser.php")
    Call<User> registerNewUser(@Field("username") String username,
                               @Field("email") String email,
                               @Field("phone") String phone,
                               @Field("password") String password);

    @FormUrlEncoded
    @POST("getuser.php")
    Call<User> getUserInfo(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("checking.php")
    Call<CheckUserResponse> checkUser(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("checkemail.php")
    Call<CheckUserResponse> checkUserEmail(@Field("email") String email);

    @FormUrlEncoded
    @POST("updatepwd.php")
    Call<CheckUserResponse> updatePassword(@Field("password") String password, @Field("phone") String phone);

    @FormUrlEncoded
    @POST("getcab.php")
    Observable<List<Cab>> getCab(@Field("CabType") String CabType,@Field("cabLocation") String cabLocation);

    @FormUrlEncoded
    @POST("insertnewoneway.php")
    Call<String> insertOneWay(@Field("fullName") String fullName,
                              @Field("phoneNumber") String phoneNumber,
                              @Field("email") String email,
                              @Field("sourceAddress") String sourceAddress,
                              @Field("destinationAddress") String destinationAddress,
                              @Field("pickupDate") String pickupDate,
                              @Field("pickupTime") String pickupTime,
                              @Field("source") String source,
                              @Field("destination") String destination,
                              @Field("Cabs") String Cabs,
                              @Field("BookAccount") String BookAccount,
                              @Field("cabFare") String cabFare,
                              @Field("cabDriver") String cabDriver,
                              @Field("cabStatus") int cabStatus,
                              @Field("cabModel") String cabModel,
                              @Field("cabTnxId") String cabTnxId);

    @FormUrlEncoded
    @POST("insertnewroundway.php")
    Call<String> insertRoundWay(@Field("fullName") String fullName,
                                @Field("phoneNumber") String phoneNumber,
                                @Field("email") String email,
                                @Field("sourceAddress") String sourceAddress,
                                @Field("destinationAddress") String destinationAddress,
                                @Field("pickupDate") String pickupDate,
                                @Field("dropDate") String dropDate,
                                @Field("pickupTime") String pickupTime,
                                @Field("source") String source,
                                @Field("destination") String destination,
                                @Field("Cabs") String Cabs,
                                @Field("BookAccount") String BookAccount,
                                @Field("cabFare") String cabFare,
                                @Field("cabDriver") String cabDriver,
                                @Field("cabStatus") int cabStatus,
                                @Field("cabModel") String cabModel,
                                @Field("cabTnxId") String cabTnxId);


    @FormUrlEncoded
    @POST("updatetoken.php")
    Call<String> updateToken(@Field("phone") String phone,
                             @Field("token") String token,
                             @Field("isServerToken") String isServerToken);


    @FormUrlEncoded
    @POST("gettoken.php")
    Call<Token> getToken(@Field("phone") String phone,
                         @Field("isServerToken") String isServerToken);

    @FormUrlEncoded
    @POST("getcabbymodel.php")
    Observable<List<Cab>> getCabByModel(@Field("cabModel") String cabModel);

    @FormUrlEncoded
    @POST("gettrip.php")
    Call<Trip> getTrip(@Field("id") String id);


    @FormUrlEncoded
    @POST("updatetrip.php")
    Call<String> updateTripStatusBooking(@Field("code") String code,
                                         @Field("id") String id,
                                         @Field("TripStatus") String TripStatus);


    @FormUrlEncoded
    @POST("gettripbooking.php")
    Observable<List<Trip>> getTripData(@Field("code") String code,
                                       @Field("BookAccount") String BookAccount);

    @FormUrlEncoded
    @POST("updateuser.php")
    Call<String> updateUser(@Field("id") String id,
                            @Field("Name") String Name,
                            @Field("oldPhone") String oldPhone,
                            @Field("image") String image,
                            @Field("Phone") String Phone,
                            @Field("Email") String Email);

    @FormUrlEncoded
    @POST("insertRating.php")
    Call<String> insertRating(@Field("CabDriver") String CabDriver,
                              @Field("BookAccount") String BookAccount,
                              @Field("name") String name,
                              @Field("image") String image,
                              @Field("Rating") String Rating,
                              @Field("Review") String Review);

    @FormUrlEncoded
    @POST("getdriverinfo.php")
    Call<RattingDriverInfo> getDriverInfo(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("gettripbooking.php")
    Observable<List<Trip>> getTripData(@Field("code") String code,
                                       @Field("date") String date,
                                       @Field("BookAccount") String CabDriver);

    @FormUrlEncoded
    @POST("getcabbydriver.php")
    Call<cabDetails> getcabbydriver(@Field("Phone") String Phone);

    @FormUrlEncoded
    @POST("getrattingcab.php")
    Call<RatingCab> getRatingCab(@Field("phone") String phone);


    @FormUrlEncoded
    @POST("insertlocaldatabase.php")
    Call<String> insertLocalDatabase(@Field("Phone") String Phone,
                                     @Field("UserDataOneWay") String UserDataOneWay,
                                     @Field("CabOneWay") String CabOneWay,
                                     @Field("UserDataRoundWay") String UserDataRoundWay,
                                     @Field("CabRoundWay") String CabRoundWay,
                                     @Field("NotificationDB") String NotificationDB,
                                     @Field("DriverPhone") String DriverPhone,
                                     @Field("MapStatus") String MapStatus);

    @FormUrlEncoded
    @POST("getlocaldatabase.php")
    Call<LocalDatabaseOfUser> getLocalDatabase(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("uploaduserimage.php")
    Call<ImageUser> UploadUserImage(@Field("image_name") String title, @Field("image") String image);

    @FormUrlEncoded
    @POST("cancelcab.php")
    Call<String> CancelCab(@Field("BookingAccount") String BookingAccount,
                           @Field("cabModel") String cabModel);

    @FormUrlEncoded
    @POST("getcancelcab.php")
    Observable<List<Trip>> getCancelCab(@Field("code") int code,
                                        @Field("BookingAccount") String CabDriver);

    @FormUrlEncoded
    @POST("gettripdetail.php")
    Call<Trip> getTripDetail(@Field("BookAccount") String BookAccount,
                             @Field("CabDriver") String CabDriver);
}
