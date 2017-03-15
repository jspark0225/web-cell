package com.spring.jspark.springwebcell.contract;

import android.content.Context;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by jspark on 2017. 3. 15..
 */

public interface MainContract {
    interface View{
        void updateIdEditText(String content);
        void updatePasswordEditText(String content);
        void updateParishSpinner(int position);
        void showToastMessage(String message);
        void goToCellMemberActivity();
    }

    interface Presenter{
        void setView(View view);
        void getPermission(Context context);
        void setAlarm(Context context);
        void onIdFocusChanged(EditText editText);
        void onPasswordFocusChanged(EditText editText);
        void onLoginButtonClicked(String id, String password, int selectedPosition, String parish);
        void getSavedLoginData();
    }
}
