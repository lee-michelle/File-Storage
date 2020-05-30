package com.enableets.edu.filestorage.master;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.core.RequestContext;
import com.enableets.edu.filestorage.master.data.ChunkInfo;
import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.FileInfo;
import com.enableets.edu.filestorage.master.data.Location;
import com.enableets.edu.filestorage.master.data.SlaveInfo;
import com.enableets.edu.filestorage.master.data.po.FileBaseInfo;
import com.enableets.edu.filestorage.master.policy.SelectionPolicy;
import com.enableets.edu.filestorage.master.policy.Slave;
import com.enableets.edu.filestorage.util.BeanUtils;
import com.enableets.edu.filestorage.util.DirUtils;
import com.enableets.edu.filestorage.util.RequestUtils;
import com.enableets.edu.filestorage.util.Utils;
import com.enableets.edu.framework.core.util.JsonUtils;
import com.enableets.edu.framework.core.util.StringUtils;

/**
 * 文件/分片上传处理，暂不支持分片多线程上传。
 * 
 * @since 2018/06/01
 */
public class UploadHandler {
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadHandler.class);
	
	public void excute() throws ServletException, IOException {
		HttpServletRequest httpRequest = RequestContext.getRequestContext().getHttpRequest();
		HttpServletResponse httpResponse = RequestContext.getRequestContext().getHttpResponse();
		SelectionPolicy selectionPolicy = RequestContext.getRequestContext().getSelectionPolicy();
		DefaultDataOperator dataOperator = RequestContext.getRequestContext().getDefaultDataOperator();
		CloseableHttpClient httpClient = RequestContext.getRequestContext().getHttpClientConnectionManager().getHttpClient();

		MultiValueMap<String, String> headers = RequestUtils.buildRequestHeaders(httpRequest);
		MultiValueMap<String, String> params = RequestUtils.buildRequestQueryParams(httpRequest);
		String verb = RequestUtils.getRequestMethod(httpRequest);
		InputStream requestEntity = RequestUtils.getRequestBody(httpRequest);
	
		String path = null;
		String writeUri = null;
		String slaveId = null;
		ChunkInfo chunkInfo = null;
		ChunkParameter chunkParam;
		
		boolean chunked = isChunked(headers); // 是否分片上传
		if (chunked) { // 如果是分片上传
			chunkParam = extractChunkInfo(headers); // 检查必要的参数
			if (chunkParam == null) { 
				Utils.responseError(httpResponse, Constants.MESSAGE_10014);// (10014)缺少参数
				return;
			}
			
			String uuid = getUuid(headers);
			String fileMd5 = getFileMd5(headers);
			long chunkSize = getChunkSize(headers);
			long fileSize = getFileSize(headers);
			int chunkIndex = getChunkIndex(headers);
			
			params.add(Constants.FIELD_NAME_FILE_MD5, fileMd5);
			params.add(Constants.FIELD_NAME_FILE_SIZE, String.valueOf(fileSize));
			params.add(Constants.FIELD_NAME_CHUNK_SIZE, String.valueOf(chunkSize));
			params.add(Constants.FIELD_NAME_CHUNK_INDEX, String.valueOf(chunkIndex));
			params.add(Constants.FIELD_NAME_CHUNK_UUID, String.valueOf(uuid));
			
			chunkInfo = dataOperator.getChunkById(uuid);
			if (chunkInfo != null) {
				slaveId = chunkInfo.getSlaveId();
				SlaveInfo slaveInfo = dataOperator.getSlaveById(slaveId);
				writeUri = slaveInfo.getWriteUri();
				path = chunkInfo.getPath();
			}
		}
		// 如果没有分片信息
		if (chunkInfo == null) {
			Slave slave = Utils.getUploadSlave(selectionPolicy, dataOperator);
			if (slave != null) {
				slaveId = slave.getSlaveId();
				writeUri = slave.getWriteUri();
				path = DirUtils.randDir(Constants.DIR_DEPTH);
			}
		}
		if (writeUri == null) {
			LOGGER.error("there is not avaliable slave server.");
			Utils.responseError(httpResponse, Constants.MESSAGE_10002);// (10002)服务暂停，请稍后重试！
			return;
		}
		params.add(Constants.PATH, path);
		CloseableHttpResponse response = null;
		boolean success = false;
		String str;
		FileInfo fileInfo = null;
		try {
			URL hostUrl = new URL(writeUri);
			response = forward(httpClient, verb, hostUrl, httpRequest, headers, params, requestEntity, false);

			HttpEntity entity = response.getEntity();
			httpResponse.setStatus(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == Constants.STATUS_CODE_SUCCESS_200) {
				if (entity != null) {
					str = EntityUtils.toString(entity);
					EntityUtils.consume(entity);
					if (!StringUtils.isEmpty(str)) {
						try {
							fileInfo = JsonUtils.convert(str, FileInfo.class);
							success = true;
						} catch (RuntimeException e) {
							LOGGER.error("parse json string from slave server error with " + str);
						}
					}
				}
			}
		} finally {
			IOUtils.closeQuietly(requestEntity);
			IOUtils.closeQuietly(response);
		}

		if (success && !chunked) { // 如果是上传文件
			processAfterUpload(fileInfo, path, slaveId);
		}
	}
	
	/**
	 * 是否分片上传
	 * @param headers 请求头集合
	 * @return
	 */
	private boolean isChunked(MultiValueMap<String, String> headers) {
		return headers.containsKey(Constants.FIELD_NAME_FILE_MD5);
	}
	
	private String getUuid(MultiValueMap<String, String> headers) {
		String uuid = headers.getFirst(Constants.FIELD_NAME_CHUNK_UUID);
		return uuid;
	}
	
	private long getFileSize(MultiValueMap<String, String> headers) {
		long fileSize = Long.valueOf(headers.getFirst(Constants.FIELD_NAME_FILE_SIZE));
		return fileSize;
	}
	
	private String getFileMd5(MultiValueMap<String, String> headers) {
		String fileMd5 = headers.getFirst(Constants.FIELD_NAME_FILE_MD5);
		return fileMd5;
	}
	
	private long getChunkSize(MultiValueMap<String, String> headers) {
		long chunkSize = Long.valueOf(headers.getFirst(Constants.FIELD_NAME_CHUNK_SIZE));
		return chunkSize;
	}
	
	private int getChunkIndex(MultiValueMap<String, String> headers) {
		int chunkIndex = Integer.valueOf(headers.getFirst(Constants.FIELD_NAME_CHUNK_INDEX));
		return chunkIndex;
	}
	
	/**
	 * 从请求头获取分片参数
	 * @param headers 请求头集合
	 * @return 分片参数对象
	 */
	private ChunkParameter extractChunkInfo(MultiValueMap<String, String> headers) {
		// 检查必须的参数
		if (!headers.containsKey(Constants.FIELD_NAME_CHUNK_UUID) ||
				!headers.containsKey(Constants.FIELD_NAME_FILE_SIZE) || 
				!headers.containsKey(Constants.FIELD_NAME_FILE_MD5) || 
				!headers.containsKey(Constants.FIELD_NAME_CHUNK_SIZE) || 
				!headers.containsKey(Constants.FIELD_NAME_CHUNK_INDEX)) {
			return null;
		}
		try {
			ChunkParameter chunkParameter = new ChunkParameter();
			String uuid = getUuid(headers);
			chunkParameter.setUuid(uuid);
			
			long fileSize = getFileSize(headers);
			chunkParameter.setFileSize(fileSize);
			
			String fileMd5 = getFileMd5(headers);
			chunkParameter.setFileMd5(fileMd5);
			
			long chunkSize = getChunkSize(headers);
			chunkParameter.setChunkSize(chunkSize);
			
			int chunkIndex = getChunkIndex(headers);
			chunkParameter.setChunkIndex(chunkIndex);
			
			return chunkParameter;
		} catch (NumberFormatException e) { // 参数格式不对
			return null;
		}
	}
	
	protected void processAfterUpload(FileInfo fileInfo, String path, String slaveId) throws IOException {
		DefaultDataOperator dataOperator = RequestContext.getRequestContext().getDefaultDataOperator();
		HttpServletResponse httpResponse = RequestContext.getRequestContext().getHttpResponse();
		ApplicationConfiguration config = RequestContext.getRequestContext().getApplicationConfiguration();
		CloseableHttpClient httpClient = RequestContext.getRequestContext().getHttpClientConnectionManager().getHttpClient();
		if (fileInfo.getUuid() == null) {
			return;
		}
		Calendar time = Calendar.getInstance();
		String filePath = path + fileInfo.getUuid();
		// 添加分片信息
		String extName = FilenameUtils.getExtension(fileInfo.getName());
		String aliasName = path + fileInfo.getName();
		// 保存文件信息
		fileInfo.setSlaveId(slaveId);
		fileInfo.setExt(extName);
		fileInfo.setFileId(fileInfo.getUuid());
		fileInfo.setStatus(Constants.FILE_DEFAULT_STATUS);
		fileInfo.setUploadTime(time.getTime());
		fileInfo.setAliasName(aliasName);
		dataOperator.addFileInfo(fileInfo);

		Location location = new Location();
		location.setFileId(fileInfo.getUuid());
		location.setPath(filePath + "." + extName);
		location.setCreateTime(time.getTime());
		location.setUpdateTime(time.getTime());
		location.setSlaveId(slaveId);
		location.setStatus(Constants.FILE_LOCATION_DEFAULT_STATUS);
		dataOperator.addFileSavedLocation(location);
		FileBaseInfo fileBaseInfo = BeanUtils.convert(fileInfo, FileBaseInfo.class);
		String str = JsonUtils.convert(fileBaseInfo);
		try {
			httpResponse.getWriter().write(str);
		} catch(BufferOverflowException e) {
			System.out.println(e);
		}
		
		if (config.isSync()) { // 如果开启了同步机制
			// 根据上传服务器，获取待同步的文件服务器
			List<SlaveInfo> list = dataOperator.getStandbySlaveServer(slaveId);
			if (list.isEmpty()) {
				return;
			}
			for (SlaveInfo slaveInfo : list) {
				String taskId = Utils.buildUUID();
				Object[] data = new Object[] { taskId, slaveId, fileInfo.getName(), config.getEstimateCostTime(), slaveInfo.getSyncUri(),
						fileInfo.getFileId(), filePath, fileInfo.getMd5() };
				Utils.sendFileSyncRequest(httpClient, config.getPutUrl(), data);
			}
		}
	}

	/**
	 * 请求转发
	 * 
	 * @param httpclient
	 * @param requestMethod
	 *            请求类型 POST/GET
	 * @param url
	 *            请求路径
	 * @param request
	 * @param headers
	 * @param params
	 * @param requestEntity
	 * @return
	 * @throws Exception
	 */
	public CloseableHttpResponse forward(CloseableHttpClient httpclient, String requestMethod, URL url, HttpServletRequest request,
			MultiValueMap<String, String> headers, MultiValueMap<String, String> params, InputStream requestEntity, boolean forceOriginalQueryStringEncoding)
			throws IOException {

		int contentLength = request.getContentLength();

		HttpHost httpHost = RequestUtils.getHttpHost(url);
		String uri = url.getPath();// 请求路径
		ContentType contentType = null;

		if (request.getContentType() != null) {
			contentType = ContentType.parse(request.getContentType());
		}

		InputStreamEntity entity = new InputStreamEntity(requestEntity, contentLength, contentType);
		HttpRequest httpRequest = buildHttpRequest(requestMethod, uri, entity, headers, params, request, forceOriginalQueryStringEncoding);
		try {
			CloseableHttpResponse response = RequestUtils.forwardRequest(httpclient, httpHost, httpRequest);
			return response;
		} finally {
		}
	}

	/**
	 * 构造request
	 * 
	 * @param requestMethod
	 *            请求方法
	 * @param uri
	 *            请求路径
	 * @param entity
	 *            请求实体
	 * @param headers
	 *            请求头
	 * @param params
	 *            参数
	 * @param request
	 * @return
	 */
	protected HttpRequest buildHttpRequest(String requestMethod, String uri, InputStreamEntity entity, MultiValueMap<String, String> headers,
			MultiValueMap<String, String> params, HttpServletRequest request, boolean forceOriginalQueryStringEncoding) {
		HttpRequest httpRequest;
		String uriWithQueryString = uri
				+ (forceOriginalQueryStringEncoding ? RequestUtils.getEncodedQueryString(request) : RequestUtils.getQueryString(params));

		switch (requestMethod.toUpperCase()) {
		case "POST":
			HttpPost httpPost = new HttpPost(uriWithQueryString);
			httpRequest = httpPost;
			httpPost.setEntity(entity);
			break;
		case "PUT":
			HttpPut httpPut = new HttpPut(uriWithQueryString);
			httpRequest = httpPut;
			httpPut.setEntity(entity);
			break;
		case "PATCH":
			HttpPatch httpPatch = new HttpPatch(uriWithQueryString);
			httpRequest = httpPatch;
			httpPatch.setEntity(entity);
			break;
		case "DELETE":
			BasicHttpEntityEnclosingRequest entityRequest = new BasicHttpEntityEnclosingRequest(requestMethod, uriWithQueryString);
			httpRequest = entityRequest;
			entityRequest.setEntity(entity);
			break;
		default:
			httpRequest = new BasicHttpRequest(requestMethod, uriWithQueryString);
		}

		httpRequest.setHeaders(RequestUtils.convertHeaders(headers));
		return httpRequest;
	}

	/**
	 * 客户端提交的分块请求参数
	 */
	private static class ChunkParameter {
		private String uuid;
		private String fileMd5;
		private long chunkSize;
		private long fileSize;
		private int chunkIndex;
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
		 * @return the chunkSize
		 */
		public long getChunkSize() {
			return chunkSize;
		}
		/**
		 * @param chunkSize the chunkSize to set
		 */
		public void setChunkSize(long chunkSize) {
			this.chunkSize = chunkSize;
		}
		/**
		 * @return the fileSize
		 */
		public long getFileSize() {
			return fileSize;
		}
		/**
		 * @param fileSize the fileSize to set
		 */
		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}
		/**
		 * @return the chunkIndex
		 */
		public int getChunkIndex() {
			return chunkIndex;
		}
		/**
		 * @param chunkIndex the chunkIndex to set
		 */
		public void setChunkIndex(int chunkIndex) {
			this.chunkIndex = chunkIndex;
		}
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
	}
	
}
