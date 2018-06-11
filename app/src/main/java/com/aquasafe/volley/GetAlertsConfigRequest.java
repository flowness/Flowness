package com.aquasafe.volley;

import com.android.volley.Request;

public class GetAlertsConfigRequest extends BasicRequest {

    public GetAlertsConfigRequest(String moduleSN, ResponseListener listener) {
        super(Request.Method.GET, "https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/alertsconfig?moduleSN=" + moduleSN, null, listener);
    }
}
