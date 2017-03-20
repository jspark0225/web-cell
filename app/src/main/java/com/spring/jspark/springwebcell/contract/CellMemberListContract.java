package com.spring.jspark.springwebcell.contract;

import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;

import java.util.ArrayList;

/**
 * Created by jspark on 2017. 3. 15..
 */

public interface CellMemberListContract {
    interface View{
        void updateMemberList(int year, int week, Cell memberList);
        void showToast(String message);
        void hideSubmitProgressDialog();
        void hideRefreshProgressDialog();
    }

    interface Presenter{
        void setView(View view);
        Cell getCellMemberData();
        void setAttendanceData(int index, int year, int week,
                               boolean isWorshipAttended, boolean isCellAttended,
                               String reason1, String reason2);
        void requestCellMemberAttendanceData(String leaderName, int year, int week);
        void onSubmitButtonClicked(int year, int week);
    }
}
