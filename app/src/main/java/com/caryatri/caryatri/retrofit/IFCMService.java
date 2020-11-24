package com.caryatri.caryatri.retrofit;

import com.caryatri.caryatri.model.DataMessage;
import com.caryatri.caryatri.model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAEL0i130:APA91bFmpESQYC8VdDRReQrxCcvMdakr-qxFeYlONxN3OK3kgl94cPETb-IxnycLFjNG-N5qThXKO9HJHlLiH6IlRI4Rfj13RADYmeMfOqgYsHVux9NCGyJPVkigXZ6yjgujoTf25PuY"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);
}
