package com.spring.jspark.springwebcell.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.adapter.CellMemberListViewAdapter;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.contract.CellMemberListContract;
import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.presenter.CellMemberListPresenter;
import com.spring.jspark.springwebcell.utils.ResourceManager;
import com.spring.jspark.springwebcell.view.CustomSpinner;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CellMemberListActivity extends AppCompatActivity implements CellMemberListContract.View {
    int weekOfYear;
    int year;
    int selectedMonth;
    int selectedDate;

    Calendar cal = Calendar.getInstance();

    @Bind(R.id.cell_member_list)
    ListView mListView;

    @Bind(R.id.calBtn)
    ImageButton calBtn;

    @Bind(R.id.dateText)
    TextView dateTextView;

    CellMemberListViewAdapter mAdapter;

    CellMemberListPresenter mPresenter;

    ProgressDialog mRefreshProgressDialog;
    ProgressDialog mSubmitProgressDialog;

    String leaderName;
    int from; // 0 : MainActivity, 1 : ParishMemberListViewAdapter.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_member_list);
        ButterKnife.bind(this);

        leaderName = getIntent().getStringExtra("leaderName");
        from = getIntent().getIntExtra("from", 0);

        mPresenter = new CellMemberListPresenter(leaderName);
        mPresenter.setView(this);

        mRefreshProgressDialog = new ProgressDialog(this);
        mRefreshProgressDialog.setMessage("데이터를 가져오는 중입니다");

        mSubmitProgressDialog = new ProgressDialog(this);
        mSubmitProgressDialog.setMessage("데이터를 전송하는 중입니다");

        weekOfYear = getIntent().getIntExtra("week", Common.getTodaysWeekOfYear());
        year = getIntent().getIntExtra("year", Common.getTodaysYear());
        selectedMonth = Common.getMonth(year, weekOfYear);
        selectedDate = Common.getDefaultDate(year, weekOfYear);

        setDateText(year, selectedMonth + 1, selectedDate);

        mAdapter = new CellMemberListViewAdapter();

        if(from == 1) {
            ((Button)findViewById(R.id.submit)).setVisibility(View.GONE);
            mAdapter.disableViews();
        }

        mAdapter.setMemberListInfo(mPresenter.getCellMemberData());
        mAdapter.setDate(year, weekOfYear);

        if(mListView.getCount() > 1)
            mListView.setAdapter(mAdapter);

        mListView.setItemsCanFocus(true);

        mPresenter.requestCellMemberAttendanceData(leaderName, year, weekOfYear);
    }

    @OnClick(R.id.calBtn)
    public void onCalendarButtonClicked(){
        // 달력 Dialog 생성
        DatePickerDialog calendar =  new DatePickerDialog(CellMemberListActivity.this, dateSetListener, year, selectedMonth, selectedDate);
        calendar.getDatePicker().setCalendarViewShown(true); // calendarview 추가
        calendar.getDatePicker().setSpinnersShown(false);  // 년 월 spinner 제거
        calendar.show();
    }

    @OnClick(R.id.submit)
    public void onSubmitButtonClicked(){
        if(mSubmitProgressDialog != null && !mSubmitProgressDialog.isShowing())
            mSubmitProgressDialog.show();

        for(int i = 0; i< mAdapter.getCount(); i++) {
            View childView = mAdapter.getViewByPosition(i);
            if (childView == null)
                continue;

            boolean isWorshipAttended = ((CheckBox) childView.findViewById(R.id.checkbox1)).isChecked();
            boolean isCellAttended = ((CheckBox) childView.findViewById(R.id.checkbox2)).isChecked();

            CustomSpinner spinner = (CustomSpinner) childView.findViewById(R.id.reason);
            EditText editText = (EditText) childView.findViewById(R.id.prayer_point);

            String reason1 = spinner.getSelectedItemPosition() == 0 ? "" : "" + spinner.getSelectedItem();
            String reason2 = editText.getText().toString().isEmpty() ? "" : "" + editText.getText().toString();

            mPresenter.setAttendanceData(i, year, weekOfYear, isWorshipAttended, isCellAttended, reason1, reason2);

            if (childView.hasFocus()) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(childView.getWindowToken(), 0);

                childView.clearFocus();
            }
        }

        mPresenter.onSubmitButtonClicked(year, weekOfYear);
    }

    long mFirstBackPressedTime = 0;

    @Override
    public void onBackPressed() {
        if(from == 0) {
            long backPressedTime = System.currentTimeMillis();

            if (backPressedTime - mFirstBackPressedTime < 2000) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(), ResourceManager.getInstance().getString(R.string.warning_for_app_terminating), Toast.LENGTH_SHORT).show();
                mFirstBackPressedTime = backPressedTime;
            }
        }else{
            finish();
        }
    }

    //17.03.09 노원태 수정 달력 DatePickerDialog 리스너
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int calYear, int calMonth, int calDate){
            cal.set(calYear,calMonth,calDate);

            year = calYear;
            selectedMonth = calMonth;
            selectedDate = calDate;
            weekOfYear = cal.get(cal.WEEK_OF_YEAR);

            setDateText(year, selectedMonth + 1, selectedDate);

            if(mRefreshProgressDialog != null && !mRefreshProgressDialog.isShowing())
                mRefreshProgressDialog.show();

            mAdapter.setDate(year, weekOfYear);
            mPresenter.requestCellMemberAttendanceData(leaderName, year, weekOfYear);
        }
    };

    private void setDateText(int year, int month, int date){
        String str = year + "년 " + month + "월 " + date + "일 " + weekOfYear + "주차";
        dateTextView.setText(str);
    }

    @Override
    public void updateMemberList(int year, int week, Cell cell) {
        final int finalYear = year;
        final int finalWeek = week;
        final Cell finalCell = cell;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setDate(finalYear, finalWeek);
                mAdapter.setMemberListInfo(finalCell);

                if(mListView.getAdapter() == null && mAdapter.getCount() > 1)
                    mListView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void showToast(String message) {
        final String finalMessage = message;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), finalMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void hideRefreshProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mRefreshProgressDialog != null && mRefreshProgressDialog.isShowing())
                    mRefreshProgressDialog.hide();
            }
        });
    }

    @Override
    public void hideSubmitProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mSubmitProgressDialog != null && mSubmitProgressDialog.isShowing())
                    mSubmitProgressDialog.hide();
            }
        });
    }
}