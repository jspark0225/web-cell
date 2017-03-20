package com.spring.jspark.springwebcell.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.activity.CellMemberListActivity;
import com.spring.jspark.springwebcell.activity.MainActivity;
import com.spring.jspark.springwebcell.activity.ParishMemberListActivity;
import com.spring.jspark.springwebcell.common.Common;
import com.spring.jspark.springwebcell.httpclient.model.Cell;
import com.spring.jspark.springwebcell.httpclient.model.CellMember;
import com.spring.jspark.springwebcell.httpclient.model.Parish;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jspark on 2017. 3. 6..
 */

public class ParishMemberListViewAdapter extends BaseAdapter{
    private static final String TAG = ParishMemberListViewAdapter.class.getSimpleName();

    private int year;
    private int week;

    boolean[] isThereUpdatedData;

    Parish mParish;
    ArrayList mEntrySet = new ArrayList();

    View.OnClickListener mOnClickListener;

    public ParishMemberListViewAdapter(View.OnClickListener clickListener){
            mOnClickListener = clickListener;
    }

    public void setMemberListInfo(Parish memberList) {
        mParish = memberList;

        mEntrySet.clear();
        mEntrySet.addAll(mParish.getCellList().entrySet());

        Log.d(TAG, "EntrySet size = " + mEntrySet.size());
        isThereUpdatedData = new boolean[mEntrySet.size()];
        clearUpdatedData();
    }

    private void clearUpdatedData(){
        if(isThereUpdatedData == null)
            return;

        for(int i=0; i<isThereUpdatedData.length; i++)
            isThereUpdatedData[i] = false;
    }

    public void setDate(int year, int week){
        this.year = year;
        this.week = week;
    }

    @Override
    public int getCount() {
        return mEntrySet.size();
    }

    @Override
    public Object getItem(int position) {
        return mEntrySet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "EntrySet size = " + mEntrySet.size());
        Log.d(TAG, "getView position = " + position);

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parish_member_list_view_item, parent, false);
        }

        if(isThereUpdatedData[position]){
            Log.d(TAG, "data has been already set. ignore");
            return convertView;
        }

        convertView.setOnClickListener(mOnClickListener);

        isThereUpdatedData[position] = true;

        TextView title = (TextView) convertView.findViewById(R.id.parish_member_list_title);
        TextView content = (TextView) convertView.findViewById(R.id.parish_member_list_content);

        final Map.Entry<String, Cell> item = (Map.Entry<String, Cell>) getItem(position);
        if(item != null){
            int nCellAttendance = item.getValue().getNumberOfCellAttendance(year, week);
            int nWorshipAttendance = item.getValue().getNumberOfWorshipAttendance(year, week);
            int nTotal = item.getValue().size();

            title.setText( item.getKey() );
            content.setText( "예배 출석(" + nWorshipAttendance + "/" + nTotal + "), 셀 출석(" + nCellAttendance + "/" + nTotal + ")" );
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