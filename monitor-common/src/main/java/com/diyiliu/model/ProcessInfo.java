package com.diyiliu.model;

import lombok.Data;

/**
 * Description: ProcessInfo
 * Author: DIYILIU
 * Update: 2018-10-09 15:43
 */

@Data
public class ProcessInfo {

    public ProcessInfo() {

    }

    public ProcessInfo(Integer pid, String name, Double memUsage, String vsz, String rss) {
        this.pid = pid;
        this.name = name;
        this.memUsage = memUsage;
        this.vsz = vsz;
        this.rss = rss;
    }

    private Integer pid;

    private String name;

    /** 内存使用率 **/
    private Double memUsage;

    /** 进程可以访问的所有内存，包括进入交换分区的内容，以及共享库占用的内存。 **/
    private String vsz;

    /** 常驻内存集（Resident Set Size），表示该进程分配的内存大小。 **/
    private String rss;
}
