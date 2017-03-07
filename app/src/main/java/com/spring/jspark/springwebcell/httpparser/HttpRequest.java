package com.spring.jspark.springwebcell.httpparser;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 7..
 */

public class HttpRequest extends  Thread{
    private static final String TAG = HttpRequest.class.getSimpleName();

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private int requestCode = -1;
    private String method = GET;
    private String requestUri = "";
    private HttpResponse response = null;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();

    public HttpRequest(int requestCode, String method, String requestUri, HttpResponse response){
        this.requestUri = requestUri;
        this.requestCode = requestCode;
        this.response = response;
        this.method = method;
    }

    public void addHeader(String key, String value){
        headers.put(key, value);
    }

    public void addParameter(String key, String value){
        parameters.put(key, value);
    }

    @Override
    public void run() {
        String url = requestUri + ( headers.size() == 0 ? "":"?");
        for(String key : parameters.keySet()){
            String param = parameters.get(key);
            try {
                param = URLEncoder.encode(param, "euc-kr");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(!url.endsWith("?"))
                url = url + "&";

            url = url + key + "=" + param;
        }

        Log.d(TAG, "requestUri = " + url);

        URL _url = null;
        try {
            _url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setRequestMethod(method);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            for(String key : headers.keySet()){
                String header = headers.get(key);
                connection.setRequestProperty(key, header);
            }

            connection.connect();

            int responseCode = connection.getResponseCode();

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"euc-kr"));
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            Map<String, List<String>> hs = connection.getHeaderFields();

            if(response != null)
                response.onHttpResponse(requestCode, responseCode, hs, builder.toString());
        } catch (MalformedURLException e) {
            if(response != null)
                response.onHttpResponse(requestCode, -1, null, "");
        } catch (ProtocolException e) {
            if(response != null)
                response.onHttpResponse(requestCode, -1, null, "");
        } catch (IOException e) {
            if(response != null)
                response.onHttpResponse(requestCode, -1, null, "");
        }
    }
}
