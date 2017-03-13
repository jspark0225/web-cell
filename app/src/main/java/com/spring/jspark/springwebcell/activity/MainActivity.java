package com.spring.jspark.springwebcell.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.spring.jspark.springwebcell.R;
import com.spring.jspark.springwebcell.httpclient.model.CellMemberInfo;
import com.spring.jspark.springwebcell.httpclient.OnHttpResponse;
import com.spring.jspark.springwebcell.httpclient.WebCellHttpClient;
import com.spring.jspark.springwebcell.view.CustomSpinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnHttpResponse {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_INTERNET_PERMISSION = 1;

    CustomSpinner mParishSpinner;
    EditText mLoginEditText;
    EditText mPasswordEditText;

    EditText loginId;
    EditText password;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    EditText loginId;
    EditText password;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);

        // permission check for Internet
        int permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        if(permissionInternet != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }

        loginId = (EditText)findViewById(R.id.login_id);
        password = (EditText)findViewById(R.id.password);

        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();






        WebCellHttpClient.getInstance().setListener(this);

        mLoginEditText = (EditText) findViewById(R.id.login_id);
        mPasswordEditText = (EditText) findViewById(R.id.password);

        mLoginEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mLoginEditText.setText("");
                }else{
                    if( mLoginEditText.getText().toString().isEmpty())
                        mLoginEditText.setText("ID를 입력하세요");
                }
            }
        });

        mPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mPasswordEditText.setText("");
                }else{
                    if( mPasswordEditText.getText().toString().isEmpty())
                        mPasswordEditText.setText("1234");
                }
            }
        });


        ((Button)findViewById(R.id.login_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParishSpinner.getSelectedItemPosition() == 0){
                    //TODO : alert message
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.please_select_parish), Toast.LENGTH_SHORT).show();

                    return;
                }

                WebCellHttpClient.getInstance().requestLogin(loginId.getText().toString(), password.getText().toString());
            }
        });

        String[] parishList = getResources().getStringArray(R.array.parish_list);

        final ArrayAdapter<String> closedAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, R.id.spinner_item_text, parishList);
        final ArrayAdapter<String> openedAdapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item_opened, R.id.spinner_item_text_opened, parishList);

        mParishSpinner = (CustomSpinner) findViewById(R.id.parish_spinner);
        mParishSpinner.setAdapter(closedAdapter);
        mParishSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected position = " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mParishSpinner.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
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
        });

        if(pref.getBoolean("login_enabled", false)){
            loginId.setText(pref.getString("id", ""));
            password.setText(pref.getString("pw", ""));
            mParishSpinner.setSelection(pref.getInt("parishPos", 0));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == REQUEST_INTERNET_PERMISSION){
            for(int i=0; i<permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    Log.d("MainActivity", "permission " + permissions[i] + " has been granted");
                else
                    Log.d("MainActivity", "permission " + permissions[i] + " has been denied");
            }
        }
    }

    @Override
    public void onLoginResult(boolean isSuccess) {
        Log.d(TAG, "onLoginResult " +isSuccess);

        final boolean _isSuccess = isSuccess;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(_isSuccess){
                    editor.putString("id", loginId.getText().toString());
                    editor.putString("pw", password.getText().toString());
                    editor.putInt("parishPos", mParishSpinner.getSelectedItemPosition());
                    editor.putBoolean("login_enabled", true);
                    editor.commit();

                    String parish = (String)mParishSpinner.getSelectedItem();
                    //WebCellHttpClient.getInstance().setParish(parish);
                    WebCellHttpClient.getInstance().requestCellMemberInfo();
                }else{
                    String errorMsg = getApplicationContext().getString(R.string.fail_to_login);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestCellMemberInfoResult(boolean isSuccess, ArrayList<CellMemberInfo> memberInfo) {
        if(isSuccess){
            Intent intent = new Intent(MainActivity.this, CellMemberListActivity.class);
            startActivity(intent);
        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String errorMsg = getApplicationContext().getResources().getString(R.string.fail_to_get_cell_member);
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    public void onSubmitCellAttandanceResult(boolean isSuccess) {

    }

    @Override
    public void onRequestCellMemberAttendanceResult(boolean isSuccess, int year, int week, ArrayList<CellMemberInfo> memberInfo) {

    }
}
