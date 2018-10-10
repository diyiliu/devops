package com.diyiliu.model;

import lombok.Data;

/**
 * Description: DiskInfo
 * Author: DIYILIU
 * Update: 2018-10-09 15:51
 */

@Data
public class DiskInfo {

    public DiskInfo() {

    }

    public DiskInfo(String name, String type, Integer usableSpace, Integer totalSpace, Double diskUsage) {
        this.name = name;
        this.type = type;
        this.usableSpace = usableSpace;
        this.totalSpace = totalSpace;
        this.diskUsage = diskUsage;
    }

    /** 分区名称 **/
    private String name;

    /** 磁盘类型 **/
    private String type;

    /** 可用空间 (MB) **/
    private Integer usableSpace;

    /** 总空间 (MB) **/
    private Integer totalSpace;

    /** 磁盘使用率 **/
    private Double diskUsage;
}
