package com.diyiliu.model;

import lombok.Data;

import java.util.List;

/**
 * Description: MonitorInfo
 * Author: DIYILIU
 * Update: 2018-10-09 15:31
 */

@Data
public class MonitorInfo {

    /**  操作系统 **/
    private String os;

    /** CPU **/
    private String cpuName;

    /** CPU 内核数量 **/
    private Integer cpuCore;

    /** CPU 逻辑处理器数量 **/
    private Integer logicalCpu;

    /** CPU 使用率 **/
    private double cpuLoad;

    /** CPU 平均使用率 **/
    private double[] avgCpuLoad;

    /** 进程数 **/
    private Integer processCount;

    /** 线程数 **/
    private Integer threadCount;

    /** 总内存 (MB) **/
    private Integer totalMemory;

    /** 剩余内存 (MB) **/
    private Integer availableMemory;

    /** 内存使用率 **/
    private Double memUsage;

    /** 占用内存前5, 进程信息 **/
    private List<ProcessInfo> processInfos;

    /** 系统磁盘信息 **/
    private List<DiskInfo> diskInfos;
}
