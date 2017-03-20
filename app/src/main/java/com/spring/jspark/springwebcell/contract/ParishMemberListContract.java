package com.spring.jspark.springwebcell.contract;

import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 16..
 */

public interface ParishMemberListContract {
    interface View{
        void updateParishMembers(Parish parish);
        void hideRefreshDialogProgress();
        void goToCellMemberListActivity(String leaderName);
    }

    interface Presenter{
        void setView(View view);
        void requestParishMembers();
        Parish getParishMember();
        int getParishWorshipAttendance(int year, int week);
        int getParishCellAttendance(int year, int week);
        int getTotal();
        public int getTotal(int year, int week);
        String getParish();
        void requestParishMemberList(int year);
        void requestCellMemberList(String leaderName, int year, int week);
        void setListener();
    }
}