package com.enableets.edu.filestorage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.enableets.edu.filestorage.core.ApplicationConfiguration;
import com.enableets.edu.filestorage.core.HealthCheckService;
import com.enableets.edu.filestorage.core.HeartbeatAgent;
import com.enableets.edu.filestorage.core.HostProperties;
import com.enableets.edu.filestorage.master.DownloadHandler;
import com.enableets.edu.filestorage.master.UploadHandler;
import com.enableets.edu.filestorage.master.data.DefaultDataOperator;
import com.enableets.edu.filestorage.master.policy.PollingCondition;
import com.enableets.edu.filestorage.master.policy.PollingSelectionPolicy;
import com.enableets.edu.filestorage.master.policy.RandomCondition;
import com.enableets.edu.filestorage.master.policy.RandomSelectionPolicy;
import com.enableets.edu.filestorage.slave.FileDownloadProcessor;
import com.enableets.edu.filestorage.slave.FileDownloadServlet;
import com.enableets.edu.filestorage.slave.FileUploadServlet;
import com.enableets.edu.filestorage.slave.sync.FileSyncServlet;
import com.enableets.edu.framework.core.EnableETSFramework;
import com.enableets.edu.module.service.EnableETSService;
import com.enableets.edu.module.workqueue.sdk.TaskExecutor;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@Configuration
@EnableETSFramework
@EnableETSService
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.enableets.edu.filestorage", "com.enableets.edu.module.workqueue.sdk" })
@MapperScan(basePackages = { "com.enableets.edu.filestorage.master.data.dao" })
@ServletComponentScan(basePackages = "com.enableets.edu.filestorage.master")
public class FileStorageBootstrap extends WebMvcConfigurerAdapter {

	/**
	 * 配置文件读取
	 */
	@Autowired
	public ApplicationConfiguration config;

	/**
	 * 数据处理器
	 */
	@Autowired
	private DefaultDataOperator dataOperator;

	/**
	 * 多线程处理
	 */
	@Autowired
	private TaskExecutor taskExecutor;

	public static void main(String[] args) {
		SpringApplication.run(FileStorageBootstrap.class, args);
	}

	@Bean
	public UploadHandler uploadHandler() {
		return new UploadHandler();
	}

	@Bean
	public DownloadHandler downloadHandler() {
		return new DownloadHandler();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		return new TaskExecutor(config.getMaxWorkThread());
	}

	/**
	 * slave端的上传与下载
	 *
	 * @return
	 */
	@Bean
	public FileDownloadProcessor getProcessor() {
		return new FileDownloadProcessor();
	}

	@Bean
	public FileUploadServlet uploadSlaveServlet() {
		return new FileUploadServlet();
	}

	@Bean
	public ServletRegistrationBean servletSlaveRegistrationBean() {
		return new ServletRegistrationBean(uploadSlaveServlet(), "/storage/slave/upload");
	}

	@Bean
	public FileDownloadServlet downloadSlaveServlet() {
		return new FileDownloadServlet();
	}

	@Bean
	public ServletRegistrationBean servletDownloadBean() {
		return new ServletRegistrationBean(downloadSlaveServlet(), "/storage/slave/download");
	}

	@Bean
	@Conditional(PollingCondition.class)
	public PollingSelectionPolicy pollingSelectionPolicy() {
		return new PollingSelectionPolicy(dataOperator);
	}

	@Bean
	@Conditional(RandomCondition.class)
	public RandomSelectionPolicy randomSelectionPolicy() {
		return new RandomSelectionPolicy(dataOperator);
	}

	@Bean
	public HeartbeatAgent heartbeatAgent() {
		HeartbeatAgent heartbeatAgent = new HeartbeatAgent(config);
		heartbeatAgent.start();
		return heartbeatAgent;
	}

	@Bean
	public FileSyncServlet fileSyncServlet() {
		return new FileSyncServlet(taskExecutor);
	}

	@Bean
	public ServletRegistrationBean servletFileSyncRegistrationBean() {
		return new ServletRegistrationBean(fileSyncServlet(), "/storage/slave/sync");
	}

	@Bean(name = "hostProperties")
	@ConfigurationProperties(prefix = "storage.host")
	public HostProperties hostProperties() {
		return new HostProperties();
	}

	@Bean
	public HealthCheckService healthCheckService() {
		HealthCheckService healthCheckService = new HealthCheckService(dataOperator, config);
		healthCheckService.start();
		return healthCheckService;
	}
}
