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

    public DiskInfo(String mount, String type, Double usableSpace, Double totalSpace, Double diskUsage) {
        this.mount = mount;
        this.type = type;
        this.usableSpace = usableSpace;
        this.totalSpace = totalSpace;
        this.diskUsage = diskUsage;
    }

    /** 盘符 **/
    private String mount;

    /** 磁盘类型 **/
    private String type;

    /** 可用空间 (GB) **/
    private Double usableSpace;

    /** 总空间 (GB) **/
    private Double totalSpace;

    /** 磁盘使用率 **/
    private Double diskUsage;
}
