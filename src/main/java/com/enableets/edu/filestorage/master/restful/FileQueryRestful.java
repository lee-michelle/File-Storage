package com.enableets.edu.filestorage.master.restful;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.FileInfo;
import com.enableets.edu.module.service.controller.ServiceControllerAdapter;
import com.enableets.edu.module.service.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 文件管理Restful
 * 
 * @author lemon
 * @since 2018/6/9
 */
@Api(tags = "(2)" + "文件查询", description = "文件查询")
@RestController
@RequestMapping("/storage/file")
public class FileQueryRestful extends ServiceControllerAdapter<FileInfo> {
	/**
	 * 数据处理器
	 */
	@Autowired
	private DefaultDataOperator dataOperator;

	@ApiOperation(value = "文件查询", notes = "文件查询")
	@RequestMapping(value = "/query", method = RequestMethod.GET, produces = { "application/json;charset=UTF-8" })
	public Response<List<FileInfo>> heartbeat(@ApiParam(value = "md5", required = false) String md5,
			@ApiParam(value = "fileName", required = false) String fileName) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("md5", md5);
		condition.put("fileName", fileName);
		List<FileInfo> list = dataOperator.query(condition);
		return responseTemplate.format(list);
	}
}
