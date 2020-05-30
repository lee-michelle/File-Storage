package com.enableets.edu.filestorage.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enableets.edu.filestorage.core.Constants;
import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.data.FileInfo;
import com.enableets.edu.filestorage.master.data.Location;
import com.enableets.edu.filestorage.master.data.SlaveInfo;
import com.enableets.edu.filestorage.master.policy.SelectionPolicy;
import com.enableets.edu.filestorage.master.policy.Slave;
import com.enableets.edu.module.io.utils.EncodeUtils;

/**
 * 工具类
 * 
 * @author lemon
 * @since 2018/6/8
 *
 */
public class Utils {

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	/**
	 * PDF文件加密
	 */
	public static final String ATTACHMENT_FILE_ENCRYPTED_YES = "1";

	/**
	 * PDF文件未加密
	 */
	public static final String ATTACHMENT_FILE_ENCRYPTED_NO = "0";

	public static FileInfo buildFileInfo(File file, String filename, 
			String uuid) throws FileNotFoundException, IOException {
		FileInfo fileInfo = new FileInfo();
		
		fileInfo.setUuid(uuid);
		fileInfo.setName(filename);
		
		String encoding = EncodeUtils.guestFileEncoding(file);
		fileInfo.setEncoding(encoding);
		
		fileInfo.setMd5(Md5Encrypt.getFileMD5(file));
		fileInfo.setSize(file.length());
		
		String ext = getFileExt(filename);
		fileInfo.setExt(ext);
		
		String sizeDisplay = getDataSize(Long.valueOf(fileInfo.getSize()));
		fileInfo.setSizeDisplay(sizeDisplay);

		return fileInfo;
	}

	/**
	 * 生成文件相关信息的对象
	 * 
	 * @param file
	 *            目标文件
	 * @return 表示文件相关信息的对象
	 * @throws FileNotFoundException
	 * @throws IOException
	 *             i/o error
	 */
	public static FileInfo buildFileInfo(File file, String fileName, String realName, String uuid, String path, Map<String, Object> params)
			throws FileNotFoundException, IOException {

		FileInfo fileInfo = new FileInfo();

		String ext = getFileExt(realName);
		String encoding = null;
		encoding = EncodeUtils.guestFileEncoding(file);
		fileInfo.setUuid(uuid);
		fileInfo.setName(realName);
		fileInfo.setEncoding(encoding);
		fileInfo.setMd5(Md5Encrypt.getFileMD5(file));
		fileInfo.setSize(file.length());
		fileInfo.setExt(ext);
		String sizeDisplay = getDataSize(Long.valueOf(fileInfo.getSize()));
		fileInfo.setSizeDisplay(sizeDisplay);
		fileInfo.setParams(params);
		if (ext != null && ext.equalsIgnoreCase("pdf")) {
		}

		return fileInfo;
	}

	/**
	 * @param fileFullName
	 *            文件全名
	 * @return ext后缀名
	 */
	public static String getFileExt(String fileFullName) {
		String fileExt = null;
		if (fileFullName != null && fileFullName.indexOf(".") != -1) {
			fileExt = fileFullName.substring(fileFullName.lastIndexOf(".") + 1).toLowerCase();
		}
		return fileExt;
	}

	/**
	 * 设置文件的MD5码
	 * 
	 * @param file
	 */
	public static String calculatedMd5(File file) {
		String md5 = Md5Encrypt.getFileMD5(file);
		return md5;
	}

	/**
	 * 格式化文件大小
	 * 
	 * @param size
	 * @return
	 */
	public static String getDataSize(Long size) {
		DecimalFormat formater = new DecimalFormat("####.00");
		if (size < 1024) {
			return String.valueOf(size);
		} else if (size < 1024 * 1024) {
			float kbsize = size / 1024f;
			return formater.format(kbsize) + "KB";
		} else if (size < 1024 * 1024 * 1024) {
			float mbsize = size / 1024f / 1024f;
			return formater.format(mbsize) + "MB";
		} else if (size < 1024 * 1024 * 1024 * 1024) {
			float gbsize = size / 1024f / 1024f / 1024f;
			return formater.format(gbsize) + "GB";
		} else {
			return "size: error";
		}
	}

