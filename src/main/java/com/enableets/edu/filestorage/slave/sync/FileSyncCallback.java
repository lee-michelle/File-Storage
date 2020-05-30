package com.enableets.edu.filestorage.slave.sync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriTemplate;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.master.data.po.FileLocationPO;
import com.enableets.edu.filestorage.master.data.service.FileLocationService;
import com.enableets.edu.filestorage.util.Utils;
import com.enableets.edu.module.workqueue.sdk.TaskStatusDefine;

/**
 * 同步下载文件
 * 
 * @author pc-psq
 * @since 2018/6/9
 */

public class FileSyncCallback implements Callable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean forceOriginalQueryStringEncoding;

	/**
	 * 配置文件获取类
	 */
	private ApplicationConfiguration config;
	/**
	 * 文件标识
	 */
	private String fileId;
	/**
	 * MD5码
	 */
	private String md5;
	/**
	 * 路径
	 */
	private String path;
	private CloseableHttpClient httpClient;
	private HttpServletRequest request;
	/**
	 * 任务标识
	 */
	private String taskId;

	/**
	 * 文件存储位置Service
	 */
	private FileLocationService fileLocationService;

	/**
	 * Constructor
	 * 
	 * @param config
	 * @param httpClient
	 * @param request
	 * @param fileLocationService
	 */
	public FileSyncCallback(ApplicationConfiguration config, CloseableHttpClient httpClient, Map<String, Object> params, HttpServletRequest request,
			FileLocationService fileLocationService) {
		this.config = config;
		this.httpClient = httpClient;
		this.request = request;
		this.fileId = (String) params.get(Constants.FIELD_NAME_FILE_ID);
		this.md5 = (String) params.get(Constants.FIELD_NAME_MD5);
		this.path = (String) params.get(Constants.FIELD_NAME_PATH);
		this.taskId = (String) params.get(Constants.FIELD_NAME_TASK_ID);
		this.fileLocationService = fileLocationService;
	}

	@Override
	public Object call() throws Exception {
		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add(Constants.FIELD_NAME_FILE_ID, fileId);
		param.add(Constants.FIELD_NAME_PATH, path);
		String verb = "GET";
		String hostUrl = config.getMasterUrl() + "download";
		URL url = new URL(hostUrl);
		String calculatedMd5 = null;
		File file = null;
		MultiValueMap<String, String> headers = this.buildRequestHeaders(request);
		CloseableHttpResponse response = forward(this.httpClient, verb, url, request, headers, param, null);
		boolean done = true;
		boolean downloadSuccess = false;
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) { // 下载失败
				downloadSuccess = false;
			} else {
				HttpEntity entity = response.getEntity();
				InputStream in = entity.getContent();

				file = new File(config.getRepository(), path);

				if (!file.exists()) { // 如果本地没有这个文件
					File dir = new File(file.getParent());
					if (!dir.exists()) {
						dir.mkdirs();
					}
					FileOutputStream out = new FileOutputStream(file);
					FileCopyUtils.copy(in, out);
				}
				downloadSuccess = true;
			}
		} finally {
			IOUtils.closeQuietly(response);
		}

		boolean md5Success = false;

		if (downloadSuccess) {
			calculatedMd5 = Utils.calculatedMd5(file);
			md5Success = calculatedMd5.equalsIgnoreCase(md5);
			if (md5Success) { // 文件存储位置保存到数据库
				Calendar calendar = Calendar.getInstance();
				FileLocationPO fileLocationPO = new FileLocationPO();
				fileLocationPO.setFileId(fileId);
				fileLocationPO.setPath(path);
				fileLocationPO.setSlaveId(config.getSlaveId());
				fileLocationPO.setStatus(Constants.FILE_LOCATION_DEFAULT_STATUS);
				fileLocationPO.setCreateTime(calendar.getTime());
				fileLocationPO.setUpdateTime(calendar.getTime());
				fileLocationService.add(fileLocationPO);
			}
		}

		Utils.sendFileSyncCompletionRequest(httpClient, config.getStatusUrl(), taskId,
				downloadSuccess & md5Success ? String.valueOf(TaskStatusDefine.DONE) : String.valueOf(TaskStatusDefine.FAILURE));

		return done;
	}

	private CloseableHttpResponse forwardRequest(CloseableHttpClient httpclient, HttpHost httpHost, HttpRequest httpRequest) throws IOException {
		return httpclient.execute(httpHost, httpRequest);
	}

	private CloseableHttpResponse forward(CloseableHttpClient httpclient, String verb, URL url, HttpServletRequest request,
			MultiValueMap<String, String> headers, MultiValueMap<String, String> params, InputStream requestEntity) throws Exception {
		int contentLength = request.getContentLength();
		HttpHost httpHost = getHttpHost(url);
		String uri = url.getPath();
		ContentType contentType = null;
		if (request.getContentType() != null) {
			contentType = ContentType.parse(request.getContentType());
		}

		InputStreamEntity entity = null;
		HttpRequest httpRequest = buildHttpRequest(verb, uri, entity, headers, params, request);
		try {
			CloseableHttpResponse response = forwardRequest(httpclient, httpHost, httpRequest);
			return response;
		} finally {
		}
	}

	private HttpHost getHttpHost(URL host) {
		HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
		return httpHost;
	}

	private String getEncodedQueryString(HttpServletRequest request) {
		String query = request.getQueryString();
		return (query != null) ? "?" + query : "";
	}

	protected HttpRequest buildHttpRequest(String verb, String uri, InputStreamEntity entity, MultiValueMap<String, String> headers,
			MultiValueMap<String, String> params, HttpServletRequest request) {
		HttpRequest httpRequest;
		String uriWithQueryString = uri + (this.forceOriginalQueryStringEncoding ? getEncodedQueryString(request) : this.getQueryString(params));
		httpRequest = new BasicHttpRequest(verb, uriWithQueryString);

		httpRequest.setHeaders(convertHeaders(headers));
		return httpRequest;
	}

	private Header[] convertHeaders(MultiValueMap<String, String> headers) {
		List<Header> list = new ArrayList<>();
		for (String name : headers.keySet()) {
			for (String value : headers.get(name)) {
				list.add(new BasicHeader(name, value));
			}
		}
		return list.toArray(new BasicHeader[0]);
	}

	protected String getQueryString(MultiValueMap<String, String> params) {
		if (params.isEmpty()) {
			return "";
		}
		StringBuilder query = new StringBuilder();
		Map<String, Object> singles = new HashMap<>();
		for (String param : params.keySet()) {
			int i = 0;
			for (String value : params.get(param)) {
				query.append("&");
				query.append(param);
				if (!"".equals(value)) { // don't add =, if original is ?wsdl,
											// output is not ?wsdl=
					String key = param;
					// if form feed is already part of param name double
					// since form feed is used as the colon replacement below
					if (key.contains("\f")) {
						key = (key.replaceAll("\f", "\f\f"));
					}
					// colon is special to UriTemplate
					if (key.contains(":")) {
						key = key.replaceAll(":", "\f");
					}
					key = key + i;
					singles.put(key, value);
					query.append("={");
					query.append(key);
					query.append("}");
				}
				i++;
			}
		}

		UriTemplate template = new UriTemplate("?" + query.toString().substring(1));
		return template.expand(singles).toString();
	}

	protected MultiValueMap<String, String> buildRequestQueryParams(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		if (map == null) {
			return params;
		}
		for (String key : map.keySet()) {
			for (String value : map.get(key)) {
				params.add(key, value);
			}
		}
		return params;
	}

	protected MultiValueMap<String, String> buildRequestHeaders(HttpServletRequest request) {
		MultiValueMap<String, String> headers = new HttpHeaders();
		return headers;
	}

	protected boolean isIncludedHeader(String headerName) {
		String name = headerName.toLowerCase();
		switch (name) {
		case "host":
		case "connection":
		case "content-length":
		case "content-encoding":
		case "server":
		case "transfer-encoding":
		case "x-application-context":
			return false;
		default:
			return true;
		}
	}

}
