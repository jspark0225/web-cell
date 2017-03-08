package com.spring.jspark.springwebcell.httpparser;

import java.util.ArrayList;

/**
 * Created by jspark on 2017. 3. 4..
 */

public interface OnHttpResponse {
    public void onLoginResult(boolean isSuccess);
    public void onRequestCellMemberInfoResult(boolean isSuccess, String leaderName, ArrayList<CellMemberInfo> memberInfo);
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, String leaderName, ArrayList<CellMemberInfo> memberInfo);
    public void onRequestCellLeaderListResult(boolean isSuccess, String parish, ArrayList<String> cellLeaderList);
    public void onSubmitCellAttandanceResult(boolean isSuccess);
}
