package com.diyiliu.server.support;

import com.diyiliu.model.MonitorInfo;

/**
 * Description: IMsgObserver
 * Author: DIYILIU
 * Update: 2018-10-15 14:58
 */
public interface IMsgObserver {

    void read(MonitorInfo monitorInfo);
}
