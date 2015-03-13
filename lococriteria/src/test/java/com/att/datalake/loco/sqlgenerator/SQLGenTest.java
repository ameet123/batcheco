package com.att.datalake.loco.sqlgenerator;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.att.datalake.loco.util.Utility;

/**
 * test sql syntax generator
 * 
 * @author ac2211
 *
 */
@ContextConfiguration
public class SQLGenTest extends AbstractTestNGSpringContextTests {
	@Autowired
	private SQLClauseBuilder sql;
	@Autowired
	private SQLStatementBuilder complete;
	
	private String column = "feature_group";
	private List<String> featureGroups;
	private List<String> featureCodes;
	private List<Integer> intList;
	private List<Date> dates;
	private List<Timestamp> times;
	private List<String> predicates;
	private Map<String, String> columns; // select clause
	private Map<String, String> transformMap;
	private Map<String, String> fromMap;
	private Map<String, Boolean> inlineMap; 
	private Map<String, String> joinMap;
	// for complete sql
	private String select;
	private String from;
	private String where;
	private String groupby;
	
	
	
	@Configuration
	static class config {
		@Bean
		SQLClauseBuilder sQLClauseBuilder() {
			return new SQLClauseBuilder();
		}
		@Bean
		SQLStatementBuilder completeSQLBuilder() {
			return new SQLStatementBuilder();
		}
	}

	@BeforeClass
	public void init() {
		joinMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("a.id", "b.id");
				put("a.name", "b.name");
			}
		};
		fromMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("loco_charge", "a");
				put("loco_adjustment", "b");
			}
		};
		inlineMap = new HashMap<String, Boolean>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("loco_charge", true);
				put("loco_adjustment", false);
			}
		};
		transformMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("a.ACTV_AMT", "Sum");
			}
		};
		columns = new HashMap<String, String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				put("BAN", "a");
				put("SUBSCRIBER_NO", "a");
				put("FINANCE_EFF_DATE", "a");
				put("PARTITION_KEY", "a");
				put("ACTV_BILL_SEQ_NO", "a");
				put("ACT_DATE", "a");
				put("FEATURE_CODE", "a");
				put("SOC", "a");
			}
		};
		predicates = new ArrayList<String>();
		featureGroups = new ArrayList<String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				add("TF");
				add("VF");
				add("PF");
			}
		};
		featureCodes = new ArrayList<String>() {
			private static final long serialVersionUID = 730952064487227447L;
			{
				add("AMBMIN");
				add("AMBREV");
				add("CVAB");
				add("CVAT");
				add("CVBK");
				add("CVCL");
				add("CVDP");
				add("CVIC");
				add("CVMT");
				add("CVOC");
				add("CVRM");
				add("CVTL");
				add("DCK");
				add("DEPOST");
				add("GENDEP");
				add("LTPYM");
				add("LTPYMM");
				add("REFUND");
				add("RFNDEP");
				add("RSLTFE");
				add("TLRNCE");
				add("XMLPA");
				add("PAFM");
			}
		};
		intList = new ArrayList<Integer>() {
			private static final long serialVersionUID = -9032302864197027958L;
			{
				add(1);
				add(2343);
				add(98323);
			}
		};
		dates = new ArrayList<Date>() {
			private static final long serialVersionUID = -9032302864197027958L;
			{
				add(new Date());
			}
		};
		times = new ArrayList<Timestamp>() {
			private static final long serialVersionUID = -9032302864197027958L;
			{
				add(new Timestamp(System.currentTimeMillis()));
			}
		};
	}

	@Test(groups = {"0"})
	public void tesJoin() {
		String s = sql.joinPredicate(joinMap);
		System.out.println(s);
		predicates.add(s);
	}
	@Test(groups = {"1"}, dependsOnGroups = {"0"})
	public void testExclusionGen() {
		String s = sql.inOutPredicate(column, featureGroups, String.class, true);
		predicates.add(s);
		System.out.println(s);
	}

	@Test(groups = {"1"}, dependsOnGroups = {"0"})
	public void testFeatureCodeEx() {
		String s = sql.inOutPredicate("feature_code", featureCodes, String.class, true);
		predicates.add(s);
		System.out.println(s);
	}
	@Test(groups = {"1"}, dependsOnGroups = {"0"})
	public void testintEx() {
		String s = sql.inOutPredicate("dummy_int", intList, Integer.class, true);
		System.out.println(s);
		predicates.add(s);
	}
	@Test(groups = {"1"}, dependsOnGroups = {"0"})
	public void tesDateEx() {
		String s = sql.inOutPredicate("dummy_date", dates, Date.class, true);
		System.out.println(s);
		predicates.add(s);
	}
	@Test(groups = {"1"}, dependsOnGroups = {"0"})
	public void tesTimestampEx() {
		String s = sql.inOutPredicate("dummy_time", times, Timestamp.class, true);
		System.out.println(s);
		predicates.add(s);
	}
	
	@Test(groups = {"2"} , dependsOnGroups = {"1"})
	public void tesFrom() {
		String s = sql.from(fromMap, null);
		this.from = s;
		System.out.println(s);
	}
	@Test(groups = {"2"} , dependsOnGroups = {"1"})
	public void tesFromInline() {
		String s = sql.from(fromMap, inlineMap);
		System.out.println(s);
	}

	@Test(groups = {"3"},dependsOnGroups = {"1", "2"})
	public void testWhere() {
		String s = sql.where(predicates, true);
		System.out.println(s);
		this.where = s;
	}
	@Test(groups = {"3"},dependsOnGroups = {"1", "2"})
	public void testGroupBy() {
		String s = sql.groupBy(columns);
		System.out.println(s);
		this.groupby = s;
	}
	
	@Test(groups = {"3"}, dependsOnGroups = {"1", "2"}, dependsOnMethods = {"testWhere"})
	public void testSELECT() {
		String s = sql.select(columns, transformMap);
		System.out.println(s);
		this.select = s;
	}
	@Test(groups = {"4"}, dependsOnGroups = {"3"})
	public void testStatement() {
		complete.doFrom(from);
		complete.doSelect(select);
		complete.doGroupBy(groupby);
		complete.doWhere(where);
		
		System.out.println(Utility.prettyPrint(complete.build()));
	}
	
	@BeforeMethod
	private void startingTest(Method method) {
		System.out.println("\n******* " + method.getName() + " ********"+"\n");
	}
}