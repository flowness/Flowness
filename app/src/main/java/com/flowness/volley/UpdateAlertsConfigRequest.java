package com.flowness.volley;

import com.android.volley.Request;

public class UpdateAlertsConfigRequest extends BasicRequest {

    public UpdateAlertsConfigRequest(String body, ResponseListener listener) {
        super(Request.Method.POST, "https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/alertsconfig", body, listener);
    }
}
