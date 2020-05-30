package com.enableets.edu.filestorage.master.data;

import java.util.Date;

public class ChunkDetails {

	private String uuid;
	
	private String fileMd5;

	private long chunkSize;

	private Integer position;

	private Long size;

	private String chunkMd5;

	private Date createTime;

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public long getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(long chunkSize) {
		this.chunkSize = chunkSize;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getChunkMd5() {
		return chunkMd5;
	}

	public void setChunkMd5(String chunkMd5) {
		this.chunkMd5 = chunkMd5;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
