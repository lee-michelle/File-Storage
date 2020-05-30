package com.enableets.edu.filestorage.master.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enableets.edu.filestorage.master.data.SlaveInfo;
import com.enableets.edu.filestorage.master.data.dao.SlaveDAO;
import com.enableets.edu.filestorage.master.data.po.SlavePO;
import com.enableets.edu.filestorage.util.BeanUtils;

/**
 * 文件存储服务器信息
 *
 * @author lemon
 * @since 2018/6/9
 */
@Service("slaveService")
public class SlaveService {

	/**
	 * 文件存储服务器DAO
	 */
	@Autowired
	private SlaveDAO slaveDAO;

	/**
	 * 查询所有服务器地址信息
	 *
	 * @return
	 */
	public List<SlavePO> query(Map<String, Object> map) {
		List<SlavePO> list = slaveDAO.query(map);
		return list;
	}

	/**
	 * 查询文件读取地址
	 *
	 * @param slaveId
	 * @return
	 */
	public List<SlavePO> selectStandbySlave(Map<String, Object> condition) {
		List<SlavePO> list = slaveDAO.selectStandbySlave(condition);
		return list;
	}

	/**
	 * 更新slave心跳时间
	 * 
	 * @param condition
	 * @return
	 */
	public boolean updateHeartBeatTime(Map<String, Object> condition) {
		slaveDAO.updateHeartBeatTime(condition);
		return true;
	}

	public boolean updateStatusAndReadonly(SlaveInfo slave) {
		slaveDAO.updateByPrimaryKey(BeanUtils.convert(slave, SlavePO.class));
		return true;

	}

	public SlavePO getSlaveById(String slaveId) {
		SlavePO po = slaveDAO.selectByPrimaryKey(slaveId);
		return po;
	}
}