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

/**
 * Created by jspark on 2017. 3. 4..
 */

public class HttpManager {
    private static final String TAG = HttpManager.class.getSimpleName();

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
        Thread t = new LoginRequestThread(id, password);
        t.start();
    }

    public void getCellMembers(int year, int week){
        Log.d(TAG, "getCellMembers year="+year+", week="+week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        GetCellMemberInfoThread t = new GetCellMemberInfoThread(year, week);
        t.start();
    }

    public void submitCellAttandance(int year, int week){
        Log.d(TAG, "submitCellAttandance year="+year+", week="+week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        SubmitCellAttandanceThread t = new SubmitCellAttandanceThread(year, week);
        t.start();
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

            Log.d(TAG, "request url = " + url);

            URL _url = null;
            try {
                _url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setUseCaches(false);
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

    class GetCellMemberInfoThread extends Thread{
        int year;
        int week;

        GetCellMemberInfoThread(int year, int week){
            this.year = year;
            this.week = week;
        }
        @Override
        public void run() {

            String range = "";
            String range1 = "";
            String range2 = "";

            try {
                range = URLEncoder.encode("청년사역", "euc-kr");
                range1 = URLEncoder.encode(mParish, "euc-kr");
                range2 = URLEncoder.encode(mUserId, "euc-kr");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String url = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT_SUN.asp" +
                    "?dsView=1"
                    +"&rno=" + (week >= 10 ? week : "0" + week)
                    +"&staticYear=" +year
                    +"&code="
                    +"&range="+range
                    +"&range1="+range1
                    +"&range2="+range2
                    +"&startMonth=01&endMonth=12";

            Log.d(TAG, "request url = " + url);

            try {
                URL _url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setUseCaches(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setRequestProperty("Cookie", mCookie);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();

                if(responseCode != 200){
                    mListener.onRequestCellMemberInfoResult(false);
                    return;
                }

                mCellMemberInfo.clear();

                StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"euc-kr")); //문자열 셋 세팅
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                Document doc = Jsoup.parse(builder.toString());
                Elements elements = doc.select("table tr td input");

                for(int i=0; i<elements.size()/4; i++){
                    CellMemberInfo memberInfo = new CellMemberInfo();
                    memberInfo.setIndex(i+1);
                    memberInfo.setId(elements.get(i*4).attr("value"));
                    memberInfo.setName(elements.get(i*4+1).attr("value"));
                    memberInfo.setChecked(elements.get(i*4+2).attributes().hasKey("checked"));
                    memberInfo.setReason(elements.get(i*4+3).attr("value"));

                    mCellMemberInfo.add(memberInfo);
               }


               if(mListener != null){
                   mListener.onRequestCellMemberInfoResult(true);
               }
            } catch (MalformedURLException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            } catch (ProtocolException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            } catch (IOException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            }
        }
    }

    class SubmitCellAttandanceThread extends Thread{
        int week;
        int year;
        public SubmitCellAttandanceThread(int year, int week) {
            this.week = week;
            this.year = year;
        }

        @Override
        public void run() {

            String range = "";
            String range1 = "";
            String range2 = "";

            try {
                range = URLEncoder.encode("청년사역", "euc-kr");
                range1 = URLEncoder.encode(mParish, "euc-kr");
                range2 = URLEncoder.encode(mUserId, "euc-kr");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
// id(1)=1137&insName(1)=이신홍&reason(1)=&id(2)=1464&insName(2)=최태현&ds(2)=O&reason(2)=&id(3)=1267&insName(3)=박지수&ds(3)=O&reason(3)=&id(4)=2079&insName(4)=허유정&reason(4)=&id(5)=2854&insName(5)=허송아&ds(5)=O&reason(5)=&id(6)=3538&insName(6)=최다솜&ds(6)=O&reason(6)=&id(7)=3683&insName(7)=김규원&ds(7)=O&reason(7)=&id(8)=3690&insName(8)=이형훈&reason(8)=&id(9)=3885&insName(9)=양석규&ds(9)=O&reason(9)=&i=9&setDate=2017&staticYear=2017&rno=10
            String url = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATTSUNOK.asp?";

            for(CellMemberInfo info : mCellMemberInfo) {
                url = url +
                        "id(" + info.getIndex() + ")=" + info.getId() +
                        "&insName(" + info.getIndex() + ")=" + info.getEncodedName() +
                        (info.isChecked()? "&ds(" + info.getIndex() + ")=O": "") +
                        "&reason(" + info.getIndex() + ")=" + info.getEncodedReason() + "&";
            }

            url = url +
                    "i=" + mCellMemberInfo.size() +
                    "&setDate=" + year +
                    "&staticYear=" + year +
                    "&rno=" + week;


            Log.d(TAG, "request url = " + url);

            try {
                URL _url = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setRequestProperty("Cookie", mCookie);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();

                if(responseCode != 200){
                    return;
                }
            } catch (MalformedURLException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            } catch (ProtocolException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            } catch (IOException e) {
                mListener.onRequestCellMemberInfoResult(false);
                e.printStackTrace();
            }
        }
    }
}
