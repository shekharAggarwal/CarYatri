package com.caryatri.caryatri.model;

import com.google.gson.annotations.SerializedName;

public class ImageUser {


    @SerializedName("image_name")
    private String Title;

    @SerializedName("image")
    private String Image;


    @SerializedName("response")
    private String Response;

    public String getResponse() {
        return Response;
    }
}