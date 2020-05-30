package com.enableets.edu.filestorage.core;

import java.util.concurrent.TimeUnit;

/**
 * host连接属性
 * 
 * @author duffy_ding
 * @since 2018/06/01
 */
public class HostProperties {

	/**
	 * The maximum number of total connections the proxy can hold open to
	 * backends.
	 */
	private Integer maxTotalConnections = 200;

	/**
	 * The maximum number of connections that can be used by a single route.
	 */
	private Integer maxPerRouteConnections = 20;

	/**
	 * The lifetime for the connection pool.
	 */
	private Integer timeToLive = -1;

	/**
	 * The time unit for timeToLive.
	 */
	private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

	public Integer getMaxTotalConnections() {
		return maxTotalConnections;
	}

	public void setMaxTotalConnections(Integer maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	public Integer getMaxPerRouteConnections() {
		return maxPerRouteConnections;
	}

	public void setMaxPerRouteConnections(Integer maxPerRouteConnections) {
		this.maxPerRouteConnections = maxPerRouteConnections;
	}

	public Integer getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(Integer timeToLive) {
		this.timeToLive = timeToLive;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
}
