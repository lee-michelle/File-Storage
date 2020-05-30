package com.enableets.edu.filestorage.core;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * slave心跳
 * 
 * @author lemon
 * @since 2018/6/9
 *
 */
public class HeartbeatAgent {
	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatAgent.class);

	/**
	 * 配置文件读取类
	 */
	private ApplicationConfiguration config;

	public HeartbeatAgent(ApplicationConfiguration config) {
		this.config = config;
	}

	/**
	 * 开启线程心跳监听
	 */
	public void start() {
		if (config.getHeartbeatInterval() <= 0) {
			return;
		}
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2);
		String urlString = config.getMasterUrl() + "server/heartbeat?slaveId=" + config.getSlaveId();

		try {
			final URL url = new URL(urlString);

			scheduled.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					InputStream inputStream = null;
					HttpURLConnection connection = null;
					try {
						// 得到connection对象。
						connection = (HttpURLConnection) url.openConnection();
						// 设置请求方式
						connection.setRequestMethod("GET");
						// 连接
						connection.connect();
						inputStream = connection.getInputStream();
					} catch (Exception e) {
						LOGGER.error("send heartbeat error", e);
					} finally {
						IOUtils.closeQuietly(inputStream);
						connection.disconnect();
					}
				}
			}, 60, config.getHeartbeatInterval(), TimeUnit.SECONDS);
		} catch (MalformedURLException e) {
			LOGGER.error("HeartbeatAgent start error", e);
		}
	}

}
