package com.att.datalake.loco;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.hadoop.fs.FsShell;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.att.datalake.loco.integration.activator.FilePickupHandler;
import com.att.datalake.loco.integration.activator.Ingester;
import com.att.datalake.loco.integration.hdfs.MoveToHdfs;

/**
 * class for non-hadoop testing which allows us instantiation of spring context
 * we are not doing component scan because unnecessary items such as hadoop come into 
 * the picture.
 * META-INF/orm.xml and @EnableJpaAuditing are both needed for @CreatedDate 
 * @author ac2211
 *
 */
@Configuration
@PropertySource("application.properties")
@ImportResource("offertest-context.xml")
@EnableAutoConfiguration(exclude = { EnableIntegrationMBeanExport.class, EnableWebSecurity.class })
@ComponentScan({"com.att.datalake.loco.integration.core", "com.att.datalake.loco.offerconfiguration.repository"})
public class SIContextConfig {

	@Mock
	private FsShell fsh;

	@InjectMocks
	@Mock
	private MoveToHdfs fileMover;
	
	@Bean
	public Ingester ingester() {
		return new Ingester(fileMover);
	}
	@Bean
	public FilePickupHandler filePickupHandler() {
		return new FilePickupHandler();
	}
}