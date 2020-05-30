package com.enableets.edu.filestorage.master.restful;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.HttpClientConnectionManager;
import com.enableets.edu.filestorage.master.data.ChunkInfo;
import com.enableets.edu.filestorage.master.data.DataOperation;
import com.enableets.edu.filestorage.master.data.FileInfo;
import com.enableets.edu.filestorage.master.data.Location;
import com.enableets.edu.filestorage.master.data.SlaveInfo;
import com.enableets.edu.filestorage.master.data.po.FileBaseInfo;
import com.enableets.edu.filestorage.util.BeanUtils;
import com.enableets.edu.filestorage.util.Utils;
import com.enableets.edu.module.service.controller.ServiceControllerAdapter;
import com.enableets.edu.module.service.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 文件分片接口
 * @author jim_shi@enable-ets.com
 * @since 2018/06/16
 */
@Api(tags = "(3)" + "文件分片", description = "文件分片")
@RestController
@RequestMapping("/storage/chunk")
public class ChunkRestful extends ServiceControllerAdapter<FileInfo> {
	
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ChunkRestful.class);
	
	/**
	 * 数据处理器
	 */
	@Autowired
	private DataOperation dataOperator;
	
	/**
	 * 配置文件读取
	 */
	@Autowired
	private ApplicationConfiguration config;
	
	@Autowired
	private HttpClientConnectionManager httpClientConnectionManager;
	
	@ApiOperation(value = "合并文件块", notes = "合并文件块")
	@RequestMapping(value = "/merge", method = RequestMethod.GET)
	public Response<FileBaseInfo> merge(@ApiParam(value = "文件分片唯一标识", required = true) String uuid,
			@ApiParam(value = "文件名", required = true) String filename) {
		
		// 1.处理分片数据
		FileInfo fileInfo = dataOperator.mergeChunk(uuid, filename);
		
		// 2.发送文件同步消息
		if (config.isSync()) { // 如果打开了文件同步开关
			// 根据上传服务器，获取待同步的文件服务器
			List<SlaveInfo> list = dataOperator.getStandbySlaveServer(fileInfo.getSlaveId());
			if (!list.isEmpty()) {
				for (SlaveInfo slaveInfo : list) {
					String taskId = Utils.buildUUID();
					String slaveId = fileInfo.getSlaveId();
					Location location = fileInfo.getLocations().iterator().next();
					String path = location.getPath();
					CloseableHttpClient httpClient = httpClientConnectionManager.getHttpClient();
					
					Object[] data = new Object[] { taskId, slaveId, fileInfo.getName(), 
							config.getEstimateCostTime(), slaveInfo.getSyncUri(),
							fileInfo.getFileId(), path, fileInfo.getMd5() };
					
					try {
						Utils.sendFileSyncRequest(httpClient, config.getPutUrl(), data);
					} catch (MalformedURLException e) {
						LOGGER.error("send file sync request error for url is malformed", e);
					}
				}
			}
		}
		
		FileBaseInfo fileBaseInfo = BeanUtils.convert(fileInfo, FileBaseInfo.class);
		return responseTemplate.format(fileBaseInfo);
	}
	
	@ApiOperation(value = "查询文件分片", notes = "查询文件分片")
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Response<CheckChunkResult> check(@ApiParam(value = "文件分片唯一标识", required = true) String uuid) {
		ChunkInfo chunkInfo = dataOperator.getChunkById(uuid);
		List<Integer> allChunk = dataOperator.getAllChunkDetails(uuid);
		
		CheckChunkResult checkChunkResult = new CheckChunkResult();
		checkChunkResult.setUuid(uuid);
		checkChunkResult.setFileMd5(chunkInfo.getFileMd5());
		checkChunkResult.setChunks(allChunk);
		checkChunkResult.setFileSize(chunkInfo.getFileSize());
		checkChunkResult.setChunkSize(chunkInfo.getChunkSize());
		
		// 计算应该有多少个分片
		int chunkNum = Long.valueOf(chunkInfo.getFileSize() / chunkInfo.getChunkSize()).intValue();
		if (chunkInfo.getFileSize() % chunkInfo.getChunkSize() > 0) {
			chunkNum ++;
		}
		checkChunkResult.setCompletion(chunkNum == allChunk.size());
		
		return responseTemplate.format(checkChunkResult);
	}
	
	private static class CheckChunkResult {
		private String uuid;
		
		private String fileMd5;
		
		private List<Integer> chunks;
		
		private Long fileSize;
		
		private Long chunkSize;
		
		private boolean completion;

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

		/**
		 * @return the fileMd5
		 */
		public String getFileMd5() {
			return fileMd5;
		}

		/**
		 * @param fileMd5 the fileMd5 to set
		 */
		public void setFileMd5(String fileMd5) {
			this.fileMd5 = fileMd5;
		}

		/**
		 * @return the chunks
		 */
		public List<Integer> getChunks() {
			return chunks;
		}

		/**
		 * @param chunks the chunks to set
		 */
		public void setChunks(List<Integer> chunks) {
			this.chunks = chunks;
		}

		/**
		 * @return the fileSize
		 */
		public Long getFileSize() {
			return fileSize;
		}

		/**
		 * @param fileSize the fileSize to set
		 */
		public void setFileSize(Long fileSize) {
			this.fileSize = fileSize;
		}

		/**
		 * @return the chunkSize
		 */
		public Long getChunkSize() {
			return chunkSize;
		}

		/**
		 * @param chunkSize the chunkSize to set
		 */
		public void setChunkSize(Long chunkSize) {
			this.chunkSize = chunkSize;
		}

		/**
		 * @return the completion
		 */
		public boolean isCompletion() {
			return completion;
		}

		/**
		 * @param completion the completion to set
		 */
		public void setCompletion(boolean completion) {
			this.completion = completion;
		}
	}
	
}
