package com.spring.jspark.springwebcell.presenter;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.contract.CellMemberListContract;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.httpclient.model.AttendanceData;
import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;
import com.spring.jspark.springwebcell.utils.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 15..
 */

public class CellMemberListPresenter implements CellMemberListContract.Presenter {
    CellMemberListContract.View mView;

    ArrayList<CellMemberInfo> mCellMemberList = null;

    boolean isWorshipAttendanceReceived = false;
    boolean isCellAttendanceReceived = false;

    public CellMemberListPresenter(){
        mCellMemberList = WebCellHttpClient.getInstance().getCellMemberInfo();
        WebCellHttpClient.getInstance().setListener(mHttpResonse);
    }

    @Override
    public void setView(CellMemberListContract.View view) {
        mView = view;
    }

    @Override
    public ArrayList<CellMemberInfo> getCellMemberData() {
        return mCellMemberList;
    }

    @Override
    public void requestCellMemberAttendanceData(int year, int week) {
        WebCellHttpClient.getInstance().getCellMemberAttendance(year, week);
    }

    @Override
    public void setAttendanceData(int index, int year, int week, boolean isWorshipAttended, boolean isCellAttended, String reason1, String reason2) {
        if(index >= mCellMemberList.size() || index < 0)
            return;

        String reason = "";
        if (!reason1.isEmpty() || !reason2.isEmpty())
            reason = reason1 + Common.REASON_DELIMETER + reason2;

        AttendanceData data = mCellMemberList.get(index).getAttendanceData(year, week);
        data.setWorshipAttended(isWorshipAttended);
        data.setCellAttended(isCellAttended);
        data.setAbsentReason(reason);


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
        public void onRequestCellMemberInfoResult(boolean isSuccess, ArrayList<CellMemberInfo> memberInfo) {

        }

        @Override
        public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, ArrayList<CellMemberInfo> memberInfo) {

            // final ArrayList<CellMemberInfo> mem = memberInfo;
            mCellMemberList = memberInfo;
            if (isSuccess) {
                mView.updateMemberList(year, week, memberInfo);
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
        public void onRequestParishMemberInfoResult(boolean isSuccess, boolean isWorship, HashMap<String, ArrayList<CellMemberInfo>> parishInfo) {

        }
    };
}