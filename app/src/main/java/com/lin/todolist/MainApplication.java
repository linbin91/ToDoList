package com.lin.todolist;

import android.app.Application;

import com.lin.netlib.GlobalConfig;
import com.lin.netlib.XRetrofit;
import com.lin.todolist.manager.PersistentCookieJarManager;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GlobalConfig globalConfig = XRetrofit.init()
                .debug(BuildConfig.DEBUG)
                .cookieJar(PersistentCookieJarManager.getInstance(this).getPersistentCookieJar());
    }
}
