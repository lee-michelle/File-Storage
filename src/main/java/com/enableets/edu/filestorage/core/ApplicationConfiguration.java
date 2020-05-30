package com.enableets.edu.filestorage.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 部署配置信息读取
 * 
 * @author lemon
 * @since 2018/6/9
 *
 */
@Component
@ConfigurationProperties()
public class ApplicationConfiguration {
	/**
	 * 根路径
	 */
	@Value("${storage.slave.repository}")
	private String repository;

	/**
	 * master请求路径
	 */
	@Value("${storage.slave.masterUrl}")
	private String masterUrl;

	/**
	 * 某个时间点线程最大连接数
	 */
	@Value("${storage.slave.maxWorkThread}")
	private Integer maxWorkThread;

	/**
	 * slaveId标识
	 */
	@Value("${storage.slave.slaveId}")
	private String slaveId;

	/**
	 * 发送消息队列接口地址
	 */
	@Value("${storage.slave.workQueue.putUrl}")
	private String putUrl;

	/**
	 * 更新任务状态Url
	 */
	@Value("${storage.slave.workQueue.statusUrl}")
	private String statusUrl;

	@Value("${storage.slave.workQueue.clientId}")
	private String clientId;

	/**
	 * 预估当前任务执行所花费的时间
	 */
	@Value("${storage.slave.estimateCostTime}")
	private String estimateCostTime;

	/**
	 * 用于设置是否将上传文件以临时文件的形式保存在磁盘的临界值（以字节为单位的int值）
	 */
	@Value("${storage.slave.sizeThreshold}")
	private Integer sizeThreshold;

	/**
	 * 开关控制是否开启同步
	 */
	@Value("${storage.master.sync}")
	private boolean sync;

	/**
	 * 心跳检测时间间隔
	 */
	@Value("${storage.health.heartbeat.interval}")
	private Integer heartbeatInterval;

	/**
	 * 每隔15秒扫描服务列表，单位秒；没有配置此参数或是配置小于等于0，则不启动
	 */
	@Value("${storage.health.evictionInterval}")
	private Integer evictionInterval;

	/**
	 * 120秒还未收到心跳的话，就将该服务设置为失效，单位秒
	 */
	@Value("${storage.health.leaseExpirationDuration}")
	private Integer leaseExpirationDuration;

	/**
	 * 文件上传临时存储目录
	 */
	@Value("${storage.slave.tmpDir}")
	private String tmpDir;

	public String getMasterUrl() {
		return masterUrl;
	}

	public void setMasterUrl(String masterUrl) {
		this.masterUrl = masterUrl;
	}

	public Integer getMaxWorkThread() {
		return maxWorkThread;
	}

	public void setMaxWorkThread(Integer maxWorkThread) {
		this.maxWorkThread = maxWorkThread;
	}

	/**
	 * @return the slaveId
	 */
	public String getSlaveId() {
		return slaveId;
	}

	/**
	 * @param slaveId
	 *            the slaveId to set
	 */
	public void setSlaveId(String slaveId) {
		this.slaveId = slaveId;
	}

	public String getPutUrl() {
		return putUrl;
	}

	public void setPutUrl(String putUrl) {
		this.putUrl = putUrl;
	}

	public String getStatusUrl() {
		return statusUrl;
	}

	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public String getEstimateCostTime() {
		return estimateCostTime;
	}

	public void setEstimateCostTime(String estimateCostTime) {
		this.estimateCostTime = estimateCostTime;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public Integer getSizeThreshold() {
		return sizeThreshold;
	}

	public void setSizeThreshold(Integer sizeThreshold) {
		this.sizeThreshold = sizeThreshold;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Integer getHeartbeatInterval() {
		return heartbeatInterval;
	}

	public void setHeartbeatInterval(Integer heartbeatInterval) {
		this.heartbeatInterval = heartbeatInterval;
	}

	public Integer getEvictionInterval() {
		return evictionInterval;
	}

	public void setEvictionIntervalTimer(Integer evictionInterval) {
		this.evictionInterval = evictionInterval;
	}

	public Integer getLeaseExpirationDuration() {
		return leaseExpirationDuration;
	}

	public void setLeaseExpirationDuration(Integer leaseExpirationDuration) {
		this.leaseExpirationDuration = leaseExpirationDuration;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

}