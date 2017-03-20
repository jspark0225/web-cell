package com.spring.jspark.springwebcell.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.EditText;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;
import com.spring.jspark.springwebcell.utils.ResourceManager;
import com.spring.jspark.springwebcell.utils.SharedPreferenceManager;
import com.spring.jspark.springwebcell.contract.MainContract;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.receiver.AlarmReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by jspark on 2017. 3. 15..
 */

public class MainPresenter implements MainContract.Presenter {
    private static final String TAG = MainPresenter.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 1;
    private static final long A_WEEK = 1000 * 60 * 60 * 24 * 7;
    private static final int REQUEST_CODE_ALARM = 1000;
    public static final String BROADCAST_WEBCELL_ALARM = "com.spring.jspark.springwebcell.alarm";

    MainContract.View mView;

    boolean isPastor = false;
    boolean isPastorButNotNow = true;

    boolean isWorshipParishInfoReceived = false;
    boolean isCellParishInfoReceived = false;

    public MainPresenter() {
        WebCellHttpClient.getInstance().setListener(mHttpResponse);
    }

    @Override
    public void setView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void getPermission(Context context) {
        // permission check for Internet
        int permissionInternet = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
        if(permissionInternet != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.INTERNET}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void setAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR) + 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Log.d(TAG, calendar.getTime().toString());

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(BROADCAST_WEBCELL_ALARM);
        PendingIntent pi = PendingIntent.getBroadcast(context, REQUEST_CODE_ALARM, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), A_WEEK, pi);
    }

    @Override
    public void onIdFocusChanged(EditText editText) {
        if(editText.hasFocus()){
            mView.updateIdEditText("");
        }else{
            if(editText.getText().toString().isEmpty())
                mView.updateIdEditText("ID를 입력하세요");
        }
    }

    @Override
    public void onPasswordFocusChanged(EditText editText) {
        if(editText.hasFocus()){
            mView.updatePasswordEditText("");
        }else{
            if(editText.getText().toString().isEmpty())
                mView.updatePasswordEditText("1004");
        }
    }

    @Override
    public void onLoginButtonClicked(String id, String password, int selectedPosition, String parish) {
        if(id == null || id.isEmpty()){
            mView.showToastMessage(ResourceManager.getInstance().getString(R.string.please_type_id));
            return;
        }

        if(password == null || password.isEmpty()){
            mView.showToastMessage(ResourceManager.getInstance().getString(R.string.please_type_password));
            return;
        }

        if(parish == null || parish.isEmpty() || selectedPosition == 0){
            mView.showToastMessage(ResourceManager.getInstance().getString(R.string.please_select_parish));
        }

        isPastor = Common.isPastor(id, parish);

        isPastorButNotNow = id.equals("김지훈") ? false : true;

        SharedPreferenceManager.getInstance().putLoginData(id, password, selectedPosition, true);
        WebCellHttpClient.getInstance().requestLogin(id, password, parish);
    }

    @Override
    public void getSavedLoginData() {
        String id = SharedPreferenceManager.getInstance().getStoredId();
        String pw = SharedPreferenceManager.getInstance().getStoredPassword();
        int parish = SharedPreferenceManager.getInstance().getStoredParish();
        boolean loginEnabled = SharedPreferenceManager.getInstance().getStoredLoginEnabled();

        if(loginEnabled){
            mView.updateIdEditText(id);
            mView.updatePasswordEditText(pw);
            mView.updateParishSpinner(parish);
        }
    }

    OnHttpResponse mHttpResponse = new OnHttpResponse() {
        @Override
        public void onLoginResult(boolean isSuccess) {
            mView.hideLoginProgressDialog();
            if (isSuccess) {
                mView.showDataLoadingProgressDialog();
                if(isPastor){
                    if(isPastorButNotNow){
                        mView.hideDataLoadingProgressDialog();
                        mView.showToastMessage("지훈 목사님이 자기만 되게 해달래요");
                    }
                    else{
                        isWorshipParishInfoReceived = false;
                        isCellParishInfoReceived = false;

                        WebCellHttpClient.getInstance().requestParishMemberInfo(Common.getTodaysYear(), true);
                        WebCellHttpClient.getInstance().requestParishMemberInfo(Common.getTodaysYear(), false);
                    }
                }
                else
                    WebCellHttpClient.getInstance().requestCellMemberInfo();
            } else {
                SharedPreferenceManager.getInstance().clearLoginData();
                String errorMsg = ResourceManager.getInstance().getString(R.string.fail_to_login);
                mView.showToastMessage(errorMsg);
            }
        }

        @Override
        public void onRequestCellMemberInfoResult(boolean isSuccess, Cell cell) {
            mView.hideDataLoadingProgressDialog();
            if(isSuccess){
                mView.goToCellMemberActivity();
            }else{
                String errorMsg = ResourceManager.getInstance().getString(R.string.fail_to_get_cell_member);
                mView.showToastMessage(errorMsg);
            }
        }

        @Override
        public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, Cell cell) {

        }

        @Override
        public void onSubmitCellAttendanceResult(boolean isSuccess) {

        }

        @Override
        public void onSubmitWorshipAttendanceResult(boolean isSuccess) {

        }

        @Override
        public void onRequestParishMemberInfoResult(boolean isSuccess, boolean isWorship, Parish parish) {
            if(isSuccess){
                if(isWorship)
                    isWorshipParishInfoReceived = true;
                else
                    isCellParishInfoReceived = true;

                if(isWorshipParishInfoReceived && isCellParishInfoReceived){
                    mView.hideDataLoadingProgressDialog();
                    mView.goToParishMemberActivity();
                }
            }else{
                mView.hideDataLoadingProgressDialog();
                String errorMsg = ResourceManager.getInstance().getString(R.string.fail_to_get_parish_member);
                mView.showToastMessage(errorMsg);
            }
        }
    };
}
