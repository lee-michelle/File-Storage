package com.enableets.edu.filestorage.core;

/**
 * 常量定义
 * 
 * @author duffy_ding lemon
 * @since 2018/06/01
 */
public class Constants {

	/** 字符编码 */
	public static String CHARACTEREN_ENCODING_UTF8 = "utf-8";

	/** 字符集 */
	public static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	/** http 请求协议 */
	public static String PROTOCOL_HTTP = "http";

	/** https 请求协议 */
	public static String PROTOCOL_HTTPS = "https";

	/**
	 * 服务器错误
	 */
	public static Integer SERVER_STATUS_CODE_ERR = 500;

	/**
	 * 服务器返回状态码
	 */
	public static Integer STATUS_CODE_SUCCESS_200 = 200;

	/**
	 * 默认状态
	 */
	public static String FILE_DEFAULT_STATUS = "ON";

	/**
	 * 默认状态
	 */
	public static String FILE_LOCATION_DEFAULT_STATUS = "ON";

	/**
	 * 服务器错误信息
	 */
	public static String MESSAGE_10002 = "(10002)服务暂停，请稍后重试！";

	/**
	 * 请求超时错误信息
	 */
	public static String MESSAGE_10010 = "(10010)请求超时";

	/**
	 * 内部服务器文件下载失败信息
	 */
	public static String MESSAGE_10030 = "(10030)服务暂停，请稍后重试";

	/**
	 * 缺少参数
	 */
	public static String MESSAGE_10014 = "(10014)缺少参数";

	/** 文件未找到错误信息 */
	public static String MESSAGE_10031 = "(10031)文件流解析失败";

	// public static String

	/**
	 * RejectedExecutionException异常返回信息
	 */
	// public static String TASK_STATUS_BUSY = "BUSY";
	/**
	 * 任务推送失败
	 */
	// public static String TASK_STATUS_FAILURE = "FAILURE";
	/**
	 * 任务推送成功
	 */
	// public static String TASK_STATUS_DONE = "DONE";

	public static String RANDOM_POLICY = "random";

	public static String POLLING_POLICY = "polling";

	/**
	 * 消息模板
	 */
	public static String PATTERN_RESPONSE = " {\"status\": \"%s\", \"statusCode\": \"%s\", \"message\": \"OK\", \"data\": \"%s\" }";

	/** map key 浏览器标识信息 */
	public static String MAP_KEY_USER_AGENT = "User-Agent";

	/** map key 文件路径 */
	public static String PATH = "path";

	/** map key 文件标识 */
	public static String FIELD_NAME_FILE_ID = "fileId";

	public static String FIELD_NAME_ALIAS_NAME = "aliasName";

	/**
	 * 文件md5码
	 */
	public static String FIELD_NAME_MD5 = "md5";
	/**
	 * 文件名称
	 */
	public static String FIELD_NAME_FILE_NAME = "fileName";

	/**
	 * 文件路径
	 */
	public static String FIELD_NAME_PATH = "path";

	public static String FIELD_NAME_TASK_ID = "taskId";

	public static String FILE_NAME_CHUNK_SIZE = "chunkSize";

	public static String FILE_NAME_CHUNK_INDEX = "chunkIndex";

	public static String FILE_NAME_SIZE = "size";

	/** socket 超时时间 */
	public static String STORAGE_MASTER_HOST_SOCKET_TIMEOUT_MILLIS = "storage.host.socket-timeout-millis";

	/** 连接超时时间 */
	public static String STORAGE_MASTER_HOST_CONNECT_TIMEOUT_MILLIS = "storage.host.connect-timeout-millis";

	public static String STORAGE_HOST_URL = "storage.host.url";
	
	/**
	 * 生成根目录级别
	 */
	public static Integer DIR_DEPTH = 2;
	
	public static final String FIELD_NAME_FILE_MD5 = "fileMd5";
	
	public static final String FIELD_NAME_FILE_SIZE = "fileSize";
	
	public static final String FIELD_NAME_CHUNK_INDEX = "chunkIndex";
	
	public static final String FIELD_NAME_CHUNK_SIZE = "chunkSize";
	
	public static final String FIELD_NAME_CHUNK_UUID = "uuid";
	
}
