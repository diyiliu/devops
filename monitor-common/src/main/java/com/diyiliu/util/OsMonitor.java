package com.diyiliu.util;

import com.diyiliu.model.DiskInfo;
import com.diyiliu.model.MonitorInfo;
import com.diyiliu.model.ProcessInfo;
import com.diyiliu.plugin.util.CommonUtil;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: OsMonitor
 * Author: DIYILIU
 * Update: 2018-10-09 16:35
 */

public class OsMonitor {
    private static final long MEBI = 1L << 20;

    private HardwareAbstractionLayer hal;
    private OperatingSystem os;

    private String host = "127.0.0.1";

    public OsMonitor(String host) {
        this();

        this.host = host;
    }

    public OsMonitor() {
        SystemInfo si = new SystemInfo();
        hal = si.getHardware();
        os = si.getOperatingSystem();
    }

    public MonitorInfo osHealth() {
        MonitorInfo info = new MonitorInfo();
        info.setOs(os.getFamily());

        CentralProcessor processor = hal.getProcessor();
        info.setCpuName(processor.getName());
        info.setCpuCore(processor.getPhysicalProcessorCount());
        info.setLogicalCpu(processor.getLogicalProcessorCount());

        Util.sleep(1000);
        info.setCpuLoad(CommonUtil.keepDecimal(processor.getSystemCpuLoad(), 2));
        info.setAvgCpuLoad(processor.getProcessorCpuLoadBetweenTicks());
        info.setProcessCount(os.getProcessCount());
        info.setThreadCount(os.getThreadCount());

        GlobalMemory memory = hal.getMemory();
        int availableMemory = new BigDecimal(memory.getAvailable() * 1d / MEBI).intValue();
        int totalMemory = new BigDecimal(memory.getTotal() * 1d / MEBI).intValue();
        info.setAvailableMemory(availableMemory);
        info.setTotalMemory(totalMemory);
        info.setMemUsage(CommonUtil.keepDecimal((memory.getTotal() - memory.getAvailable()) * 1d / memory.getTotal(), 2));

        info.setProcessInfos(checkProcess());
        info.setDiskInfos(checkDisk());

        return info;
    }

    public List<ProcessInfo> checkProcess() {
        List<ProcessInfo> infoList = new ArrayList();

        GlobalMemory memory = hal.getMemory();
        // Sort by highest MEMORY
        List<OSProcess> processList = Arrays.asList(os.getProcesses(5, OperatingSystem.ProcessSort.MEMORY));
        for (OSProcess process : processList) {
            int pid = process.getProcessID();
            String name = process.getName();
            Double memUsage = CommonUtil.keepDecimal(process.getResidentSetSize() * 1d / memory.getTotal(), 2);
            String vsz = FormatUtil.formatBytes(process.getVirtualSize());
            String rss = FormatUtil.formatBytes(process.getResidentSetSize());

            if (memUsage > 0.01) {
                infoList.add(new ProcessInfo(pid, name, memUsage, vsz, rss));
            }
        }

        return infoList;
    }

    public List<DiskInfo> checkDisk() {
        List<DiskInfo> infoList = new ArrayList();

        FileSystem fileSystem = os.getFileSystem();
        OSFileStore[] fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();

            if (total > 0) {
                String mount = fs.getMount().replaceAll("/", "").replaceAll("\\\\", "");
                String volume = fs.getVolume();
                String name = os.getFamily().toLowerCase().indexOf("windows") > -1 ? mount : volume;

                String type = fs.getType();
                int usableM = new BigDecimal(usable * 1d / MEBI).intValue();
                int totalM = new BigDecimal(total * 1d / MEBI).intValue();

                Double diskUsage = CommonUtil.keepDecimal((totalM - usableM) * 1d / totalM, 2);
                infoList.add(new DiskInfo(name, type, usableM, totalM, diskUsage));
            }
        }

        return infoList;
    }

    public String getHost() {
        return host;
    }
}
