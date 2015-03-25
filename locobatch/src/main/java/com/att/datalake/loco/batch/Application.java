package com.att.datalake.loco.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import com.att.datalake.loco.batch.job.LocoMrOnlyJob;
import com.att.datalake.loco.batch.shared.LocoConfiguration;
import com.att.datalake.loco.batch.shared.LocoConfiguration.RuntimeData;
import com.att.datalake.loco.batch.step.Step20;
import com.att.datalake.loco.batch.task.Hello2;
import com.att.datalake.loco.batch.task.HelloWorld;

// removed spring boot since we need to scan explicitly the other maven modjules
//@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@PropertySource("application.properties")
@ComponentScan({"com.att.datalake.loco"})
@ImportResource({"hadoop.xml"})
public class Application implements CommandLineRunner {

//	@Value("${spring.datasource.url}")
//	private String url;
//	@Value("${spring.datasource.username}")
//	private String user;
//	@Value("${spring.datasource.password}")
//	private String pass;



	
//	@Bean
//	public DataSource datasource() {
//		LOGGER.info("db url:{}", user, pass, url);
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setUrl(url);
//		dataSource.setUsername(user);
//		dataSource.setPassword(pass);
//		return dataSource;
//	}


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Override
	public void run(String... arg0) throws Exception {
		System.out.println("Running Loco Batch...");
		
	}
}
