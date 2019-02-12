package com.lin.todolist.http;


import com.google.gson.Gson;
import com.lin.netlib.BaseView;
import com.lin.netlib.GsonCallback;
import com.lin.todolist.exception.ApiException;


public abstract class MyGsonCallback<T> extends GsonCallback<T> {

    public MyGsonCallback(BaseView baseView) {
        super(baseView);
    }

    public MyGsonCallback(BaseView baseView, int requestId) {
        super(baseView, requestId);
    }

    @Override
    protected String convertResponse(String response) {

        ResponseItem responseItem = new Gson().fromJson(response, ResponseItem.class);
        if (!responseItem.isSuccess()) {
            throw new ApiException(responseItem.getErrorMsg(), responseItem.getErrorCode());
        }
        return super.convertResponse(response);
    }
}
