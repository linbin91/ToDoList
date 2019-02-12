package com.lin.todolist.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.lin.todolist.R;
import com.lin.todolist.activity.LoginActivity;
import com.lin.todolist.fragment.base.BaseFragment;
import com.lin.todolist.manager.PersistentCookieJarManager;
import com.lin.todolist.manager.SharePreferenceManager;

import butterknife.BindView;
import butterknife.OnClick;


public class SettingFragment extends BaseFragment {
    @BindView(R.id.user_name)
    TextView mUserName;

    public static SettingFragment newInstance() {

        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initData() {
        String userName = SharePreferenceManager.getInstance(getContext()).getUserName();
        if (!TextUtils.isEmpty(userName)) {
            mUserName.setText(SharePreferenceManager.getInstance(getContext()).getUserName());
        }

    }

    @OnClick(R.id.logout)
    void onClickLogout() {
        SharePreferenceManager.getInstance(getContext()).clear();
        PersistentCookieJarManager.getInstance(getContext()).getPersistentCookieJar().clear();
        startActivity(LoginActivity.class);
        getActivity().finish();
    }
}
