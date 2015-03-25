package com.att.datalake.loco.batch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import com.att.datalake.loco.batch.util.Constants;

/**
 * main application class which is run by spring boot
 * We need ComponentScan explicitly, since we need to scan across multiple
 * maven modules, so removing @SpringBootApplication and adding 
 * Configuration and ComponentScan 
 * Job configuration is in {@link BatchConfiguration} which is a component and as such 
 * a bean. It gets loaded and the appropriate job is launched
 * @author ac2211
 *
 */
@Configuration
@EnableAutoConfiguration
@PropertySource("application.properties")
@ComponentScan({"com.att.datalake.loco"})
@ImportResource({"hadoop.xml"})
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}


	@Override
	public void run(String... arg0) throws Exception {
		System.out.println(Constants.VERSION);		
	}
}