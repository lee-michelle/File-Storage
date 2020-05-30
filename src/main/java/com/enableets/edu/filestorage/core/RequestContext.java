package com.enableets.edu.filestorage.core;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.policy.SelectionPolicy;
import com.netflix.config.DynamicIntProperty;

public class RequestContext {

	private static RequestContext instance = null;

	private static ThreadLocal<Map<String, Object>> threadMap = null;

	/**
	 * Constructor
	 */
	private RequestContext() {
		threadMap = new ThreadLocal<Map<String, Object>>() {
			protected Map<String, Object> initialValue() {
				return new HashMap<String, Object>();
			}
		};
	}

	public static RequestContext getRequestContext() {
		if (instance == null) {
			synchronized (RequestContext.class) {
				if (instance == null) {
					instance = new RequestContext();
				}
			}
		}

		return instance;
	}

	public HttpServletRequest getHttpRequest() {
		return (HttpServletRequest) threadMap.get().get("httpRequest");
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		threadMap.get().put("httpRequest", httpRequest);
	}

	public HttpServletResponse getHttpResponse() {
		return (HttpServletResponse) threadMap.get().get("httpResponse");
	}

	public void setHttpResponse(HttpServletResponse httpResponse) {
		threadMap.get().put("httpResponse", httpResponse);
	}

	public SelectionPolicy getSelectionPolicy() {
		return (SelectionPolicy) threadMap.get().get("selectionPolicy");
	}

	public void setSelectionPolicy(SelectionPolicy selectionPolicy) {
		threadMap.get().put("selectionPolicy", selectionPolicy);
	}

	public DefaultDataOperator getDefaultDataOperator() {
		return (DefaultDataOperator) threadMap.get().get("defaultDataOperator");
	}

	public void setDefaultDataOperator(DefaultDataOperator defaultDataOperator) {
		threadMap.get().put("defaultDataOperator", defaultDataOperator);
	}

	public ApplicationConfiguration getApplicationConfiguration() {
		return (ApplicationConfiguration) threadMap.get().get("applicationConfiguration");
	}

	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		threadMap.get().put("applicationConfiguration", applicationConfiguration);
	}

	public HttpClientConnectionManager getHttpClientConnectionManager() {
		return (HttpClientConnectionManager) threadMap.get().get("httpClientConnectionManager");
	}

	public void setHttpClientConnectionManager(HttpClientConnectionManager httpClientConnectionManager) {
		threadMap.get().put("httpClientConnectionManager", httpClientConnectionManager);
	}

	public DynamicIntProperty getSocketTimeout() {
		return (DynamicIntProperty) threadMap.get().get("socketTimeout");
	}

	public void setSocketTimeout(DynamicIntProperty socketTimeout) {
		threadMap.get().put("socketTimeout", socketTimeout);
	}

	public DynamicIntProperty getConnectionTimeout() {
		return (DynamicIntProperty) threadMap.get().get("connectionTimeout");
	}

	public void setConnectionTimeout(DynamicIntProperty connectionTimeout) {
		threadMap.get().put("connectionTimeout", connectionTimeout);
	}

}
