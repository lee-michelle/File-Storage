package com.enableets.edu.filestorage.master.data.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 文件存储位置PO
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Entity
@Table(name = "storage_file_location")
public class FileLocationPO {
	/**
	 * 文件标识
	 */
	@Id
	@Column(name = "file_id")
	private String fileId;

	/**
	 * slave标识
	 */
	@Id
	@Column(name = "slave_id")
	private String slaveId;

	/**
	 * 存储路径
	 */
	@Column(name = "path")
	private String path;

	/**
	 * 状态
	 */
	@Column(name = "status")
	private String status;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@Column(name = "update_time")
	private Date updateTime;

	/**
	 * slave名称
	 */
	@Transient
	private String slaveName;

	/**
	 * 群组标识
	 */
	@Transient
	private String groupId;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getSlaveId() {
		return slaveId;
	}

	public void setSlaveId(String slaveId) {
		this.slaveId = slaveId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getSlaveName() {
		return slaveName;
	}

	public void setSlaveName(String slaveName) {
		this.slaveName = slaveName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
