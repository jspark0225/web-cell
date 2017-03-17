package com.spring.jspark.springwebcell.contract;

import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 16..
 */

public interface ParishMemberListContract {
    interface View{
        void updateParishMembers(HashMap<String, ArrayList<CellMemberInfo>> parishMembers);
        void hideRefreshDialogProgress();
    }

    interface Presenter{
        void setView(View view);
        void requestParishMembers();
        HashMap<String, ArrayList<CellMemberInfo>> getParishMember();
        int getTotalWorshipAttendance(int year, int week);
        int getTotalCellAttendance(int year, int week);
        int getTotalWorshipAbsence(int year, int week);
        int getTotalCellAbsence(int year, int week);
        int getTotal();
        String getParish();
        void requestParishMemberList(int year);
    }
}