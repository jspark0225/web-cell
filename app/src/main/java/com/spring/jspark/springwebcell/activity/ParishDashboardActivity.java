package com.spring.jspark.springwebcell.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.adapter.CellMemberListViewAdapter;
import com.spring.jspark.springwebcell.adapter.ParishDashboardAdapter;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.httpparser.CellMemberInfo;
import com.spring.jspark.springwebcell.httpparser.HttpManager;
import com.spring.jspark.springwebcell.httpparser.HttpRequest;
import com.spring.jspark.springwebcell.httpparser.OnHttpResponse;

import java.util.ArrayList;
import java.util.List;

public class ParishDashboardActivity extends AppCompatActivity implements OnHttpResponse{
    private static final String TAG = ParishDashboardActivity.class.getSimpleName();

    ParishDashboardAdapter mAdapter;

    TextView titleParish;
    TextView titleDate;

    TextView overallWorshipAttendance;
    TextView overallCellAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parish_dashboard);

        titleParish = (TextView) findViewById(R.id.dashboard_title_parish_tv);
        titleDate = (TextView) findViewById(R.id.dashboard_title_date_tv);

        overallWorshipAttendance = (TextView) findViewById(R.id.dashboard_overall_worship);
        overallCellAttendance = (TextView) findViewById(R.id.dashboard_overall_cell);

        ListView listView = (ListView) findViewById(R.id.dashboard_listview);
        mAdapter = new ParishDashboardAdapter();

        listView.setAdapter(mAdapter);

        HttpManager.getInstance().setListener(this);
        HttpManager.getInstance().requestLeaderNameList();
    }

    @Override
    public void onLoginResult(boolean isSuccess) {

    }

    @Override
    public void onRequestCellMemberInfoResult(boolean isSuccess, String leaderName, ArrayList<CellMemberInfo> memberInfo) {
        if(isSuccess)
            HttpManager.getInstance().getCellMemberAttendance(leaderName, Common.getTodaysYear(), Common.getTodaysWeekOfYear());
    }

    @Override
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, final String leaderName, ArrayList<CellMemberInfo> memberInfo) {
        if(!isSuccess)
            return;

        final int nTotal = memberInfo.size();
        int nCellAttendantee = 0;
        int nWorshipAttendantee = 0;

        for(CellMemberInfo m : memberInfo){
            if(m.getAttendanceData(Common.getTodaysYear(), Common.getTodaysWeekOfYear()).isCellAttended())
                nCellAttendantee++;
            if(m.getAttendanceData(Common.getTodaysYear(), Common.getTodaysWeekOfYear()).isWorshipAttended())
                nWorshipAttendantee++;
        }

        final int finalNCellAttendantee = nCellAttendantee;
        final int finalNWorshipAttendantee = nWorshipAttendantee;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addData(leaderName, nTotal, finalNCellAttendantee, finalNWorshipAttendantee);
                mAdapter.notifyDataSetChanged();

                overallWorshipAttendance.setText("주일예배(" + mAdapter.getWorshipAttendance() + "/" + mAdapter.getTotal() + ")");
                overallCellAttendance.setText("셀모임(" + mAdapter.getCellAttendance() + "/" + mAdapter.getTotal() + ")");
            }
        });
    }

    @Override
    public void onRequestCellLeaderListResult(boolean isSuccess, final String parish, ArrayList<String> cellLeaderList) {
        Log.d(TAG, "onRequestCellLeaderListResult isSuccess=" + isSuccess);
        if(isSuccess){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    titleParish.setText(parish);
                }
            });

            for(String s : cellLeaderList){
                HttpManager.getInstance().requestCellMemberInfo(s);
            }
        }
    }

    @Override
    public void onSubmitCellAttandanceResult(boolean isSuccess) {

    }
}
