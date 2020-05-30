package com.enableets.edu.filestorage.slave;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.master.data.ChunkInfo;
import com.enableets.edu.filestorage.master.data.ChunkDetails;
import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.FileInfo;
import com.enableets.edu.filestorage.util.Utils;
import com.enableets.edu.framework.core.util.JsonUtils;
import com.enableets.edu.framework.core.util.StringUtils;

/**
 * 文件上传slave 端Servlet
 * 
 * @author lemon
 * @since 2018/6/1
 */
@Controller
public class FileUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 定义缓冲数组的大小
	 */
	private static final int BUFFER_SIZE = 1024 * 4;

	/** map key 文件路径 */
	private static final String PATH = "path";

	/**
	 * 配置信息读取
	 */
	@Autowired
	private ApplicationConfiguration config;

	@Autowired
	private DefaultDataOperator dataOperator;

	/**
	 * 日志
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(config.getSizeThreshold());
		factory.setRepository(new File(config.getTmpDir()));
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 存储目录
		File repository = new File(config.getRepository());
		
		if (!repository.exists()) {
			boolean bln = repository.mkdirs(); // 创建目录，如果需要
			if (!bln) { 
				if (!repository.exists()) { 
					response.setContentType(Constants.DEFAULT_CONTENT_TYPE);
					response.setCharacterEncoding(Constants.CHARACTEREN_ENCODING_UTF8);
					response.setStatus(Constants.SERVER_STATUS_CODE_ERR);
					response.getWriter().println(Constants.MESSAGE_10030);
					return;
				}
			}
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();

		try {
			List<FileItem> formList = upload.parseRequest(request);
			Iterator<FileItem> formItem = formList.iterator();
			String key = null;
			while (formItem.hasNext()) {
				FileItem item = (FileItem) formItem.next();
				key = item.getFieldName();
				if (!item.isFormField()) { // 文件
					paramMap.put("file", item.getInputStream());
					paramMap.put("filename", item.getName());
				} 
				else {
					paramMap.put(key, item.getString("utf-8"));
				}
			}

			boolean chunked = isChunked(request);
			if (!chunked) { // 文件
				processFile(request, response, repository, paramMap);
			} 
			else { // 分片
				processChunk(request, response, repository, paramMap);
			}
		} catch (FileUploadException e) {
			LOGGER.error("file upload error on slave server", e);
			response.setContentType(Constants.DEFAULT_CONTENT_TYPE);
			response.setCharacterEncoding(Constants.CHARACTEREN_ENCODING_UTF8);
			response.setStatus(Constants.SERVER_STATUS_CODE_ERR);
			response.getWriter().write(Constants.MESSAGE_10031);
			response.getWriter().flush();
		}
	}
	
	/**
	 * 是否分片上传
	 * @param request
	 * @return
	 */
	private boolean isChunked(HttpServletRequest request) {
		return request.getParameterMap().containsKey(Constants.FIELD_NAME_CHUNK_INDEX);
	}
	
	/**
	 * 处理文件上传
	 * @param request
	 * @param response
	 * @param repository
	 * @param paramMap
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processFile(HttpServletRequest request, HttpServletResponse response, 
			File repository, Map<String, Object> paramMap) 
					throws ServletException, IOException {
		String filename = (String) paramMap.get("filename");
		
		String ext = Utils.getFileExt(filename);
		String uuid = Utils.buildUUID();
		String savedFileName = uuid;
		if (StringUtils.isNotEmpty(ext)) {
			savedFileName = uuid + "." + ext;
		}
		
		String path = request.getParameter(PATH);
		File dir = new File(repository, path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File savedFile = new File(dir, savedFileName);
		
		OutputStream os = null;
		InputStream in = null;
		BufferedOutputStream out = null;

		in = (InputStream) paramMap.get("file");
		os = new FileOutputStream(savedFile);
		out = new BufferedOutputStream(os);
		FileCopyUtils.copy(in, out);

		FileInfo fileInfo = Utils.buildFileInfo(savedFile, filename, uuid);
		responseResult(request, response, fileInfo);
	}
	
	/**
	 * 处理分片上传
	 * @param request
	 * @param response
	 * @param repository
	 * @param paramMap
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processChunk(HttpServletRequest request, HttpServletResponse response,
			File repository, Map<String, Object> paramMap) 
			throws ServletException, IOException {
		String chunkUuid = request.getParameter(Constants.FIELD_NAME_CHUNK_UUID);
		// 文件的MD5
		String fileMd5 = request.getParameter(Constants.FIELD_NAME_FILE_MD5);
		long fileSize = Long.valueOf(request.getParameter(Constants.FIELD_NAME_FILE_SIZE));
		int chunkIndex = Integer.valueOf(request.getParameter(Constants.FIELD_NAME_CHUNK_INDEX));
		long chunkSize = Long.valueOf(request.getParameter(Constants.FIELD_NAME_CHUNK_SIZE));
		
		ChunkInfo chunkInfo = prepareChunkInfo(request, chunkUuid, fileMd5, fileSize, 
				chunkSize, chunkIndex);
		
		File chunkFile = new File(repository, chunkInfo.getPath());
		RandomAccessFile raf = null;
		if (!chunkFile.getParentFile().exists()) { // 创建目录，如果目录不存在
			chunkFile.getParentFile().mkdirs();
		}
		if (!chunkFile.exists()) {
			raf = new RandomAccessFile(chunkFile, "rw");
			raf.setLength(fileSize); // file的大小
		}
		else {
			raf = new RandomAccessFile(chunkFile, "rw");
		}

		// 写文件
		int skipBytes = (int) (chunkIndex * Long.valueOf(chunkSize));
		InputStream in = (InputStream) paramMap.get("file");
		int size = writeFile(in, raf, skipBytes);
		
		prepareChunkDetails(chunkInfo, chunkUuid, chunkIndex, size);
		
		FileInfo fileInfo = new FileInfo();
		fileInfo.setMd5(fileMd5);
		fileInfo.setSize(fileSize);
		
		fileInfo.getParams().put(Constants.FILE_NAME_CHUNK_INDEX, chunkIndex);
		responseResult(request, response, fileInfo);
	}
	
	/**
	 * 处理分片信息
	 * @param request
	 * @param fileMd5 文件md5
	 * @param fileSize 文件大小
	 * @param chunkSize 固定分片大小
	 * @param chunkIndex 分片索引
	 * @return 分片信息
	 */
	private ChunkInfo prepareChunkInfo(HttpServletRequest request, String chunkUuid, String fileMd5, 
			long fileSize, long chunkSize, int chunkIndex) {
		// 查询是否已经存在文件分片信息
		ChunkInfo chunkInfo = dataOperator.getChunkById(chunkUuid);
		if (chunkInfo != null) {
			return chunkInfo;
		} 
		
		chunkInfo = new ChunkInfo();
		String uuid = Utils.buildUUID();
		String path = request.getParameter(PATH);
		String chunkPath = path + uuid;
		chunkInfo.setUuid(chunkUuid);
		chunkInfo.setPath(chunkPath);
		chunkInfo.setFileMd5(fileMd5);
		chunkInfo.setFileSize(fileSize);
		chunkInfo.setChunkSize(chunkSize);
		chunkInfo.setSlaveId(config.getSlaveId());
		chunkInfo.setCreateTime(Calendar.getInstance().getTime());
		dataOperator.addChunk(chunkInfo);
		
		return chunkInfo;
	}
	
	/**
	 * 处理分片明细
	 * @param chunkInfo 分片信息
	 * @param chunkIndex 分片索引
	 * @param size 当前分片大小
	 */
	private void prepareChunkDetails(ChunkInfo chunkInfo,  String chunkUuid, int chunkIndex, long size) {
		// 查询是否已经存在当前文件分片
		ChunkDetails chunkInfoDetail = dataOperator.getChunkDetailsById(chunkUuid, chunkIndex);
		if (chunkInfoDetail != null) {
			return;
		}
		
		chunkInfoDetail = new ChunkDetails();
		chunkInfoDetail.setUuid(chunkUuid);
		chunkInfoDetail.setFileMd5(chunkInfo.getFileMd5());
		chunkInfoDetail.setChunkSize(chunkInfo.getChunkSize());
		chunkInfoDetail.setPosition(chunkIndex);
		// 当前分片大小
		chunkInfoDetail.setSize(size);
		chunkInfoDetail.setCreateTime(Calendar.getInstance().getTime());
		dataOperator.addChunkDetails(chunkInfoDetail);
	}
	
	/**
	 * 把分片流写到文件
	 * @param in
	 * @param raf
	 * @param skipBytes
	 * @return 返回写入的字节数
	 * @throws IOException
	 */
	private int writeFile(InputStream in, RandomAccessFile raf, int skipBytes)
			throws IOException {
		try {
			raf.skipBytes(skipBytes);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len;
			int total = 0;
			while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
				raf.write(buffer, 0, len);
				total += len;
			}
			return total;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(raf);
		}
	}

	protected void responseResult(HttpServletRequest request, HttpServletResponse response, Object body) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(JsonUtils.convert(body));
		response.getWriter().flush();
	}
}
