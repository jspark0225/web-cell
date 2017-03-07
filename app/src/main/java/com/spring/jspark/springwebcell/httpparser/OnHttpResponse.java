package com.spring.jspark.springwebcell.httpparser;

import java.util.ArrayList;

/**
 * Created by jspark on 2017. 3. 4..
 */

public interface OnHttpResponse {
    public void onLoginResult(boolean isSuccess);
    public void onRequestCellMemberInfoResult(boolean isSuccess);
    public void onRequestCellMemberAttendanceResult(boolean isSuccess);
    public void onSubmitCellAttandanceResult(boolean isSuccess);
}
