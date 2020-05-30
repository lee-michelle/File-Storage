package com.enableets.edu.filestorage.master.data.dao;

import java.util.List;
import java.util.Map;

import com.enableets.edu.filestorage.master.data.po.SlavePO;
import com.enableets.edu.framework.core.dao.BaseDao;

/**
 * 文件存储服务器DAO
 * 
 * @author lemon
 * 
 * @since 2019/6/9
 *
 */
public interface SlaveDAO extends BaseDao<SlavePO> {
	/**
	 * 查询服务器信息
	 * 
	 * @return
	 */
	public List<SlavePO> query(Map<String, Object> map);

	/**
	 * 上传完毕之后，根据上传服务器，获取待同步的文件服务器
	 * 
	 * @param condition
	 * @return
	 */
	public List<SlavePO> selectStandbySlave(Map<String, Object> condition);

	/**
	 * 更新slave心跳时间
	 * 
	 * @param map
	 */
	public void updateHeartBeatTime(Map<String, Object> map);

}
