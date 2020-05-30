package com.enableets.edu.filestorage.master.data.po;

/**
 * 文件储存实体
 * 
 * @ClassName: StoreFileBean
 * @author david_tian@wistronits.com
 * @since 2013/9/4
 */

/**
 * 文件储存实体
 * 
 * @ClassName: StoreFileBean
 * @author david_tian@wistronits.com
 * @since 2013/9/4
 */
public class FileBaseInfo {

	/**
	 * 档案的Id
	 */
	protected String fileId;

	/**
	 * 档案名
	 */
	protected String name;

	/**
	 * MD5
	 */
	protected String md5;

	/**
	 * 文件大小
	 */
	protected float size;

	/**
	 * 格式化后文件大小
	 */
	protected String sizeDisplay;

	/**
	 * encoding
	 */
	protected String encoding;

	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}

	/**
	 * @param md5
	 *            the md5 to set
	 */
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSizeDisplay() {
		return sizeDisplay;
	}

	public void setSizeDisplay(String sizeDisplay) {
		this.sizeDisplay = sizeDisplay;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

}
