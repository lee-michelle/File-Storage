package com.enableets.edu.filestorage.slave;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.FileCopyUtils;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.util.MimetypeProvider;
import com.enableets.edu.filestorage.util.Utils;

/**
 * 文件下载处理 该类会捕捉文件下载过程中产生的所有异常,并返回响应的状态码 902 文件流输出错误 404 文件不存在
 * 
 * @author emon_li@enable-ets.com
 * @since 2018/6/1
 */
@Scope("prototype")
public class FileDownloadProcessor {
	private static String PATH = "path";
	/**
	 * 日志打印
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadProcessor.class);

	/**
	 * 下载错误-文件不存在
	 */
	public static final int DOWNLOAD_ERROR_FILE_NOT_EXIST = 404;

	/**
	 * 忽略附档消息头
	 */
	protected static final String IGNORE_DISPOSITION = "ignoreDisposition";

	@Autowired
	private ApplicationConfiguration config;

	/**
	 * 处理下载的请求
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param downloadInfo
	 *            DownloadInfo
	 */
	public void process(HttpServletRequest request, HttpServletResponse response) {
		processDownloadRequestInner(request, response);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param downloadInfo
	 */
	private void processDownloadRequestInner(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setCharacterEncoding("utf8");
			// 下载开始位置
			long startPos = 0L;
			String range = request.getHeader("Range");
			if (range != null) {
				startPos = Long.parseLong(request.getHeader("Range").replaceAll("bytes=", "").split("-")[0]);
			} else {
				startPos = 0L;
			}
			outputStream(request, response, startPos);
		} catch (FileNotFoundException e) {
			response.setStatus(DOWNLOAD_ERROR_FILE_NOT_EXIST);
		} catch (Throwable e) {
			LOGGER.error("read file error:", e);
			response.setStatus(Constants.SERVER_STATUS_CODE_ERR);
		}
	}

	/**
	 * 输出字节流
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param downloadInfo
	 *            要下载的文件的信息
	 * @param startPos
	 *            起始位置
	 * @throws ServletException
	 *             ServletException
	 * @throws IOException
	 *             IOException
	 */
	private void outputStream(HttpServletRequest req, HttpServletResponse resp, long startPos) throws ServletException, IOException {
		// 输入流
		InputStream inputStream = null;
		// 输出流
		OutputStream outputStream = null;
		File file = new File(config.getRepository(), getFileName(req));
		inputStream = new BufferedInputStream(new FileInputStream(file));
		if (startPos > 0L) {
			inputStream.skip(startPos);
		}
		// 设置响应的头部信息
		setResponseHeader(req, resp, startPos, file.length());
		outputStream = new BufferedOutputStream(resp.getOutputStream());
		try {
			FileCopyUtils.copy(inputStream, outputStream);
		} catch (ClientAbortException e) {
			// ignore
		}
	}

	/**
	 * 设置响应的头部信息
	 * 
	 * @param req
	 *            HttpServletRequest
	 * @param resp
	 *            HttpServletResponse
	 * @param downloadInfo
	 *            下载文件的信息
	 * @param startPos
	 *            起始位置
	 * @param fileLength
	 *            long
	 * @throws UnsupportedEncodingException
	 */
	private void setResponseHeader(HttpServletRequest request, HttpServletResponse response, long startPos, long fileLength)
			throws UnsupportedEncodingException {
		// response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Accept-Ranges", "bytes");
		// 断点响应的状态是206，正常响应的状态是200
		if (startPos > 0L) {
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
		}
		response.setHeader("Content-Length", new StringBuilder().append(fileLength - startPos).toString());
		// Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]
		String contentRange = new StringBuffer("bytes ").append(Long.toString(startPos)).append("-").append(Long.toString(fileLength - 1)).append("/")
				.append(Long.toString(fileLength)).toString();

		response.setHeader("Content-Range", contentRange);

		response.setHeader("Content-Type", MimetypeProvider.getMimeTypeByExtension(Utils.getFileExt(getFileName(request))));
	}

	/**
	 * 获取文件路径
	 * @param request
	 * @return
	 */
	private String getFileName(HttpServletRequest request) {
		return request.getParameter(PATH);
	}
	
}
