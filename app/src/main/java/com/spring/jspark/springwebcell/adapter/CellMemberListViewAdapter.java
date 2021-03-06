package com.spring.jspark.springwebcell.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.httpclient.model.Attendance;
import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.view.CustomSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 6..
 */

public class CellMemberListViewAdapter extends BaseAdapter{
    private static final String TAG = CellMemberListViewAdapter.class.getSimpleName();

    private int year;
    private int week;

    String reason = "";

    Cell mCell;
    boolean[] isThereUpdatedData;

    Map<Integer, View> mChildViews = new HashMap<>();

    boolean isDiableRequired = false;

    public CellMemberListViewAdapter(){

    }

    public void disableViews(){
        isDiableRequired = true;
    }

    public void setMemberListInfo(Cell cell) {
        mCell = cell;
        isThereUpdatedData = new boolean[cell.size()];
        clearUpdatedData();
    }

    private void clearUpdatedData(){
        for(int i=0; i<isThereUpdatedData.length; i++)
            isThereUpdatedData[i] = false;
    }

    public void setDate(int year, int week){
        this.year = year;
        this.week = week;
    }

    public View getViewByPosition(int position){
        return mChildViews.get(position);
    }

    @Override
    public int getCount() {
        return mCell.size();
    }

    @Override
    public Object getItem(int position) {
        return mCell.getCellMemberByPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView position = " + position);

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_member_list_view_item, parent, false);

            CustomSpinner reasonSpinner = (CustomSpinner) convertView.findViewById(R.id.reason);
            reasonSpinner.setAdapter(ArrayAdapter.createFromResource(convertView.getContext(), R.array.reason_list, R.layout.cell_member_list_item_spinner));
            reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            mChildViews.put(position, convertView);
        }

        if(isThereUpdatedData[position]){
            Log.d(TAG, "data has been already set. ignore");
            return convertView;
        }

        isThereUpdatedData[position] = true;

        CheckBox worshipCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox1);
        CheckBox cellCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox2);
        TextView textView = (TextView) convertView.findViewById(R.id.textview);
        CustomSpinner reasonSpinner = (CustomSpinner) convertView.findViewById(R.id.reason);
        EditText reasonEditText = (EditText) convertView.findViewById(R.id.prayer_point);

        CellMember info = mCell.getCellMemberByPosition(position);
        Attendance attendance = info.getAttendanceData(year, week);

        boolean isWorshipAttended = attendance.isWorshipAttended();
        boolean isCellAttended = attendance.isCellAttended();

        worshipCheckBox.setChecked(isWorshipAttended);
        cellCheckBox.setChecked(isCellAttended);
        textView.setText(info.getName());

        String reason = info.getAttendanceData(year, week).getAbsentReason();

        if(reason == null)
            reason = "";

        //결석사유:@@@@/기타:@@@@
        String absentReason = "";
        String otherReason = "";

        if(reason.contains(Common.REASON_DELIMETER)){
            String [] reasons = reason.split(Common.REASON_DELIMETER);
            absentReason = reasons.length > 0 ? reasons[0] : "";
            otherReason = reasons.length > 1 ? reasons[1] : "";

            int reasonPosition = 0;
            for(int i = 0; i< reasonSpinner.getAdapter().getCount(); i++){
                if(reasonSpinner.getItemAtPosition(i).equals(absentReason)){
                    reasonPosition = i;
                    break;
                }
            }

            reasonSpinner.setSelection(reasonPosition);
        }
        else{
            absentReason = "";
            otherReason = reason;
            reasonSpinner.setSelection(0);
        }

        reasonEditText.setText(otherReason);

        if(isDiableRequired){
            worshipCheckBox.setEnabled(false);
            cellCheckBox.setEnabled(false);
            reasonSpinner.setEnabled(false);
            reasonEditText.setEnabled(false);
        }

        return convertView;
    }


    @Override
    public void notifyDataSetChanged() {
        Log.d(TAG, "notifyDataSetChanged()");
        clearUpdatedData();
        super.notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}