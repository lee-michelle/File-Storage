package com.enableets.edu.filestorage.slave.sync;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.core.HostProperties;
import com.enableets.edu.filestorage.core.HttpClientConnectionManager;
import com.enableets.edu.filestorage.master.data.service.FileLocationService;
import com.enableets.edu.filestorage.util.Utils;
import com.enableets.edu.framework.core.util.JsonUtils;
import com.enableets.edu.module.workqueue.sdk.Message;
import com.enableets.edu.module.workqueue.sdk.TaskExecutor;
import com.enableets.edu.module.workqueue.sdk.TaskStatusDefine;

/***
 * 文件同步Servlet**
 * 
 * @author pc-psq
 * @since 2018/6/9
 */
// @WebServlet(name = "fileSyncServlet", urlPatterns = "/storage/slave/sync")
public class FileSyncServlet extends HttpServlet {
	/**
	*
	*/
	private static final long serialVersionUID = 1L;

	/**
	 * http url scheme
	 */
	String HTTP_SCHEME = "http";

	/**
	 * https url scheme
	 */
	String HTTPS_SCHEME = "https";

	/**
	 * Host properties controlling default connection pool properties.
	 */
	private HostProperties hostProperties;

	private boolean sslHostnameValidationEnabled;

	private TaskExecutor taskExecutor;

	@Autowired
	private FileLocationService fileLocationService;

	@Autowired
	private ApplicationConfiguration config;

	private PoolingHttpClientConnectionManager connectionManager;

	@Autowired
	private HttpClientConnectionManager httpClientConnectionManager;

	public FileSyncServlet(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(req, response);
	}

	/**
	 * 文件同步请求
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String str = Utils.getBodyContextAsString(request, "utf-8");
			Message message = JsonUtils.convert(str, Message.class);
			Map<?, ?> map = message.getData();
			String fileId = (String) map.get(Constants.FIELD_NAME_FILE_ID);
			String path = (String) map.get(Constants.FIELD_NAME_PATH);
			String md5 = (String) map.get(Constants.FIELD_NAME_MD5);
			String taskId = message.getTaskId();

			if (fileId.isEmpty() || path.isEmpty() || md5.isEmpty() || taskId.isEmpty()) {
				httpClientConnectionManager.responseResult(request, response, TaskStatusDefine.ABANDON);
				return;
			}

			Map<String, Object> params = new HashMap<String, Object>();
			params.put(Constants.FIELD_NAME_FILE_ID, fileId);
			params.put(Constants.FIELD_NAME_PATH, path);
			params.put(Constants.FIELD_NAME_MD5, md5);
			params.put(Constants.FIELD_NAME_TASK_ID, taskId);

			File file = new File(config.getRepository(), path);
			if (file.exists()) {
				httpClientConnectionManager.responseResult(request, response, TaskStatusDefine.DONE);
				return;
			}
			FileSyncCallback fileSyncCallback = new FileSyncCallback(config, httpClientConnectionManager.getHttpClient(), params, request, fileLocationService);

			String result = taskExecutor.execute(fileSyncCallback, taskId);
			httpClientConnectionManager.responseResult(request, response, result);
		} catch (RejectedExecutionException ex) {
			response.setContentType(Constants.DEFAULT_CONTENT_TYPE);
			response.setCharacterEncoding(Constants.CHARACTEREN_ENCODING_UTF8);
			response.setStatus(Constants.SERVER_STATUS_CODE_ERR);
			response.getWriter().println(TaskStatusDefine.BUSY);
		}
	}

}