package com.enableets.edu.filestorage.master.data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * slave
 * 
 * @author pc-psq
 * @since 2018/6/9
 */
@JsonInclude(value = Include.NON_NULL)
public class SlaveInfo {

	/**
	 * slave标识
	 */
	private String slaveId;

	/**
	 * slave名称
	 */
	private String name;

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
	 * 服务器管理URI接口地址
	 */
	private String adminUri;
	/**
	 * 备注
	 */
	private String description;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 读/写标记
	 */
	private String readonly;

	/**
	 * 心跳检测时间
	 */
	private Date lastHeartbeatTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 创建者
	 */
	private String creator;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 更新者
	 */
	private String updator;

	/**
	 * 群组标识
	 */
	private String groupId;

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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getAdminUri() {
		return adminUri;
	}

	public void setAdminUri(String adminUri) {
		this.adminUri = adminUri;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public Date getLastHeartbeatTime() {
		return lastHeartbeatTime;
	}

	public void setLastHeartbeatTime(Date lastHeartbeatTime) {
		this.lastHeartbeatTime = lastHeartbeatTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
