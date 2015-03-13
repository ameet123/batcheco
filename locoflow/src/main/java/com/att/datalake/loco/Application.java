package com.att.datalake.loco;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.att.datalake.loco.datashipping.DataPacking;
import com.att.datalake.loco.datashipping.ExtractOfferPoints;
import com.att.datalake.loco.mrprocessor.Processor;
import com.att.datalake.loco.mrprocessor.model.ProcessorResult;
import com.att.datalake.loco.offercriteria.OfferParser;
import com.att.datalake.loco.offercriteria.RuntimeSyntaxBuilder;
import com.att.datalake.loco.offercriteria.model.OfferSpecification;
import com.att.datalake.loco.util.Constants;
import com.att.datalake.loco.util.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ImportResource( {"hadoop.xml", "offer-context.xml"})
@Configuration
@EnableAspectJAutoProxy
public class Application implements CommandLineRunner {

	@Resource
	@Qualifier("pigProcessor")
	private Processor pigProcessor;

	@Resource
	@Qualifier("hiveMetaProcessor")
	private Processor hiveMetaProcessor;

	@Autowired
	private OfferParser op;
	@Autowired
	private RuntimeSyntaxBuilder rb;
	@Autowired
	private ExtractOfferPoints eop;
	@Autowired
	private DataPacking dp;
	/**
	 * we need to wire the Interface rather than implementation. then we need a
	 * qualifier to suggest, which implementation we need since this is
	 * injection of component by name, we are using @Resource as opposed to
	 * 
	 * @Autowired, although I guess both will work
	 */
	@Resource
	@Qualifier("hiveProcessor")
	private Processor hp;

	@Value("${offer.file}")
	private String filename;

	public static void main(String[] args) {		
		// start the application
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		context.registerShutdownHook();
	}

	public void run(String... args) throws Exception {
		System.out.println(Constants.VERSION);
		String equalLine = Utility.pad("", 80, '=');
		System.out.println("Loco Processing in steps \n\n");

		// Test of file parsing and hive SQL running
		System.out.println("\n\nStep 0: define offer file\n\n");
		System.out.println(equalLine);
		op.setFilename(filename);
		System.out.println("\n\nStep 1: parse offer file\n\n");
		System.out.println(equalLine);
		List<OfferSpecification> offers = op.parse();
		System.out.println("Offers Found # " + offers.size());
		System.out.println("\n\nStep 2: Build runtime Syntax from offer file\n\n");
		System.out.println(Utility.pad("", 80, '='));
		List<String> sqls = rb.build(offers);
		System.out.println("\n\nStep 3: Execute Map/Reduce processing to build the offers\n\n");
		System.out.println(equalLine);
		int i = 1;
		for (String s : sqls) {
			System.out.println("Running Job # "+ (i++));
			System.out.println(Utility.prettyPrint(s));
			ProcessorResult pr = hp.run(sqls, false);
			System.out.println("Record count:" + pr.getRecordCount() + " Success:" + pr.isQuerySuccess());
			if (!pr.isQuerySuccess()) {
				System.exit(1);
			}
		}

		System.out.println("\n\nStep 4: Extract the offer data to local file system\n\n");
		System.out.println(equalLine);
		System.out.println("Offers downloaded:" + eop.get());
		System.out.println("\n\nStep 5: Pack output file for shipping\n\n");
		System.out.println(equalLine);
		dp.pack(true);
	}

	/**
	 * main purpose is to get the loader path and add that to pig script
	 * 
	 * @return
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public static Gson gson() {
		return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	}
}