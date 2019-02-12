package com.lin.todolist.manager;


import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.lin.netlib.RetrofitHttpManager;


public class PersistentCookieJarManager {

    private volatile static PersistentCookieJarManager sInstance;
    private PersistentCookieJar mPersistentCookieJar;

    private PersistentCookieJarManager(Context context) {
        mPersistentCookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
    }

    public static PersistentCookieJarManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (RetrofitHttpManager.class) {
                if (sInstance == null) {
                    sInstance = new PersistentCookieJarManager(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public PersistentCookieJar getPersistentCookieJar() {
        return mPersistentCookieJar;
    }
}
