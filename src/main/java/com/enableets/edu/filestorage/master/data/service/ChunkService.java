package com.enableets.edu.filestorage.master.data.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.master.data.dao.ChunkDAO;
import com.enableets.edu.filestorage.master.data.dao.ChunkDetailsDAO;
import com.enableets.edu.filestorage.master.data.dao.FileDAO;
import com.enableets.edu.filestorage.master.data.dao.FileLocationDAO;
import com.enableets.edu.filestorage.master.data.po.ChunkDetailsPO;
import com.enableets.edu.filestorage.master.data.po.ChunkInfoPO;
import com.enableets.edu.filestorage.master.data.po.FileLocationPO;
import com.enableets.edu.filestorage.master.data.po.FilePO;
import com.enableets.edu.filestorage.util.Utils;

import tk.mybatis.mapper.entity.Example;

@Service
public class ChunkService {

	@Autowired
	private ChunkDAO chunkDao;

	@Autowired
	private FileDAO fileDao;
	
	@Autowired
	private FileLocationDAO fileLocationDAO;
	
	@Autowired
	private ChunkDetailsDAO chunkDetailsDAO;
	
	@Autowired
	private ChunkDAO chunkDAO;

	public boolean add(ChunkInfoPO chunk) {
		chunkDao.insert(chunk);
		return true;
	}

	public ChunkInfoPO getChunkById(String uuid) {
		ChunkInfoPO chunkInfoPO = chunkDao.selectByPrimaryKey(uuid);
		return chunkInfoPO;
	}
	
	public List<Integer> getAllChunk(String uuid) {
		Example chunkDetailsExample = new Example(ChunkDetailsPO.class);
		chunkDetailsExample.createCriteria().andEqualTo("uuid", uuid);
		List<ChunkDetailsPO> list = chunkDetailsDAO.selectByExample(chunkDetailsExample);
		Iterator<ChunkDetailsPO> it = list.iterator();
		List<Integer> result = new ArrayList<Integer>(list.size());
		while (it.hasNext()) {
			ChunkDetailsPO po = it.next();
			result.add(po.getPosition());
		}
		return result;
	}
	
	/**
	 * 合并分片
	 */
	@Transactional
	public Object[] merge(String uuid, String filename) {
		// 1.写入文件信息表
		ChunkInfoPO chunkInfoPO = this.getChunkById(uuid);
		FilePO filePO = new FilePO();
		filePO.setMd5(chunkInfoPO.getFileMd5());
		filePO.setExt(FilenameUtils.getExtension(filename));
		filePO.setSize(chunkInfoPO.getFileSize());
		filePO.setSizeDisplay(Utils.getDataSize(chunkInfoPO.getFileSize()));
		filePO.setName(filename);
		filePO.setSlaveId(chunkInfoPO.getSlaveId());
		filePO.setStatus(Constants.FILE_DEFAULT_STATUS);
		filePO.setUpdateTime(chunkInfoPO.getCreateTime());
		filePO.setUpdateTime(Calendar.getInstance().getTime());
		parsePath(chunkInfoPO, filePO);
		fileDao.insert(filePO);
		
		// 2.写入文件存储位置表
		FileLocationPO fileLocationPO = new FileLocationPO();
		fileLocationPO.setFileId(filePO.getFileId());
		fileLocationPO.setSlaveId(filePO.getSlaveId());
		fileLocationPO.setPath(chunkInfoPO.getPath());
		fileLocationPO.setStatus(Constants.FILE_LOCATION_DEFAULT_STATUS);
		fileLocationPO.setCreateTime(Calendar.getInstance().getTime());
		fileLocationPO.setUpdateTime(Calendar.getInstance().getTime());
		fileLocationDAO.insert(fileLocationPO);
		
		// 3.删除文件分片信息表和明细表的资料
		Example chunkDetailsExample = new Example(ChunkDetailsPO.class);
		chunkDetailsExample.createCriteria().andEqualTo("uuid", uuid);
		chunkDetailsDAO.deleteByExample(chunkDetailsExample);
		
		chunkDAO.deleteByPrimaryKey(uuid);
		
		Object[] result = new Object[]{filePO, fileLocationPO};
		return result;
	}
	
	/**
	 * 解析分片的路径
	 * @param chunkInfoPO
	 * @param filePO
	 */
	private void parsePath(ChunkInfoPO chunkInfoPO, FilePO filePO) {
		String path = chunkInfoPO.getPath();
		
		int index = path.lastIndexOf("/") + 1;
		String uuid = path.substring(index);
		filePO.setUuid(uuid);
		filePO.setFileId(uuid);
		
		String prefix = path.substring(0, index);
		String aliasName = prefix + filePO.getName();
		filePO.setAliasName(aliasName);
	}
	
}
