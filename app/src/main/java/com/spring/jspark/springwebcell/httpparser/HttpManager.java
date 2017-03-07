package com.spring.jspark.springwebcell.httpparser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    public CellMemberInfo getCellMemberInfo(int index){
        for(CellMemberInfo info : mCellMemberInfo){
            if(info.getIndex() == index)
                return info;
        }

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

    public void getCellMembers(int year, int week){
        Log.d(TAG, "getCellMembers year="+year+", week="+week);
        if(!isLoggedIn){
            Log.e(TAG, "NOT Logged in");
            return;
        }

        getWorshipAttendance(year, week);
        getCellAttendance(year, week);
    }

    public void getWorshipAttendance(int year, int week){
        String requestUri = "https://sinch.dimode.co.kr/webrange/cell/weeklyRangeATT_SUN.asp";

        HttpRequest getWorshipAttendanceRequest = new HttpRequest(REQUEST_CODE_GET_WORSHIP_ATTENDANCE, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_GET_WORSHIP_ATTENDANCE)
                    return;

                if(statusCode != 200){
                    mListener.onRequestCellMemberInfoResult(false);
                    return;
                }



                ArrayList<CellMemberInfo> cellMemberInfo = new ArrayList<>();
//                mCellMemberInfo.clear();

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr td input");

                Log.d(TAG, "5");
                synchronized (mCellMemberInfo) {
                    Log.d(TAG, "6");
                    for (int i = 0; i < elements.size() / 4; i++) {
                        CellMemberInfo memberInfo = new CellMemberInfo();
                        memberInfo.setIndex(i + 1);
                        memberInfo.setId(elements.get(i * 4).attr("value"));
                        memberInfo.setName(elements.get(i * 4 + 1).attr("value"));
                        memberInfo.setWorshipAttended(elements.get(i * 4 + 2).attributes().hasKey("checked"));
                        memberInfo.setReason(elements.get(i * 4 + 3).attr("value"));

                        CellMemberInfo info = getCellMemberInfo(memberInfo.getIndex());
                        if(info != null)
                            memberInfo.setCellAttended( info.isCellAttended() );

                        cellMemberInfo.add(memberInfo);
                    }

                    Log.d(TAG, "7");
                    mCellMemberInfo = cellMemberInfo;
                }
                Log.d(TAG, "8");

                if(mListener != null)
                    mListener.onRequestCellMemberInfoResult(true);
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

        HttpRequest getCellAttendanceRequest = new HttpRequest(REQUEST_CODE_GET_CELL_ATTENDANCE, HttpRequest.GET, requestUri, new HttpResponse() {
            @Override
            public void onHttpResponse(int requestCode, int statusCode, Map<String, List<String>> headers, String body) {
                if(requestCode != REQUEST_CODE_GET_CELL_ATTENDANCE)
                    return;

                if(statusCode != 200){
                    mListener.onRequestCellMemberInfoResult(false);
                    return;
                }

                ArrayList<CellMemberInfo> cellMemberInfo = new ArrayList<>();
//                mCellMemberInfo.clear();

                Document doc = Jsoup.parse(body);
                Elements elements = doc.select("table tr td input");


                Log.d(TAG, "1");
                synchronized (mCellMemberInfo){
                    Log.d(TAG, "2");
                    for(int i=0; i<elements.size()/4; i++){
                        CellMemberInfo memberInfo = new CellMemberInfo();
                        memberInfo.setIndex(i+1);
                        memberInfo.setId(elements.get(i*4).attr("value"));
                        memberInfo.setName(elements.get(i*4+1).attr("value"));
                        memberInfo.setCellAttended(elements.get(i*4+2).attributes().hasKey("checked"));
                        memberInfo.setReason(elements.get(i*4+3).attr("value"));

                        CellMemberInfo info = getCellMemberInfo(memberInfo.getIndex());
                        if(info != null)
                            memberInfo.setWorshipAttended( info.isWorshipAttended() );

                        cellMemberInfo.add(memberInfo);
                    }
                    Log.d(TAG, "3");

                    mCellMemberInfo = cellMemberInfo;
                }
                Log.d(TAG, "4");

                if(mListener != null)
                    mListener.onRequestCellMemberInfoResult(true);
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
            request.addParameter("id(" + info.getIndex() + ")", info.getId());
            request.addParameter("insName(" + info.getIndex() + ")", info.getName());
            if(info.isCellAttended())
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
