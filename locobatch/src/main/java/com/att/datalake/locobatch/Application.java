package com.att.datalake.locobatch;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.att.datalake.locobatch.task.Hello2;
import com.att.datalake.locobatch.task.HelloWorld;

@SpringBootApplication
@Configuration
@EnableBatchProcessing
@PropertySource("application.properties")
@EnableAutoConfiguration
public class Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String user;
	@Value("${spring.datasource.password}")
	private String pass;
	
	@Autowired
	private JobBuilderFactory jobBuilders;

	@Autowired
	private StepBuilderFactory stepBuilders;

	@Bean
	public DataSource datasource() {
		LOGGER.info("db url:{}", user, pass, url);
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		 dataSource.setUrl(url);
		 dataSource.setUsername(user);
		 dataSource.setPassword(pass);
		return dataSource;
	}

	@Bean
	public Job helloWorldJob() {
		return jobBuilders.get("helloWorldJob").incrementer(new RunIdIncrementer()).flow(step()).next(step2()).end().build();
	}

	@Bean
	public Step step() {
		return stepBuilders.get("step5").allowStartIfComplete(true).tasklet(tasklet()).build();
	}
	@Bean
	public Step step2() {
		return stepBuilders.get("step6").tasklet(tasklet2()).build();
	}

	@Bean
	public Tasklet tasklet() {
		return new HelloWorld();
	}
	@Bean
	public Tasklet tasklet2() {
		return new Hello2();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
