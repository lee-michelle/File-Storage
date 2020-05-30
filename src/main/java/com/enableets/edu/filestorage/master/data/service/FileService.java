package com.enableets.edu.filestorage.master.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enableets.edu.filestorage.master.data.dao.FileDAO;
import com.enableets.edu.filestorage.master.data.po.FilePO;

/**
 * 文件信息Service
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Service
public class FileService {

	/**
	 * 文件信息DAO
	 */
	@Autowired
	private FileDAO fileDAO;

	/**
	 * 添加文件上传信息到slave
	 * 
	 * @param filePO
	 * @param fileLocationPO
	 * @return
	 */

	public boolean saveFile(FilePO filePO) {
		fileDAO.insert(filePO);
		return true;
	}

	/**
	 * 获取文件信息
	 * 
	 * @param map
	 * @return
	 */
	public List<FilePO> getFileInfo(Map<String, Object> map) {
		List<FilePO> fileInfo = fileDAO.getFileInfo(map);
		return fileInfo;
	}

	/**
	 * 根据文件标识获取文件信息
	 * 
	 * @param aliasName
	 * @return
	 */
	public FilePO getByFileId(String fileId) {
		FilePO fileInfo = fileDAO.getByFileId(fileId);
		return fileInfo;
	}

	/**
	 * 根据FileName和MD5查询文件信息
	 * 
	 * @param map
	 * @return
	 */
	public List<FilePO> query(Map<String, Object> map) {
		List<FilePO> list = fileDAO.query(map);
		return list;
	}
}
