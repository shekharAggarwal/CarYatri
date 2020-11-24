package com.caryatri.caryatri.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPI {
    @GET
    Call<Object> getPath(@Url String url);
}
