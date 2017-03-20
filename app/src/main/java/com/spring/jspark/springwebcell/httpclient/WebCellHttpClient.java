package com.spring.jspark.springwebcell.httpclient;

import android.util.Log;

import com.spring.jspark.springwebcell.httpclient.model.Attendance;
import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 4..
 */

public class WebCellHttpClient {
    private static final String TAG = WebCellHttpClient.class.getSimpleName();
    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_GET_WORSHIP_ATTENDANCE = 2;
    private static final int REQUEST_CODE_GET_CELL_ATTENDANCE = 3;
    private static final int REQUEST_CODE_SUBMIT_WORSHIP_ATTENDANCE = 4;
    private static final int REQUEST_CODE_REQUEST_CELL_MEMBER_INFO = 5;
    private static final int REQUEST_CODE_REQUEST_PARISH_MEMBER_INFO = 6;
    private static final int REQUEST_CODE_LOGIN_INFO = 7;

    private String mCookie = "";
    private OnHttpResponse mListener = null;
    private static WebCellHttpClient mInstance = null;

    private String mUserId = "";
    private String mParish = "";
    private boolean isLoggedIn = false;
/*
    private ArrayList<CellMember> mCellMembers = new ArrayList<>();

    private HashMap<String, ArrayList<CellMember>> mParishMemberInfo = new HashMap<>();
*/
    private Parish mParishInfo;

    private WebCellHttpClient(){

    }

    public static WebCellHttpClient getInstance(){
        if(mInstance == null){
            mInstance = new WebCellHttpClient();
        }

        return mInstance;
    }

    public String getUserId(){return mUserId;}

    public Cell getCell() {
        return getCell(mUserId);
    }

    public Cell getCell(String leaderName){
        return mParishInfo.getCell(leaderName);
    }

    public Parish getParish() { return mParishInfo; }

    public void setListener(OnHttpResponse listener){
        this.mListener = listener;
    }

    private void setParish(String parish){
        mParishInfo = new Parish(parish);
        this.mParish = parish;
    }

    public String getParishName(){ return mParish; }

