package com.enableets.edu.filestorage.util;

import java.util.Random;

/**
 * 路径工具类
 * 
 * @author lemon
 * @since 2018/6/1
 */
public class DirUtils {

	/**
	 * 随机生成子目录
	 * 
	 * @param rootDir
	 *            起始目录
	 * @param level
	 *            子目录层级
	 * @param mkdirs
	 *            是否生成生成的目录
	 * @return 完整的目录
	 */
	public static String randDir(int level) {
		final String allChar = "0123456789abcdefghijklmnopqrstuvwxyz";

		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; i++) {
			sb.append("/");
			String p = String.valueOf(allChar.charAt(random.nextInt(allChar.length())));
			sb.append(p);
		}
		sb.append("/");
		return sb.toString();
	}

}
