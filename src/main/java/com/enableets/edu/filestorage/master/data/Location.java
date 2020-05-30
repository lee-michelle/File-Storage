package com.enableets.edu.filestorage.master.data;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 文件存储位置
 * 
 * @author lemon
 * @since 2018/6/9
 */
@JsonInclude(value = Include.NON_NULL)
public class Location {
	/**
	 * 文件标识
	 */
	private String fileId;

	/**
	 * slave标识
	 */
	private String slaveId;

	/**
	 * 文件存储路径
	 */
	private String path;

	/**
	 * 文件状态
	 */
	private String status;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以将该格式字符串转换为date
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以控制代码中时间为0点
	private Date createTime;

	/**
	 * 创建者
	 */
	private String creator;

	/**
	 * 更新时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以将该格式字符串转换为date
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以控制代码中时间为0点
	private Date updateTime;

	/**
	 * 更新时间
	 */
	private String updator;

	/**
	 * slave名称
	 */
	private String slaveName;

	/**
	 * 群组标识
	 */
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
