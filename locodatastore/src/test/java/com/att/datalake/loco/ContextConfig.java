package com.att.datalake.loco;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * class for non-hadoop testing which allows us instantiation of spring context
 * we are not doing component scan because unnecessary items such as hadoop come into 
 * the picture.
 * META-INF/orm.xml and @EnableJpaAuditing are both needed for @CreatedDate 
 * we need component scan for offerDAO
 * @author ac2211
 *
 */
@Configuration
@PropertySource("application.properties")
@EnableAutoConfiguration
@EnableJpaAuditing
@EnableJpaRepositories
@ComponentScan("com.att.datalake.loco.offerconfiguration.repository")
public class ContextConfig {

}