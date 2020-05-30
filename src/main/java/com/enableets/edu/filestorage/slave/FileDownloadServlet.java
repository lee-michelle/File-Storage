package com.enableets.edu.filestorage.slave;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 并发数受控制的文件下载的servlet
 * 
 * @author lemon
 * @since 2018/6/1
 */
// @WebServlet(name = "fileSyncServlet", urlPatterns ="/storage/slave/sync")
@Controller
public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * 文件下载处理 该类会捕捉文件下载过程中产生的所有异常,并返回响应的状态码 902 文件流输出错误 404 文件不存在
	 */
	@Autowired
	FileDownloadProcessor processor;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processor.process(request, response);
	}
}
