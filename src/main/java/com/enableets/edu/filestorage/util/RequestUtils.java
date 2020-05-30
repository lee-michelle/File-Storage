package com.enableets.edu.filestorage.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriTemplate;

public class RequestUtils {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

	/** 不需要转发的头信息名称 */
	private static final Set<String> excludeHeadersName = new HashSet<String>(
			Arrays.asList("host", "connection", "content-length", "content-encoding", "server", "transfer-encoding", "x-application-context"));

	/**
	 * 执行请求
	 * 
	 * @param httpclient
	 *            请求客户端
	 * @param httpHost
	 *            请求host
	 * @param httpRequest
	 * @return
	 * @throws IOException
	 */
	public static CloseableHttpResponse forwardRequest(CloseableHttpClient httpclient, HttpHost httpHost, HttpRequest httpRequest) throws IOException {
		return httpclient.execute(httpHost, httpRequest);
	}

	/**
	 * 获取请求url后面的参数
	 * 
	 * @param request
	 * @return
	 */
	public static String getEncodedQueryString(HttpServletRequest request) {
		String query = request.getQueryString();
		return (query != null) ? "?" + query : "";
	}

	/**
	 * 转换headmap为header信息
	 * 
	 * @param headers
	 *            请求头map信息
	 * @return
	 */
	public static Header[] convertHeaders(MultiValueMap<String, String> headers) {
		List<Header> list = new ArrayList<>();
		for (String name : headers.keySet()) {
			for (String value : headers.get(name)) {
				list.add(new BasicHeader(name, value));
			}
		}
		return list.toArray(new BasicHeader[0]);
	}

	/**
	 * Get url encoded query string. Pay special attention to single parameters
	 * with no values and parameter names with colon (:) from use of
	 * UriTemplate.
	 * 
	 * @param params
	 *            Un-encoded request parameters
	 * @return
	 */
	public static String getQueryString(MultiValueMap<String, String> params) {
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

	/**
	 * 生成请求host
	 * 
	 * @param host
	 *            请求连接
	 * @return
	 */
	public static HttpHost getHttpHost(URL host) {
		HttpHost httpHost = new HttpHost(host.getHost(), host.getPort(), host.getProtocol());
		return httpHost;
	}

	/**
	 * 获取请求方式 POST/GET
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestMethod(HttpServletRequest request) {
		String sMethod = request.getMethod();
		return sMethod.toUpperCase();
	}

	/**
	 * 获取请求输入流
	 * 
	 * @param request
	 * @return
	 */
	public static InputStream getRequestBody(HttpServletRequest request) {
		InputStream requestEntity = null;
		try {
			requestEntity = request.getInputStream();
		} catch (IOException ex) {
			LOGGER.error("get request body error", ex);
		}
		return requestEntity;
	}

	/**
	 * 获取请求header信息
	 * 
	 * @param request
	 * @return
	 */
	public static MultiValueMap<String, String> buildRequestHeaders(HttpServletRequest request) {
		MultiValueMap<String, String> headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames == null) {
			return headers;
		}
		while (headerNames.hasMoreElements()) {
			String name = headerNames.nextElement();
			if (isIncludedHeader(name)) {
				Enumeration<String> values = request.getHeaders(name);
				while (values.hasMoreElements()) {
					String value = values.nextElement();
					headers.add(name, value);
				}
			}
		}
		return headers;
	}

	/**
	 * 从request中得到查询参数map
	 * 
	 * @param request
	 * @return
	 */
	public static MultiValueMap<String, String> buildRequestQueryParams(HttpServletRequest request) {
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

	/**
	 * 请求头信息是否为需要转换的请求头信息
	 * 
	 * @param headerName
	 *            请求头名称
	 * @return
	 */
	public static boolean isIncludedHeader(String headerName) {
		String name = headerName.toLowerCase();
		if (excludeHeadersName.contains(name)) {
			return false;
		}
		return true;
	}

	/**
	 * 设置回应头信息
	 * 
	 * @param httpServletResponse
	 * @param headers
	 */
	public static void setResponseHeaders(HttpServletResponse httpServletResponse, Header[] headers) {
		if (headers == null) {
			return;
		}
		for (Header header : headers) {
			httpServletResponse.addHeader(header.getName(), header.getValue());
		}
	}

}
