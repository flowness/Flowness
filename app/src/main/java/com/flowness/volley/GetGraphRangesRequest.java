package com.flowness.volley;

import com.android.volley.Request;

public class GetGraphRangesRequest extends BasicRequest {

    public GetGraphRangesRequest(String body, ResponseListener listener) {
        super(Request.Method.POST, "https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/chart", body, listener);
    }
}
