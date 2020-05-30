package com.enableets.edu.filestorage.master.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.SlaveInfo;

/**
 * 支持轮询的Slave选择策略
 * <p>
 * 通过取最后活动时间为最小的Slave,以达到轮询的目的
 */
public class PollingSelectionPolicy implements SelectionPolicy {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PollingSelectionPolicy.class);

	private List<Slave> slaveCache = new ArrayList<Slave>();

	private Object lock = new Object();

	private DefaultDataOperator defaultDataOperator;

	public PollingSelectionPolicy(DefaultDataOperator defaultDataOperator) {
		this.defaultDataOperator = defaultDataOperator;
	}

	public PollingSelectionPolicy() {
	}

	/**
	 * 获取slave信息
	 */
	@Override
	public Slave getSlave(List<String> slaveIdList) {

		if (slaveCache.isEmpty()) {
			synchronized (lock) {
				if (slaveCache.isEmpty()) {
					List<SlaveInfo> tmp = defaultDataOperator.getAllSlaveServer();
					for (Iterator<SlaveInfo> it = tmp.iterator(); it.hasNext();) {
						SlaveInfo po = it.next();
						Slave slave = new Slave();
						slave.setReadUri(po.getReadUri());
						slave.setWriteUri(po.getWriteUri());
						slave.setSyncUri(po.getSyncUri());
						slave.setSlaveId(po.getSlaveId());
						slaveCache.add(slave);
					}
				}
			}
		}

		List<Slave> avaliableServerList = new ArrayList<>(slaveIdList.size());
		for (Iterator<Slave> it = slaveCache.iterator(); it.hasNext();) {
			Slave server = it.next();
			if (slaveIdList.contains(server.getSlaveId())) {
				avaliableServerList.add(server);
			}
		}
		if (avaliableServerList.isEmpty()) {
			return null;
		}

		Slave result = getSlaveInternal(avaliableServerList);
		return result;
	}

	private Slave getSlaveInternal(List<Slave> list) {
		try {
			synchronized (slaveCache) {
				// 按照活动时间，从小到大排序
				list.sort(new Comparator<Slave>() {
					@Override
					public int compare(Slave o1, Slave o2) {
						return new Long(o1.getLastActiveTime()).compareTo(o2.getLastActiveTime());
					}
				});

				Slave first = list.iterator().next();
				long currentTime = System.currentTimeMillis();

				// 如果当前时间大于其最后活动时间
				if (currentTime >= first.getLastActiveTime()) {
					first.setLastActiveTime(currentTime);
					return first;
				}
			}
		} catch (Exception e) {
			LOGGER.error("getSlaveInternal is error", e);
		}
		return null;
	}

}
