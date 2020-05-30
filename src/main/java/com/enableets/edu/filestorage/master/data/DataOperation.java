package com.enableets.edu.filestorage.master.data;

import java.util.List;
import java.util.Map;

/**
 * 数据处理器
 * 
 * @author lemon
 * @since 2018/6/8
 *
 */
public interface DataOperation {
	/**
	 * 获取所有文件服务器
	 * 
	 * @param map
	 * @return
	 */
	List<SlaveInfo> getAllSlaveServer();
	
	/**
	 * 获取所有文件服务器
	 * @param includeDead
	 */
	List<SlaveInfo> getAllSlaveServer(boolean includeDead);
	
	/**
	 * 获取所有文件服务器
	 * 
	 * @param map
	 * @return
	 */
	List<SlaveInfo> getUploadSlaveServer();

	/**
	 * 上传完毕之后，根据上传服务器，获取待同步的文件服务器
	 * 
	 * @param slaveId
	 * @return
	 */
	List<SlaveInfo> getStandbySlaveServer(String slaveId);

	/**
	 * 通过文件标识获取文件信息
	 * 
	 * @param fileId
	 * @return
	 */
	FileInfo getFileInfoById(String fileId);

	/**
	 * 通过文件别名获取文件信息
	 * 
	 * @param condition
	 * @return
	 */
	List<FileInfo> queryFileInfo(Map<String, Object> condition);

	/**
	 * 获文件存储位置集合
	 * 
	 * @param fileId
	 * @return
	 */
	List<Location> getFileSavedLocation(String fileId);

	/**
	 * 新增文件存储位置
	 * 
	 * @param location
	 * @return
	 */
	boolean addFileSavedLocation(Location location);

	/**
	 * 新增文件
	 * 
	 * @param fileInfo
	 * @return
	 */
	boolean addFileInfo(FileInfo fileInfo);

	/**
	 * 根据fileName和md5查询文件信息
	 * 
	 * @param condition
	 * @return
	 */
	List<FileInfo> query(Map<String, Object> condition);

	/**
	 * 更新slave心跳时间
	 * 
	 * @param slaveId
	 * @param lastHeartbeatCheckTime
	 * @return
	 */
	boolean updateHeartBeatTime(Map<String, Object> condition);
	
	/**
	 * 合并分片
	 * @param fileMd5
	 * @param chunkSize
	 * @param filename
	 */
	FileInfo mergeChunk(String uuid, String filename);

	public boolean updateStatusAndReadonly(SlaveInfo slaveInfo);

	public SlaveInfo getSlaveById(String slaveId);

	public boolean addChunk(ChunkInfo chunkInfo);
	
	public boolean addChunkDetails(ChunkDetails chunkInfoDetail);

	ChunkInfo getChunkById(String uuid);
	
	List<Integer> getAllChunkDetails(String uuid);

	ChunkDetails getChunkDetailsById(String uuid, int position);
}
