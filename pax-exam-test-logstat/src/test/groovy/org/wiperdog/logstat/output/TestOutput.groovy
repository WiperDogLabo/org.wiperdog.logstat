package org.wiperdog.logstat.output;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

import javax.inject.Inject;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.junit.runner.JUnitCore;
import org.osgi.service.cm.ManagedService;
import org.wiperdog.logstat.service.LogStat;
import org.jruby.embed.InvokeFailedException;
import org.jruby.embed.ScriptingContainer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TestOutput {
	public TestOutput() {
	}

	@Inject
	private org.osgi.framework.BundleContext context;
	@Configuration
	public Option[] config() {
		return options(
		cleanCaches(true),
		frameworkStartLevel(6),
		// felix log level
		systemProperty("felix.log.level").value("4"), // 4 = DEBUG
		// setup properties for fileinstall bundle.
		systemProperty("felix.home").value(wd),
		// Pax-exam make this test code into OSGi bundle at runtime, so
		// we need "groovy-all" bundle to use this groovy test code.
		mavenBundle("org.codehaus.groovy", "groovy-all", "2.2.1").startLevel(2),
		mavenBundle("org.jruby", "jruby-complete", "1.7.10").startLevel(2),
		mavenBundle("org.wiperdog", "org.wiperdog.directorywatcher", "0.1.0").startLevel(3),
		mavenBundle("org.wiperdog", "org.wiperdog.jrubyrunner", "1.0").startLevel(3),
		mavenBundle("org.wiperdog", "org.wiperdog.logstat", "1.0").startLevel(3),
		junitBundles()
		);
	}

	private LogStat svc;
	HashMap<String , Object> input_conf;
	HashMap<String , Object> output_conf;
	HashMap<String , Object> filter;
	HashMap<String , Object> conf;
	HashMap<String , Object> configOutput;
	BufferedReader br;
	String line;
	String result;
	String expected;
	String logs_test_dir;
	String wd = System.getProperty("user.dir");
	private String logstatDir ;
	def outFile ;
	@Before
	public void prepare() {
		input_conf = new HashMap<String, Object>();
		output_conf = new HashMap<String, Object>();
		filter = new HashMap<String, Object>();
		conf = new HashMap<String, Object>();
		result = "";
		expected = "";
		logs_test_dir = wd + "/src/test/resources/data_test/output";
		// filter data of log
		filter = [
			"filter_type" : "match_field",
			"filter_conf" : [
				"date": "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) [0-9]{2} \\d{2}:\\d{2}:\\d{2}",
				"message": "(vmRHEL55x64).*"
			]
		]
		// get data of plaintext
		input_conf.put("input_type", "file");
		input_conf.put("path", logs_test_dir);
		input_conf.put("file_format", "plain_text");
		input_conf.put("monitor_type", "line");
		input_conf.put("start_pos", 3);
		//input_conf.put("asc_by_fname", true);
		// set output config
		outFile = wd + "/src/test/resources/data_test/output/output/output.log"
		output_conf.put("type", "file");
		configOutput = [
			"path": outFile
		]
		output_conf.put("config", configOutput);
		logstatDir = wd + "/src/test/resources/logstat"
		new File(outFile).delete();
		try {
			svc = context.getService(context.getServiceReference(LogStat.class.getName()));
		} catch (Exception e) {
			e.printStackTrace()
		}
	}

	@After
	public void finish() {
		//new File(outFile).delete();
	}

	//===========================Check output to File===============================
	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'file'.
	 * Value of output_conf['config']['path'] to path exist.
	 * Expected: write data to file successfully.
	 */
	@Test
	public void testOutput_01() {

		input_conf.put("start_file_name", "result_testString_01.log");

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)
		// get output data of func
		assertTrue(new File(outFile).exists())
		result = (new File(outFile)).text
		// get data expected to comparse
		expected = (new File(wd + "/src/test/resources/data_test/output/expected/expected_testString_01.log")).text
		assertNotNull(result)
		assertEquals(expected, result)
	}

	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'file'.
	 * Value of output_conf['config']['path'] is 'stdout'.
	 * Expected: return data to console.
	 */
	@Test
	public void testOutput_02() {
		input_conf.put("start_file_name", "result_testString_01.log");

		configOutput = [
			"path": "stdout"
		]
		output_conf.put("type", "file");
		output_conf.put("config", configOutput);

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)
		assertFalse((new File(outFile)).exists())
	}

	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'file'.
	 * Value of output_conf['config']['path'] is empty.
	 * Expected: return data to console.
	 */
	@Test
	public void testOutput_03() {
		input_conf.put("start_file_name", "result_testString_01.log");

		configOutput = [
			"path": ""
		]
		output_conf.put("type", "file");
		output_conf.put("config", configOutput);

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)
		assertFalse((new File(outFile)).exists())
	}

	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'file'.
	 * Value of output_conf['config']['path'] is null.
	 * Expected: return data to console.
	 */
	@Test
	public void testOutput_04() {
		input_conf.put("start_file_name", "result_testString_01.log");

		configOutput = [
			"path": null
		]
		output_conf.put("type", "file");
		output_conf.put("config", configOutput);

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)
		assertFalse((new File(outFile)).exists())
	}

	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'file'.
	 * Value of output_conf['config']['path'] to path does not exist.
	 * Expected: return data is null => not create file output.log
	 */
	@Test
	public void testOutput_05() {
		input_conf.put("start_file_name", "result_testString_01.log");

		configOutput = [
			"path": "/user/path/not/exist/output.log"
		]
		output_conf.put("type", "file");
		output_conf.put("config", configOutput);

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)
		assertFalse((new File(outFile)).exists())
	}

	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'file'.
	 * Value of output_conf does not contains 'config'.
	 * Expected: return data is null => not create file output.log
	 */
	@Test
	public void testOutput_06() {
		input_conf.put("start_file_name", "result_testString_01.log");

		output_conf.put("type", "file");
		output_conf.remove("config")
		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)
		assertFalse((new File(outFile)).exists())
	}

	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'file'.
	 * Value of output_conf['config'] does not contains 'path'.
	 * Expected: return data is null => not create file output.log
	 */
	@Test
	public void testOutput_07() {
		input_conf.put("start_file_name", "result_testString_01.log");

		configOutput = []
		output_conf.put("type", "file");
		output_conf.put("config", configOutput);

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)
		assertFalse((new File(outFile)).exists())
	}

	//===========================Check output of Job================================
	/**
	 * Check func with all of variable: data, output_conf is true.
	 * Value of output_conf['type'] is 'job'.
	 * Expected: return data.
	 */
	@Test
	public void testOutput_08() {
		input_conf.put("start_file_name", "result_testString_01.log");

		output_conf.put("type", "job");
		output_conf.remove("config")

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		Map<String,Object> result = svc.runLogStat(logstatDir,conf)
		JsonSlurper js = new JsonSlurper();
		//expected = (new File(wd + "/src/test/resources/data_test/output/expected/expected_testString_02.log")).text
		assertNotNull(result)
		def objExpected = js.parse(new File(wd + "/src/test/resources/data_test/output/expected/expected_testString_02.log"))
		assertEquals(result['persistent_data']['start_file_name'], objExpected['persistent_data']['start_file_name']);
	}

}