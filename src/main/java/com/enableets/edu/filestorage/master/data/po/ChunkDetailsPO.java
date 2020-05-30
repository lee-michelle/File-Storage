package com.enableets.edu.filestorage.master.data.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "storage_chunk_details")
public class ChunkDetailsPO {
	
	@Id
	@Column(name = "uuid")
	private String uuid;
	
	@Id
	@Column(name = "position")
	private Integer position;
	
	@Column(name = "file_md5")
	private String fileMd5;

	@Column(name = "chunk_size")
	private long chunkSize;

	@Column(name = "size")
	private Long size;

	@Column(name = "chunk_md5")
	private String chunkMd5;

	@Column(name = "create_time")
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
