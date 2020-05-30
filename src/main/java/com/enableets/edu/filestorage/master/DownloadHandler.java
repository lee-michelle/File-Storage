package com.enableets.edu.filestorage.master;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.core.RequestContext;
import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.FileInfo;
import com.enableets.edu.filestorage.master.data.Location;
import com.enableets.edu.filestorage.master.policy.SelectionPolicy;
import com.enableets.edu.filestorage.master.policy.Slave;
import com.enableets.edu.filestorage.util.RequestUtils;
import com.enableets.edu.filestorage.util.Utils;
import com.enableets.edu.framework.core.util.StringUtils;

public class DownloadHandler {

	/**
	 * 下载文件
	 * 
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public void excute() throws IOException, MalformedURLException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) RequestContext.getRequestContext().getHttpRequest();
		HttpServletResponse httpServletResponse = (HttpServletResponse) RequestContext.getRequestContext().getHttpResponse();
		SelectionPolicy selectionPolicy = RequestContext.getRequestContext().getSelectionPolicy();
		DefaultDataOperator dataOperator = RequestContext.getRequestContext().getDefaultDataOperator();
		CloseableHttpClient httpClient = RequestContext.getRequestContext().getHttpClientConnectionManager().getHttpClient();

		String fileId = httpServletRequest.getParameter(Constants.FIELD_NAME_FILE_ID);
		String aliasName = httpServletRequest.getParameter(Constants.FIELD_NAME_ALIAS_NAME);
		if (StringUtils.isEmpty(fileId) && StringUtils.isEmpty(aliasName)) {
			// 缺失参数
			Utils.responseError(httpServletResponse, Constants.MESSAGE_10014);
			return;
		}
		List<FileInfo> list = Utils.getFileInfo(fileId, aliasName, dataOperator);
		// 当查询出的集合个数大于1或者为空时返回错误信息
		if (CollectionUtils.isEmpty(list) || list.size() > 1) {
			Utils.responseError(httpServletResponse, Constants.MESSAGE_10014);
			return;
		}
		FileInfo fileInfo = list.iterator().next();
		List<Location> locations = dataOperator.getFileSavedLocation(fileInfo.getFileId());
		if (CollectionUtils.isEmpty(locations)) {
			// 内部服务器错误
			Utils.responseError(httpServletResponse, Constants.MESSAGE_10030);
			return;
		}
		Slave slave = Utils.getInternalDownloadUrl(locations, selectionPolicy);
		if (slave == null) {
			// 内部服务器错误
			Utils.responseError(httpServletResponse, Constants.MESSAGE_10030);
			return;
		}
		URL hostUrl = new URL(slave.getReadUri());
		MultiValueMap<String, String> headers = RequestUtils.buildRequestHeaders(httpServletRequest);
		String verb = RequestUtils.getRequestMethod(httpServletRequest);
		InputStream requestEntity = RequestUtils.getRequestBody(httpServletRequest);

		MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
		param.add(Constants.PATH, locations.iterator().next().getPath());
		CloseableHttpResponse response = forward(httpClient, verb, hostUrl, httpServletRequest, headers, param, requestEntity, false);
		httpServletResponse.setStatus(response.getStatusLine().getStatusCode());
		HttpEntity entity = response.getEntity();
		try {
			httpServletResponse.setStatus(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == Constants.STATUS_CODE_SUCCESS_200) {
				// 文件名处理
				Utils.addFileNameHeader(fileInfo.getName(), headers.getFirst(Constants.MAP_KEY_USER_AGENT), httpServletResponse);
				RequestUtils.setResponseHeaders(httpServletResponse, response.getAllHeaders());
				Utils.response(entity.getContent(), httpServletResponse.getOutputStream());
				httpServletResponse.getOutputStream().flush();
			}
		} finally {
			IOUtils.closeQuietly(response);
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
}
