package com.spring.jspark.springwebcell.presenter;

import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.contract.ParishMemberListContract;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;

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
        mView.updateParishMembers(WebCellHttpClient.getInstance().getParish());
    }

    @Override
    public Parish getParishMember() {
        return WebCellHttpClient.getInstance().getParish();
    }

    @Override
    public int getParishWorshipAttendance(int year, int week) {
        return getParishMember().getNumberOfWorshipAttended(year, week);
    }

    @Override
    public int getParishCellAttendance(int year, int week) {
        return getParishMember().getNumberOfCellAttended(year, week);
    }

    @Override
    public String getParish() {
        return WebCellHttpClient.getInstance().getParishName();
    }

    @Override
    public int getTotal() {
        return getParishMember().getNumberOfMembers();
    }

    @Override
    public int getTotal(int year, int week){ return getParishMember().getTotalNumberOfParish(year, week);}

    @Override
    public void requestParishMemberList(int year){
        WebCellHttpClient.getInstance().requestParishMemberInfo(year, true);
        WebCellHttpClient.getInstance().requestParishMemberInfo(year, false);
    }

    @Override
    public void requestCellMemberList(String leaderName, int year, int week) {
        WebCellHttpClient.getInstance().requestCellMemberInfo(leaderName);
    }

    @Override
    public void setListener() {
        WebCellHttpClient.getInstance().setListener(mHttpResponse);
    }

    private OnHttpResponse mHttpResponse = new OnHttpResponse() {
        @Override
        public void onLoginResult(boolean isSuccess) {

        }

        @Override
        public void onRequestCellMemberInfoResult(boolean isSuccess, Cell memberInfo) {
            mView.hideRefreshDialogProgress();
            if(isSuccess){
                mView.goToCellMemberListActivity(memberInfo.getLeaderName());
            }
        }

        @Override
        public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, Cell memberInfo) {
        }

        @Override
        public void onRequestParishMemberInfoResult(boolean isSuccess, boolean isWorship, Parish parishInfo) {
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
