package com.enableets.edu.filestorage.master.data.dao;

import java.util.List;
import java.util.Map;

import com.enableets.edu.filestorage.master.data.po.FilePO;
import com.enableets.edu.framework.core.dao.BaseDao;

/**
 * 文件信息DAO
 * 
 * @author lemon
 * @since 2018/6/9
 *
 */
public interface FileDAO extends BaseDao<FilePO> {

	/**
	 * 添加文件信息
	 * 
	 * @param filePO
	 * @return
	 */
	public int add(FilePO filePO);

	/**
	 * 根据slaveId查询读取文件的地址
	 * 
	 * @param slaveId
	 * @return
	 */
	public String getReadUrl(String slaveId);

	/**
	 * 根据MD5码和fileName查询文件信息（参数可选）
	 * 
	 * @param md5
	 * @param fileName
	 * @return
	 */
	public List<FilePO> query(Map<String, Object> map);

	/**
	 * 根据fileId查询文件信息
	 * 
	 * @param fileId
	 * @return
	 */

	public FilePO getByFileId(String fileId);

	/**
	 * 根据fileId、aliasName查询文件信息
	 * 
	 * @param map
	 * @return
	 */
	public List<FilePO> getFileInfo(Map<String, Object> map);

}
