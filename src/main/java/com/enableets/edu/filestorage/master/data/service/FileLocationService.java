package com.enableets.edu.filestorage.master.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enableets.edu.filestorage.master.data.dao.FileLocationDAO;
import com.enableets.edu.filestorage.master.data.po.FileLocationPO;

/**
 * 文件存储位置Service
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Service
public class FileLocationService {
	/**
	 * 文件存储位置DAO
	 */
	@Autowired
	private FileLocationDAO fileLocationDAO;

	/**
	 * 根据fileId查询文件存储信息
	 * 
	 * @param fileId
	 * @return
	 */
	public List<FileLocationPO> getByFileId(String fileId) {
		List<FileLocationPO> list = fileLocationDAO.getByFileId(fileId);
		return list;
	}

	/**
	 * 添加文件位置信息
	 * 
	 * @param fileLocationPO
	 * @return
	 */
	public boolean add(FileLocationPO fileLocationPO) {
		fileLocationDAO.insert(fileLocationPO);
		return true;
	}
}
