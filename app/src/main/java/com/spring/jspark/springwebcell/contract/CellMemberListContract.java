package com.spring.jspark.springwebcell.contract;

import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;

import java.util.ArrayList;

/**
 * Created by jspark on 2017. 3. 15..
 */

public interface CellMemberListContract {
    interface View{
        void updateMemberList(int year, int week, ArrayList<CellMemberInfo> memberList);
        void showToast(String message);
    }

    interface Presenter{
        void setView(View view);
        ArrayList<CellMemberInfo> getCellMemberData();
        void setAttendanceData(int index, int year, int week,
                               boolean isWorshipAttended, boolean isCellAttended,
                               String reason1, String reason2);
        void requestCellMemberAttendanceData(int year, int week);
        void onSubmitButtonClicked(int year, int week);
    }
}
