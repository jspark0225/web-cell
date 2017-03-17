package com.spring.jspark.springwebcell.presenter;

import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.contract.ParishMemberListContract;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 16..
 */

public class ParishMemberListPresenter implements ParishMemberListContract.Presenter {
    ParishMemberListContract.View mView;

    boolean isWorshipInfoReceived = false;
    boolean isCellInfoReceived = false;

    public ParishMemberListPresenter(){
        WebCellHttpClient.getInstance().setListener(mHttpResponse);
    }

    @Override
    public void setView(ParishMemberListContract.View view) {
        mView = view;
    }

    @Override
    public void requestParishMembers() {
        mView.hideRefreshDialogProgress();
        mView.updateParishMembers(WebCellHttpClient.getInstance().getParishMemberInfo());
    }

    @Override
    public HashMap<String, ArrayList<CellMemberInfo>> getParishMember() {
        return WebCellHttpClient.getInstance().getParishMemberInfo();
    }

    @Override
    public int getTotalWorshipAttendance(int year, int week) {
        int nWorshipAttendance = 0;
        for(String key : getParishMember().keySet()){
            nWorshipAttendance += Common.getNumberOfWorshipAttendance(year, week, getParishMember().get(key));
        }

        return nWorshipAttendance;
    }

    @Override
    public int getTotalCellAttendance(int year, int week) {
        int nCellAttendance = 0;
        for(String key : getParishMember().keySet()){
            nCellAttendance += Common.getNumberOfCellAttendance(year, week, getParishMember().get(key));
        }

        return nCellAttendance;
    }

    @Override
    public int getTotalWorshipAbsence(int year, int week) {
        int nWorshipAbsence = 0;
        for(String key : getParishMember().keySet()){
            nWorshipAbsence += Common.getNumberOfWorshipAbsence(year, week, getParishMember().get(key));
        }

        return nWorshipAbsence;
    }

    @Override
    public int getTotalCellAbsence(int year, int week) {
        int nCellAbsence = 0;
        for(String key : getParishMember().keySet()){
            nCellAbsence += Common.getNumberOfCellAbsence(year, week, getParishMember().get(key));
        }

        return nCellAbsence;
    }

    @Override
    public String getParish() {
        return WebCellHttpClient.getInstance().getParish();
    }

    @Override
    public int getTotal() {
        int total = 0;

        HashMap<String, ArrayList<CellMemberInfo>> parishMember = getParishMember();

        for(String key : parishMember.keySet()){
            total += parishMember.get(key).size();
        }

        return total;
    }

    @Override
    public void requestParishMemberList(int year){
        WebCellHttpClient.getInstance().requestParishMemberInfo(year, true);
        WebCellHttpClient.getInstance().requestParishMemberInfo(year, false);
    }

    private OnHttpResponse mHttpResponse = new OnHttpResponse() {
        @Override
        public void onLoginResult(boolean isSuccess) {

        }

        @Override
        public void onRequestCellMemberInfoResult(boolean isSuccess, ArrayList<CellMemberInfo> memberInfo) {

        }

        @Override
        public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, ArrayList<CellMemberInfo> memberInfo) {
        }

        @Override
        public void onRequestParishMemberInfoResult(boolean isSuccess, boolean isWorship, HashMap<String, ArrayList<CellMemberInfo>> parishInfo) {
            if(isSuccess) {
                if(isWorship)
                    isWorshipInfoReceived = true;
                else
                    isCellInfoReceived = true;

                if(isWorshipInfoReceived && isCellInfoReceived){
                    mView.hideRefreshDialogProgress();
                    mView.updateParishMembers(parishInfo);
                }
            }else{
                mView.hideRefreshDialogProgress();
            }
        }

        @Override
        public void onSubmitCellAttendanceResult(boolean isSuccess) {

        }

        @Override
        public void onSubmitWorshipAttendanceResult(boolean isSuccess) {

        }
    };
}
