package com.att.datalake.loco.offer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import com.att.datalake.loco.offercriteria.OfferParser;
import com.att.datalake.loco.util.Utility;

/**
 * test that the {@link OfferParser} is able to generate the model object from
 * file
 * 
 * @author ac2211
 *
 */
@ContextConfiguration(classes = { OfferYamlLoadTest.class })
public class OfferYamlLoadTest extends AbstractTestNGSpringContextTests {

	private final String LIST_TEST = "listTest.yml";
	private final String MAP_TEST = "mapTest.yml";
	private final String COMPLEX_TEST = "complexMap.yml";
	private final String OFFER_MAP = "OfferMap.yml";
	private Yaml y;

	@BeforeClass
	public void init() {
		y = new Yaml();
	}

	 @Test
	public void testListYamlLoad() throws IOException {
		String yml = Utility.classpathFileToString(LIST_TEST, getClass());
		System.out.println(yml);

		@SuppressWarnings("unchecked")
		List<String> offers = (List<String>) y.load(yml);
		System.out.println(offers);
	}

	 @Test
	public void testMapYamlLoad() throws IOException {
		String yml = Utility.classpathFileToString(MAP_TEST, getClass());
		System.out.println(yml);

		@SuppressWarnings("unchecked")
		Map<String, String> offers = (Map<String, String>) y.load(yml);
		System.out.println("Size of map:" + offers.size());
		for (Entry<String, String> e : offers.entrySet()) {
			System.out.println(e.getKey() + "=>" + e.getValue());
		}
		System.out.println(offers);
	}

	 @Test
	public void testComplexMapYamlLoad() throws IOException {
		String yml = Utility.classpathFileToString(COMPLEX_TEST, getClass());
		System.out.println(yml);

		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> offers = (Map<String, Map<String, String>>) y.load(yml);
		System.out.println("Size of map:" + offers.size());
		for (Entry<String, Map<String, String>> e : offers.entrySet()) {
			System.out.println(e.getKey() + "=>");
			for (Entry<String, String> f : e.getValue().entrySet()) {
				System.out.println("\t" + f.getKey() + "==>" + f.getValue());
			}
		}
		System.out.println(offers);
	}

	@Test
	public void testOfferMapYamlLoad() throws IOException {
		String yml = Utility.classpathFileToString(OFFER_MAP, getClass());
		System.out.println(yml);

		@SuppressWarnings("unchecked")
		Map<String, Map<String, String>> offers = (Map<String, Map<String, String>>) y.load(yml);
		System.out.println("Size of map:" + offers.size());
		Map<String, String> offerMap = offers.get("offers");
		System.out.println("size of internal:" + offerMap.size());

		for (Entry<String, String> f : offerMap.entrySet()) {
			System.out.println("\t" + f.getKey() + "==>" + f.getValue());
		}

	}
}