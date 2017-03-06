package com.spring.jspark.springwebcell.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.spring.jspark.springwebcell.httpparser.OnHttpResponse;
import com.spring.jspark.springwebcell.httpparser.WebCellHttpParser;
import com.spring.jspark.springwebcell.R;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, OnHttpResponse {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_INTERNET_PERMISSION = 1;

    Spinner parishSelector = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // permission check for Internet
        int permissionInternet = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET);
        if(permissionInternet != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_PERMISSION);
        }

        WebCellHttpParser.getInstance().setListener(this);

        ((Button)findViewById(R.id.login_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginId = ((EditText)findViewById(R.id.login_id)).getText().toString();
                String password = ((EditText)findViewById(R.id.password)).getText().toString();

                WebCellHttpParser.getInstance().login(loginId, password);
            }
        });

        parishSelector = (Spinner)findViewById(R.id.parish_spinner);
        parishSelector.setAdapter( ArrayAdapter.createFromResource(this,R.array.parish_list, android.R.layout.simple_spinner_dropdown_item));
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
                    String parish = (String)parishSelector.getSelectedItem();
                    Toast.makeText(MainActivity.this, "parish=" + parish, Toast.LENGTH_SHORT).show();
                    WebCellHttpParser.getInstance().getCellMembers(parish);
                }else{

                }
            }
        });
    }
}