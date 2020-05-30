package com.enableets.edu.filestorage.master.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;

import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.SlaveInfo;

/**
 * 随机选取Slave策略
 * 
 * @author pc-psq
 * @since 2018/6/9
 */
public class RandomSelectionPolicy implements SelectionPolicy {

	private List<Slave> slaveCache = new ArrayList<Slave>();

	private Object lock = new Object();

	private DefaultDataOperator defaultDataOperator;

	public RandomSelectionPolicy(DefaultDataOperator defaultDataOperator) {
		this.defaultDataOperator = defaultDataOperator;
	}

	public RandomSelectionPolicy() {
	}

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
			Collections.emptyList();
		}

		int index = RandomUtils.nextInt(avaliableServerList.size());
		Slave result = avaliableServerList.get(index);

		return result;
	}

}
