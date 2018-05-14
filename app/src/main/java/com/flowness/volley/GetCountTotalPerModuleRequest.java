package com.flowness.volley;

import com.android.volley.Request;

public class GetCountTotalPerModuleRequest extends BasicRequest {

    public GetCountTotalPerModuleRequest(String moduleSN, ResponseListener listener) {
        super(Request.Method.GET, "https://yg8rvhiiq0.execute-api.eu-west-1.amazonaws.com/poc/total?moduleSN=" + moduleSN, null, listener);
    }
}
