package com.spring.jspark.springwebcell.httpclient;

import java.util.List;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 7..
 */

public interface HttpResponse {
    public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body);
}