	/**
	 * 下载文件下载文件名称header
	 * 
	 * @param filename
	 *            文件名
	 * @param agent
	 *            浏览器标识
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	public static void addFileNameHeader(String filename, String agent, HttpServletResponse response) throws UnsupportedEncodingException {
		if (agent == null) { // 如果没有UA，则默认使用IE的方式进行编码，因为毕竟IE还是占多数的
			filename = URLEncoder.encode(filename, "utf-8");
			// 原始的空格使用urlEncode 编码后转换为+号(基于历史原因), 而ie解析时会直接作为+号处理,
			// 因此需要手工替换一下这个特殊字符.
			filename = filename.replaceAll("\\+", "%20");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
			return;
		}

		agent = agent.toLowerCase();
		if (agent.indexOf("msie") != -1 || agent.indexOf("trident") != -1) {
			filename = URLEncoder.encode(filename, "utf-8");
			// 原始的空格使用urlEncode 编码后转换为+号(基于历史原因), 而ie解析时会直接作为+号处理,
			// 因此需要手工替换一下这个特殊字符.
			filename = filename.replaceAll("\\+", "%20");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
		} else if (agent.indexOf("opera") != -1) {
			// Opera浏览器只能采用filename*
			filename = URLEncoder.encode(filename, "utf-8");
			response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + filename);
		} else if (agent.indexOf("applewebkit") != -1 || agent.indexOf("mozilla") != -1 || agent.indexOf("safari") != -1) {
			// Chrome、FireFox、Safari
			// filename部分只能使用utf-8的原始字节，而http header 必须使用单字节编码的字符串,
			// 因此需要将原始内容重新构造为iso-8单字节编码的字符串
			filename = new String(filename.getBytes("utf-8"), "ISO_8859_1");
			response.addHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
		}
	}

	/**
	 * 发送文件同步请求
	 * 
	 * @param httpClient
	 *            CloseableHttpClient
	 * @param workQueueUrl
	 *            任务队列url
	 * @param params
	 *            文件信息
	 * @return boolean 成功/失败
	 * @throws MalformedURLException
	 *             workQueueUrl 格式不对
	 */

	public static boolean sendFileSyncRequest(CloseableHttpClient httpClient, String workQueueUrl, Object[] params) throws MalformedURLException {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"taskId\":").append("\"").append(params[0]).append("\"").append(",");
		sb.append("\"targetId\":").append("\"").append(params[1]).append("\"").append(",");
		sb.append("\"targetName\":").append("\"").append(params[2]).append("\"").append(",");
		sb.append("\"estimateCostTime\":").append("\"").append(params[3]).append("\"").append(",");
		sb.append("\"targetUri\":").append("\"").append(params[4]).append("\"").append(",");
		sb.append("\"data\":").append("{").append("\"fileId\":").append("\"").append(params[5]).append("\"").append(",");
		sb.append("\"path\":").append("\"").append(params[6]).append("\"").append(",");
		sb.append("\"md5\":").append("\"").append(params[7]).append("\"").append("}");
		sb.append("}");
		byte[] content = sb.toString().getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
		InputStreamEntity entity = new InputStreamEntity(inputStream, new Long(content.length), ContentType.parse("application/json"));

		URL hostUrl = new URL(workQueueUrl);
		HttpHost httpHost = new HttpHost(hostUrl.getHost(), hostUrl.getPort(), hostUrl.getProtocol());
		HttpPost httpPost = new HttpPost(hostUrl.getPath());
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;

		try {
			response = httpClient.execute(httpHost, httpPost);
			return true;
		} catch (Exception e) {
			LOGGER.error("send file synchronized message error", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(response);
		}
		return false;
	}

	/**
	 * 消息推送
	 * 
	 * @param httpClient
	 * @param statusUrl
	 * @param taskId
	 * @param code
	 * @return
	 */
	public static boolean sendFileSyncCompletionRequest(CloseableHttpClient httpClient, String statusUrl, String taskId, String code) {
		CloseableHttpResponse response = null;
		String url = statusUrl + "?taskId=" + taskId + "&code=" + code;
		HttpGet httpGet = new HttpGet(url);
		try {
			response = httpClient.execute(httpGet);
			return true;
		} catch (Exception e) {
			LOGGER.error("send file synchronized completion message error", e);
		} finally {
			IOUtils.closeQuietly(response);
		}
		return false;
	}

