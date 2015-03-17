package com.att.datalake.loco.offerconfiguration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * class to wire db connections and set up all access
 * @author ac2211
 *
 */
@Configuration
@PropertySource("application.properties")
@EnableAutoConfiguration
@EnableJpaAuditing
@EnableJpaRepositories
@ComponentScan("com.att.datalake.loco.offerconfiguration.repository")
public class LocoDatastoreContext {

}
