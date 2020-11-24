package com.caryatri.caryatri.model;

public class CheckUserResponse {

    private String error_msg;

    public CheckUserResponse() {
    }

    public CheckUserResponse(String error_msg) {

        this.error_msg = error_msg;
    }


    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
