package com.lin.netlib;


public interface BaseView {
    void start(int requestId);

    void error(Throwable t, int requestId);

    void end(int requestId);
}
