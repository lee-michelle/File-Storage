package com.enableets.edu.filestorage.master.policy;

/**
 * Slave
 * 
 * @author lemon
 * @since 2018/6/9
 */
public class Slave {
	/**
	 * slave标识
	 */
	private String slaveId;

	/**
	 * 写文件流URI接口地址
	 */
	private String writeUri;

	/**
	 * 读文件流URI接口地址
	 */
	private String readUri;

	/**
	 * 同步文件流URI接口地址
	 */
	private String syncUri;

	/**
	 * 最后活动时间
	 */
	private long lastActiveTime = 0L;

	public String getSlaveId() {
		return slaveId;
	}

	public void setSlaveId(String slaveId) {
		this.slaveId = slaveId;
	}

	public String getWriteUri() {
		return writeUri;
	}

	public void setWriteUri(String writeUri) {
		this.writeUri = writeUri;
	}

	public String getReadUri() {
		return readUri;
	}

	public void setReadUri(String readUri) {
		this.readUri = readUri;
	}

	public String getSyncUri() {
		return syncUri;
	}

	public void setSyncUri(String syncUri) {
		this.syncUri = syncUri;
	}

	public long getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

}
