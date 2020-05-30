package com.enableets.edu.filestorage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import org.apache.http.entity.mime.content.AbstractContentBody;

public class UploadBlockStreamBody extends AbstractContentBody {

	private File file;
	private int start; // 第一个字节是0
	private int end;

	private UploadBlockStreamBody(String mimeType) {
		super(mimeType);
	}

	/**
	 * 自定义的ContentBody构造子
	 * 
	 * @param blockNumber分块数
	 * @param blockIndex当前第几块
	 * @param targetFile要上传的文件
	 */
	public UploadBlockStreamBody(File file, int start, int end) {
		this("application/octet-stream");

		this.file = file;
		this.start = start;
		this.end = end;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");// 负责读取数据
		raf.skipBytes(start);

		final int blockSize = 1024;
		byte[] buffer = new byte[blockSize];

		int blockNum = (end - start + 1) / blockSize;
		int remained = (end - start + 1) % blockSize;

		int len;
		// 分块读取
		for (int i = 0; i < blockNum; i++) {
			len = raf.read(buffer, 0, blockSize);
			out.write(buffer, 0, len);
		}

		// 剩下的字节
		if (remained > 0) {
			len = raf.read(buffer, 0, remained);
			out.write(buffer, 0, len);
		}

		if (raf != null) {
			raf.close();
		}
		out.flush();
	}

	@Override
	public String getCharset() {
		return "utf-8";
	}

	@Override
	public String getTransferEncoding() {
		// TODO Auto-generated method stub
		return "binary";
	}

	@Override
	public String getFilename() {
		// TODO Auto-generated method stub
		return this.file.getName();
	}

	@Override
	public long getContentLength() {
		return this.end - this.start + 1;
	}

}