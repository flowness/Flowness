package com.flowness.volley;

import com.android.volley.Request;

public class ApproveAlertRequest extends BasicRequest {

    public ApproveAlertRequest(String body, ResponseListener listener) {
        super(Request.Method.POST, "https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/approve", body, listener);
    }
}
