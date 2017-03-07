package com.spring.jspark.springwebcell.httpparser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 4..
 */

public class HttpManager {
    private static final String TAG = HttpManager.class.getSimpleName();
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_GET_WORSHIP_ATTENDANCE = 2;
    private static final int REQUEST_CODE_GET_CELL_ATTENDANCE = 3;
    private static final int REQUEST_CODE_SUBMIT_WORSHIP_ATTENDANCE = 4;
    private static final int REQUEST_CODE_REQUEST_CELL_MEMBER_INFO = 5;

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

    public CellMemberInfo getCellMemberInfoById(String id){
        for(CellMemberInfo info : mCellMemberInfo)
            if(info.getId().equals(id))
                return info;

        return null;
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

    public void requestCellMemberInfo(){
        Log.d(TAG, "requestCellMemberInfo");
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/rangelist_pers.asp";
        HttpRequest request = new HttpRequest(REQUEST_CODE_REQUEST_CELL_MEMBER_INFO, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_REQUEST_CELL_MEMBER_INFO)
                    return;

                if(statusCode != 200){
                    if(mListener != null)
                        mListener.onRequestCellMemberInfoResult(false);

                    return;
                }

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr[height=20] td[bgcolor=#FFFFFF]");

                mCellMemberInfo.clear();

                for(int i=0; i<elements.size()/11; i++){
                    String name             = elements.get(i * 11 + 0).child(0).child(0).text();
                    String id               = elements.get(i * 11 + 0).child(0).attr("href").split("tid=")[1].split("&")[0];
                    String registerDate     = elements.get(i * 11 + 1).text();
                    String birthday         = elements.get(i * 11 + 2).text();
                    String email            = elements.get(i * 11 + 3).child(0).text();
                    String phoneNumber      = elements.get(i * 11 + 4).child(0).text();
                    String address          = elements.get(i * 11 + 5).child(0).text();
                    String workingCompany   = elements.get(i * 11 + 6).child(0).text();
                    String major            = elements.get(i * 11 + 7).child(0).text();
                    String cellInfo         = elements.get(i * 11 + 8).child(0).text();
                    String prevChurch       = elements.get(i * 11 + 9).child(0).text();
                    String otherInfo        = elements.get(i * 11 + 10).child(0).text();

                    CellMemberInfo memberInfo = new CellMemberInfo();
                    memberInfo.setName(name);
                    memberInfo.setId(id);
                    memberInfo.setRegisteredDate(registerDate);
                    memberInfo.setBirthday(birthday);
                    memberInfo.setPhoneNumber(phoneNumber);
                    memberInfo.setAddress(address);

                    mCellMemberInfo.add(memberInfo);
                }
                if(mListener != null)
                    mListener.onRequestCellMemberInfoResult(true);
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.addHeader("Cookie", mCookie);

        request.addParameter("range", "청년사역");
        request.addParameter("range1", mParish);
        request.addParameter("range2", mUserId);

        request.start();
    }

    public void getCellMemberAttendance(int year, int week){
        Log.d(TAG, "getCellMemberAttendance year="+year+", week="+week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        getWorshipAttendance(year, week);
        getCellAttendance(year, week);
    }

    public void getWorshipAttendance(int year, int week){
        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT_SUN.asp";

        final int targetYear = year;
        final int targetWeek = week;

        HttpRequest getWorshipAttendanceRequest = new HttpRequest(REQUEST_CODE_GET_WORSHIP_ATTENDANCE, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_GET_WORSHIP_ATTENDANCE)
                    return;

                if(statusCode != 200){
                    mListener.onRequestCellMemberAttendanceResult(false);
                    return;
                }

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr td input");

                synchronized (mCellMemberInfo) {
                    for (int i = 0; i < elements.size() / 4; i++) {
                        String id = elements.get(i * 4).attr("value");
                        CellMemberInfo memberInfo = getCellMemberInfoById(id);

                        if(memberInfo == null)
                            continue;

                        CellMemberInfo.AttendanceData data = memberInfo.getAttendanceData(targetYear, targetWeek);

                        data.setWorshipAttended(elements.get(i * 4 + 2).attributes().hasKey("checked"));
                        data.setWorshipAbsentReason(elements.get(i * 4 + 3).attr("value"));
                    }
                }

                if(mListener != null)
                    mListener.onRequestCellMemberAttendanceResult(false);
            }
        });

        getWorshipAttendanceRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        getWorshipAttendanceRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        getWorshipAttendanceRequest.addHeader("Cookie", mCookie);

        getWorshipAttendanceRequest.addParameter("dsView", "1");
        getWorshipAttendanceRequest.addParameter("rno", (week >= 10 ? "" + week : "0" + week));
        getWorshipAttendanceRequest.addParameter("staticYear", "" + year);
        getWorshipAttendanceRequest.addParameter("code", "");
        getWorshipAttendanceRequest.addParameter("range", "청년사역");
        getWorshipAttendanceRequest.addParameter("range1", mParish);
        getWorshipAttendanceRequest.addParameter("range2", mUserId);
        getWorshipAttendanceRequest.addParameter("startMonth", "01");
        getWorshipAttendanceRequest.addParameter("endMonth", "12");

        getWorshipAttendanceRequest.start();
    }

