package com.spring.jspark.springwebcell.httpparser;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 4..
 */

public class CustomRequest extends Request {
    private static final String TAG = CustomRequest.class.getSimpleName();
    Response.Listener<NetworkResponse> mListener;
    Map<String, String> headers = new HashMap<>();

    public CustomRequest(int method, String url, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        mListener = listener;
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        Log.d(TAG, "parseNetworkResponse " + response.toString() );

//        String parsed;
//        try {
//            parsed = new String(response.data, "euc-kr" /*HttpHeaderParser.parseCharset(response.headers)*/);
//        } catch (UnsupportedEncodingException e) {
//            parsed = new String(response.data);
//        }

//        cookie = response.headers.get("Set-Cookie").split(";")[0];

        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(Object response) {
        NetworkResponse res = (NetworkResponse) response;

        mListener.onResponse(res);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        return headers;
    }

    public void addHeader(String key, String value){
        Log.d("jspark", "addHeader key="+key+",value="+value);
        headers.put(key, value);
    }
}
