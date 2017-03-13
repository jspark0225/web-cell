package com.spring.jspark.springwebcell.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.spring.jspark.springwebcell.httpclient.model.AttendanceData;
import com.spring.jspark.springwebcell.httpclient.HttpRequest;
import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;
import com.spring.jspark.springwebcell.view.CustomSpinner;

import java.util.ArrayList;
import java.util.Calendar;

import static java.security.AccessController.getContext;

public class CellMemberListActivity extends AppCompatActivity implements OnHttpResponse {
    public static final int RESULT_CODE_TERMINATE = 1;

    int weekOfYear;
    int year;
    int selMonth;
    int selDate;

    Calendar cal = Calendar.getInstance();
    Toast toast;

    ListView mListView;
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

        mListView = (ListView) findViewById(R.id.cell_member_list);
        mAdapter = new CellMemberListViewAdapter(mCellMemberList, year, weekOfYear);
        mListView.setAdapter(mAdapter);
        mListView.setItemsCanFocus(true);

        ((Button) findViewById(R.id.submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i< mAdapter.getCount(); i++){
                    View childView = mAdapter.getViewByPosition(i);
                    if(childView == null)
                        continue;

                    boolean isWorshipAttended = ((CheckBox) childView.findViewById(R.id.checkbox1)).isChecked();
                    boolean isCellAttended = ((CheckBox) childView.findViewById(R.id.checkbox2)).isChecked();

                    CustomSpinner spinner = (CustomSpinner) childView.findViewById(R.id.reason);
                    EditText editText = (EditText) childView.findViewById(R.id.prayer_point);

                    String reason1 = spinner.getSelectedItemPosition() == 0 ? "" : "" + spinner.getSelectedItem();
                    String reason2 = editText.getText().toString().isEmpty() ? "" : "" + editText.getText().toString();

                    AttendanceData data = mCellMemberList.get(i).getAttendanceData(year, weekOfYear);
                    data.setCellAttended(isCellAttended);
                    data.setWorshipAttended(isWorshipAttended);

                    if(!reason1.isEmpty() || !reason2.isEmpty())
                        data.setAbsentReason(reason1 + Common.REASON_DELIMETER + reason2);
                    else
                        data.setAbsentReason("");

                    if(childView.hasFocus()){
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(childView.getWindowToken(), 0);


                        childView.clearFocus();
                    }
                }

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
    public void onSubmitCellAttandanceResult(final boolean isSuccess) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String response = isSuccess ? "웹셀 체크 완료되었습니다" : "웹셀 체크에 실패했습니다";
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT ).show();
            }
        });

    }

    @Override
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, final int year, final int week, ArrayList<CellMemberInfo> memberInfo) {

        // final ArrayList<CellMemberInfo> mem = memberInfo;
        mCellMemberList = memberInfo;
        if (isSuccess) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setDate(year, week);
                    mAdapter.setMemberListInfo(mCellMemberList);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    long mFirstBackPressedTime = 0;

    @Override
    public void onBackPressed() {
        long backPressedTime = System.currentTimeMillis();

        if(backPressedTime - mFirstBackPressedTime < 2000){
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "'뒤로'버튼 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT).show();
            mFirstBackPressedTime = backPressedTime;
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