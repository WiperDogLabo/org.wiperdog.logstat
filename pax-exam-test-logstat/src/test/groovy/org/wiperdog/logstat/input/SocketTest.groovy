package org.wiperdog.logstat.input;
import static org.junit.Assert.*
import static org.ops4j.pax.exam.CoreOptions.*

import javax.inject.Inject

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.ops4j.pax.exam.Configuration
import org.ops4j.pax.exam.Option
import org.ops4j.pax.exam.junit.PaxExam
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy
import org.ops4j.pax.exam.spi.reactors.PerClass
import org.wiperdog.logstat.common.TestUTCommon
import org.wiperdog.logstat.service.LogStat
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class SocketTest {
	public SocketTest() {
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
	BufferedReader br;
	String line;
	ProcessBuilder builder;
	Process proc;
	String result;
	TestUTCommon test_common = new TestUTCommon();
	String wd = System.getProperty("user.dir");
	private String logstatDir ;

	@Before
	public void prepare() {
		input_conf = new HashMap<String, Object>();
		output_conf = new HashMap<String, Object>();
		filter = new HashMap<String, Object>();
		conf = new HashMap<String, Object>();
		output_conf.put("type", "file");
		result = "";
		// filter data of log
		filter = [
			"filter_type" : "match_field",
			"filter_conf" : [
				"message" : '^.*$'
			]
		]
		// get data of socket log
		input_conf.put("input_type", "socket");
		logstatDir = wd + "/src/test/resources/logstat"
		
		try {
			svc = context.getService(context.getServiceReference(LogStat.class.getName()));
			//Run logs_generator script
			def logs_gen_dir = wd + "/logs_generator"
			def listCmd = new ArrayList<String>();
			listCmd.add("./logs_generator.sh");
			listCmd.add("socket")
			builder = new ProcessBuilder();
			builder.directory(new File(logs_gen_dir))
			builder.command(listCmd);
			proc = builder.start();
		} catch (Exception e) {
			e.printStackTrace()
		}
		

		
	}

	@After
	public void finish() {
		//proc.terminate();
		proc.destroy();
	}

	/**
	 * Check output with input config contains one variable is port.
	 * Func will be get default of timeout for run.
	 * Expected: return data contains message of log level err. 
	 */
	@Test
	public void testSocket_01() {
		println "2222222222222"
		input_conf.put("port", 2809);
		input_conf.put("timeout", 15);
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_01.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_01.log")
		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);

		svc.runLogStat(logstatDir,conf)
		// result data
		result = readFileOutput("src/test/resources/data_test/input/testSocket/output/testSocket_01.log");
		assertNotNull(result)
		assertTrue(result.contains('[Socket] : This is a log message from socket !'))
		Thread.sleep(3000)
	}

	/**
	 * Check output with input config contains two variable is port and timeout.
	 * Expected: return data contains message of log level err.
	 */
	@Test
	public void testSocket_02() {
		input_conf.put("port", 2809);
		input_conf.put("timeout", 15);
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_02.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_02.log")
		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);

		svc.runLogStat(logstatDir,conf)
		// result data
		result = readFileOutput("src/test/resources/data_test/input/testSocket/output/testSocket_02.log");
		assertNotNull(result)
		assertTrue(result.contains('[Socket] : This is a log message from socket !'))
		Thread.sleep(3000)
	}

	/**
	 * Check output with input config contains two variable is port and host
	 * Expected: return data contains message of log level err.
	 */
	@Test
	public void testSocket_03() {
		input_conf.put("port", 2809);
		input_conf.put("host", "localhost");
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_03.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_03.log")

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);

		svc.runLogStat(logstatDir,conf)
		// result data
		result = readFileOutput("src/test/resources/data_test/input/testSocket/output/testSocket_03.log");
		assertNotNull(result)
		assertTrue(result.contains('[Socket] : This is a log message from socket !'))
		Thread.sleep(3000)
	}

	/**
	 * Check output with input config contains all of variable is port, timeout and host
	 * Expected: return data contains message of log level err.
	 */
	@Test
	public void testSocket_04() {
		input_conf.put("port", 2809);
		input_conf.put("timeout", 30);
		input_conf.put("host", "localhost");
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_04.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_04.log")

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);

		svc.runLogStat(logstatDir,conf)
		// result data
		result = readFileOutput("src/test/resources/data_test/input/testSocket/output/testSocket_04.log");
		assertNotNull(result)
		assertTrue(result.contains('[Socket] : This is a log message from socket !'))
		Thread.sleep(3000)
	}

	/**
	 * Check output with input config contains one variable is port and value of port is out of range
	 * Expected: return data is null.
	 */
	@Test
	public void testSocket_05() {
		input_conf.put("port", 66666);
		input_conf.put("timeout", 15);
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_05.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_05.log")

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);
		svc.runLogStat(logstatDir,conf)

		assertFalse((new File("src/test/resources/data_test/input/testSocket/output/testSocket_05.log")).exists())
		Thread.sleep(3000)
	}

	/**
	 * Check output with input config contains port is null
	 * Expected: return data is null.
	 */
	@Test
	public void testSocket_06() {
		input_conf.put("port", null);
		input_conf.put("timeout", 15);
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_06.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_06.log")

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);

		svc.runLogStat(logstatDir,conf)
		assertFalse((new File("src/test/resources/data_test/input/testSocket/output/testSocket_06.log")).exists())
		Thread.sleep(3000)
	}

	/**
	 * Check output with input config contains two variable is port, host. Value of host does not exist.
	 * Expected: return data is null.
	 */
	@Test
	public void testSocket_07() {
		input_conf.put("port", 2809);
		input_conf.put("port", 15);
		input_conf.put("host", "10.0.1.289");
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_07.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_07.log")

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);

		svc.runLogStat(logstatDir,conf)
		assertFalse((new File("src/test/resources/data_test/input/testSocket/output/testSocket_07.log")).exists())
		Thread.sleep(3000)
	}

	/**
	 * Check output with value of host is null.
	 * Expected: return data contains message of log level err.
	 */
	@Test
	public void testSocket_08() {
		input_conf.put("port", 2809);
		input_conf.put("timeout", 15);
		input_conf.put("host", null);
		def outFile = ["path":"src/test/resources/data_test/input/testSocket/output/testSocket_08.log"]
		output_conf.put("config", outFile)
		test_common.cleanData("src/test/resources/data_test/input/testSocket/output/testSocket_08.log")

		conf.put("input",input_conf);
		conf.put("filter",filter);
		conf.put("output",output_conf);

		svc.runLogStat(logstatDir,conf)
		// result data
		result = readFileOutput("src/test/resources/data_test/input/testSocket/output/testSocket_08.log");
		assertNotNull(result)
		assertTrue(result.contains('[Socket] : This is a log message from socket !'))
		Thread.sleep(3000)
	}

	/**
	 * @param filePath
	 *            path to file contains data output for test
	 * @return data output need to compare
	 * @throws IOException
	 */
	public String readFileOutput(String filePath) throws IOException {
		// get data output need to compare, read from file
		try {
			String output = "";
			br = new BufferedReader(new FileReader(filePath));
			while ((line = br.readLine()) != null) {
				if (output != "") {
					output += "\n" + line;
				} else {
					output += line;
				}
			}
			return output;
		} catch (Exception ex) {
			//			ex.printStackTrace();
			//			throw ex;
			System.out.println("No data output !");
		}
	}
}
