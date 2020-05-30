package com.enableets.edu.filestorage.master.data.dao;

import java.util.List;

import com.enableets.edu.filestorage.master.data.po.FileLocationPO;
import com.enableets.edu.framework.core.dao.BaseDao;

/**
 * 文件存储地址DAO
 * 
 * @author lemon
 * 
 * @since 2018/6/9
 *
 */
public interface FileLocationDAO extends BaseDao<FileLocationPO> {

	/**
	 * 根据fileId查询读取文件的地址
	 * 
	 * @param fileId
	 * @return
	 */
	public List<FileLocationPO> getByFileId(String fileId);

}
