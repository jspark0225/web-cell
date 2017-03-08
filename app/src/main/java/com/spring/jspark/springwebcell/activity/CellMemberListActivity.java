package com.spring.jspark.springwebcell.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.adapter.CellMemberListViewAdapter;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.httpparser.CellMemberInfo;
import com.spring.jspark.springwebcell.httpparser.HttpManager;
import com.spring.jspark.springwebcell.httpparser.OnHttpResponse;

import java.util.ArrayList;

public class CellMemberListActivity extends AppCompatActivity implements OnHttpResponse{

    int weekOfYear;
    int year;

    CellMemberListViewAdapter mAdapter;
    ArrayList<CellMemberInfo> mCellMemberList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_member_list);

        weekOfYear = Common.getTodaysWeekOfYear();
        year = Common.getTodaysYear();

        HttpManager.getInstance().setListener(this);
        mCellMemberList = HttpManager.getInstance().getCellMemberInfo();

        ListView listView = (ListView) findViewById(R.id.cell_member_list);
        mAdapter = new CellMemberListViewAdapter(mCellMemberList, year, weekOfYear);
        listView.setAdapter(mAdapter);

        ((Button)findViewById(R.id.submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpManager.getInstance().submitAttendance(year, weekOfYear);
            }
        });

        HttpManager.getInstance().getCellMemberAttendance(year, weekOfYear);
    }

    @Override
    public void onLoginResult(boolean isSuccess) {

    }

    @Override
    public void onRequestCellMemberInfoResult(boolean isSuccess, String leaderName, ArrayList<CellMemberInfo> memberInfo) {

    }

    @Override
    public void onSubmitCellAttandanceResult(boolean isSuccess) {
    }

    @Override
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, String leaderName, ArrayList<CellMemberInfo> memberInfo) {
        final ArrayList<CellMemberInfo> mem = memberInfo;
        if(isSuccess){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setMemberListInfo( mem );
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onRequestCellLeaderListResult(boolean isSuccess, String parish, ArrayList<String> cellLeaderList) {

    }
}