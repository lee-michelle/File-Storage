package com.enableets.edu.filestorage.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.map.MultiValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.SlaveInfo;

/**
 * 监听服务器是否正常
 * 
 * @author lemon
 *
 */
public class HealthCheckService {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckService.class);

	private static DefaultDataOperator dataOperator;

	private static ApplicationConfiguration config;

	public HealthCheckService(DefaultDataOperator dataOperator, ApplicationConfiguration config) {
		this.dataOperator = dataOperator;
		this.config = config;
	}

	public void start() {
		if (config.getEvictionInterval() <= 0) {
			return;
		}
		
		ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2);

		scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				List<SlaveInfo> slaveInfoList = dataOperator.getAllSlaveServer(true);// 查询所有的slave信息
				MultiValueMap map = new MultiValueMap();
				for (SlaveInfo s : slaveInfoList) {
					map.put(s.getGroupId(), s);
				}

				Date currentTime = Calendar.getInstance().getTime();
				for (Iterator<Map.Entry> it = map.entrySet().iterator(); it.hasNext();) {

					// 当前组里面的所有的文件服务器
					List<SlaveInfo> all = (List<SlaveInfo>) it.next().getValue();
					// 当前组里面的活着的文件服务器
					List<SlaveInfo> lived = new ArrayList<SlaveInfo>();

					// 遍历当前组里面的文件服务器
					for (Iterator slaveIt = all.iterator(); slaveIt.hasNext();) {

						SlaveInfo slaveInfo = (SlaveInfo) slaveIt.next();

						// 文件服务器超时
						if ((currentTime.getTime() - slaveInfo.getLastHeartbeatTime().getTime()) / 1000 > config.getLeaseExpirationDuration()) {
							// 如果原来的状态为ON时
							if (slaveInfo.getStatus().equalsIgnoreCase("ON")) {
								// 只有原来的状态为ON时才更新status状态为OFF，同时READONLY更新为Y
								slaveInfo.setStatus("OFF");
								slaveInfo.setReadonly("Y");
								slaveInfo.setUpdateTime(currentTime);
								// DB操作 ...(更新数据库)
								dataOperator.updateStatusAndReadonly(slaveInfo);
							}
						} else {
							// 如果原来的状态为OFF时
							if (slaveInfo.getStatus().equalsIgnoreCase("OFF")) {
								// 只有原来的状态为OFF时才更新status状态为ON，同时READONLY更新为Y
								slaveInfo.setStatus("ON");
								slaveInfo.setReadonly("Y");
								// DB操作 ...（更新数据库）
								dataOperator.updateStatusAndReadonly(slaveInfo);
							}
							lived.add(slaveInfo);
						}
					}
					// 原本是否有可写的服务器
					boolean hasWriteSalve = false;
					// 看看当前组是否有可写的服务器
					for (Iterator slaveIt = lived.iterator(); slaveIt.hasNext();) {
						SlaveInfo slaveInfo = (SlaveInfo) slaveIt.next();
						if (slaveInfo.getReadonly().equalsIgnoreCase("N")) {
							hasWriteSalve = true;
							break;
						}
					}
					// 如果没有
					if (!hasWriteSalve) {
						// 从lived随机选取一个，更新DB
						if (!lived.isEmpty()) {
							int index = (int) (Math.random() * lived.size());
							SlaveInfo slaveInfo = lived.get(index);
							slaveInfo.setReadonly("N");
							dataOperator.updateStatusAndReadonly(slaveInfo);
						} else {
							break;
						}
					}
					// 清空集合
					lived.clear();
					all.clear();
				}
			}
		}, 60, config.getEvictionInterval(), TimeUnit.SECONDS);
	}

}
