package com.lin.todolist.activity;

import android.text.TextUtils;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.lin.todolist.R;
import com.lin.todolist.base.BaseActivity;
import com.lin.todolist.http.HttpUtils;
import com.lin.todolist.http.ResponseItem;
import com.lin.todolist.manager.SharePreferenceManager;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.account)
    AutoCompleteTextView mAccount;
    @BindView(R.id.password)
    EditText mPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.regitster)
    void clickRegister() {
        startActivity(RegisterActivity.class);
    }

    @OnClick(R.id.login)
    void clickLogin() {
        mAccount.setError(null);
        mPassword.setError(null);
        if (TextUtils.isEmpty(mAccount.getText())) {
            mAccount.setError(getString(R.string.input_account));
            mAccount.setFocusable(true);
            mAccount.setFocusableInTouchMode(true);
            mAccount.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mPassword.getText())) {
            mPassword.setError(getString(R.string.input_password));
            mPassword.setFocusable(true);
            mPassword.setFocusableInTouchMode(true);
            mPassword.requestFocus();
            return;
        }

        requestLogin();
    }

    @Override
    protected boolean isLoadingEnable() {
        return true;
    }

    private void requestLogin() {
        HttpUtils.requestLogin(this, mAccount.getText().toString(), mPassword.getText().toString());
    }

    public void updateUI(ResponseItem<JsonObject> response) {
        if (response.isSuccess()) {
            SharePreferenceManager.getInstance(this).setUserName(response.getData().get("username").getAsString());
            startActivity(MainActivity.class);
            finish();
        }
    }
}

