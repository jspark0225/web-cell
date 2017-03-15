package com.spring.jspark.springwebcell.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.utils.ResourceManager;
import com.spring.jspark.springwebcell.utils.SharedPreferenceManager;
import com.spring.jspark.springwebcell.contract.MainContract;
import com.spring.jspark.springwebcell.presenter.MainPresenter;
import com.spring.jspark.springwebcell.view.CustomSpinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity implements MainContract.View, CustomSpinner.OnSpinnerEventsListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_CODE_TERMINATE = 0;

    @Bind(R.id.parish_spinner)
    CustomSpinner mParishSpinner;

    @Bind(R.id.login_id)
    EditText mLoginEditText;

    @Bind(R.id.password)
    EditText mPasswordEditText;

    MainPresenter mPresenter;

    ArrayAdapter<String> closedAdapter;
    ArrayAdapter<String> openedAdapter;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SharedPreferenceManager.getInstance().setContex(this);
        ResourceManager.getInstance().setContext(this);

        mPresenter = new MainPresenter();
        mPresenter.setView(this);
        mPresenter.getPermission(this);
        mPresenter.setAlarm(this);


        String[] parishList = getResources().getStringArray(R.array.parish_list);
        closedAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, R.id.spinner_item_text, parishList);
        openedAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item_opened, R.id.spinner_item_text_opened, parishList);

        mParishSpinner.setAdapter(closedAdapter);
        mParishSpinner.setSpinnerEventsListener(this);

        mPresenter.getSavedLoginData();
    }

    @OnItemSelected(value = R.id.parish_spinner, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(int position){
        Log.d(TAG, "onItemSelected position = " + position);
    }

    @OnItemSelected(value = R.id.parish_spinner, callback = OnItemSelected.Callback.NOTHING_SELECTED)
    public void nothingSelected(){
        Log.d(TAG, "nothingSelected");
    }

    @OnFocusChange(R.id.login_id)
    public void onIdFocusChange(){
        mPresenter.onIdFocusChanged(mLoginEditText);
    }

    @OnFocusChange(R.id.password)
    public void onPasswordFocusChange(){
        mPresenter.onPasswordFocusChanged(mPasswordEditText);
    }

    @OnClick(R.id.login_btn)
    public void onLoginClicked(){
        String id = mLoginEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String parish = (String) mParishSpinner.getSelectedItem();
        int position = mParishSpinner.getSelectedItemPosition();

        mPresenter.onLoginButtonClicked(id, password, position, parish);
    }

    @Override
    public void onSpinnerOpened(Spinner spinner) {
        Log.d(TAG, "onSpinnerOpened");
        int position = mParishSpinner.getSelectedItemPosition();
        mParishSpinner.setAdapter(openedAdapter);
        mParishSpinner.setSelection(position);
    }

    @Override
    public void onSpinnerClosed(Spinner spinner) {
        Log.d(TAG, "onSpinnerClosed");
        int position = mParishSpinner.getSelectedItemPosition();
        mParishSpinner.setAdapter(closedAdapter);
        mParishSpinner.setSelection(position);
    }

    @Override
    public void updateIdEditText(String content) {
        final String finalContent = content;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoginEditText.setText(finalContent);
            }
        });
    }

    @Override
    public void updatePasswordEditText(String content) {
        final String finalContent = content;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPasswordEditText.setText(finalContent);
            }
        });
    }

    @Override
    public void updateParishSpinner(int position) {
        final int finalPosition = position;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mParishSpinner.setSelection(finalPosition);
            }
        });
    }

    @Override
    public void showToastMessage(String message) {
        final String finalMessage = message;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), finalMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void goToCellMemberActivity() {
        Intent intent = new Intent(MainActivity.this, CellMemberListActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TERMINATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_TERMINATE){
            finish();
        }
    }
}