	/**
	 * 获取文件同步需要的参数
	 * 
	 * @param request
	 * @param charsetName
	 * @return
	 */
	public static String getBodyContextAsString(HttpServletRequest request, String charsetName) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = request.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charsetName)));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
		return sb.toString();
	}

	/**
	 * 获取文件所在服务器位置
	 * 
	 * @param httpServletResponse
	 * @param fileId
	 * @param fileService
	 * @return
	 */
	public static List<FileInfo> getFileInfo(String fileId, String aliasName, DefaultDataOperator defaultDataOperator) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put(Constants.FIELD_NAME_FILE_ID, fileId);
		condition.put(Constants.FIELD_NAME_ALIAS_NAME, aliasName);
		List<FileInfo> list = defaultDataOperator.queryFileInfo(condition);
		return list;

	}

	/**
	 * 获取读取文件地址
	 * 
	 * @param list
	 * @param selectionPolicy
	 * @return
	 */
	public static Slave getInternalDownloadUrl(List<Location> list, SelectionPolicy selectionPolicy) {
		List<String> slaveIdList = new ArrayList<>();
		for (Iterator<Location> it = list.iterator(); it.hasNext();) {
			Location po = it.next();
			String slaveId = po.getSlaveId();
			slaveIdList.add(slaveId);
		}
		Slave slave = selectionPolicy.getSlave(slaveIdList);
		return slave;
	}

	/**
	 * 设置请求错误返回结果
	 * 
	 * @param httpServletResponse
	 * @param message
	 * @throws IOException
	 */
	public static void responseError(HttpServletResponse httpServletResponse, String message) throws IOException {
		httpServletResponse.setContentType(Constants.DEFAULT_CONTENT_TYPE);
		httpServletResponse.setCharacterEncoding(Constants.CHARACTEREN_ENCODING_UTF8);
		httpServletResponse.setStatus(Constants.SERVER_STATUS_CODE_ERR);
		httpServletResponse.getWriter().println(message);
	}

	/**
	 * 时间格式化
	 * 
	 * @param date
	 * @return
	 * @throws IOException
	 */
	public static String formatDate(Date date) throws IOException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 获取上传文件服务器信息
	 * 
	 * @param params
	 * @return
	 */
	public static Slave getUploadSlave(SelectionPolicy selectionPolicy, DefaultDataOperator dataOperator) {
		List<SlaveInfo> list = dataOperator.getUploadSlaveServer();
		List<String> slaveIdList = new ArrayList<>();
		for (Iterator<SlaveInfo> it = list.iterator(); it.hasNext();) {
			SlaveInfo slave = it.next();
			String slaveId = slave.getSlaveId();
			slaveIdList.add(slaveId);
		}
		Slave slave = selectionPolicy.getSlave(slaveIdList);
		return slave;
	}

	/**
	 * 时间工具类计算总时间
	 * 
	 * @param ms
	 * @return
	 */
	public static String friendDuration(long ms) {
		short ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;
		long day = ms / (long) dd;
		long hour = (ms - day * (long) dd) / (long) hh;
		long minute = (ms - day * (long) dd - hour * (long) hh) / (long) mi;
		long second = (ms - day * (long) dd - hour * (long) hh - minute * (long) mi) / (long) ss;
		long milliSecond = ms - day * (long) dd - hour * (long) hh - minute * (long) mi - second * (long) ss;
		StringBuilder str = new StringBuilder();
		if (day > 0L) {
			str.append(day).append("天,");
		}
		if (hour > 0L) {
			str.append(hour).append("小时,");
		}
		if (minute > 0L) {
			str.append(minute).append("分钟,");
		}
		if (second > 0L) {
			str.append(second).append("秒,");
		}
		if (milliSecond > 0L) {
			str.append(milliSecond).append("毫秒,");
		}
		if (str.length() > 0) {
			str = str.deleteCharAt(str.length() - 1);
		}
		return str.toString();
	}

	/**
	 * 返回输出信息
	 * 
	 * @param zin
	 *            需返回的数据流
	 * @param out
	 *            回应response 输出流
	 * @throws Exception
	 */
	public static void response(InputStream zin, OutputStream out) throws IOException {
		byte[] bytes = new byte[1024];
		int bytesRead = -1;
		while ((bytesRead = zin.read(bytes)) != -1) {
			out.write(bytes, 0, bytesRead);
		}
	}

	/**
	 * 创建随机UUID
	 * 
	 * @return
	 */
	public static String buildUUID() {
		return UUID.randomUUID().toString().replaceAll("\\-", "").toUpperCase();
	}
}
