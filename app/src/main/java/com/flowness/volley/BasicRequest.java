package com.flowness.volley;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class BasicRequest implements com.android.volley.Response.ErrorListener{
    Request mRequest;

    ResponseListener mResponseListener;
    String mUrl;
    String mPostBody;
    int mRequestType = Request.Method.GET;
    static RequestQueue mRequestQueue;

    int timeout = 10000;
    int retries = 3;

    public BasicRequest()
    {
        //default constructor
    }

    public BasicRequest(int requestType, String url, String postBody, ResponseListener listener) {
        mResponseListener = listener;
        mUrl = url;
        mPostBody = postBody;
        mRequestType = requestType;
    }

    void initRequest()
    {
        mRequest = new Request(mRequestType, mUrl, this) {
            @Override
            protected com.android.volley.Response parseNetworkResponse(NetworkResponse response) {
                String responseString = handleResponse(response);

//                Timber.e("Response success * " + responseString);

                BasicRequest.this.deliverResponse(Response.STATUS_SUCCESS, "", responseString, response.statusCode, response.headers);

                return com.android.volley.Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(Object response) {

            }

            @Override
            public int compareTo(@NonNull Object o) {
                return 0;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return BasicRequest.this.getBody();
            }

            @Override
            public String getBodyContentType() {
                return BasicRequest.this.getBodyContentType();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return BasicRequest.this.getHeaders();
            }
        };
    }

    @Override
    public void onErrorResponse(VolleyError error) {
//        Timber.e("Response error * " + error.getMessage());
        deliverResponse(Response.STATUS_ERROR, error.getMessage(), "", -1, null);
    }

    public Map<String, String> getHeaders()
    {
        HashMap<String, String> headers = new HashMap<>();

        return headers;
    }

    public byte[] getBody()
    {
        if(mPostBody != null)
        {
//            Timber.e("POST BODY = " + mPostBody);
            return mPostBody.getBytes();
        }
//        else Timber.e("POST BODY is null");

        return null;
    }

    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    public JSONObject getJSONBody()
    {
        return new JSONObject();
    }

    public String handleResponse(NetworkResponse response)
    {
        if(response != null)
        {
            return new String(response.data);
        }

        return null;
    }

    void deliverResponse(final int status, final String message, final String data, final int resultCode, final Map<String, String> headers)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(mResponseListener != null)
                {
                    Response response = new Response();
                    response.status = status;
                    response.message = message;
                    response.data = data;
                    response.resultCode = resultCode;
                    response.headers = headers;

                    mResponseListener.onResponse(response);
                }
            }
        });

    }

    public void setResponseListener(ResponseListener listener)
    {
        mResponseListener = listener;
    }

    public void execute(Context context)
    {
//        Timber.e("request url = " + mUrl);

        initRequest();
        getRequestQueue(context);

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                retries,
                0));
        mRequestQueue.add(mRequest);
        mRequestQueue.start();
    }

    public RequestQueue getRequestQueue(Context context)
    {
        if(mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public interface ResponseListener
    {
        void onResponse(Response response);
    }

    public static class Response
    {
        public static final int STATUS_ERROR = 0;
        public static final int STATUS_SUCCESS = 1;
        public int status;
        public String data = "";
        public String message = "";
        public int resultCode = 0;
        public Map<String, String> headers = new HashMap<>();

        public <T> T getData(Class<T> tClass)
        {
            return new Gson().fromJson(data, tClass);
        }

        public boolean isSuccess()
        {
            return status == STATUS_SUCCESS;
        }

        @Override
        public String toString() {
            return data;
        }

        public String getHeader(String key)
        {
            if(key != null && headers != null && headers.containsKey(key))
            {
                return headers.get(key);
            }

            return null;
        }
    }
}
