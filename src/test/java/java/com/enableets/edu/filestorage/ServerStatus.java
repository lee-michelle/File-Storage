package com.enableets.edu.filestorage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hyperic.jni.ArchNotSupportedException;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SigarLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.enableets.edu.filestorage.bean.CpuInfoBean;
import com.enableets.edu.filestorage.util.BeanUtils;
import com.enableets.edu.framework.core.util.JsonUtils;

/**
 * Hello world!
 *
 */
public class ServerStatus {

	/**
	 * 初始化sigar的配置文件
	 * 
	 * @throws IOException
	 * @throws SigarException
	 */
	private static void initSigar() throws IOException, SigarException {
		SigarLoader loader = new SigarLoader(Sigar.class);
		String lib = null;

		try {
			lib = loader.getLibraryName();
		} catch (ArchNotSupportedException var7) {
			var7.printStackTrace();
		}
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("classpath:sigar/" + lib);

		if (resource.exists()) {
			InputStream is = resource.getInputStream();
			File tempDir = FileUtils.getTempDirectory();

			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(tempDir, lib), false));

			org.springframework.util.FileCopyUtils.copy(is, os);

			is.close();
			os.close();
			System.setProperty("org.hyperic.sigar.path", tempDir.getCanonicalPath());
		}

		System.out.println(System.getProperty("java.library.path"));

		System.out.println(getCpu());
	}

	/**
	 * 获取CPU信息
	 * 
	 * @return
	 * @throws SigarException
	 */
	private static String getCpu() throws SigarException {
		Sigar sigar = new Sigar();
		List<CpuInfoBean> list = new ArrayList<CpuInfoBean>();
		// CPU的总量（单位：HZ）及CPU的相关信息
		CpuInfo infos[] = sigar.getCpuInfoList();
		CpuPerc cpuList[] = null;
		String cpuInfo[] = null;
		String userdCpu[] = null;
		cpuList = sigar.getCpuPercList();// 使用率
		userdCpu = new String[infos.length];
		cpuInfo = new String[infos.length];
		for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
			CpuInfo info = infos[i];
			CpuInfoBean bean = BeanUtils.convert(info, CpuInfoBean.class);
			// 当前CPU的用户使用率、系统使用率、当前等待率、当前空闲率、总的使用率
			bean.setWait(cpuList[i].getWait());
			bean.setUser(cpuList[i].getUser());
			bean.setSys(cpuList[i].getSys());
			bean.setNice(cpuList[i].getNice());
		}
		return JsonUtils.convert(cpuInfo);
	}

	/**
	 * 静态工具类：获取当前CPU的用户使用率、系统使用率、当前等待率、当前空闲率、总的使用率
	 * 
	 * @param cpu：当前CPU
	 */
	private static String printCpuPerc(CpuPerc cpu) {
		String usedCpu;
		usedCpu = String.format(Constants.USED_CPU_MESSAGE, CpuPerc.format(cpu.getUser()), CpuPerc.format(cpu.getSys()), CpuPerc.format(cpu.getWait()),
				CpuPerc.format(cpu.getNice()), CpuPerc.format(cpu.getIdle()), CpuPerc.format(cpu.getCombined()));

		return usedCpu.toString();
	}

	//
	/**
	 * 获取服务器内存
	 * 
	 * @return
	 * @throws SigarException
	 */
	private static Object[] getMemory() throws SigarException {
		Sigar sigar = new Sigar();
		// 物理内存信息
		Mem mem = sigar.getMem();
		// 内存总量
		System.out.println("内存总量:    " + mem.getTotal() / 1024L + "K av");
		// 当前内存使用量
		System.out.println("当前内存使用量:    " + mem.getUsed() / 1024L + "K used");
		Object[] object = new Object[] { mem.getTotal() / 1024L, mem.getUsed() / 1024L };
		System.out.println(object[0]);
		return object;
	}
	/*
	 * public static String getOS() { String osMessage =
	 * String.format(Constants.OS_MESSAGE, OS.getArch(), OS.getName(),
	 * OS.getVersion()); return osMessage; }
	 */

	// 获取JDK的版本
	public static String getJDKVersion() {
		// jdk版本
		System.out.println("jvmversion:" + System.getProperties().getProperty("java.version"));

		return System.getProperties().getProperty("java.version");
	}

	// 获取JVM内存(完成)
	public static Object[] getJVM() {
		Sigar sigar = new Sigar();
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		Object[] object = null;
		String pid = runtime.getName().split("@")[0];
		try {
			float totalMem = sigar.getMem().getTotal() / 1024 / 1024;// JVM总内存
			float usedMem = sigar.getProcMem(pid).getResident() / 1024 / 1024; // JVM用户使用内存
			object = new Object[] { totalMem, usedMem };
		} catch (SigarException e) {
			e.printStackTrace();
		}
		return object;
	}

	// 系统运行时长
	public static String getRunningTime() {
		// 系统运行时间
		Sigar sigar = new Sigar();
		long pid = sigar.getPid();
		long startTime = 0;
		try {
			startTime = sigar.getProcTime(pid).getStartTime();
		} catch (SigarException e) {
			e.printStackTrace();
		}

		// System.out.println("runningTime:" +
		// friendDuration(System.currentTimeMillis() - startTime));
		return String.valueOf(Utils.friendDuration(System.currentTimeMillis() - startTime));
	}

	/**
	 * 获取 网卡信息 * @return
	 */
	public static String getNetcardInfo() {
		Sigar sigar = new Sigar();
		String[] netInterfaceList;
		String[] result = null;
		try {
			netInterfaceList = sigar.getNetInterfaceList();
			result = new String[netInterfaceList.length];
			for (int i = 0; i < netInterfaceList.length; i++) {
				String netInterface = netInterfaceList[i];// 网络接口
				NetInterfaceConfig netInterfaceConfig = sigar.getNetInterfaceConfig(netInterface);
				result[i] = String.format(Constants.NET_MESSAGE, i + 1, netInterfaceConfig.getAddress(), netInterfaceConfig.getNetmask());
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}

		// 获取网络流量信息
		return JsonUtils.convert(result);
	}

}
