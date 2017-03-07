package com.spring.jspark.springwebcell.httpparser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 4..
 */

public class HttpManager {
    private static final String TAG = HttpManager.class.getSimpleName();
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_GET_CELL_MEMBER = 2;
    private static final int REQUEST_CODE_SUBMIT_WORSHIP_ATTENDANCE = 3;

    private String mCookie = "";
    private OnHttpResponse mListener = null;
    private static HttpManager mInstance = null;

    private String mUserId = "";
    private String mParish = "";
    private boolean isLoggedIn = false;

    private ArrayList<CellMemberInfo> mCellMemberInfo = new ArrayList<>();

    private HttpManager(){

    }

    public static HttpManager getInstance(){
        if(mInstance == null){
            mInstance = new HttpManager();
        }

        return mInstance;
    }

    public ArrayList<CellMemberInfo> getCellMemberInfo() {
        return mCellMemberInfo;
    }

    public void setListener(OnHttpResponse listener){
        this.mListener = listener;
    }

    public void setParish(String parish){
        this.mParish = parish;
    }

    public void login(String id, String password){
        mUserId = id;

        String requestUri = "https://sinch.dimode.co.kr/include/loginCheck.asp";
        HttpRequest request = new HttpRequest(REQUEST_CODE_LOGIN, HttpRequest.POST, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_LOGIN)
                    return;

                if(statusCode == 302){
                    isLoggedIn = true;
                    if(headers != null && headers.containsKey("Set-Cookie"))
                        mCookie = headers.get("Set-Cookie").get(0);
                    if(mListener != null)
                        mListener.onLoginResult(true);
                }else{
                    isLoggedIn = false;
                    if(mListener != null)
                        mListener.onLoginResult(false);
                }
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

        request.addParameter("loginID", id);
        request.addParameter("loginPWD", password);

        request.start();
    }

    public void getCellMembers(int year, int week){
        Log.d(TAG, "getCellMembers year="+year+", week="+week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT_SUN.asp";

        HttpRequest request = new HttpRequest(REQUEST_CODE_GET_CELL_MEMBER, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_GET_CELL_MEMBER)
                    return;

                if(statusCode != 200){
                    mListener.onRequestCellMemberInfoResult(false);
                    return;
                }

                mCellMemberInfo.clear();

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr td input");

                for(int i=0; i<elements.size()/4; i++){
                    CellMemberInfo memberInfo = new CellMemberInfo();
                    memberInfo.setIndex(i+1);
                    memberInfo.setId(elements.get(i*4).attr("value"));
                    memberInfo.setName(elements.get(i*4+1).attr("value"));
                    memberInfo.setWorshipAttended(elements.get(i*4+2).attributes().hasKey("checked"));
                    memberInfo.setReason(elements.get(i*4+3).attr("value"));

                    mCellMemberInfo.add(memberInfo);
                }

                if(mListener != null)
                    mListener.onRequestCellMemberInfoResult(true);
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.addHeader("Cookie", mCookie);

        request.addParameter("dsView", "1");
        request.addParameter("rno", (week >= 10 ? "" + week : "0" + week));
        request.addParameter("staticYear", "" + year);
        request.addParameter("code", "");
        request.addParameter("range", "청년사역");
        request.addParameter("range1", mParish);
        request.addParameter("range2", mUserId);
        request.addParameter("startMonth", "01");
        request.addParameter("endMonth", "12");

        request.start();
    }

    public void submitCellAttandance(int year, int week){
        Log.d(TAG, "submitCellAttandance year="+year+", week="+week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATTSUNOK.asp";

        HttpRequest request = new HttpRequest(REQUEST_CODE_SUBMIT_WORSHIP_ATTENDANCE, HttpRequest.POST, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_SUBMIT_WORSHIP_ATTENDANCE)
                    return;

                if(statusCode != 200){
                    if(mListener != null)
                        mListener.onSubmitCellAttandanceResult(false);
                }else{
                    if(mListener != null)
                        mListener.onSubmitCellAttandanceResult(true);
                }
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.addHeader("Cookie", mCookie);

        for(CellMemberInfo info : mCellMemberInfo){
            request.addParameter("id(" + info.getIndex() + ")", info.getId());
            request.addParameter("insName(" + info.getIndex() + ")", info.getName());
            if(info.isWorshipAttended())
                request.addParameter("ds(" + info.getIndex() + ")", "O");
            request.addParameter("reason(" + info.getIndex() + ")", info.getReason());
        }

        request.addParameter("i", ""+mCellMemberInfo.size());
        request.addParameter("setDate", "" + year);
        request.addParameter("staticYear", "" + year);
        request.addParameter("rno", "" + week);

        request.start();
    }
}
