package com.enableets.edu.filestorage.master.restful;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.SlaveInfo;
import com.enableets.edu.module.service.controller.ServiceControllerAdapter;
import com.enableets.edu.module.service.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * slave管理restful
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Api(tags = "(1)" + "Slave心跳", description = "Slave心跳")
@RestController
@RequestMapping("/storage/server")
public class HealthRestful extends ServiceControllerAdapter<SlaveInfo> {

	/**
	 * 数据处理器
	 */
	@Autowired
	private DefaultDataOperator dataOperator;

	@ApiOperation(value = "Slave心跳", notes = "Slave心跳")
	@RequestMapping(value = "/heartbeat", method = RequestMethod.GET, produces = { "application/json;charset=UTF-8" })
	public Response<Boolean> slave(@ApiParam(value = "slave标示", required = true) String slaveId) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("slaveId", slaveId);
		condition.put("lastHeartbeatTime", Calendar.getInstance().getTime());
		dataOperator.updateHeartBeatTime(condition);
		return responseTemplate.format(true);
	}

}
