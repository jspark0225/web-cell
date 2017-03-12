package com.spring.jspark.springwebcell.activity;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.adapter.CellMemberListViewAdapter;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.httpclient.HttpRequest;
import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;

import java.util.ArrayList;
import java.util.Calendar;

public class CellMemberListActivity extends AppCompatActivity implements OnHttpResponse {

    int weekOfYear;
    int year;
    int selMonth;
    int selDate;

    Calendar cal = Calendar.getInstance();
    Toast toast;

    CellMemberListViewAdapter mAdapter;
    ArrayList<CellMemberInfo> mCellMemberList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_member_list);

        onLoginResult(true);

        weekOfYear = Common.getTodaysWeekOfYear();
        year = Common.getTodaysYear();
        selMonth = Common.getTodaysMonth();
        selDate = Common.getDeafaultDate();

        setDateText(year, selMonth, selDate);
        ImageButton calBtn = (ImageButton)findViewById(R.id.calBtn);

        // 달력 버튼 이벤트 리스너
        calBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 달력 Dialog 생성
                DatePickerDialog calendar =  new DatePickerDialog(CellMemberListActivity.this, dateSetListener, year, selMonth, selDate);
                calendar.getDatePicker().setCalendarViewShown(true); // calendarview 추가
                calendar.getDatePicker().setSpinnersShown(false);  // 년 월 spinner 제거
                calendar.show();

            }
        }



        );



        WebCellHttpClient.getInstance().setListener(this);
        mCellMemberList = WebCellHttpClient.getInstance().getCellMemberInfo();

        ListView listView = (ListView) findViewById(R.id.cell_member_list);
        mAdapter = new CellMemberListViewAdapter(mCellMemberList, year, weekOfYear);
        listView.setAdapter(mAdapter);

        ((Button) findViewById(R.id.submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebCellHttpClient.getInstance().submitAttendance(year, weekOfYear);
            }
        });

        WebCellHttpClient.getInstance().getCellMemberAttendance(year, weekOfYear);
    }

    @Override
    public void onLoginResult(boolean isSuccess) {

    }

    @Override
    public void onRequestCellMemberInfoResult(boolean isSuccess, ArrayList<CellMemberInfo> memberInfo) {

    }

    @Override
    public void onSubmitCellAttandanceResult(boolean isSuccess) {
    }

    @Override
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, ArrayList<CellMemberInfo> memberInfo) {

        // final ArrayList<CellMemberInfo> mem = memberInfo;
        mCellMemberList = memberInfo;
        if (isSuccess) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setMemberListInfo(mCellMemberList);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    //17.03.09 노원태 수정 달력 DatePickerDialog 리스너
      private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int calYear, int calMonth, int calDate){


            cal.set(calYear,calMonth,calDate);

            year = calYear;
            selMonth = calMonth;
            selDate = calDate;
            weekOfYear = cal.get(cal.WEEK_OF_YEAR);

            setDateText(year, selMonth, selDate);

            mAdapter.setDate(year, weekOfYear);
            WebCellHttpClient.getInstance().getCellMemberAttendance(year,weekOfYear);

        }
    };



    private void setDateText(int year, int month, int date){

        TextView dateText = (TextView)findViewById(R.id.dateText);
        month = month+1;
        String str = year + "년 " + month + "월 " + date + "일 " + weekOfYear + "주차";
        dateText.setText(str);

    }


}