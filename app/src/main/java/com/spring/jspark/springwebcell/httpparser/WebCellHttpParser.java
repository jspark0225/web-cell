package com.spring.jspark.springwebcell.httpparser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by jspark on 2017. 3. 4..
 */

public class WebCellHttpParser {
    private static final String TAG = WebCellHttpParser.class.getSimpleName();

    private String mCookie = "";
    private OnHttpResponse mListener = null;
    private static WebCellHttpParser mInstance = null;

    private String mUserId = "";
    private boolean isLoggedIn = false;

    private WebCellHttpParser(){

    }

    public static WebCellHttpParser getInstance(){
        if(mInstance == null){
            mInstance = new WebCellHttpParser();
        }

        return mInstance;
    }

    public void setListener(OnHttpResponse listener){
        this.mListener = listener;
    }

    public void login(String id, String password){
        Thread t = new LoginRequestThread(id, password);
        t.start();
    }

    public void getCellMembers(String parish){
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        GetCellMemberThread t = new GetCellMemberThread(parish);
        t.start();
    }


    public void test(){
    }

    class LoginRequestThread extends Thread{
        String mId;
        String mPassword;

        LoginRequestThread(String id, String password){
            mId = id;
            mPassword = password;
        }
        @Override
        public void run() {
            String url = "https://sinch.dimode.co.kr/include/loginCheck.asp";
            String loginId = "";
            try {
                loginId = URLEncoder.encode(mId, "euc-kr");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            url = url + "?loginID=" + loginId + "&loginPWD=" + mPassword;

            URL _url = null;
            try {
                _url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                if(responseCode == 302){
                    isLoggedIn = true;
                    mCookie = connection.getHeaderField("Set-Cookie");
                    mListener.onLoginResult(true);
                }else{
                    isLoggedIn = false;
                    mListener.onLoginResult(false);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class GetCellMemberThread extends Thread{
        String mParish;
        GetCellMemberThread(String parish){
            mParish = parish;
        }

        @Override
        public void run() {
            if(!isLoggedIn){
                Log.e(TAG, "NOT Logged in");
                return;
            }

            String url = "https://sinch.dimode.co.kr/webrange/cell/rangelist_pers.asp";
            String range = "";
            String range1 = "";
            try {
                range = URLEncoder.encode("청년사역", "euc-kr");
                range1 = URLEncoder.encode(mParish, "euc-kr");
            } catch (UnsupportedEncodingException e) {
                range = "";
                range1 = "";
                e.printStackTrace();
            }

            url = url + "?range=" + range + "&range1=" + range1;

            try {
                URL _url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setRequestProperty("Cookie", mCookie);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();

                StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"euc-kr")); //문자열 셋 세팅
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                Log.d(TAG, "test\n" + builder.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
