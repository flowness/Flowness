package com.aquasafe.volley;

import com.android.volley.Request;

public class RegisterRequest extends BasicRequest {

    public RegisterRequest(String body, ResponseListener listener) {
        super(Request.Method.POST, "https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/register", body, listener);
    }
}
