package com.enableets.edu.filestorage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FileUploadServletController {

	@RequestMapping(value = "/index")
	public String fileUpload(Model model) {
		return "index";
	}

	@RequestMapping(value = "/down")
	public String fileDown(Model model) {
		return "indexDown";
	}

	@RequestMapping(value = "/sync")
	public String fileSync(Model model) {
		return "sync";
	}

	@RequestMapping(value = "/query")
	public String query(Model model) {
		return "query";
	}

	@RequestMapping(value = "/add")
	public void add(Model model) {

	}

	@RequestMapping(value = "/upload")
	public String upload(Model model) {
		return "upload";
	}

}
