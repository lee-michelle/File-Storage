package com.enableets.edu.filestorage.master.data.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * slave PO
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Entity
@Table(name = "storage_slave")
public class SlavePO {
	/**
	 * Slave标识
	 */
	@Id
	@Column(name = "slave_id")
	private String slaveId;
	/**
	 * 名称
	 */
	@Column(name = "name")
	private String name;
	/**
	 * 读文件流URI接口地址
	 */
	@Column(name = "read_uri")
	private String readUri;
	/**
	 * 写文件流URI接口地址
	 */
	@Column(name = "write_uri")
	private String writeUri;

	/**
	 * 同步地址
	 */
	@Column(name = "sync_uri")
	private String syncUri;

	/**
	 * 服务器管理URI接口地址
	 */
	@Column(name = "admin_uri")
	private String adminUri;
	/**
	 * 备注
	 */
	@Column(name = "description")
	private String description;
	/**
	 * 状态
	 */
	@Column(name = "status")
	private String status;
	/**
	 * 读/写标记
	 */
	@Column(name = "readonly")
	private String readonly;

	/**
	 * 心跳检测时间
	 */
	@Column(name = "last_heartbeat_time")
	private Date lastHeartbeatTime;
	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Date createTime;
	/**
	 * 创建者
	 */
	@Column(name = "creator")
	private String creator;
	/**
	 * 更新时间
	 */
	@Column(name = "update_time")
	private Date updateTime;
	/**
	 * 更新者
	 */
	@Column(name = "updator")
	private String updator;

	@Transient
	private String groupId;

	public String getSlaveId() {
		return slaveId;
	}

	public void setSlaveId(String slaveId) {
		this.slaveId = slaveId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReadUri() {
		return readUri;
	}

	public void setReadUri(String readUri) {
		this.readUri = readUri;
	}

	public String getWriteUri() {
		return writeUri;
	}

	public void setWriteUri(String writeUri) {
		this.writeUri = writeUri;
	}

	public String getAdminUri() {
		return adminUri;
	}

	public void setAdminUri(String adminUri) {
		this.adminUri = adminUri;
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getUpdator() {
		return updator;
	}

	public void setUpdator(String updator) {
		this.updator = updator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getLastHeartbeatTime() {
		return lastHeartbeatTime;
	}

	public void setLastHeartbeatTime(Date lastHeartbeatTime) {
		this.lastHeartbeatTime = lastHeartbeatTime;
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

}
