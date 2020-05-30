package com.enableets.edu.filestorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

public class UploadHelper {
	/** socket超时时间 */
	private static final int socketTimeout = 10000;

	/** 链接超时时间 */
	private static final int connectTimeout = 10000;

	private static String url = "http://localhost:8087/storage/upload";

	public static void main(String[] args) throws ClientProtocolException, IOException {
		// File file = new File("E:/tools/mysql-5.7.20-winx64.zip");
		// upload(url, file, chunkSize);
	}

	public static void upload(String url, File file, int chunkSize) throws ClientProtocolException, IOException {
		long totalSize = file.length();
		int chunkNum = Long.valueOf(totalSize / chunkSize).intValue();
		int index = 0;
		long finished = 0;
		String uuid = UUID.randomUUID().toString().replaceAll("\\-", "").toUpperCase();
		for (; index < chunkNum; index++) {
			int start = index * chunkSize;
			int end = (index + 1) * chunkSize - 1;
			upload(url, uuid, file, start, end, index, chunkSize);
			finished += chunkSize;
		}

		// 上传剩下的字节
		if (totalSize % chunkSize > 0) {
			int start = (int) finished;
			int end = (int) totalSize - 1;
			upload(url, uuid, file, start, end, index, chunkSize);
		}
	}

	public static void upload(String url, String uuid, File file, int start, int end, int position, Integer chunkSize)
			throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(requestConfig);

		// 此处一定要用RFC6532，网上普遍用的BROWSER_COMPATIBLE依然会出现中文名乱码
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);

		UploadBlockStreamBody chunk = new UploadBlockStreamBody(file, start, end);
		multipartEntityBuilder.addPart("file", chunk);

		httpPost.setEntity(multipartEntityBuilder.build());
		HttpRequest httpRequest = httpPost;

		if (file.length() > chunkSize) {
			List<Header> headers = new ArrayList<Header>();
			String md5 = Md5Encrypt.getFileMD5(file);
			Header header = new BasicHeader("fileMd5", md5);
			headers.add(header);

			header = new BasicHeader("uuid", uuid);
			headers.add(header);

			Long fileSize = file.length();
			header = new BasicHeader("fileSize", String.valueOf(fileSize));
			headers.add(header);

			header = new BasicHeader("chunkIndex", String.valueOf(position));
			headers.add(header);

			header = new BasicHeader("chunkSize", String.valueOf(chunkSize));
			headers.add(header);

			Header[] arr = new Header[headers.size()];
			arr = headers.toArray(arr);
			httpRequest.setHeaders(arr);
		}

		CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity entity = httpResponse.getEntity();
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuf = new StringBuffer();
			String str;
			while ((str = br.readLine()) != null) {
				strBuf.append(str);
			}
			System.out.println(strBuf);
		}
		httpClient.close();
		if (httpResponse != null) {
			httpResponse.close();
		}
	}

}
