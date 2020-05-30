package com.enableets.edu.filestorage.master;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.HttpClientConnectionManager;
import com.enableets.edu.filestorage.core.RequestContext;
import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.policy.SelectionPolicy;

/**
 * 文件上传下载代理类
 * 
 * @author duffy_ding
 * @since 2018/06/01
 */
@WebServlet(name = "proxyServlet", urlPatterns = { "/storage/upload", "/storage/download" })
@CrossOrigin
public class ProxyServlet extends HttpServlet {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyServlet.class);

	/** serialVersionUID */
	private static final long serialVersionUID = -8626746228483118583L;

	/**
	 * Slave分配策略
	 */
	@Autowired
	private SelectionPolicy selectionPolicy;

	@Autowired
	private HttpClientConnectionManager httpClientConnectionManager;

	@Autowired
	private UploadHandler uploadHandler;

	@Autowired
	private DownloadHandler downloadHandler;
	/**
	 * 配置文件读取
	 */
	@Autowired
	private ApplicationConfiguration config;
	/**
	 * 数据处理
	 */
	@Autowired
	private DefaultDataOperator dataOperator;

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		download(httpServletRequest, httpServletResponse);
	}

	/**
	 * 下载文件
	 * 
	 * @param httpServletRequest
	 * @param httpServletResponse
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private void download(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {

		RequestContext.getRequestContext().setHttpRequest(httpServletRequest);
		RequestContext.getRequestContext().setHttpResponse(httpServletResponse);
		RequestContext.getRequestContext().setSelectionPolicy(selectionPolicy);
		RequestContext.getRequestContext().setDefaultDataOperator(dataOperator);
		RequestContext.getRequestContext().setHttpClientConnectionManager(httpClientConnectionManager);
		downloadHandler.excute();
	}

	@Override
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		httpResponse.addHeader("Access-Control-Allow-Origin", "*");
		upload(httpRequest, httpResponse);
	}

	/**
	 * 上传文件
	 * 
	 * @param httpRequest
	 * @param httpResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	private void upload(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
		RequestContext.getRequestContext().setApplicationConfiguration(config);
		RequestContext.getRequestContext().setHttpRequest(httpRequest);
		RequestContext.getRequestContext().setHttpResponse(httpResponse);
		RequestContext.getRequestContext().setSelectionPolicy(selectionPolicy);
		RequestContext.getRequestContext().setDefaultDataOperator(dataOperator);
		RequestContext.getRequestContext().setApplicationConfiguration(config);
		RequestContext.getRequestContext().setHttpClientConnectionManager(httpClientConnectionManager);
		uploadHandler.excute();
	}

}
