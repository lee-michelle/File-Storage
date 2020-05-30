package com.enableets.edu.filestorage.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * mimeType工具类，提供获取文档mimeType和extension(后缀)的方法
 * 
 * @author lemon
 * @since 2018/6/11
 *
 */
public class MimetypeProvider {
	/**
	 * 非openoffice文档mimeType配置文件路径
	 */
	private static final String MIME_TYPE_PATH = "classpath:mimetype-map.xml";

	/**
	 * openoffice文档的mimeType配置文件路径
	 */
	private static final String MIME_TYPE_OPENOFFICE_PATH = "classpath:mimetype-map-openoffice.xml";

	/**
	 * 日志
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MimetypeProvider.class);

	/**
	 * 配置文件中所有的已经配置的mimeType
	 */
	private static final List<String> MIME_TYPES;

	/**
	 * extension -- mimeType之间的映射,多对一的关系
	 * 如果一个mimeType配置了多个extension,则该映射只存储默认的extension，或者配置的第一个extension
	 */
	private static final Map<String, String> MIME_TYPE_TO_EXTENSION;

	/**
	 * mimeType -- extension之间的映射,一对多的关系
	 */
	private static final Map<String, String> EXTENSION_TO_MIME_TYPE;

	/**
	 * mimeType显示值集合
	 */
	private static final Map<String, String> MIME_TYPE_TO_DISPLAY;

	/**
	 * extension显示值集合,如果该extension没有指定此属性值，则使用该extension的mimeType的dispaly属性值
	 */
	private static final Map<String, String> EXTENSION_TO_DISPLAY;

	/**
	 * mimeType配置文件的地址
	 */
	private static final List<String> SOURCE_LOCATIONS;

	static {
		SOURCE_LOCATIONS = new ArrayList<String>();
		MIME_TYPE_TO_EXTENSION = new HashMap<String, String>();
		MIME_TYPE_TO_DISPLAY = new HashMap<String, String>();
		EXTENSION_TO_DISPLAY = new HashMap<String, String>();
		EXTENSION_TO_MIME_TYPE = new HashMap<String, String>();
		MIME_TYPES = new ArrayList<String>();
		SOURCE_LOCATIONS.add(MIME_TYPE_PATH);
		SOURCE_LOCATIONS.add(MIME_TYPE_OPENOFFICE_PATH);
		Iterator<String> iterator = SOURCE_LOCATIONS.iterator();
		while (iterator.hasNext()) {
			String path = iterator.next();
			DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
			try {
				InputStream inputStream = defaultResourceLoader.getResource(path).getInputStream();
				SAXReader saxReader = new SAXReader();
				try {
					Document document = saxReader.read(inputStream);
					Element rootElement = document.getRootElement();
					Iterator<Element> elementsIterator = rootElement.elementIterator("mimetypes");
					while (elementsIterator.hasNext()) {
						Element mimeType = elementsIterator.next();
						List<Element> list = mimeType.elements("mimetype");
						// 开始对mimeType进行遍历
						for (Element element : list) {
							String mimeTypeValue = element.attributeValue("mimetype");
							String mimeTypeDisaplyValue = element.attributeValue("display");
							MIME_TYPES.add(mimeTypeValue);
							MIME_TYPE_TO_DISPLAY.put(mimeTypeValue, mimeTypeDisaplyValue);
							List<Element> extensions = element.elements("extension");
							boolean isFirst = true;
							for (Element extension : extensions) {
								String extensionValue = extension.getText();
								String extensionDisplayValue = extension.attributeValue("display");
								EXTENSION_TO_MIME_TYPE.put(extensionValue, mimeTypeValue);
								if (extensionDisplayValue != null && extensionDisplayValue.length() > 0) {
									EXTENSION_TO_DISPLAY.put(extensionValue, extensionDisplayValue);
								} else if (mimeTypeDisaplyValue != null && mimeTypeDisaplyValue.length() > 0) {
									EXTENSION_TO_DISPLAY.put(extensionValue, mimeTypeDisaplyValue);
								}
								boolean isDefault = Boolean.parseBoolean(extension.attributeValue("default"));
								if (isDefault || isFirst) {
									MIME_TYPE_TO_EXTENSION.put(mimeTypeValue, extensionValue);
								}
								isFirst = false;
							}
						}
					}
				} catch (DocumentException e) {
					LOGGER.error(" saxReader error:" + e);
				}
			} catch (Throwable e) {
				LOGGER.error(e + "");
			}

		}
	}

	/**
	 * 根据文档的后缀名(extension)去获取该文档的mimeType
	 * 
	 * @param extension
	 *            后缀名 如 doc
	 * @return mimeType
	 */
	public static String getMimeTypeByExtension(String extension) {
		return EXTENSION_TO_MIME_TYPE.get(extension);
	}

	/**
	 * 根据给定的mimeType去获取该文档默认的extension(或者配置文件中配置的第一个extension)
	 * 
	 * @param mimeType
	 *            mimeType
	 * @return 默认的后缀名
	 */
	public static String getDefaultExtensionByMimeType(String mimeType) {
		return MIME_TYPE_TO_EXTENSION.get(mimeType);
	}

	/**
	 * 根据给定的mimeType获取所有的extension
	 * 
	 * @param mimeType
	 *            mimeType
	 * @return 所有的后缀名
	 */
	public static List<String> getExtensionsByMimeType(String mimeType) {
		List<String> extensions = new ArrayList<String>();
		Iterator<Entry<String, String>> iterator = EXTENSION_TO_MIME_TYPE.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			if (entry.getValue().equals(mimeType)) {
				extensions.add(entry.getKey());
			}
		}
		return extensions;
	}
}
