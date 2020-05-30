package com.enableets.edu.filestorage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.ClientProtocolException;

public class TestFileUpload {

	private static String url = "http://192.168.118.32:9157/storage/slave/upload";
	// private static Integer chunkSize = 1024;
	private static Integer chunkSize = 1024 * 1024 * 50000;// 不分片

	public static void main(String[] args) {
		File dir = new File("E:/test");
		Long start = System.currentTimeMillis();
		File[] files = dir.listFiles();
		int rows = 1; // 每个批次10个文件
		int batch = files.length / rows;

		for (int i = 0; i < batch; i++) {
			CountDownLatch batchLock = new CountDownLatch(rows);
			Long batchStart = System.currentTimeMillis();
			for (int j = 0; j < rows; j++) {
				File file = files[i * rows + j];
				if (!file.isFile()) { // 只上传文件夹中的file
					return;
				}
				new Thread(new Runnable() {
					public void run() {
						try {
							UploadHelper.upload(url, file, chunkSize);
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							batchLock.countDown();
						}
					}
				}).start();
			}
			try {
				batchLock.await(); // 等待每个批次结束
				long end = System.currentTimeMillis();
				System.out.println("完成第" + (i + 1) + "个批次，cost" + ((end - batchStart) / 1000) + "s");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("完成所有文件上传cost" + ((end - start) / 1000) + "s");
	}
}
