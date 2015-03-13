package com.att.datalake.loco;

import org.mockito.Mockito;
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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;

/**
 * class for non-hadoop testing which allows us instantiation of spring context
 * we slowly add a component at a time and create a new SI flow xml file and
 * then add that here
 * 
 * About Mock: we need 2 beans: {@link Ingester}, {@link FilePickupHandler}
 * FilePickupHandler is simple, no mock needed Ingester requires
 * {@link MoveToHdfs} which needs {@link FsShell} which needs hadoop and is in
 * locohadoop project Now, what this means is that 1. we certainly need to mock
 * Fsh 2. we mock MoveToHdfs with inject mocks so that the mocked fsh is
 * injected in it 3. we manually instantiate Ingester with the mocked MoveToHdfs
 * 
 * @author ac2211
 *
 */
@Configuration
@PropertySource("application.properties")
@ImportResource("offertest2-context.xml")
@EnableAutoConfiguration(exclude = { EnableIntegrationMBeanExport.class, EnableWebSecurity.class })
@ComponentScan({ "com.att.datalake.loco.offerconfiguration.repository", "com.att.datalake.loco.integration.core","com.att.datalake.loco.integration.beanprocess"})
public class SIContext2Config {	

	/**
	 * we use null, because the bean post processor will inject the {@link MoveToHdfs}
	 * object and stub it as well.
	 * @return
	 */
	@Bean
	public Ingester ingester() {
		return new Ingester(fileMover());
	}

	@Bean
	public FilePickupHandler filePickupHandler() {
		return new FilePickupHandler();
	}
	
	@Bean
	public MoveToHdfs fileMover() {
	    MoveToHdfs fileMover = Mockito.mock(MoveToHdfs.class);
	    Mockito.when(fileMover.move(anyString(), anyString(), anyBoolean())).thenReturn(true);
	    return fileMover;
	}
}