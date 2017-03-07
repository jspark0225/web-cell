package com.spring.jspark.springwebcell.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
        mAdapter = new CellMemberListViewAdapter(mCellMemberList);
        listView.setAdapter(mAdapter);

        ((Button)findViewById(R.id.submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpManager.getInstance().submitCellAttandance(year, weekOfYear);
            }
        });
    }

    @Override
    public void onLoginResult(boolean isSuccess) {

    }

    @Override
    public void onRequestCellMemberInfoResult(boolean isSuccess) {
        if(isSuccess){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CellMemberListActivity.this, "onRequestCellMemberInfoResult", Toast.LENGTH_SHORT).show();
                    mAdapter.setMemberListInfo( HttpManager.getInstance().getCellMemberInfo() );
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onSubmitCellAttandanceResult(boolean isSuccess) {
        if(isSuccess)
            HttpManager.getInstance().getCellMembers(year, weekOfYear);
    }
}