package com.enableets.edu.filestorage.master.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 文件信息
 * 
 * @author lemon
 * @since 2018/6/9
 */
@JsonInclude(value = Include.NON_NULL)
public class FileInfo {
	/**
	 * 文件标识
	 */
	private String fileId;

	/**
	 * slave标识
	 */
	private String slaveId;

	/**
	 * 文件唯一编码，对外用这个字段
	 */
	private String uuid;

	/**
	 * 文件名称
	 */
	private String name;

	/**
	 * 兼容2.0别名
	 */
	private String aliasName;

	/**
	 * 文件大小
	 */
	private Long size;

	/**
	 * 格式化后文件大小
	 */
	private String sizeDisplay;

	/**
	 * 后缀名
	 */
	private String ext;

	/**
	 * MD5码
	 */
	private String md5;

	/**
	 * 状态
	 */
	private String status;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 编码格式
	 */
	private String encoding;

	/**
	 * 上传时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以将该格式字符串转换为date
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以控制代码中时间为0点
	private Date uploadTime;

	/**
	 * 更新时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以将该格式字符串转换为date
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 可以控制代码中时间为0点
	private Date updateTime;

	/**
	 * 文件存储位置集合
	 */
	private List<Location> locations;

	/**
	 * 文件分片信息
	 */
	private Map<String, Object> params = new HashMap<String, Object>();

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

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
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

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