    public void getCellAttendance(int year, int week){
        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT_DAY.asp";

        final int targetYear = year;
        final int targetWeek = week;

        HttpRequest getCellAttendanceRequest = new HttpRequest(REQUEST_CODE_GET_CELL_ATTENDANCE, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_GET_CELL_ATTENDANCE)
                    return;

                if(statusCode != 200){
                    if(mListener != null)
                        mListener.onRequestCellMemberAttendanceResult(false);
                    return;
                }

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr td input");

                synchronized (mCellMemberInfo) {
                    for (int i = 0; i < elements.size() / 4; i++) {
                        String id = elements.get(i * 4).attr("value");
                        CellMemberInfo memberInfo = getCellMemberInfoById(id);

                        if(memberInfo == null)
                            continue;

                        CellMemberInfo.AttendanceData data = memberInfo.getAttendanceData(targetYear, targetWeek);

                        data.setCellAttended(elements.get(i * 4 + 2).attributes().hasKey("checked"));
                        data.setCellAbsentReason(elements.get(i * 4 + 3).attr("value"));
                    }
                }

                if(mListener != null)
                    mListener.onRequestCellMemberAttendanceResult(true);
            }
        });

        getCellAttendanceRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
        getCellAttendanceRequest.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        getCellAttendanceRequest.addHeader("Cookie", mCookie);

        getCellAttendanceRequest.addParameter("dsView", "2");
        getCellAttendanceRequest.addParameter("rno", (week >= 10 ? "" + week : "0" + week));
        getCellAttendanceRequest.addParameter("staticYear", "" + year);
        getCellAttendanceRequest.addParameter("code", "");
        getCellAttendanceRequest.addParameter("range", "청년사역");
        getCellAttendanceRequest.addParameter("range1", mParish);
        getCellAttendanceRequest.addParameter("range2", mUserId);
        getCellAttendanceRequest.addParameter("startMonth", "01");
        getCellAttendanceRequest.addParameter("endMonth", "12");

        getCellAttendanceRequest.start();
    }

    public void submitAttendance(int year, int week){
        submitWorshipAttendance(year, week);
        submitCellAttendance(year, week);
    }

    public void submitWorshipAttendance(int year, int week){
        Log.d(TAG, "submitWorshipAttendance year="+year+", week="+week);
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
            CellMemberInfo.AttendanceData data = info.getAttendanceData(year, week);
            request.addParameter("id(" + data.getIndex() + ")", info.getId());
            request.addParameter("insName(" + data.getIndex() + ")", info.getName());
            if(data.isWorshipAttended())
                request.addParameter("ds(" + data.getIndex() + ")", "O");
            request.addParameter("reason(" + data.getIndex() + ")", data.getWorshipAbsentReason());
        }

        request.addParameter("i", ""+mCellMemberInfo.size());
        request.addParameter("setDate", "" + year);
        request.addParameter("staticYear", "" + year);
        request.addParameter("rno", "" + week);

        request.start();
    }

    public void submitCellAttendance(int year, int week){
        Log.d(TAG, "submitCellAttendance year="+year+", week="+week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATTDAYOK.asp";

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
            CellMemberInfo.AttendanceData data = info.getAttendanceData(year, week);
            request.addParameter("id(" + data.getIndex() + ")", info.getId());
            request.addParameter("insName(" + data.getIndex() + ")", info.getName());
            if(data.isCellAttended())
                request.addParameter("ds(" + data.getIndex() + ")", "O");
            request.addParameter("reason(" + data.getIndex() + ")", data.getCellAbsentReason());
        }

        request.addParameter("i", ""+mCellMemberInfo.size());
        request.addParameter("setDate", "" + year);
        request.addParameter("staticYear", "" + year);
        request.addParameter("rno", "" + week);

        request.start();
    }
}
