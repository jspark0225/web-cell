package com.spring.jspark.springwebcell.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.adapter.ParishMemberListViewAdapter;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.contract.ParishMemberListContract;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;
import com.spring.jspark.springwebcell.presenter.ParishMemberListPresenter;
import com.spring.jspark.springwebcell.utils.ResourceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParishMemberListActivity extends AppCompatActivity implements ParishMemberListContract.View{
    private static final String TAG = ParishMemberListActivity.class.getSimpleName();

    int weekOfYear;
    int year;
    int selectedMonth;
    int selectedDate;

    Calendar cal = Calendar.getInstance();

    @Bind(R.id.parish_calendar_btn)
    ImageButton calBtn;

    @Bind(R.id.parish_date_text)
    TextView dateTextView;

    @Bind(R.id.parish_member_list)
    ListView mListView;

    @Bind(R.id.parish_member_list_overall_title)
    TextView mOverallTitle;

    @Bind(R.id.parish_member_list_overall_content)
    TextView mOverAllContent;

    ParishMemberListViewAdapter mAdapter;

    ParishMemberListPresenter mPresenter = new ParishMemberListPresenter();

    ProgressDialog mRefreshDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parish_member_list);
        ButterKnife.bind(this);
        mPresenter.setView(this);
        mAdapter = new ParishMemberListViewAdapter(mOnLayoutClickListener);

        mRefreshDialog = new ProgressDialog(this);
        mRefreshDialog.setMessage("데이터를 가져오는 중입니다");

        weekOfYear = Common.getTodaysWeekOfYear();
        year = Common.getTodaysYear();
        selectedMonth = Common.getTodaysMonth();
        selectedDate = Common.getDeafaultDate();

        setDateText(year, selectedMonth + 1, selectedDate);

        mAdapter.setMemberListInfo(mPresenter.getParishMember());
        mAdapter.setDate(year, weekOfYear);

        mListView.setAdapter(mAdapter);
        mListView.setItemsCanFocus(true);
        setOverallTitle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDateText(year, selectedMonth + 1, selectedDate);

        mAdapter.setMemberListInfo(mPresenter.getParishMember());
        mAdapter.setDate(year, weekOfYear);

        //mListView.setAdapter(mAdapter);
        //mListView.setItemsCanFocus(true);
        setOverallTitle();
    }

    private void setOverallTitle(){
        int nWorshipAttendance = mPresenter.getParishWorshipAttendance(year, weekOfYear);
        int nCellAttendance = mPresenter.getParishCellAttendance(year, weekOfYear);
        int nWorshipTotal = mPresenter.getTotal(year, weekOfYear);
        int nCellTotal = mPresenter.getTotal(year, weekOfYear);

        mOverallTitle.setText( mPresenter.getParish() );
        mOverAllContent.setText( "예배 출석(" + nWorshipAttendance + "/" + nWorshipTotal + "), 셀 출석(" + nCellAttendance + "/" + nCellTotal + ")" );
    }

    long mFirstBackPressedTime = 0;

    @Override
    public void onBackPressed() {
        long backPressedTime = System.currentTimeMillis();

        if(backPressedTime - mFirstBackPressedTime < 2000){
            finish();
        }else{
            Toast.makeText(getApplicationContext(), ResourceManager.getInstance().getString(R.string.warning_for_app_terminating), Toast.LENGTH_SHORT).show();
            mFirstBackPressedTime = backPressedTime;
        }
    }

    @OnClick(R.id.parish_calendar_btn)
    public void onCalendarButtonClicked(){
        // 달력 Dialog 생성
        DatePickerDialog calendar =  new DatePickerDialog(ParishMemberListActivity.this, dateSetListener, year, selectedMonth, selectedDate);
        calendar.getDatePicker().setCalendarViewShown(true); // calendarview 추가
        calendar.getDatePicker().setSpinnersShown(false);  // 년 월 spinner 제거
        calendar.show();
    }

    //17.03.09 노원태 수정 달력 DatePickerDialog 리스너
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int calYear, int calMonth, int calDate){

            Log.d("jisu", "year = " + year + ", calYear = " + calYear);
            boolean isNeedToRefresh = (calYear != year);

            cal.set(calYear,calMonth,calDate);

            year = calYear;
            selectedMonth = calMonth;
            selectedDate = calDate;
            weekOfYear = cal.get(cal.WEEK_OF_YEAR);

            setDateText(year, selectedMonth + 1, selectedDate);

            mAdapter.setDate(year, weekOfYear);
            if(mRefreshDialog != null && !mRefreshDialog.isShowing())
                mRefreshDialog.show();

            if(isNeedToRefresh)
                mPresenter.requestParishMemberList(year);
            else
                mPresenter.requestParishMembers();
        }
    };

    private void setDateText(int year, int month, int date){
        String str = year + "년 " + month + "월 " + date + "일 " + weekOfYear + "주차";
        dateTextView.setText(str);
    }

    @Override
    public void updateParishMembers(Parish parishMembers) {
        final Parish finalParishMembers = parishMembers;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setDate(year, weekOfYear);
                mAdapter.setMemberListInfo(finalParishMembers);
                mAdapter.notifyDataSetChanged();
                setOverallTitle();
            }
        });
    }

    @Override
    public void hideRefreshDialogProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mRefreshDialog != null && mRefreshDialog.isShowing())
                    mRefreshDialog.hide();
            }
        });
    }

    public View.OnClickListener mOnLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick");
            View view = v.findViewById(R.id.parish_member_list_title);
            if(view != null){
                if(mRefreshDialog != null && !mRefreshDialog.isShowing())
                    mRefreshDialog.show();

                String leaderName = ((TextView) view).getText().toString();
                mPresenter.requestCellMemberList(leaderName, year, weekOfYear);
            }
        }
    };

    @Override
    public void goToCellMemberListActivity(final String leaderName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ParishMemberListActivity.this, CellMemberListActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("week", weekOfYear);
                intent.putExtra("leaderName", leaderName);
                intent.putExtra("from", 1);
                ParishMemberListActivity.this.startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode = " + requestCode);
        mPresenter.setListener();
        mAdapter.notifyDataSetChanged();
    }
}
