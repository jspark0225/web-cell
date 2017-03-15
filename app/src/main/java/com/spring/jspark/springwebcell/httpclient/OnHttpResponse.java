package com.spring.jspark.springwebcell.httpclient;

import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;

import java.util.ArrayList;

/**
 * Created by jspark on 2017. 3. 4..
 */

public interface OnHttpResponse {
    public void onLoginResult(boolean isSuccess);
    public void onRequestCellMemberInfoResult(boolean isSuccess, ArrayList<CellMemberInfo> memberInfo);
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, ArrayList<CellMemberInfo> memberInfo);
    public void onSubmitCellAttendanceResult(boolean isSuccess);
    public void onSubmitWorshipAttendanceResult(boolean isSuccess);
}
