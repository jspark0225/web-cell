package com.spring.jspark.springwebcell.presenter;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.contract.CellMemberListContract;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.httpclient.model.Attendance;
import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;
import com.spring.jspark.springwebcell.utils.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 15..
 */

public class CellMemberListPresenter implements CellMemberListContract.Presenter {
    CellMemberListContract.View mView;

    Cell mCellMemberList = null;

    boolean isWorshipAttendanceReceived = false;
    boolean isCellAttendanceReceived = false;

    public CellMemberListPresenter(String leaderName){
        mCellMemberList = WebCellHttpClient.getInstance().getCell(leaderName);
        WebCellHttpClient.getInstance().setListener(mHttpResonse);
    }

    @Override
    public void setView(CellMemberListContract.View view) {
        mView = view;
    }

    @Override
    public Cell getCellMemberData() {
        return mCellMemberList;
    }

    @Override
    public void requestCellMemberAttendanceData(String leaderName, int year, int week) {
        WebCellHttpClient.getInstance().getCellMemberAttendance(leaderName, year, week);
    }

    @Override
    public void setAttendanceData(int index, int year, int week, boolean isWorshipAttended, boolean isCellAttended, String reason1, String reason2) {
        String reason = "";
        if (!reason1.isEmpty() || !reason2.isEmpty())
            reason = reason1 + Common.REASON_DELIMETER + reason2;

        mCellMemberList.setAttendanceData(index, year, week, isWorshipAttended, isCellAttended, reason);

    }

    @Override
    public void onSubmitButtonClicked(int year, int week) {
        isWorshipAttendanceReceived = false;
        isCellAttendanceReceived = false;
        WebCellHttpClient.getInstance().submitAttendance(year, week);
    }

    OnHttpResponse mHttpResonse = new OnHttpResponse() {
        @Override
        public void onLoginResult(boolean isSuccess) {

        }

        @Override
        public void onRequestCellMemberInfoResult(boolean isSuccess, Cell cell) {

        }

        @Override
        public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, Cell cell) {

            // final ArrayList<CellMember> mem = memberInfo;
            mCellMemberList = cell;
            if (isSuccess) {
                mView.updateMemberList(year, week, mCellMemberList);
            }

            mView.hideRefreshProgressDialog();
        }

        @Override
        public void onSubmitCellAttendanceResult(boolean isSuccess) {
            if(isSuccess)
                isCellAttendanceReceived = true;
            else {
                mView.hideSubmitProgressDialog();
                mView.showToast(ResourceManager.getInstance().getString(R.string.webcell_submit_failure));
            }

            if(isCellAttendanceReceived && isWorshipAttendanceReceived){
                mView.hideSubmitProgressDialog();
                mView.showToast(ResourceManager.getInstance().getString(R.string.webcell_submit_success));
            }
        }

        @Override
        public void onSubmitWorshipAttendanceResult(boolean isSuccess) {
            if(isSuccess)
                isWorshipAttendanceReceived = true;
            else {
                mView.hideSubmitProgressDialog();
                mView.showToast(ResourceManager.getInstance().getString(R.string.webcell_submit_failure));
            }

            if(isCellAttendanceReceived && isWorshipAttendanceReceived) {
                mView.hideSubmitProgressDialog();
                mView.showToast(ResourceManager.getInstance().getString(R.string.webcell_submit_success));
            }
        }

        @Override
        public void onRequestParishMemberInfoResult(boolean isSuccess, boolean isWorship, Parish parish) {

        }
    };
}