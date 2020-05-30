package com.enableets.edu.filestorage.master.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.master.data.po.ChunkDetailsPO;
import com.enableets.edu.filestorage.master.data.po.ChunkInfoPO;
import com.enableets.edu.filestorage.master.data.po.FileLocationPO;
import com.enableets.edu.filestorage.master.data.po.FilePO;
import com.enableets.edu.filestorage.master.data.po.SlavePO;
import com.enableets.edu.filestorage.master.data.service.ChunkInfoDetailService;
import com.enableets.edu.filestorage.master.data.service.ChunkService;
import com.enableets.edu.filestorage.master.data.service.FileLocationService;
import com.enableets.edu.filestorage.master.data.service.FileService;
import com.enableets.edu.filestorage.master.data.service.SlaveService;
import com.enableets.edu.filestorage.util.BeanUtils;

/**
 * 数据处理器实现
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Service
public class DefaultDataOperator implements DataOperation {

	/**
	 * Slave Service
	 */
	@Autowired
	private SlaveService slaveService;

	/**
	 * 文件Service
	 */
	@Autowired
	private FileService fileService;

	/**
	 * 文件存储位置Service
	 */
	@Autowired
	private FileLocationService fileLocationService;

	@Autowired
	private ChunkService chunkService;

	@Autowired
	private ChunkInfoDetailService chunkInfoDetailService;

	/**
	 * 获取所有文件服务器
	 */
	@Override
	public List<SlaveInfo> getAllSlaveServer() {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("status", "ON");
		List<SlavePO> list = slaveService.query(condition);
		if (!list.isEmpty()) {
			return BeanUtils.convert(list, SlaveInfo.class);
		}
		return Collections.emptyList();
	}
	
	/**
	 * 获取所有文件服务器
	 */
	@Override
	public List<SlaveInfo> getAllSlaveServer(boolean includeDead) {
		Map<String, Object> condition = new HashMap<String, Object>();
		if (!includeDead) {
			condition.put("status", "ON");
		}
		List<SlavePO> list = slaveService.query(condition);
		if (!list.isEmpty()) {
			return BeanUtils.convert(list, SlaveInfo.class);
		}
		return Collections.emptyList();
	}

	@Override
	public List<SlaveInfo> getUploadSlaveServer() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("readonly", "N");
		List<SlavePO> list = slaveService.query(map);
		if (!list.isEmpty()) {
			return BeanUtils.convert(list, SlaveInfo.class);
		}
		return Collections.emptyList();
	}

	/**
	 * 上传完毕之后，根据上传服务器，获取待同步的文件服务器
	 */
	@Override
	public List<SlaveInfo> getStandbySlaveServer(String slaveId) {
		Map<String, Object> condition = new HashMap<>();
		condition.put("slaveId", slaveId);
		List<SlavePO> list = slaveService.selectStandbySlave(condition);
		if (!list.isEmpty()) {
			return BeanUtils.convert(list, SlaveInfo.class);
		}
		return Collections.emptyList();
	}

	/**
	 * 通过文件标识获取文件信息
	 */
	@Override
	public FileInfo getFileInfoById(String fileId) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.FIELD_NAME_FILE_ID, fileId);
		FilePO filePO = fileService.getByFileId(fileId);
		if (filePO != null) {
			return BeanUtils.convert(filePO, FileInfo.class);
		}
		return null;
	}

	/**
	 * 通过fileId 或aliasName获取文件信息
	 */
	@Override
	public List<FileInfo> queryFileInfo(Map<String, Object> condition) {
		List<FilePO> filePO = fileService.getFileInfo(condition);
		if (filePO != null) {
			return BeanUtils.convert(filePO, FileInfo.class);
		}
		return Collections.emptyList();
	}

	/**
	 * 根据fileId查询文件存储信息
	 */
	@Override
	public List<Location> getFileSavedLocation(String fileId) {
		List<FileLocationPO> list = fileLocationService.getByFileId(fileId);
		if (!list.isEmpty()) {
			return BeanUtils.convert(list, Location.class);
		}
		return Collections.emptyList();
	}

	/**
	 * 添加文件存储信息
	 */
	@Override
	public boolean addFileSavedLocation(Location location) {
		boolean success = fileLocationService.add(BeanUtils.convert(location, FileLocationPO.class));
		return success;
	}

	/**
	 * 添加文件
	 */
	@Override
	public boolean addFileInfo(FileInfo fileInfo) {
		boolean success = fileService.saveFile(BeanUtils.convert(fileInfo, FilePO.class));
		return success;
	}

	/**
	 * 查询文件信息
	 */
	@Override
	public List<FileInfo> query(Map<String, Object> condition) {
		List<FilePO> list = fileService.query(condition);
		if (!list.isEmpty()) {
			return BeanUtils.convert(list, FileInfo.class);
		}
		return Collections.emptyList();
	}

	/**
	 * 更新slave心跳时间
	 */
	@Override
	public boolean updateHeartBeatTime(Map<String, Object> condition) {
		boolean success = slaveService.updateHeartBeatTime(condition);
		return success;
	}

	/**
	 * 更新服务状态和readonly状态
	 */
	@Override
	public boolean updateStatusAndReadonly(SlaveInfo slaveInfo) {
		slaveService.updateStatusAndReadonly(slaveInfo);
		return true;
	}

	/**
	 * 根据slaveId查询slave信息
	 * 
	 * @param slaveId
	 * @return
	 */
	@Override
	public SlaveInfo getSlaveById(String slaveId) {
		SlavePO slavePO = slaveService.getSlaveById(slaveId);
		if (slavePO != null) {
			return BeanUtils.convert(slavePO, SlaveInfo.class);
		}
		return null;
	}

	@Override
	public boolean addChunk(ChunkInfo chunkInfo) {
		chunkService.add(BeanUtils.convert(chunkInfo, ChunkInfoPO.class));
		return true;
	}
	
	@Override
	public boolean addChunkDetails(ChunkDetails chunkInfoDetail) {
		chunkInfoDetailService.add(BeanUtils.convert(chunkInfoDetail, ChunkDetailsPO.class));
		return true;
	}

	@Override
	public ChunkInfo getChunkById(String uuid) {
		ChunkInfoPO po = chunkService.getChunkById(uuid);
		return BeanUtils.convert(po, ChunkInfo.class);
	}
	
	@Override
	public List<Integer> getAllChunkDetails(String uuid) {
		return chunkService.getAllChunk(uuid);
	}

	@Override
	public ChunkDetails getChunkDetailsById(String uuid, int position) {
		ChunkDetailsPO po = chunkInfoDetailService.getChunkDetailsById(uuid, position);
		return BeanUtils.convert(po, ChunkDetails.class);
	}

	@Override
	public FileInfo mergeChunk(String uuid, String filename) {
		Object[] arr = chunkService.merge(uuid, filename);
		FilePO filePO = (FilePO)arr[0];
		
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileId(filePO.getFileId());
		fileInfo.setMd5(filePO.getMd5());
		fileInfo.setSlaveId(filePO.getSlaveId());
		fileInfo.setName(filePO.getName());
		fileInfo.setSize(filePO.getSize());
		fileInfo.setSizeDisplay(filePO.getSizeDisplay());
		fileInfo.setExt(filePO.getExt());
		
		FileLocationPO fileLocationPO = (FileLocationPO)arr[1];
		Location location = new Location();
		location.setFileId(filePO.getFileId());
		location.setPath(fileLocationPO.getPath());
		location.setSlaveId(filePO.getSlaveId());
		
		List<Location> locations = new ArrayList<Location>();
		locations.add(location);
		fileInfo.setLocations(locations);
		
		return fileInfo;
	}

}
