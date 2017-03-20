package com.spring.jspark.springwebcell.httpclient;

import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 4..
 */

public interface OnHttpResponse {
    public void onLoginResult(boolean isSuccess);
    public void onRequestCellMemberInfoResult(boolean isSuccess, Cell cell);
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, Cell cell);
    public void onRequestParishMemberInfoResult(boolean isSuccess, boolean isWorship, Parish parish);
    public void onSubmitCellAttendanceResult(boolean isSuccess);
    public void onSubmitWorshipAttendanceResult(boolean isSuccess);
}
