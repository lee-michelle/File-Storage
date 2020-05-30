package com.enableets.edu.filestorage;

/**
 * 常量定义
 * 
 * @author duffy_ding
 * @since 2018/06/01
 */
public class Constants {

	/** 字符编码 */
	public static String CHARACTEREN_ENCODING_UTF8 = "utf-8";

	/** 字符集 */
	public static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	/** 默认文件状态 */
	public static Integer DEFAULT_FILE_STATUS = 1;

	/** http 请求协议 */
	public static String PROTOCOL_HTTP = "http";

	/** https 请求协议 */
	public static String PROTOCOL_HTTPS = "https";

	/**
	 * 服务器错误返回状态码
	 */
	public static Integer STSTUS_CODE = 500;

	/**
	 * 服务器返回状态码
	 */
	public static String DEFAULT_STSTUS_CODE = "200";

	/**
	 * 服务器地址为空错误信息
	 */

	public static String ERROR_MESSAGE = "（10002）服务器忙，请稍后重试！";

	/**
	 * 请求超时错误信息
	 */
	public static String TIME_OUT_MESSAGE = "请求超时";

	/**
	 * 内部服务器文件下载失败信息
	 */
	public static String ERROR_FILE_MESSAGE = "内部服务器文件下载失败";

	/**
	 * 下载文件存在返回结果
	 */
	public static String MESSAGE_DONE = "DONE";

	/**
	 * 
	 */
	public static String MESSAGE_OK = "OK";

	/**
	 * 文件下载失败
	 */
	public static String MESSAGE_FAILTRUE = "FAILTRUE";

	/**
	 * RejectedExecutionException异常返回信息
	 */
	public static String MESSAGE_BUSY = "BUSY";
	/**
	*
	*/
	public static String MESSAGE_FAILURE = "FAILURE";
	/**
	*
	*/
	public static String MESSAGE_SUCCESS = "SUCCESS";

	/**
	 * 请输入查询条件
	 */
	public static String NO_FILEID = "请输入查询条件";

	/**
	 * 任务推送失败返回信息
	 */
	public static String MESSAGE = " {\"status\": \"failure\", \"statusCode\": \"200\", \"message\": \"%s\", \"data\": null }";

	/**
	 * 查询文件成功返回信息
	 */
	public static String DEFAULT_MESSAGE = " {\"status\": \"%s\", \"statusCode\": \"%s\", \"message\": \"OK\", \"data\": \"%s\" }";

	public static String CPU_MESSAGE = "{\"第%s块CPU信息\":{\"CPU的总量MHz\":\"%s\",\"CPU生产商\":\"%s\",\"CPU类别\":\"%s\",\"CPU缓存数量\":\"%s\",\"CPU使用率\":\"%s\"}}";

	public static String USED_CPU_MESSAGE = "{\"用户使用率\":\"%s\",\"系统使用率\":\"%s\",\"当前等待率\":\"%s\",\"当前错误率\":\"%s\",\"当前空闲率\":\"%s\",\"总的使用率\":\"%s\"}";
	/**
	 * 操作系统信息
	 */
	public static String OS_MESSAGE = " {\"操作系统名称\": \"%s\", \"操作系统内核类型\": \"%s\" ,\"操作系统的版本号\":\"%s\"}";
	/**
	 * 网卡信息
	 */
	public static String NET_MESSAGE = " {\"第%s张网卡信息\":{\"IP地址\": \"%s\", \"子网掩码\": \"%s\"}}";

}