    public void requestLogin(final String id, String password, String parish){
        mUserId = id;
        setParish(parish);

        String requestUri = "https://sinch.dimode.co.kr/include/loginCheck.asp";
        HttpRequest request = new HttpRequest(REQUEST_CODE_LOGIN, HttpRequest.POST, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_LOGIN)
                    return;

                if(statusCode == 302){
                    isLoggedIn = true;
                    if(headers != null && headers.containsKey("Set-Cookie")) {
                        mCookie = headers.get("Set-Cookie").get(0);
                    }
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


    public void requestParishMemberInfo(int year, boolean isWorship){
        final int finalYear = year;
        final boolean finalIsWorship = isWorship;

        Log.d(TAG, "requestCellMemberInfo");
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT.asp";
        HttpRequest request = new HttpRequest(REQUEST_CODE_REQUEST_PARISH_MEMBER_INFO, HttpRequest.POST, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_REQUEST_PARISH_MEMBER_INFO)
                    return;

                if(statusCode != 200){
                    synchronized (mListener) {
                        if (mListener != null)
                            mListener.onRequestParishMemberInfoResult(false, finalIsWorship, null);

                        return;
                    }
                }

                parseParishMemberInfo(finalIsWorship, finalYear, body);

                synchronized (mListener) {
                    if (mListener != null)
                        mListener.onRequestParishMemberInfoResult(true, finalIsWorship, mParishInfo);
                }
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.addHeader("Cookie", mCookie);

        request.addParameter("range", "청년사역");
        request.addParameter("range1", mParish);
        request.addParameter("range2", "");

        request.addParameter("staticYear", "" + year);
        request.addParameter("startMonth", "01");
        request.addParameter("endMonth", "12");
        request.addParameter("dsView", isWorship?"1": "2");

        request.start();
    }

    public void requestCellMemberInfo(){
        requestCellMemberInfo(mUserId);
    }

    public void requestCellMemberInfo(final String leaderName){
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
                    synchronized (mListener) {
                        if (mListener != null)
                            mListener.onRequestCellMemberInfoResult(false, null);

                        return;
                    }
                }

                parseCellMemberInfo(leaderName, body);

                synchronized (mListener) {
                    if (mListener != null)
                        mListener.onRequestCellMemberInfoResult(true, mParishInfo.getCell(leaderName));
                }
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.addHeader("Cookie", mCookie);

        request.addParameter("range", "청년사역");
        request.addParameter("range1", mParish);
        request.addParameter("range2", leaderName);

        request.start();
    }

    public void getCellMemberAttendance(int year, int week){
        getCellMemberAttendance(mUserId, year, week);
    }

    public void getCellMemberAttendance(String leaderName, int year, int week){
        Log.d(TAG, "getCellMemberAttendance year=" + year+", week=" + week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        getWorshipAttendance(leaderName, year, week);
        getCellAttendance(leaderName, year, week);
    }

    private void getWorshipAttendance(final String leaderName, int year, int week){
        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT_SUN.asp";

        final int targetYear = year;
        final int targetWeek = week;

        HttpRequest getWorshipAttendanceRequest = new HttpRequest(REQUEST_CODE_GET_WORSHIP_ATTENDANCE, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_GET_WORSHIP_ATTENDANCE)
                    return;

                if(statusCode != 200){
                    synchronized (mListener){
                        if(mListener != null)
                            mListener.onRequestCellMemberAttendanceResult(false, targetYear, targetWeek, null);
                    }
                    return;
                }

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr td input");

                Cell cell = mParishInfo.getCell(leaderName);

                synchronized (cell) {
                    for (int i = 0; i < elements.size() / 4; i++) {
                        String id = elements.get(i * 4).attr("value");
                        String index = elements.get(i * 4).attr("name").replace("id", "").replace("(", "").replace(")", "");
                        CellMember memberInfo = cell.getCellMemberById(id);

                        if(memberInfo == null)
                            continue;

                        Attendance data = memberInfo.getAttendanceData(targetYear, targetWeek);

                        data.setIndex(Integer.parseInt(index));
                        data.setWorshipAttended(elements.get(i * 4 + 2).attributes().hasKey("checked"));

                        String reason = elements.get(i * 4 + 3).attr("value");

                        if(reason != null && !reason.isEmpty())
                            data.setAbsentReason(reason);
                    }
                }

                synchronized (mListener) {
                    if (mListener != null)
                        mListener.onRequestCellMemberAttendanceResult(true, targetYear, targetWeek, cell);
                }
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
        getWorshipAttendanceRequest.addParameter("range2", leaderName);
        getWorshipAttendanceRequest.addParameter("startMonth", "01");
        getWorshipAttendanceRequest.addParameter("endMonth", "12");

        getWorshipAttendanceRequest.start();
    }

    private void getCellAttendance(final String leaderName, int year, int week){
        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT_DAY.asp";

        final int targetYear = year;
        final int targetWeek = week;

        HttpRequest getCellAttendanceRequest = new HttpRequest(REQUEST_CODE_GET_CELL_ATTENDANCE, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_GET_CELL_ATTENDANCE)
                    return;

                if(statusCode != 200){
                    synchronized (mListener) {
                        if (mListener != null)
                            mListener.onRequestCellMemberAttendanceResult(false, targetYear, targetWeek, null);
                    }
                    return;
                }

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr td input");

                Cell cell = mParishInfo.getCell(leaderName);

                synchronized (cell) {
                    for (int i = 0; i < elements.size() / 4; i++) {
                        String id = elements.get(i * 4).attr("value");
                        CellMember memberInfo = cell.getCellMemberById(id);

                        if(memberInfo == null)
                            continue;

                        Attendance data = memberInfo.getAttendanceData(targetYear, targetWeek);

                        data.setCellAttended(elements.get(i * 4 + 2).attributes().hasKey("checked"));

                        String reason = elements.get(i * 4 + 3).attr("value");

                        if(reason != null && !reason.isEmpty() && !data.isAbsentReasonExists())
                            data.setAbsentReason(reason);
                    }
                }
                synchronized (mListener) {
                    if (mListener != null)
                        mListener.onRequestCellMemberAttendanceResult(true, targetYear, targetWeek, cell);
                }
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
        getCellAttendanceRequest.addParameter("range2", leaderName);
        getCellAttendanceRequest.addParameter("startMonth", "01");
        getCellAttendanceRequest.addParameter("endMonth", "12");

        getCellAttendanceRequest.start();
    }

    public void submitAttendance(int year, int week){
        submitAttendance(mUserId, year, week);
    }

    public void submitAttendance(String leaderName, int year, int week){
        submitWorshipAttendance(leaderName, year, week);
        submitCellAttendance(leaderName, year, week);
    }

    private void submitWorshipAttendance(String leaderName, int year, int week){
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
                        mListener.onSubmitWorshipAttendanceResult(false);
                }else{
                    if(mListener != null)
                        mListener.onSubmitWorshipAttendanceResult(true);
                }
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.addHeader("Cookie", mCookie);

        Cell cell = mParishInfo.getCell(leaderName);

        for(CellMember info : cell.getCellMemberList()){
            Attendance data = info.getAttendanceData(year, week);
            request.addParameter("id(" + data.getIndex() + ")", info.getId());
            request.addParameter("insName(" + data.getIndex() + ")", info.getName());
            if(data.isWorshipAttended())
                request.addParameter("ds(" + data.getIndex() + ")", "O");
            request.addParameter("reason(" + data.getIndex() + ")", data.getAbsentReason());
        }

        request.addParameter("i", ""+ cell.getCellMemberList().size());
        request.addParameter("setDate", "" + year);
        request.addParameter("staticYear", "" + year);
        request.addParameter("rno", "" + week);

        request.start();
    }

    private void submitCellAttendance(String leaderName, int year, int week){
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
                        mListener.onSubmitCellAttendanceResult(false);
                }else{
                    if(mListener != null)
                        mListener.onSubmitCellAttendanceResult(true);
                }
            }
        });

        request.addHeader("Content-Type", "application/x-www-form-urlencoded");
        request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        request.addHeader("Cookie", mCookie);

        Cell cell = mParishInfo.getCell(leaderName);

        for(CellMember info : cell.getCellMemberList()){
            Attendance data = info.getAttendanceData(year, week);
            request.addParameter("id(" + data.getIndex() + ")", info.getId());
            request.addParameter("insName(" + data.getIndex() + ")", info.getName());
            if(data.isCellAttended())
                request.addParameter("ds(" + data.getIndex() + ")", "O");

            String cellAbsentReason = data.getAbsentReason().length() > 25 ? data.getAbsentReason().substring(0, 24) : data.getAbsentReason();
            //request.addParameter("reason(" + data.getIndex() + ")", cellAbsentReason);
        }

        request.addParameter("i", ""+ cell.getCellMemberList().size());
        request.addParameter("setDate", "" + year);
        request.addParameter("staticYear", "" + year);
        request.addParameter("rno", "" + week);

        request.start();
    }

    private void parseCellMemberInfo(String leaderName, String body){
        Document doc = Jsoup.parse(body);
        Elements elements = doc.select("table tr[height=20] td[bgcolor=#FFFFFF]");


        Cell newCell = mParishInfo.getCell(leaderName);

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

            CellMember memberInfo = newCell.getCellMember(name);
            memberInfo.setId(id);
            memberInfo.setPhoneNumber(phoneNumber);
        }
    }

    private void parseParishMemberInfo(boolean isWorship, int year, String body){
        Log.d(TAG, "parseParishMemberInfo isWorship = " + isWorship);
        Document doc = Jsoup.parse(body);
        Elements elements = doc.select("table tr td[title]");

        Elements totalElements = doc.select("td[bgcolor=#C1F2FF][width=80]");

        for(Element e : elements){
            String leaderName = e.attr("title").replace(mParish, "").replace("/", "");
            String memberName = e.text();

            CellMember cellMember = mParishInfo.getCell(leaderName).getCellMember(memberName);

            Element traverse = e.nextElementSibling();

            int week = 1;
            while(traverse != null && !traverse.hasAttr("title")){
                Attendance data = cellMember.getAttendanceData(year, week);
                boolean isAttended = traverse.text().equals("O") ? true : false;
                boolean isChecked = traverse.text().equals("") ? false : true;

                if(isWorship){
                    data.setWorshipAttended(isAttended);
                    data.setWorshipChecked(isChecked);
                }
                else{
                    data.setCellAttended(isAttended);
                    data.setCellChecked(isChecked);
                }

                traverse = traverse.nextElementSibling();
                week++;
            }
        }


        int week = 1;
        for(Element e : totalElements){
            String value = e.text().length() >= 1 ? e.text().substring(0, e.text().length()-1) : "0";
            int total = Integer.parseInt(value);
            mParishInfo.setTotalNumberOfParish(year, week, total);
            week++;
        }
    }
}