package com.enableets.edu.filestorage.master.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enableets.edu.filestorage.master.data.dao.ChunkDetailsDAO;
import com.enableets.edu.filestorage.master.data.po.ChunkDetailsPO;

@Service
public class ChunkInfoDetailService {

	@Autowired
	private ChunkDetailsDAO chunkInfoDetailDAO;

	public boolean add(ChunkDetailsPO po) {
		chunkInfoDetailDAO.insert(po);
		return true;
	}

	public ChunkDetailsPO getChunkDetailsById(String uuid, int position) {
		ChunkDetailsPO condition = new ChunkDetailsPO();
		condition.setUuid(uuid);
		condition.setPosition(position);
		ChunkDetailsPO chunkInfoDetailPO = chunkInfoDetailDAO.selectOne(condition);
		return chunkInfoDetailPO;
	}
}
