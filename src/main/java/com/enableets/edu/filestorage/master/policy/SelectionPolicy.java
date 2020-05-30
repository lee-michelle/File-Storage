package com.enableets.edu.filestorage.master.policy;

import java.util.List;

/**
 * Slave分配策略
 * 
 * @author lemon
 * @since 2018/6/9
 */
public interface SelectionPolicy {

	Slave getSlave(List<String> serverIdList);

}
