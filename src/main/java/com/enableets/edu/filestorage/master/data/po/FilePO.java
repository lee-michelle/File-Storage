package com.enableets.edu.filestorage.master.data.po;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 文件信息PO
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Entity
@Table(name = "storage_file")
public class FilePO {
	/**
	 * 
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
	 * 文件唯一编码，对外用这个字段
	 */
	@Column(name = "uuid")
	private String uuid;

	/**
	 * 文件名称
	 */
	@Column(name = "name")
	private String name;

	/**
	 * 兼容2.0别名
	 */
	@Column(name = "alias_name")
	private String aliasName;

	/**
	 * 文件大小
	 */
	@Column(name = "size")
	private long size;

	/**
	 * 格式化后的文件大小
	 */
	@Column(name = "size_display")
	private String sizeDisplay;

	/**
	 * 文件名后缀
	 */
	@Column(name = "ext")
	private String ext;

	/**
	 * md5码
	 */
	@Column(name = "md5")
	private String md5;

	/**
	 * 状态
	 */
	@Column(name = "status")
	private String status;

	/**
	 * 描述
	 */
	@Column(name = "description")
	private String description;

	/**
	 * 文件上传时间
	 */
	@Column(name = "upload_time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以将该格式字符串转换为date
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以控制代码中时间为0点
	private Date uploadTime;

	/**
	 * 更新时间
	 */
	@Column(name = "update_time")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以将该格式字符串转换为date
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以控制代码中时间为0点
	private Date updateTime;

	/**
	 * 文件存储位置集合
	 */
	@Transient
	private List<FileLocationPO> locations;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSizeDisplay() {
		return sizeDisplay;
	}

	public void setSizeDisplay(String sizeDisplay) {
		this.sizeDisplay = sizeDisplay;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSlaveId() {
		return slaveId;
	}

	public void setSlaveId(String slaveId) {
		this.slaveId = slaveId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public List<FileLocationPO> getLocations() {
		return locations;
	}

	public void setLocations(List<FileLocationPO> locations) {
		this.locations = locations;
	}

}
