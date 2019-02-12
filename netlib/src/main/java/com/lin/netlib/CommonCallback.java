package com.lin.netlib;

import okhttp3.ResponseBody;
import retrofit2.Call;


public abstract class CommonCallback {

    public void onStart(Call<ResponseBody> call){

    }

    public abstract void onResponse(Call<ResponseBody> call, ResponseBody responseBody);


    public abstract void onFailure(Call<ResponseBody> call, Throwable t);

    public void onFinish(Call<ResponseBody> call) {

    }
}
