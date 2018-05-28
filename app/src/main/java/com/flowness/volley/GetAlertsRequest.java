package com.flowness.volley;

import com.android.volley.Request;

public class GetAlertsRequest extends BasicRequest {

    public GetAlertsRequest(String moduleSN, ResponseListener listener) {
        super(Request.Method.GET, "https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/notification?moduleSN=" + moduleSN, null, listener);
    }
}
