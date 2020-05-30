package com.enableets.edu.filestorage.bean;

public class CpuInfoBean {
	/**
	 * CPU生产商
	 */
	private String vendor;
	/**
	 * CPU类别
	 */
	private String model;
	/**
	 * CPU总量
	 */
	private int mhz;
	/**
	 * 缓冲存储器数量
	 */
	private long cacheSize = 0L;

	/**
	 * CPU拥护使用率
	 */
	private double user;
	/**
	 * CPU系统使用率
	 */
	private double sys;
	/**
	 * CPU当前错误率
	 */
	private double nice;
	/**
	 * CPU当前空闲率
	 */
	private double idle;
	/**
	 * CPU当前等待率
	 */
	private double wait;
	/**
	 * CPU总的使用率
	 */
	private double combined;

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getMhz() {
		return mhz;
	}

	public void setMhz(int mhz) {
		this.mhz = mhz;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

	public double getUser() {
		return user;
	}

	public void setUser(double user) {
		this.user = user;
	}

	public double getSys() {
		return sys;
	}

	public void setSys(double sys) {
		this.sys = sys;
	}

	public double getNice() {
		return nice;
	}

	public void setNice(double nice) {
		this.nice = nice;
	}

	public double getIdle() {
		return idle;
	}

	public void setIdle(double idle) {
		this.idle = idle;
	}

	public double getWait() {
		return wait;
	}

	public void setWait(double wait) {
		this.wait = wait;
	}

	public double getCombined() {
		return combined;
	}

	public void setCombined(double combined) {
		this.combined = combined;
	}

}
