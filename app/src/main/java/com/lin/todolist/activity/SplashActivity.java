package com.lin.todolist.activity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.lin.todolist.R;
import com.lin.todolist.base.BaseActivity;
import com.lin.todolist.constant.Constant;
import com.lin.todolist.manager.PersistentCookieJarManager;

import java.util.List;

import butterknife.BindView;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class SplashActivity extends BaseActivity {


    @BindView(R.id.root_view)
    View mView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        mView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void redirectTo() {
        List<Cookie> cookies = PersistentCookieJarManager.getInstance(this).getPersistentCookieJar().loadForRequest(HttpUrl.parse(Constant.BASE_URL));
        if (cookies.isEmpty()) {
            startActivity(LoginActivity.class);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("user_name", cookies.get(0).name());
            startActivity(MainActivity.class, bundle);
        }
        finish();

    }
}
