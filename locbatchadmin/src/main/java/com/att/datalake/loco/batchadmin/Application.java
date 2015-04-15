package com.att.datalake.loco.batchadmin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.att.datalake.loco.offerconfiguration.model.Offer;
import com.att.datalake.loco.offerconfiguration.repository.BatchJobDAO;
import com.att.datalake.loco.offerconfiguration.repository.OfferDAO;

@SpringBootApplication
@ComponentScan({"com.att.datalake.loco.offerconfiguration", "com.att.datalake.loco.batchadmin"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

@Controller
@RequestMapping("/")
class HomeController {
	
	@RequestMapping(method=RequestMethod.GET)
	public String home() {
		return "home";
	}
}

@RestController
@RequestMapping("/api")
class RestApiController {
	
	@Autowired
	private OfferDAO dao;
	@Autowired
	private BatchJobDAO batchDao;
	
	@RequestMapping("/inner")
	public List<String> api() {
		List<String> criteria = new ArrayList<String>();
		for(Offer o: dao.findAllOffers()) {
			criteria.add(o.getOfferCriteria());
		}
		return criteria;
	}
	@RequestMapping("/offerCount")
	public int getOfferCount() {
		return (int) dao.countOffers();
	}
	
	@RequestMapping("/jobCount")
	public int getJobCount() {
		return (int) batchDao.countJobs();
	}
	
}
