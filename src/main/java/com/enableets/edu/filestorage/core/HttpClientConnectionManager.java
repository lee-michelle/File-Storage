package com.enableets.edu.filestorage.core;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

@Component
public class HttpClientConnectionManager {

	@Autowired
	@Qualifier("hostProperties")
	private HostProperties hostProperties;

	private static DynamicIntProperty SOCKET_TIMEOUT;

	private static DynamicIntProperty CONNECTION_TIMEOUT;

	private CloseableHttpClient httpClient;

	private static PoolingHttpClientConnectionManager connectionManager;

	private static boolean sslHostnameValidationEnabled;

	private final Timer connectionManagerTimer = new Timer("connectionManagerTimer", true);

	@PostConstruct
	protected void initialize() {
		SOCKET_TIMEOUT = DynamicPropertyFactory.getInstance()
				.getIntProperty(Constants.STORAGE_MASTER_HOST_SOCKET_TIMEOUT_MILLIS, 10000);

		CONNECTION_TIMEOUT = DynamicPropertyFactory.getInstance()
				.getIntProperty(Constants.STORAGE_MASTER_HOST_CONNECT_TIMEOUT_MILLIS, 10000);

		this.httpClient = newClient(hostProperties, SOCKET_TIMEOUT, CONNECTION_TIMEOUT);
		SOCKET_TIMEOUT.addCallback(this.clientloader);
		CONNECTION_TIMEOUT.addCallback(this.clientloader);

		this.connectionManagerTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (HttpClientConnectionManager.this.connectionManager == null) {
					return;
				}
				HttpClientConnectionManager.this.connectionManager.closeExpiredConnections();
			}
		}, 30000, 5000);
	}

	@PreDestroy
	public void stop() {
		this.connectionManagerTimer.cancel();
	}

	private final Runnable clientloader = new Runnable() {
		@Override
		public void run() {
			try {
				HttpClientConnectionManager.this.httpClient.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			HttpClientConnectionManager.this.httpClient = newClient(hostProperties, SOCKET_TIMEOUT, CONNECTION_TIMEOUT);
		}
	};

	public CloseableHttpClient newClient(HostProperties hostProperties, DynamicIntProperty SOCKET_TIMEOUT,
			DynamicIntProperty CONNECTION_TIMEOUT) {
		final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT.get())
				.setConnectTimeout(CONNECTION_TIMEOUT.get()).setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();

		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		if (!sslHostnameValidationEnabled) {
			httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
		}
		return httpClientBuilder.setConnectionManager(newConnectionManager()).disableContentCompression()
				.useSystemProperties().setDefaultRequestConfig(requestConfig)
				.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
				.setRedirectStrategy(new RedirectStrategy() {
					@Override
					public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
							throws ProtocolException {
						return false;
					}

					@Override
					public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
							throws ProtocolException {
						return null;
					}
				}).build();
	}

	public PoolingHttpClientConnectionManager newConnectionManager() {
		try {
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
						throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
						throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			} }, new SecureRandom());

			RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder
					.<ConnectionSocketFactory> create()
					.register(Constants.PROTOCOL_HTTP, PlainConnectionSocketFactory.INSTANCE);
			if (sslHostnameValidationEnabled) {
				registryBuilder.register(Constants.PROTOCOL_HTTPS, new SSLConnectionSocketFactory(sslContext));
			} else {
				registryBuilder.register(Constants.PROTOCOL_HTTPS,
						new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE));
			}
			final Registry<ConnectionSocketFactory> registry = registryBuilder.build();

			connectionManager = new PoolingHttpClientConnectionManager(registry, null, null, null,
					hostProperties.getTimeToLive(), hostProperties.getTimeUnit());
			connectionManager.setMaxTotal(hostProperties.getMaxTotalConnections());
			connectionManager.setDefaultMaxPerRoute(hostProperties.getMaxPerRouteConnections());
			return connectionManager;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public CloseableHttpClient getHttpClient() {
		return this.httpClient;
	}

	public void responseResult(HttpServletRequest request, HttpServletResponse response, Object body)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=utf-8");
		response.getWriter().print(JsonUtils.convert(body));
		response.getWriter().flush();
	}
}
