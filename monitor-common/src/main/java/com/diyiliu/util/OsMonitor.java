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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: OsMonitor
 * Author: DIYILIU
 * Update: 2018-10-09 16:35
 */

public class OsMonitor {
    private static final long GIBI = 1L << 30;

    private HardwareAbstractionLayer hal;
    private OperatingSystem os;

    public OsMonitor() {
        SystemInfo si = new SystemInfo();
        hal = si.getHardware();
        os = si.getOperatingSystem();
    }

    public MonitorInfo osHealth() {
        MonitorInfo info = new MonitorInfo();
        info.setOs(os.getFamily());

        CentralProcessor processor = hal.getProcessor();
        Util.sleep(1000);
        info.setCpuLoad(CommonUtil.keepDecimal(processor.getSystemCpuLoad(), 2));
        info.setAvgCpuLoad(processor.getProcessorCpuLoadBetweenTicks());
        info.setProcessCount(os.getProcessCount());
        info.setThreadCount(os.getThreadCount());

        GlobalMemory memory = hal.getMemory();
        Double availableMemory = CommonUtil.keepDecimal(memory.getAvailable() * 1d / GIBI, 2);
        Double totalMemory = CommonUtil.keepDecimal(memory.getTotal() * 1d / GIBI, 2);
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

            infoList.add(new ProcessInfo(pid, name, memUsage, vsz, rss));
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
            String mount = fs.getMount().replaceAll("/", "").replaceAll("\\\\", "");
            String type = fs.getType();
            Double diskUsage = CommonUtil.keepDecimal((total - usable) * 1d / total, 2);

            Double usableG = CommonUtil.keepDecimal(usable * 1d / GIBI, 1);
            Double totalG = CommonUtil.keepDecimal(total * 1d / GIBI, 1);

            infoList.add(new DiskInfo(mount, type, usableG, totalG, diskUsage));
        }

        return infoList;
    }
}
